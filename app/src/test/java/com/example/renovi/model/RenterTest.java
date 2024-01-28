package com.example.renovi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RenterTest {

    @Test
    public void testGetRoundedRentasString() {
        // Arrange
        Renter renter = new Renter("xyz", "Max", "Mustermann", new BigDecimal(0));

        // Act
        renter.setRent(new BigDecimal("500.123"));
        String result = renter.getRoundedRentasString();

        // Assert
        assertEquals("500,12", result);
    }

    @Test
    public void testUpdateRent() {
        // Arrange
        Renter renter = new Renter("1", "John", "Doe", new BigDecimal("100.00"));
        BigDecimal allObjectsValue = new BigDecimal("50.00");

        // Act
        renter.updateRent(allObjectsValue);

        // Assert
        assertEquals(new BigDecimal("150.00"), renter.getRent());
    }

    @Test
    public void testSetRentDifferenceInPercentage() {
        // Arrange
        Renter renter = new Renter("1", "John", "Doe", new BigDecimal("100.00"));
        BigDecimal allObjectsValue = new BigDecimal("50.00");

        // Act
        renter.setRentDifferenceInPercentage(allObjectsValue);

        // Assert
        assertEquals(new BigDecimal("50.00").setScale(2, RoundingMode.HALF_UP), renter.getRentDifferenceInPercentage().setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void testSetRentDifferenceInPercentageWithZeroRent() {
        // Arrange
        Renter renter = new Renter("1", "John", "Doe", BigDecimal.ZERO);
        BigDecimal allObjectsValue = new BigDecimal("50.00");

        // Act
        renter.setRentDifferenceInPercentage(allObjectsValue);

        // Assert
        assertEquals(BigDecimal.ZERO, renter.getRentDifferenceInPercentage());
    }
}
