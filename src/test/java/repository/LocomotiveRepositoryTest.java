package repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Budget;
import domain.CurrentUser;
import domain.Locomotive;
import domain.User;

class LocomotiveRepositoryTest {

    private LocomotiveRepository repository;

    @BeforeEach
    void setUp() {
        repository = new LocomotiveRepository();
    }

    @Test
    void testAddLocomotiveObject() {
        Locomotive loco = new Locomotive("Test Model", 5000, "Steam", 100, 200, 1900, 1950, 120);

        boolean result = repository.addLocomotive(loco);

        assertTrue(result);
        assertEquals(1, repository.getLocomotives().size());
        assertTrue(repository.checkIfLocomotiveExists(loco));
    }

    @Test
    void testAddLocomotiveObjectDuplicate() {
        Locomotive loco1 = new Locomotive("Test Model", 5000, "Steam", 100, 200, 1900, 1950, 120);
        Locomotive loco2 = new Locomotive("Test Model", 6000, "Diesel", 150, 250, 1920, 1970, 150);

        repository.addLocomotive(loco1);

        boolean result = repository.addLocomotive(loco2);

        assertFalse(result);
        assertEquals(1, repository.getLocomotives().size());
    }

    @Test
    void testAddLocomotiveWithParameters() {
        boolean result = repository.addLocomotive("Test Model", 5000, "Steam", 100, 200, 1900, 1950, 120);

        assertTrue(result);
        assertEquals(1, repository.getLocomotives().size());

        Optional<Locomotive> addedLoco = repository.getLocomotive("Test Model");
        assertTrue(addedLoco.isPresent());
        assertEquals("Test Model", addedLoco.get().getModelName());
        assertEquals(5000, addedLoco.get().getPrice());
        assertEquals("Steam", addedLoco.get().getType());
    }

    @Test
    void testAddLocomotiveWithParametersDuplicate() {
        repository.addLocomotive("Test Model", 5000, "Steam", 100, 200, 1900, 1950, 120);

        boolean result = repository.addLocomotive("Test Model", 6000, "Diesel", 150, 250, 1920, 1970, 150);

        assertFalse(result);
        assertEquals(1, repository.getLocomotives().size());
    }

    @Test
    void testCheckIfLocomotiveExists() {
        Locomotive loco = new Locomotive("Test Model", 5000, "Steam", 100, 200, 1900, 1950, 120);

        assertFalse(repository.checkIfLocomotiveExists(loco));

        repository.addLocomotive(loco);

        assertTrue(repository.checkIfLocomotiveExists(loco));

        Locomotive loco2 = new Locomotive("Test Model", 6000, "Diesel", 150, 250, 1920, 1970, 150);
        assertTrue(repository.checkIfLocomotiveExists(loco2));

        Locomotive loco3 = new Locomotive("Different Model", 5000, "Steam", 100, 200, 1900, 1950, 120);
        assertFalse(repository.checkIfLocomotiveExists(loco3));
    }

    @Test
    void testDeleteLocomotive() {

        Locomotive loco = new Locomotive("Test Model", 5000, "Steam", 100, 200, 1900, 1950, 120);
        repository.addLocomotive(loco);

        boolean result = repository.deleteLocomotive(loco);

        assertTrue(result);
        assertEquals(0, repository.getLocomotives().size());
    }

    @Test
    void testDeleteNonExistentLocomotive() {
        Locomotive loco = new Locomotive("Test Model", 5000, "Steam", 100, 200, 1900, 1950, 120);

        boolean result = repository.deleteLocomotive(loco);

        assertFalse(result);
    }

    @Test
    void testGetLocomotiveByModelName() {
        Locomotive loco = new Locomotive("Test Model", 5000, "Steam", 100, 200, 1900, 1950, 120);
        repository.addLocomotive(loco);

        Optional<Locomotive> result = repository.getLocomotive("Test Model");

        assertTrue(result.isPresent());
        assertEquals("Test Model", result.get().getModelName());
        assertEquals(5000, result.get().getPrice());
    }

    @Test
    void testGetNonExistentLocomotiveByModelName() {
        Optional<Locomotive> result = repository.getLocomotive("Non-existent Model");

        assertFalse(result.isPresent());
    }

    @Test
    void testGetLocomotives() {
        repository.addLocomotive("Model 1", 5000, "Steam", 100, 200, 1900, 1950, 120);
        repository.addLocomotive("Model 2", 6000, "Diesel", 150, 250, 1920, 1970, 150);
        repository.addLocomotive("Model 3", 7000, "Electric", 50, 150, 1950, 2000, 200);

        Set<Locomotive> locomotives = repository.getLocomotives();

        assertEquals(3, locomotives.size());

        boolean hasModel1 = false, hasModel2 = false, hasModel3 = false;

        for (Locomotive loco : locomotives) {
            switch (loco.getModelName()) {
                case "Model 1": hasModel1 = true; break;
                case "Model 2": hasModel2 = true; break;
                case "Model 3": hasModel3 = true; break;
            }
        }

        assertTrue(hasModel1 && hasModel2 && hasModel3);
    }

    @Test
    void testBuyLocomotiveWithObject() {
        CurrentUser session = CurrentUser.getInstance();
        User user = new User("testuser", "test@email.com", "password", "ROLE_PLAYER");
        session.setUser(user);

        Locomotive locomotive = new Locomotive("Steam Engine", 5000, "Steam", 100, 200, 1900, 1950, 120);
        Budget budget = new Budget(10000, null, null, user);
        boolean result = repository.buyLocomotive(locomotive, budget);
        assertTrue(result);
        assertEquals(5000, budget.getBudget());
        assertEquals(1, user.getLocomotives().size());
        assertEquals(locomotive, user.getLocomotives().get(0));
    }

    @Test
    void testBuyLocomotiveWithModelName() {
        CurrentUser session = CurrentUser.getInstance();
        User user = new User("testuser", "test@email.com", "password", "ROLE_PLAYER");
        session.setUser(user);

        repository.addLocomotive("Available Model", 6000, "Steam", 110, 190, 1920, 1960, 130);
        Budget budget = new Budget(8000, null, null, user);
        boolean result = repository.buyLocomotive("Available Model", budget);
        assertTrue(result);
        assertEquals(2000, budget.getBudget());
        assertEquals(1, user.getLocomotives().size());
    }

    @Test
    void testCopyFrom() {
        LocomotiveRepository sourceRepo = new LocomotiveRepository();
        sourceRepo.addLocomotive("Model A", 5000, "Steam", 100, 200, 1900, 1950, 120);
        sourceRepo.addLocomotive("Model B", 6000, "Diesel", 150, 250, 1920, 1970, 150);

        repository.addLocomotive("Existing Model", 8000, "Hybrid", 80, 180, 1980, 2020, 220);
        assertEquals(1, repository.getLocomotives().size());

        repository.copyFrom(sourceRepo);

        assertEquals(2, repository.getLocomotives().size());
        assertTrue(repository.getLocomotive("Model A").isPresent());
        assertTrue(repository.getLocomotive("Model B").isPresent());
        assertFalse(repository.getLocomotive("Existing Model").isPresent());
    }
}