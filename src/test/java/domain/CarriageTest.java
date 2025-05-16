package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarriageTest {

    @Test
    void createPeopleCarriage() {
        Carriage carriage = new Carriage(Carriage.Type.PEOPLE);

        assertEquals(Carriage.Type.PEOPLE, carriage.getType());
        assertEquals(70, carriage.getCapacity());
        assertEquals(0, carriage.getCapacityOwned());
    }

    @Test
    void createMailCarriage() {
        Carriage carriage = new Carriage(Carriage.Type.MAIL);

        assertEquals(Carriage.Type.MAIL, carriage.getType());
        assertEquals(30, carriage.getCapacity());
        assertEquals(0, carriage.getCapacityOwned());
    }

    @Test
    void shouldThrowExceptionWhenTypeIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Carriage(null);
        });
        assertEquals("Type cannot be null", exception.getMessage());
    }

    @Test
    void shouldUpdateCapacityOwned() {
        Carriage carriage = new Carriage(Carriage.Type.PEOPLE);

        carriage.setCapacityOwned(50);

        assertEquals(50, carriage.getCapacityOwned());
    }

    @Test
    void shouldHaveCorrectToStringFormat() {
        Carriage carriage = new Carriage(Carriage.Type.MAIL);

        String result = carriage.toString();

        assertEquals("Carriage type: MAILcapacity: 30", result);
    }
}