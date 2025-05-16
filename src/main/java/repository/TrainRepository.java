package repository;

import domain.Train;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Repository for storing and querying Trains instances.
 * <p>
 * Provides methods to add trains, delete trains, find a specific train and retrieve all trains.
 * </p>
 */
public class TrainRepository {
    private final List<Train> trains = new ArrayList<>();

    /**
     * Saves a train to the repository.
     * If the train already exists (by reference), it won't be added again.
     * @param train the train to save
     * @return the saved train
     */
    public Train save(Train train) {
        if (train == null) {
            throw new IllegalArgumentException("Train cannot be null");
        }
        if (!trains.contains(train)) {
            trains.add(train);
        }
        return train;
    }

    /**
     * Deletes a train from the repository.
     * @param train the train to delete
     * @return true if removed, false if not found
     */
    public boolean delete(Train train) {
        return trains.remove(train);
    }

    /**
     * Finds all trains in the repository.
     * @return unmodifiable list of trains
     */
    public List<Train> getTrains() {
        return Collections.unmodifiableList(trains);
    }

    /**
     * Finds a train by reference.
     * @param train the train to search for
     * @return Optional with the train if found
     */
    public Optional<Train> findTrain(Train train) {
        return trains.stream()
                .filter(t -> t.equals(train))
                .findFirst();
    }
}