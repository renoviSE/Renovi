package com.example.renovi.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RenovationTest {

    private String object  ="Tür";
    private String benefits = "Brandschutz";
    private String disadvantages = "Teuer";

    private String cost = "100€";
    private String paragraph = "§559";
    private String condition = "gut";


    @Test
    public void testGetObjectValues() {

        Renovation yourObject = new Renovation(object, benefits, disadvantages, cost, paragraph,  condition);

        // Test für den Fall "gut"
        yourObject.setCost("100");
        yourObject.setCondition("gut");
        assertEquals(new BigDecimal("8.00"), yourObject.getObjectValue());

        // Test für den Fall "mittel"
        yourObject.setCondition("mittel");
        assertEquals(new BigDecimal("4.00"), yourObject.getObjectValue());

        // Test für den Default-Fall
        yourObject.setCondition("schlecht");
        assertEquals(new BigDecimal("0"), yourObject.getObjectValue());

    }

    @Test
    public void getterSetterTest(){

        Renovation yourObject = new Renovation(object, benefits, disadvantages, cost, paragraph,  condition);

        //getter Test
        String[] benefitArray = benefits.split(", ");
        assertEquals(yourObject.getObject(), object);
        assertEquals(yourObject.getCost(), cost);
        assertArrayEquals(yourObject.getBenefits(),benefitArray);
        assertEquals(yourObject.getCondition(), condition);
        assertEquals(yourObject.getDisadvantages(),disadvantages);
        assertEquals(yourObject.getParagraph(), paragraph);

        //setter Test
        String conditionSetterTest ="schlecht";
        String costSetterTest = "1000";

        yourObject.setCondition(conditionSetterTest);
        assertEquals(yourObject.getCondition(),conditionSetterTest);

        yourObject.setCost(costSetterTest);
        assertEquals(yourObject.getCost(),costSetterTest);
    }

    @Test
    void testRenovationWithValidInputs() {
        // Arrange
        String object  ="Tür";
        String benefits = "Brandschutz";
        String disadvantages = "Teuer";

        String cost = "100€";
        String paragraph = "§559";
        String condition = "gut";

        // Act
        Renovation yourObject = new Renovation(object, benefits, disadvantages, cost, paragraph,  condition);

        // Asserts not Null
        assertNotNull(object);
        assertNotNull(benefits);
        assertNotNull(disadvantages);
        assertNotNull(cost);
        assertNotNull(paragraph);
        assertNotNull(condition);

        //Assert Equals
        String[] benefitArray = benefits.split(", ");
        assertEquals(yourObject.getObject(), object);
        assertEquals(yourObject.getCost(), cost);
        assertArrayEquals(yourObject.getBenefits(),benefitArray);
        assertEquals(yourObject.getCondition(), condition);
        assertEquals(yourObject.getDisadvantages(),disadvantages);
        assertEquals(yourObject.getParagraph(), paragraph);

    }

    @Test
    void testRenovationdWithEmptyStrings() {
        // Arrange
        String object  ="";
        String benefits = "";
        String disadvantages = "";

        String cost = "";
        String paragraph = "";
        String condition = "";

        // Act
        Renovation yourObject = new Renovation(object, benefits, disadvantages, cost, paragraph,  condition);

        // Asserts not Null
        assertNotNull(object);
        assertNotNull(benefits);
        assertNotNull(disadvantages);
        assertNotNull(cost);
        assertNotNull(paragraph);
        assertNotNull(condition);

        //Assert Equals
        String[] benefitArray = benefits.split(", ");
        assertEquals(yourObject.getObject(), object);
        assertEquals(yourObject.getCost(), cost);
        assertArrayEquals(yourObject.getBenefits(),benefitArray);
        assertEquals(yourObject.getCondition(), condition);
        assertEquals(yourObject.getDisadvantages(),disadvantages);
        assertEquals(yourObject.getParagraph(), paragraph);
    }

    @Test
    void testRenovationWithLongStrings() {
        // Arrange
        String object  ="Tür".repeat(1000);
        String benefits = "Brandschutz".repeat(1000);
        String disadvantages = "Teuer".repeat(1000);

        String cost = "100€".repeat(1000);
        String paragraph = "§559".repeat(1000);
        String condition = "gut".repeat(1000);

        // Act
        Renovation yourObject = new Renovation(object, benefits, disadvantages, cost, paragraph,  condition);

        // Asserts not Null
        assertNotNull(object);
        assertNotNull(benefits);
        assertNotNull(disadvantages);
        assertNotNull(cost);
        assertNotNull(paragraph);
        assertNotNull(condition);

        //Assert Equals
        String[] benefitArray = benefits.split(", ");
        assertEquals(yourObject.getObject(), object);
        assertEquals(yourObject.getCost(), cost);
        assertArrayEquals(yourObject.getBenefits(),benefitArray);
        assertEquals(yourObject.getCondition(), condition);
        assertEquals(yourObject.getDisadvantages(),disadvantages);
        assertEquals(yourObject.getParagraph(), paragraph);
    }

    @Test
    void testRenovationWithSpecialCharacters() {
        // Arrange
        String object  ="Tür!§!&$!§$%&/()(=)";
        String benefits = "Brandschutz☆*: .｡. o(≧▽≦)o .｡.:*☆";
        String disadvantages = "Teuerಥ_ಥ";

        String cost = "100€©№№>₡₱₾௹₼﷼";
        String paragraph = "§559╰(*°▽°*)╯^_^";
        String condition = "gutO_O";

        // Act
        Renovation yourObject = new Renovation(object, benefits, disadvantages, cost, paragraph,  condition);

        // Asserts not Null
        assertNotNull(object);
        assertNotNull(benefits);
        assertNotNull(disadvantages);
        assertNotNull(cost);
        assertNotNull(paragraph);
        assertNotNull(condition);

        //Assert Equals
        String[] benefitArray = benefits.split(", ");
        assertEquals(yourObject.getObject(), object);
        assertEquals(yourObject.getCost(), cost);
        assertArrayEquals(yourObject.getBenefits(),benefitArray);
        assertEquals(yourObject.getCondition(), condition);
        assertEquals(yourObject.getDisadvantages(),disadvantages);
        assertEquals(yourObject.getParagraph(), paragraph);
    }
    @Test
    void testRenovationWithSpace() {
        // Arrange
        String object  ="T ü r";
        String benefits = "Brand schutz";
        String disadvantages = "Te u er";

        String cost = "100 €";
        String paragraph = "§ 559";
        String condition = "g u t";

        // Act
        Renovation yourObject = new Renovation(object, benefits, disadvantages, cost, paragraph,  condition);

        // Asserts not Null
        assertNotNull(object);
        assertNotNull(benefits);
        assertNotNull(disadvantages);
        assertNotNull(cost);
        assertNotNull(paragraph);
        assertNotNull(condition);

        //Assert Equals
        String[] benefitArray = benefits.split(", ");
        assertEquals(yourObject.getObject(), object);
        assertEquals(yourObject.getCost(), cost);
        assertArrayEquals(yourObject.getBenefits(),benefitArray);
        assertEquals(yourObject.getCondition(), condition);
        assertEquals(yourObject.getDisadvantages(),disadvantages);
        assertEquals(yourObject.getParagraph(), paragraph);
    }

}
