package com.example.renovi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;


class PersonTest {

    @Test
    public void testPersonWithValidInputs() {
        String role = "Person";
        String id = "123";
        String firstName = "John";
        String lastName = "Doe";

        // Act
        Person person = new Person(role, id, firstName, lastName);

        // Assert
        assertNotNull(person);
        assertEquals(role, person.getRole());
        assertEquals(id, person.getId());
        assertEquals(firstName, person.getFirstName());
        assertEquals(lastName, person.getLastName());
    }

    @Test
    void testPersonWithEmptyStrings() {
        // Arrange
        String role = "";
        String id = "";
        String firstName = "";
        String lastName = "";

        // Act
        Person person = new Person(role, id, firstName, lastName);

        // Assert
        assertNotNull(person);
        assertEquals(role, person.getRole());
        assertEquals(id, person.getId());
        assertEquals(firstName, person.getFirstName());
        assertEquals(lastName, person.getLastName());
    }

    @Test
    void testPersonWithLongStrings() {
        // Arrange
        String role = "Person".repeat(1000);
        String id = "123".repeat(1000);
        String firstName = "John".repeat(1000);
        String lastName = "Doe".repeat(1000);

        // Act
        Person person = new Person(role, id, firstName, lastName);

        // Assert
        assertNotNull(person);
        assertEquals(role, person.getRole());
        assertEquals(id, person.getId());
        assertEquals(firstName, person.getFirstName());
        assertEquals(lastName, person.getLastName());
    }

    @Test
    void testPersonWithSpecialCharacters() {
        // Arrange
        String role = "Person!@#$%^&*()";
        String id = "123-456_ABC";
        String firstName = "Jöhn";
        String lastName = "Dœ";

        // Act
        Person person = new Person(role, id, firstName, lastName);

        // Assert
        assertNotNull(person);
        assertEquals(role, person.getRole());
        assertEquals(id, person.getId());
        assertEquals(firstName, person.getFirstName());
        assertEquals(lastName, person.getLastName());
    }
    @Test
    void testPersonWithSpace() {
        // Arrange
        String role = "Per Son";
        String id = "123 456 ABC";
        String firstName = "Jöh n";
        String lastName = "D o e";

        // Act
        Person person = new Person(role, id, firstName, lastName);

        // Assert
        assertNotNull(person);
        assertEquals(role, person.getRole());
        assertEquals(id, person.getId());
        assertEquals(firstName, person.getFirstName());
        assertEquals(lastName, person.getLastName());
    }

}