package com.example.renovi.model;

import java.io.Serializable;
import java.util.List;

public class Renovierung implements Serializable {
    //private int id;
    private String object;
    private String advantages;
    private String disadvantages;
    private String cost;
    private String paragraph;
    //private int mieterId;

    public Renovierung(String object, String advantages, String disadvantages, String cost, String paragraph) {
        this.object = object;
        this.advantages = advantages;
        this.disadvantages = disadvantages;
        this.cost = cost;
        this.paragraph = paragraph;
    }

    public String getCost() {
        return cost;
    }
}
