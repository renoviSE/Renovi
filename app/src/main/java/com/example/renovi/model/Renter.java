package com.example.renovi.model;

public class Renter {
    private String id;
    private String firstName;
    private String lastName;
    private float rent;

    public Renter(String id, String firstName, String lastName, float rent) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public float getRent() {
        return rent;
    }

    public String getRentasString() {
        return String.valueOf(rent).replace(".", ",");
    }
}
