package repository;

import domain.Carriage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CarriageRepositoryTest {

    private CarriageRepository repository;
    private Carriage carriage1;
    private Carriage carriage2;

    @BeforeEach
    void setUp() {
        repository = new CarriageRepository();
        carriage1 = new Carriage(Carriage.Type.PEOPLE);
        carriage2 = new Carriage(Carriage.Type.MAIL);
    }

    @Test
    void save_nullCarriage_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test
    void save_newCarriage_addsToRepository() {
        Carriage saved = repository.save(carriage1);
        assertSame(carriage1, saved);
        List<Carriage> all = repository.findAll();
        assertEquals(1, all.size());
        assertTrue(all.contains(carriage1));
    }

    @Test
    void save_existingCarriage_doesNotDuplicate() {
        repository.save(carriage1);
        repository.save(carriage1);
        List<Carriage> all = repository.findAll();
        assertEquals(1, all.size());
    }

    @Test
    void delete_existingCarriage_returnsTrueAndRemoves() {
        repository.save(carriage1);
        boolean removed = repository.delete(carriage1);
        assertTrue(removed);
        assertFalse(repository.findAll().contains(carriage1));
    }

    @Test
    void delete_nonExistingCarriage_returnsFalse() {
        boolean removed = repository.delete(carriage2);
        assertFalse(removed);
    }

    @Test
    void findAll_unmodifiableList_throwsOnModification() {
        repository.save(carriage1);
        List<Carriage> all = repository.findAll();
        assertThrows(UnsupportedOperationException.class, () -> all.add(carriage2));
    }

    @Test
    void find_existingCarriage_returnsOptionalPresent() {
        repository.save(carriage1);
        Optional<Carriage> found = repository.find(carriage1);
        assertTrue(found.isPresent());
        assertEquals(carriage1, found.get());
    }

    @Test
    void find_nonExistingCarriage_returnsOptionalEmpty() {
        Optional<Carriage> found = repository.find(carriage2);
        assertFalse(found.isPresent());
    }
}
