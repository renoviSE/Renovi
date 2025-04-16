package com.example.renovi.model;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Renter extends Person {

    private BigDecimal rent;
    private BigDecimal rentDifference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Renter(String role, String id, String firstName, String lastName, BigDecimal rent) {
        super(role, id, firstName, lastName);
        this.rent = rent;
        this.rentDifference = new BigDecimal("0");
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
