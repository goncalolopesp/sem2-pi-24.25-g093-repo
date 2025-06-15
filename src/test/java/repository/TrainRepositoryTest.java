package repository;

import domain.Carriage;
import domain.Locomotive;
import domain.Scenario;
import domain.Train;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe TrainRepository
 */
class TrainRepositoryTest {

    private TrainRepository repository;
    private Train train1;
    private Train train2;
    private String userEmail1 = "user1@example.com";
    private String userEmail2 = "user2@example.com";

    @BeforeEach
    void setUp() {
        repository = new TrainRepository();
        Locomotive locomotive1 = new Locomotive("Trevithick-1", 10, "Steam", 8, 6, 1800, 1835, 10);
        Locomotive locomotive2 = new Locomotive("Stephenson Rocket", 16, "Steam", 8, 6, 1829, 1851, 16);
        List<Carriage> carriages1 = new ArrayList<>();
        carriages1.add(new Carriage(Carriage.Type.PEOPLE));
        carriages1.add(new Carriage(Carriage.Type.MAIL));

        List<Carriage> carriages2 = new ArrayList<>();
        carriages2.add(new Carriage(Carriage.Type.PEOPLE));
        carriages2.add(new Carriage(Carriage.Type.PEOPLE));
        carriages2.add(new Carriage(Carriage.Type.MAIL));
        Scenario test = new Scenario("TestScenario", "TestMap", new int[]{1870, 1880}, 1, Collections.singletonList("steam"), Collections.singletonList("none"), 1, 1);
        train1 = new Train(locomotive1, carriages1, userEmail1,test);
        train2 = new Train(locomotive2, carriages2, userEmail2,test);
    }

    @Test
    void saveShouldAddTrainToRepository(){
        Train result = repository.save(train1);
        assertEquals(train1, result);
        assertEquals(1, repository.getTrains().size());
        assertTrue(repository.getTrains().contains(train1));
    }

    @Test
    void saveShouldNotAddDuplicateTrain() {
        repository.save(train1);
        Train result = repository.save(train1);
        assertEquals(train1, result);
        assertEquals(1, repository.getTrains().size());
    }

    @Test
    void saveShouldThrowExceptionWhenTrainIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            repository.save(null);
        });
        assertEquals("Train cannot be null", exception.getMessage());
    }

    @Test
    void deleteShouldRemoveTrainFromRepository() {
        repository.save(train1);
        boolean result = repository.delete(train1);
        assertTrue(result);
        assertEquals(0, repository.getTrains().size());
        assertFalse(repository.getTrains().contains(train1));
    }

    @Test
    void deleteShouldReturnFalseWhenTrainNotFound() {
        boolean result = repository.delete(train1);
        assertFalse(result);
    }

    @Test
    void getTrainsShouldReturnAllTrains() {
        repository.save(train1);
        repository.save(train2);
        List<Train> result = repository.getTrains();
        assertEquals(2, result.size());
        assertTrue(result.contains(train1));
        assertTrue(result.contains(train2));
    }

    @Test
    @DisplayName("GetTrains should return unmodifiable list")
    void getTrainsShouldReturnUnmodifiableList() {
        repository.save(train1);
        List<Train> result = repository.getTrains();
        assertThrows(UnsupportedOperationException.class, () -> {
            result.add(train2);
        });
    }

    @Test
    void getTrainsShouldReturnEmptyListWhenNoTrains() {
        List<Train> result = repository.getTrains();
        assertTrue(result.isEmpty());
    }

    @Test
    void findTrainShouldReturnTrainWhenFound() {
        repository.save(train1);
        Optional<Train> result = repository.findTrain(train1);
        assertTrue(result.isPresent());
        assertEquals(train1, result.get());
    }

    @Test
    void findTrainShouldReturnEmptyOptionalWhenTrainNotFound() {
        Optional<Train> result = repository.findTrain(train1);

        assertFalse(result.isPresent());
    }

    @Test
    void repositoryShouldMaintainMultipleTrainsCorrectly() {
        repository.save(train1);
        repository.save(train2);

        repository.delete(train1);

        assertEquals(1, repository.getTrains().size());
        assertFalse(repository.getTrains().contains(train1));
        assertTrue(repository.getTrains().contains(train2));

        Scenario test = new Scenario("TestScenario", "TestMap", new int[]{1870, 1880}, 1, Collections.singletonList("steam"), Collections.singletonList("none"), 1, 1);
        Train train3 = new Train(
                new Locomotive("John Bull", 23, "Steam", 9, 7, 1831, 1853, 25),
                List.of(new Carriage(Carriage.Type.MAIL)),
                "user3@example.com",test
        );
        repository.save(train3);

        assertEquals(2, repository.getTrains().size());
        assertTrue(repository.getTrains().contains(train2));
        assertTrue(repository.getTrains().contains(train3));
    }
}