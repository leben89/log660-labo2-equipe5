package ca.ets.log660.labo2.model;

public class CarteCredit {
    private Integer id;
    private String carteType;
    private String numero;
    private String expDate;
    private Integer cvv;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCarteType() {
        return carteType;
    }

    public void setCarteType(String carteType) {
        this.carteType = carteType;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public Integer getCvv() {
        return cvv;
    }

    public void setCvv(Integer cvv) {
        this.cvv = cvv;
    }
}
