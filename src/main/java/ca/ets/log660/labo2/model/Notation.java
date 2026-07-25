package ca.ets.log660.labo2.model;

public class Notation {
    private int idFilm;
    private int idClient;
    private int cote;
    private String date;

    public Notation(int idFilm, int idClient, int cote, String date) {
        this.idFilm = idFilm;
        this.idClient = idClient;
        this.cote = cote;
        this.date = date;
    }

    public void setIdFilm(int idFilm){
        this.idFilm = idFilm;
    }

    public int getIdFilm(){
        return idFilm;
    }

    public void setIdClient(int idClient){
        this.idClient = idClient;
    }

    public int getIdClient(){
        return idClient;
    }

    public void setCote(int cote){
        this.cote = cote;
    }

    public int getCote(){
        return cote;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getDate(){
        return date;
    }

    @Override
    public String toString(){
        return idFilm + ", " + idClient + ", " + cote + ", " + date;
    }
}
