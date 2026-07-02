package ca.ets.log660.labo2.facade;

import java.util.List;

public class CriteresRechercheFilm {
    private String titre;
    private Integer anneeMin;
    private Integer anneeMax;
    private List<String> pays;
    private List<String> langues;
    private List<String> genres;
    private Integer realisateurId;
    private List<Integer> acteurIds;

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

    public List<String> getPays() {
        return pays;
    }

    public void setPays(List<String> pays) {
        this.pays = pays;
    }

    public List<String> getLangues() {
        return langues;
    }

    public void setLangues(List<String> langues) {
        this.langues = langues;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public Integer getRealisateurId() {
        return realisateurId;
    }

    public void setRealisateurId(Integer realisateurId) {
        this.realisateurId = realisateurId;
    }

    public List<Integer> getActeurIds() {
        return acteurIds;
    }

    public void setActeurIds(List<Integer> acteurIds) {
        this.acteurIds = acteurIds;
    }
}
