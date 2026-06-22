package ca.ets.log660.labo2.model;

public class FilmProduit {
    private Integer id;
    private Film film;
    private Integer copieDispo;
    private String statut;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Integer getCopieDispo() {
        return copieDispo;
    }

    public void setCopieDispo(Integer copieDispo) {
        this.copieDispo = copieDispo;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
