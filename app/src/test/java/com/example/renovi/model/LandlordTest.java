package com.example.renovi.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LandlordTest {

    @Test
    void testLandlordWithValidInputs() {
        // Arrange
        String role = "Landlord";
        String id = "123";
        String firstName = "John";
        String lastName = "Doe";

        // Act
        Landlord landlord = new Landlord(role, id, firstName, lastName);

        // Assert
        assertNotNull(landlord);
        assertEquals(role, landlord.getRole());
        assertEquals(id, landlord.getId());
        assertEquals(firstName, landlord.getFirstName());
        assertEquals(lastName, landlord.getLastName());
    }

    @Test
    void testLandlordWithEmptyStrings() {
        // Arrange
        String role = "";
        String id = "";
        String firstName = "";
        String lastName = "";

        // Act
        Landlord landlord = new Landlord(role, id, firstName, lastName);

        // Assert
        assertNotNull(landlord);
        assertEquals(role, landlord.getRole());
        assertEquals(id, landlord.getId());
        assertEquals(firstName, landlord.getFirstName());
        assertEquals(lastName, landlord.getLastName());
    }

    @Test
    void testLandlordWithLongStrings() {
        // Arrange
        String role = "Landlord".repeat(1000);
        String id = "123".repeat(1000);
        String firstName = "John".repeat(1000);
        String lastName = "Doe".repeat(1000);

        // Act
        Landlord landlord = new Landlord(role, id, firstName, lastName);

        // Assert
        assertNotNull(landlord);
        assertEquals(role, landlord.getRole());
        assertEquals(id, landlord.getId());
        assertEquals(firstName, landlord.getFirstName());
        assertEquals(lastName, landlord.getLastName());
    }

    @Test
    void testLandlordWithSpecialCharacters() {
        // Arrange
        String role = "Landlord!@#$%^&*()";
        String id = "123-456_ABC";
        String firstName = "Jöhn";
        String lastName = "Dœ";

        // Act
        Landlord landlord = new Landlord(role, id, firstName, lastName);

        // Assert
        assertNotNull(landlord);
        assertEquals(role, landlord.getRole());
        assertEquals(id, landlord.getId());
        assertEquals(firstName, landlord.getFirstName());
        assertEquals(lastName, landlord.getLastName());
    }
}