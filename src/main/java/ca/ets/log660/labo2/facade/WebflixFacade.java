package ca.ets.log660.labo2.facade;

import ca.ets.log660.labo2.model.Client;
import ca.ets.log660.labo2.model.Film;
import ca.ets.log660.labo2.model.FilmProduit;
import ca.ets.log660.labo2.model.Location;
import ca.ets.log660.labo2.model.Personne;
import ca.ets.log660.labo2.util.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
            if (nonVide(criteres.getGenres())) {
                hql.append(" join f.genres g ");
            }
            if (criteres.getRealisateurId() != null) {
                hql.append(" join f.realisateurs r ");
            }
            if (nonVideIds(criteres.getActeurIds())) {
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
                hql.append(" and lower(p.nomPays) in (:pays) ");
                params.put("pays", enMinuscules(criteres.getPays()));
            }
            if (nonVide(criteres.getLangues())) {
                hql.append(" and lower(f.langue) in (:langues) ");
                params.put("langues", enMinuscules(criteres.getLangues()));
            }
            if (nonVide(criteres.getGenres())) {
                hql.append(" and lower(g.nomGenre) in (:genres) ");
                params.put("genres", enMinuscules(criteres.getGenres()));
            }
            if (criteres.getRealisateurId() != null) {
                hql.append(" and r.id = :realisateurId ");
                params.put("realisateurId", criteres.getRealisateurId());
            }
            if (nonVideIds(criteres.getActeurIds())) {
                hql.append(" and a.id in (:acteurIds) ");
                params.put("acteurIds", criteres.getActeurIds());
            }

            hql.append(" order by f.titre asc, f.annee desc ");

            Query<Film> query = session.createQuery(hql.toString(), Film.class);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            // query.setMaxResults(100);
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

    public Personne consulterPersonne(Integer personneId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Personne personne = session.get(Personne.class, personneId);
            if (personne == null) {
                return null;
            }
            Hibernate.initialize(personne.getPhoto());
            Hibernate.initialize(personne.getFilmsCommeActeur());
            Hibernate.initialize(personne.getFilmsCommeRealisateur());
            return personne;
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

    public List<String> listerGenres() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select distinct g.nomGenre from Genre g order by g.nomGenre", String.class).list();
        }
    }

    public List<String> listerPays() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select distinct p.nomPays from Pays p order by p.nomPays", String.class).list();
        }
    }

    public List<String> listerLangues() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select distinct f.langue from Film f where f.langue is not null order by f.langue",
                    String.class).list();
        }
    }

    public Map<Integer, String> listerRealisateurs() {
        return listerPersonnes("realisateurs");
    }

    public Map<Integer, String> listerActeurs() {
        return listerPersonnes("acteurs");
    }

    /**
     * Retourne les personnes participant a au moins un film pour la relation
     * donnee (acteurs ou realisateurs), sous forme id -> nom complet, triees
     * par nom. Le parametre relation est une constante interne, pas une entree
     * utilisateur, donc sans risque d'injection.
     */
    private Map<Integer, String> listerPersonnes(String relation) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> lignes = session.createQuery(
                    "select distinct r.id, r.prenom, r.nom from Film f join f." + relation + " r order by r.nom, r.prenom",
                    Object[].class).list();
            Map<Integer, String> personnes = new LinkedHashMap<>();
            for (Object[] ligne : lignes) {
                String prenom = ligne[1] == null ? "" : (String) ligne[1];
                String nom = ligne[2] == null ? "" : (String) ligne[2];
                personnes.put((Integer) ligne[0], (prenom + " " + nom).trim());
            }
            return personnes;
        }
    }

    private boolean nonVide(String valeur) {
        return valeur != null && !valeur.trim().isEmpty();
    }

    private boolean nonVide(List<String> valeurs) {
        return valeurs != null && valeurs.stream().anyMatch(this::nonVide);
    }

    private boolean nonVideIds(List<Integer> ids) {
        return ids != null && !ids.isEmpty();
    }

    private List<String> enMinuscules(List<String> valeurs) {
        return valeurs.stream()
                .filter(this::nonVide)
                .map(v -> v.trim().toLowerCase())
                .collect(java.util.stream.Collectors.toList());
    }

    private String like(String valeur) {
        return "%" + valeur.trim().toLowerCase() + "%";
    }
}
