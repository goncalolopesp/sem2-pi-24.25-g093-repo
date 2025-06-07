package repository;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class RailwayLineRepositoryTest {

    private RailwayLineRepository repository;
    private Station station1;
    private Station station2;
    private RailwayLine railwayLine;
    private Scenario scenario;
    private Map map;

    @BeforeEach
    void setUp() {
        repository = new RailwayLineRepository();

        // Configurar estações de teste
        station1 = new Station(
                Station.Type.STATION,
                "Estação A",
                "Mapa 1",
                "Cenário 1",
                "usuario@teste.com",
                100,
                500,
                10,
                20,
                Station.Orientation.NE
        );

        station2 = new Station(
                Station.Type.STATION,
                "Estação B",
                "Mapa 1",
                "Cenário 1",
                "usuario@teste.com",
                100,
                500,
                30,
                40,
                Station.Orientation.SW
        );

        // Configurar cenário e mapa
        scenario = new Scenario("Cenário 1", "Descrição do cenário", new int[]{1870, 1880},
                1,
                Collections.singletonList("steam"),
                Collections.singletonList("none"),
                1.0,
                1.0);;
        map = new Map("Mapa 1", 100, 100, 1);

        // Criar linha ferroviária para teste
        railwayLine = new RailwayLine(
                station1,
                station2,
                RailwayLineType.SINGLE,
                100,
                "usuario@teste.com",
                scenario,
                map
        );
    }

    @Test
    void save_ShouldAddRailwayLineToRepository() {
        repository.save(railwayLine);
        assertEquals(1, repository.getRailwayLines().size());
        assertTrue(repository.getRailwayLines().contains(railwayLine));
    }

    @Test
    void getRailwayLines_ShouldReturnCopyOfList() {
        repository.save(railwayLine);
        var lines = repository.getRailwayLines();

        // Modificar a lista retornada não deve afetar o repositório
        lines.clear();
        assertEquals(1, repository.getRailwayLines().size());
    }

    @Test
    void connectionExists_WithExistingConnection_ShouldReturnTrue() {
        repository.save(railwayLine);
        assertTrue(repository.connectionExists(station1, station2));
        // Teste bidirecional
        assertTrue(repository.connectionExists(station2, station1));
    }

    @Test
    void connectionExists_WithNonExistingConnection_ShouldReturnFalse() {
        Station station3 = new Station(
                Station.Type.STATION,
                "Estação C",
                "Mapa 1",
                "Cenário 1",
                "usuario@teste.com",
                100,
                500,
                50,
                60,
                Station.Orientation.NW
        );

        repository.save(railwayLine);
        assertFalse(repository.connectionExists(station1, station3));
    }

    @Test
    void countByScenarioMapAndOwner_WithMatchingCriteria_ShouldReturnCorrectCount() {
        repository.save(railwayLine);

        long count = repository.countByScenarioMapAndOwner(
                scenario,
                map,
                "usuario@teste.com"
        );

        assertEquals(1, count);
    }

    @Test
    void countByScenarioMapAndOwner_WithNonMatchingCriteria_ShouldReturnZero() {
        repository.save(railwayLine);

        Scenario differentScenario = new Scenario("Outro", "Outro cenário", new int[]{1870, 1880},
                1,
                Collections.singletonList("steam"),
                Collections.singletonList("none"),
                1.0,
                1.0);;

        long count = repository.countByScenarioMapAndOwner(
                differentScenario,
                map,
                "usuario@teste.com"
        );

        assertEquals(0, count);
    }

    @Test
    void countByScenarioMapAndOwner_WithEmptyRepository_ShouldReturnZero() {
        long count = repository.countByScenarioMapAndOwner(
                scenario,
                map,
                "usuario@teste.com"
        );

        assertEquals(0, count);
    }
}
