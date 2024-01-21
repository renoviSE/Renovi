package com.example.renovi.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Renovation implements Serializable {
    //private int id;
    private String object;
    private String advantages;
    private String disadvantages;
    private String cost;
    private String paragraph;
    private String condition;


//private int mieterId;

    public Renovation(String object, String advantages, String disadvantages, String cost, String paragraph, String condition) {
        this.object = object;
        this.advantages = advantages;
        this.disadvantages = disadvantages;
        this.cost = cost;
        this.paragraph = paragraph;
        this.condition = condition;
    }

    public String getCost() {
        return cost;
    }

    public String getObject() {
        return object;
    }

    public String getAdvantages() {
        return advantages;
    }

    public String getDisadvantages() {
        return disadvantages;
    }

    public String getParagraph() {
        return paragraph;
    }

    public String getCondition() {
        return condition;
    }

    public BigDecimal getObjectValue() { // BigDecimal soll der beste Datentyp sein um mit Währung zurechnen
        BigDecimal bigDecimalcost = new BigDecimal(cost);
        switch (condition) {
            case "gut": return bigDecimalcost.multiply(new BigDecimal("0.08")); // cost - (cost * %)
            case "mittel": return bigDecimalcost.multiply(new BigDecimal("0.04"));
            default: return bigDecimalcost;
        }
    }
}
