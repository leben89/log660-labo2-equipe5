package ca.ets.log660.labo2.model;

import java.util.HashSet;
import java.util.Set;

public class Personne extends Utilisateur {
    private String lieuNaissance;
    private Photo photo;
    private String bio;
    private Set<Film> filmsCommeActeur = new HashSet<>();
    private Set<Film> filmsCommeRealisateur = new HashSet<>();

    public String getLieuNaissance() {
        return lieuNaissance;
    }

    public void setLieuNaissance(String lieuNaissance) {
        this.lieuNaissance = lieuNaissance;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Set<Film> getFilmsCommeActeur() {
        return filmsCommeActeur;
    }

    public void setFilmsCommeActeur(Set<Film> filmsCommeActeur) {
        this.filmsCommeActeur = filmsCommeActeur;
    }

    public Set<Film> getFilmsCommeRealisateur() {
        return filmsCommeRealisateur;
    }

    public void setFilmsCommeRealisateur(Set<Film> filmsCommeRealisateur) {
        this.filmsCommeRealisateur = filmsCommeRealisateur;
    }
}
