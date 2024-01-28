package com.example.renovi.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

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

    public void setRent(BigDecimal rent) {
        this.rent = rent;
    }

    public String getRoundedRentasString() {
        return rent.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ",");
    }

    public BigDecimal getRentDifferenceInPercentage() {return rentDifference;}

    public void updateRent(BigDecimal allObjectsValue) {rent = rent.add(allObjectsValue);}

    public void setRentDifferenceInPercentage(BigDecimal allObjectsValue) {
        if (rent.compareTo(BigDecimal.ZERO) != 0) {
            rentDifference = allObjectsValue.divide(rent, MathContext.DECIMAL128).multiply(new BigDecimal("100"));
        } else {
            rentDifference = BigDecimal.ZERO;
        }
    }
}
