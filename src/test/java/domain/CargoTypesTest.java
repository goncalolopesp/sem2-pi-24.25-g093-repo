package domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class CargoTypesTest {
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        // Configurar captura da saída do console
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testFromPickupMenuOptionsValidSingleChoice() {
        List<CargoTypes> result = CargoTypes.fromPickupMenuOptions("1");

        assertEquals(1, result.size());
        assertEquals(CargoTypes.PEOPLE, result.get(0));
    }

    @Test
    void testFromPickupMenuOptionsMultipleValidChoices() {
        List<CargoTypes> result = CargoTypes.fromPickupMenuOptions("1 2 3");

        assertEquals(3, result.size());
        assertEquals(CargoTypes.PEOPLE, result.get(0));
        assertEquals(CargoTypes.MAIL, result.get(1));
        assertEquals(CargoTypes.COAL, result.get(2));
    }

    @Test
    void testFromPickupMenuOptionsIgnoreDuplicates() {
        List<CargoTypes> result = CargoTypes.fromPickupMenuOptions("1 1 2 2 3");

        assertEquals(3, result.size());
        assertEquals(CargoTypes.PEOPLE, result.get(0));
        assertEquals(CargoTypes.MAIL, result.get(1));
        assertEquals(CargoTypes.COAL, result.get(2));
    }

    @Test
    void testFromPickupMenuOptionsIgnoreInvalidNumbers() {
        List<CargoTypes> result = CargoTypes.fromPickupMenuOptions("1 99 2");

        assertEquals(2, result.size());
        assertEquals(CargoTypes.PEOPLE, result.get(0));
        assertEquals(CargoTypes.MAIL, result.get(1));
    }

    @Test
    void testFromPickupMenuOptionsIgnoreInvalidInput() {
        List<CargoTypes> result = CargoTypes.fromPickupMenuOptions("1 abc 2");

        assertEquals(2, result.size());
        assertEquals(CargoTypes.PEOPLE, result.get(0));
        assertEquals(CargoTypes.MAIL, result.get(1));
    }

    @Test
    void testFromPickupMenuOptionsThrowsExceptionForEmptyInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            CargoTypes.fromPickupMenuOptions("");
        });
    }

    @Test
    void testFromPickupMenuOptionsThrowsExceptionForInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            CargoTypes.fromPickupMenuOptions("abc");
        });
    }

    @Test
    void testPrintPickupCargoOptions() {
        CargoTypes.printPickupCargoOptions();

        String output = outputStream.toString();
        assertTrue(output.contains("Select cargo types"));
        assertTrue(output.contains("[1] People"));
        assertTrue(output.contains("[2] Mail"));
        assertTrue(output.contains("[3] Coal"));
        assertTrue(output.contains("[4] Iron"));
        assertTrue(output.contains("[5] Steel"));
    }

    @Test
    void cleanup() {
        System.setOut(originalOut);
    }

    @Test
    void testToString() {
        String expected = "PEOPLE";
        assertEquals(expected, CargoTypes.PEOPLE.toString());
    }
}