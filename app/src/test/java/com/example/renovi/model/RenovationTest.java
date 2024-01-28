package com.example.renovi.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RenovationTest {
    @Test
    public void testGetObjectValue() {
        Renovation yourObject = new Renovation("Tür", "Brandschutz", "Teuer", "100€", "§559", "gut");

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
}
