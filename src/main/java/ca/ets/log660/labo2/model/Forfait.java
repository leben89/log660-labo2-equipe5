package ca.ets.log660.labo2.model;

public class Forfait {
    private String code;
    private Double cout;
    private Integer maxLocations;
    private String dureeMax;
    private String typeForfait;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getCout() {
        return cout;
    }

    public void setCout(Double cout) {
        this.cout = cout;
    }

    public Integer getMaxLocations() {
        return maxLocations;
    }

    public void setMaxLocations(Integer maxLocations) {
        this.maxLocations = maxLocations;
    }

    public String getDureeMax() {
        return dureeMax;
    }

    public void setDureeMax(String dureeMax) {
        this.dureeMax = dureeMax;
    }

    public String getTypeForfait() {
        return typeForfait;
    }

    public void setTypeForfait(String typeForfait) {
        this.typeForfait = typeForfait;
    }
}
