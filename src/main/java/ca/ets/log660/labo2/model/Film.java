package ca.ets.log660.labo2.model;

import java.util.HashSet;
import java.util.Set;

public class Film {
    private Integer id;
    private String titre;
    private Integer annee;
    private String langue;
    private Integer duree;
    private String resume;
    private Set<Genre> genres = new HashSet<>();
    private Set<Pays> paysProduction = new HashSet<>();
    private Set<Personne> acteurs = new HashSet<>();
    private Set<Personne> realisateurs = new HashSet<>();
    private Set<Photo> posters = new HashSet<>();
    private Set<Video> bandesAnnonces = new HashSet<>();
    private Set<FilmProduit> produits = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Integer getAnnee() {
        return annee;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public Set<Pays> getPaysProduction() {
        return paysProduction;
    }

    public void setPaysProduction(Set<Pays> paysProduction) {
        this.paysProduction = paysProduction;
    }

    public Set<Personne> getActeurs() {
        return acteurs;
    }

    public void setActeurs(Set<Personne> acteurs) {
        this.acteurs = acteurs;
    }

    public Set<Personne> getRealisateurs() {
        return realisateurs;
    }

    public void setRealisateurs(Set<Personne> realisateurs) {
        this.realisateurs = realisateurs;
    }

    public Set<Photo> getPosters() {
        return posters;
    }

    public void setPosters(Set<Photo> posters) {
        this.posters = posters;
    }

    public Set<Video> getBandesAnnonces() {
        return bandesAnnonces;
    }

    public void setBandesAnnonces(Set<Video> bandesAnnonces) {
        this.bandesAnnonces = bandesAnnonces;
    }

    public Set<FilmProduit> getProduits() {
        return produits;
    }

    public void setProduits(Set<FilmProduit> produits) {
        this.produits = produits;
    }

    public int getCopiesDisponiblesTotales() {
        int total = 0;
        for (FilmProduit produit : produits) {
            if (produit.getCopieDispo() != null) {
                total += produit.getCopieDispo();
            }
        }
        return total;
    }
}
