package ca.ets.log660.labo2.model;

public class Adresse {
    private Integer id;
    private String numeroCivic;
    private String ville;
    private String province;
    private String codePostal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumeroCivic() {
        return numeroCivic;
    }

    public void setNumeroCivic(String numeroCivic) {
        this.numeroCivic = numeroCivic;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }
}
