package repository;

import domain.Carriage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Repository for storing and querying Carriage instances.
 * <p>
 * Provides methods to save carriage, delete carriages, find a specific carriage and retrieve all carriages.
 * </p>
 */
public class CarriageRepository {
    private final List<Carriage> carriages = new ArrayList<>();

    /**
     * Saves a carriage to the repository.
     * If the carriage already exists (by reference), it won't be added again.
     * @param carriage the carriage to save
     * @return the saved carriage
     */
    public Carriage save(Carriage carriage) {
        if (carriage == null) {
            throw new IllegalArgumentException("Carriage cannot be null");
        }
        if (!carriages.contains(carriage)) {
            carriages.add(carriage);
        }
        return carriage;
    }

    /**
     * Deletes a carriage from the repository.
     * @param carriage the carriage to delete
     * @return true if removed, false if not found
     */
    public boolean delete(Carriage carriage) {
        return carriages.remove(carriage);
    }

    /**
     * Finds all carriages in the repository.
     * @return unmodifiable list of carriages
     */
    public List<Carriage> findAll() {
        return Collections.unmodifiableList(carriages);
    }

    /**
     * Finds a carriage by reference.
     * @param carriage the carriage to search for
     * @return Optional with the carriage if found
     */
    public Optional<Carriage> find(Carriage carriage) {
        return carriages.stream()
                .filter(c -> c.equals(carriage))
                .findFirst();
    }
}
