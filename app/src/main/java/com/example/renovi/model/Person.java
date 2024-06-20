package com.example.renovi.model;

import java.math.BigDecimal;

public class Person {

    private String role;
    private String id;
    private String firstName;
    private String lastName;

    public Person(String role, String id, String firstName, String lastName) {
        this.role = role;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getRole() {return role;}

    public String getId() {return id;}

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


}
