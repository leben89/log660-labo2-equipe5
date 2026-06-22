package ca.ets.log660.labo2.facade;

public class CriteresRechercheFilm {
    private String titre;
    private Integer anneeMin;
    private Integer anneeMax;
    private String pays;
    private String langue;
    private String genre;
    private String realisateur;
    private String acteur;

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Integer getAnneeMin() {
        return anneeMin;
    }

    public void setAnneeMin(Integer anneeMin) {
        this.anneeMin = anneeMin;
    }

    public Integer getAnneeMax() {
        return anneeMax;
    }

    public void setAnneeMax(Integer anneeMax) {
        this.anneeMax = anneeMax;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRealisateur() {
        return realisateur;
    }

    public void setRealisateur(String realisateur) {
        this.realisateur = realisateur;
    }

    public String getActeur() {
        return acteur;
    }

    public void setActeur(String acteur) {
        this.acteur = acteur;
    }
}
