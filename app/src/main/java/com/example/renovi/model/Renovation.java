package com.example.renovi.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Renovation implements Serializable {
    //private int id;
    private String object;
    private String benefits;
    private String disadvantages;
    private String cost;
    private String paragraph;
    private String state;


//private int mieterId;

    public Renovation(String object, String benefits, String disadvantages, String cost, String paragraph, String condition) {
        this.object = object;
        this.benefits = benefits;
        this.disadvantages = disadvantages;
        this.cost = cost;
        this.paragraph = paragraph;
        this.state = condition;
    }

    public String getCost() {
        return cost;
    }

    public String getObject() {
        return object;
    }

    public String[] getBenefits() {
        return benefits.split(", ");
    }

    public String getDisadvantages() {
        return disadvantages;
    }

    public String getParagraph() {
        return paragraph;
    }

    public String getCondition() {
        return state;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public void setCondition(String condition) {
        this.state = condition;
    }

    public BigDecimal getObjectValue() { // BigDecimal soll der beste Datentyp sein um mit Währung zurechnen
        BigDecimal bigDecimalcost = new BigDecimal(cost);
        switch (state) {
            case "gut": return bigDecimalcost.multiply(new BigDecimal("0.08")); // cost - (cost * %)
            case "mittel": return bigDecimalcost.multiply(new BigDecimal("0.04"));
            default: return new BigDecimal("0");
        }
    }
}
