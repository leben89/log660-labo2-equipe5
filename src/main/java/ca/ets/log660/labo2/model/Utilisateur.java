package ca.ets.log660.labo2.model;

import java.util.Date;

public class Utilisateur {
    private Integer id;
    private String nom;
    private String prenom;
    private Date dateNaissance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getNomComplet() {
        String n = nom == null ? "" : nom;
        String p = prenom == null ? "" : prenom;
        return (p + " " + n).trim();
    }
}
