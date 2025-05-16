package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainTest {
    private Locomotive locomotive;
    private List<Carriage> carriages;
    private Train train;
    private final String userEmail = "user@example.com";

    @BeforeEach
    void setUp() {
        locomotive = new Locomotive("Trevithick-1", 10, "Steam", 8, 6, 1800, 1835, 10);

        carriages = new ArrayList<>();
        carriages.add(new Carriage(Carriage.Type.PEOPLE));
        carriages.add(new Carriage(Carriage.Type.MAIL));

        train = new Train(locomotive, carriages, userEmail);
    }

    @Test
    void constructorShouldThrowExceptionWhenLocomotiveIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Train(null, carriages, userEmail);
        });
        assertEquals("Locomotive cannot be null", exception.getMessage());
    }

    @Test
    void constructorShouldThrowExceptionWhenCarriagesIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Train(locomotive, null, userEmail);
        });
        assertEquals("Train must have at least one carriage", exception.getMessage());
    }

    @Test
    void constructorShouldThrowExceptionWhenCarriagesIsEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Train(locomotive, new ArrayList<>(), userEmail);
        });
        assertEquals("Train must have at least one carriage", exception.getMessage());
    }

    @Test
    void getLocomotiveShouldReturnCorrectLocomotive() {
        Locomotive result = train.getLocomotive();
        assertEquals(locomotive, result);
    }

    @Test
    void getCarriagesShouldReturnUnmodifiableList() {
        List<Carriage> result = train.getCarriages();

        assertEquals(2, result.size());
        assertThrows(UnsupportedOperationException.class, () -> {
            result.add(new Carriage(Carriage.Type.PEOPLE));
        });
    }

    @Test
    void getUserEmailShouldReturnCorrectEmail() {
        String result = train.getUserEmail();

        assertEquals(userEmail, result);
    }

    @Test
    void addCarriageShouldAddNewCarriage() {
        Carriage newCarriage = new Carriage(Carriage.Type.PEOPLE);

        train.addCarriage(newCarriage);

        List<Carriage> carriageList = train.getCarriages();
        assertEquals(3, carriageList.size());
        assertTrue(carriageList.contains(newCarriage));
    }

    @Test
    void addCarriageShouldThrowExceptionWhenCarriageIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            train.addCarriage(null);
        });
        assertEquals("Carriage cannot be null", exception.getMessage());
    }

    @Test
    void removeCarriageShouldReturnTrueWhenCarriageExists() {
        Carriage existingCarriage = carriages.get(0);

        boolean result = train.removeCarriage(existingCarriage);

        assertTrue(result);
        assertEquals(1, train.getCarriages().size());
        assertFalse(train.getCarriages().contains(existingCarriage));
    }

    @Test
    void removeCarriageShouldReturnFalseWhenCarriageDoesNotExist() {
        Carriage nonExistingCarriage = new Carriage(Carriage.Type.PEOPLE);
        nonExistingCarriage.setCapacityOwned(65);

        boolean result = train.removeCarriage(nonExistingCarriage);

        assertFalse(result);
        assertEquals(2, train.getCarriages().size());
    }

    @Test
    void trainShouldHandleDifferentCarriageTypesCorrectly() {
        Carriage peopleCarriage = new Carriage(Carriage.Type.PEOPLE);
        Carriage mailCarriage = new Carriage(Carriage.Type.MAIL);

        train.addCarriage(peopleCarriage);
        train.addCarriage(mailCarriage);

        List<Carriage> carriageList = train.getCarriages();
        assertEquals(4, carriageList.size());

        boolean hasPeopleCarriage = carriageList.stream()
                .anyMatch(c -> c.getType() == Carriage.Type.PEOPLE);
        boolean hasMailCarriage = carriageList.stream()
                .anyMatch(c -> c.getType() == Carriage.Type.MAIL);

        assertTrue(hasPeopleCarriage);
        assertTrue(hasMailCarriage);
    }
}