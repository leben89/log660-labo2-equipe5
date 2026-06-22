package ca.ets.log660.labo2.model;

import java.util.HashSet;
import java.util.Set;

public class Location {
    private Integer id;
    private Client client;
    private Set<FilmProduit> filmProduits = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Set<FilmProduit> getFilmProduits() {
        return filmProduits;
    }

    public void setFilmProduits(Set<FilmProduit> filmProduits) {
        this.filmProduits = filmProduits;
    }
}
