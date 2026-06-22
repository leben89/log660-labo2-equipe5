package ca.ets.log660.labo2.facade;

import ca.ets.log660.labo2.model.Client;
import ca.ets.log660.labo2.model.Film;
import ca.ets.log660.labo2.model.FilmProduit;
import ca.ets.log660.labo2.model.Location;
import ca.ets.log660.labo2.util.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebflixFacade {

    public Client connecter(String courriel, String motDePasse) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Client> query = session.createQuery(
                    "from Client c where lower(c.courriel) = :courriel and c.motDePasse = :motDePasse",
                    Client.class);
            query.setParameter("courriel", courriel.trim().toLowerCase());
            query.setParameter("motDePasse", motDePasse);
            query.setMaxResults(1);
            Client client = query.uniqueResult();
            if (client != null) {
                Hibernate.initialize(client.getForfait());
            }
            return client;
        }
    }

    public List<Film> rechercherFilms(CriteresRechercheFilm criteres) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Map<String, Object> params = new HashMap<>();
            StringBuilder hql = new StringBuilder("select distinct f from Film f ");

            if (nonVide(criteres.getPays())) {
                hql.append(" join f.paysProduction p ");
            }
            if (nonVide(criteres.getGenre())) {
                hql.append(" join f.genres g ");
            }
            if (nonVide(criteres.getRealisateur())) {
                hql.append(" join f.realisateurs r ");
            }
            if (nonVide(criteres.getActeur())) {
                hql.append(" join f.acteurs a ");
            }

            hql.append(" where 1 = 1 ");

            if (nonVide(criteres.getTitre())) {
                hql.append(" and lower(f.titre) like :titre ");
                params.put("titre", like(criteres.getTitre()));
            }
            if (criteres.getAnneeMin() != null) {
                hql.append(" and f.annee >= :anneeMin ");
                params.put("anneeMin", criteres.getAnneeMin());
            }
            if (criteres.getAnneeMax() != null) {
                hql.append(" and f.annee <= :anneeMax ");
                params.put("anneeMax", criteres.getAnneeMax());
            }
            if (nonVide(criteres.getPays())) {
                hql.append(" and lower(p.nomPays) like :pays ");
                params.put("pays", like(criteres.getPays()));
            }
            if (nonVide(criteres.getLangue())) {
                hql.append(" and lower(f.langue) like :langue ");
                params.put("langue", like(criteres.getLangue()));
            }
            if (nonVide(criteres.getGenre())) {
                hql.append(" and lower(g.nomGenre) like :genre ");
                params.put("genre", like(criteres.getGenre()));
            }
            if (nonVide(criteres.getRealisateur())) {
                hql.append(" and (lower(r.nom) like :realisateur or lower(r.prenom) like :realisateur) ");
                params.put("realisateur", like(criteres.getRealisateur()));
            }
            if (nonVide(criteres.getActeur())) {
                hql.append(" and (lower(a.nom) like :acteur or lower(a.prenom) like :acteur) ");
                params.put("acteur", like(criteres.getActeur()));
            }

            hql.append(" order by f.titre asc, f.annee desc ");

            Query<Film> query = session.createQuery(hql.toString(), Film.class);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            query.setMaxResults(100);
            return query.list();
        }
    }

    public Film consulterFilm(Integer filmId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Film film = session.get(Film.class, filmId);
            if (film == null) {
                return null;
            }
            Hibernate.initialize(film.getGenres());
            Hibernate.initialize(film.getPaysProduction());
            Hibernate.initialize(film.getActeurs());
            Hibernate.initialize(film.getRealisateurs());
            Hibernate.initialize(film.getPosters());
            Hibernate.initialize(film.getBandesAnnonces());
            Hibernate.initialize(film.getProduits());
            return film;
        }
    }

    public Location louerFilm(Integer clientId, Integer filmProduitId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Client client = session.get(Client.class, clientId);
            if (client == null) {
                throw new IllegalArgumentException("Client introuvable.");
            }

            FilmProduit produit = session.get(FilmProduit.class, filmProduitId);
            if (produit == null) {
                throw new IllegalArgumentException("Produit de film introuvable.");
            }
            session.buildLockRequest(LockOptions.UPGRADE).lock(produit);

            Integer copies = produit.getCopieDispo() == null ? 0 : produit.getCopieDispo();
            if (copies < 1) {
                throw new IllegalStateException("Aucune copie disponible pour ce film.");
            }

            long nombreLocations = compterProduitsLoues(session, clientId);
            int limiteForfait = client.getForfait().getMaxLocations();
            if (nombreLocations + 1 > limiteForfait) {
                throw new IllegalStateException("Limite de locations du forfait atteinte.");
            }

            Location location = new Location();
            location.setClient(client);
            location.getFilmProduits().add(produit);
            produit.setCopieDispo(copies - 1);

            session.save(location);
            session.update(produit);
            transaction.commit();
            return location;
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    private long compterProduitsLoues(Session session, Integer clientId) {
        Query<Long> query = session.createQuery(
                "select count(fp.id) from Location l join l.filmProduits fp where l.client.id = :clientId",
                Long.class);
        query.setParameter("clientId", clientId);
        return query.uniqueResult();
    }

    private boolean nonVide(String valeur) {
        return valeur != null && !valeur.trim().isEmpty();
    }

    private String like(String valeur) {
        return "%" + valeur.trim().toLowerCase() + "%";
    }
}
