package com.example.renovi.model;

import java.math.BigDecimal;
import java.math.MathContext;

public class Renter {
    private String id;
    private String firstName;
    private String lastName;
    private BigDecimal rent;
    private BigDecimal rentDifference;

    public Renter(String id, String firstName, String lastName, BigDecimal rent) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.rent = rent;
        this.rentDifference = new BigDecimal("0");
    }

    public String getId() {return id;}

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public BigDecimal getRent() {
        return rent;
    }

    public BigDecimal getRentDifferenceInPercentage() {return rentDifference;}

    public void setRent(BigDecimal allObjectsValue) {rent = rent.add(allObjectsValue);}

    public void setRentDifferenceInPercentage(BigDecimal allObjectsValue) {
        if (rent.compareTo(BigDecimal.ZERO) != 0) {
            rentDifference = allObjectsValue.divide(rent, MathContext.DECIMAL128).multiply(new BigDecimal("100"));
        } else {
            rentDifference = BigDecimal.ZERO;
        }
    }

    public String getRentasString() {return String.valueOf(rent).replace(".", ",");}
}
