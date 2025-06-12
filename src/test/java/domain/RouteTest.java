
package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteTest {

    private Route route;
    private List<PointOfRoute> points;
    private String playerEmail;
    private String mapName;
    private String scenarioName;
    private Station station1;
    private Station station2;
    private Train train;

    @BeforeEach
    void setUp() {
        // Criar estações para teste
        station1 = new Station(
                Station.Type.STATION,
                "Estação A",
                "Mapa Teste",
                "Cenário Teste",
                "dono@email.com",
                100,
                1000,
                10,
                20,
                Station.Orientation.NE
        );

        station2 = new Station(
                Station.Type.TERMINAL,
                "Estação B",
                "Mapa Teste",
                "Cenário Teste",
                "dono@email.com",
                200,
                2000,
                30,
                40,
                Station.Orientation.SW
        );
        points = new ArrayList<>();
        points.add(new PointOfRoute(station1,
                Arrays.asList(Carriage.Type.MAIL, Carriage.Type.PEOPLE),
                CargoMode.FULL));
        points.add(new PointOfRoute(station2,
                Arrays.asList(Carriage.Type.COAL, Carriage.Type.IRON),
                CargoMode.HALF));
        playerEmail = "player@test.com";
        mapName = "Mapa Teste";
        scenarioName = "Cenário Teste";
        route = new Route(points, playerEmail, mapName, scenarioName);
        Locomotive locomotive = new Locomotive("Loco1", 100, "Steam", 1000,100,1000,1000,100);
        List<Carriage> carriages = Arrays.asList(
                new Carriage(Carriage.Type.MAIL, 100),
                new Carriage(Carriage.Type.PEOPLE, 100)
        );
        train = new Train(locomotive, carriages, playerEmail);
    }

    @Test
    void constructor_WithValidParameters_ShouldCreateRoute() {
        assertNotNull(route);
        assertEquals(points, route.getPointsOfRoute());
        assertEquals(playerEmail, route.getPlayerEmail());
        assertEquals(mapName, route.getMapName());
        assertEquals(scenarioName, route.getScenarioName());
        assertNull(route.getTrain());
    }

    @Test
    void getId_ShouldGenerateCorrectFormat() {
        String expectedId = "Estação A-Estação B-player@test.com_Mapa Teste_Cenário Teste";
        assertEquals(expectedId, route.getId());
    }

    @Test
    void getPointsOfRoute_ShouldReturnAllPoints() {
        List<PointOfRoute> returnedPoints = route.getPointsOfRoute();
        assertEquals(2, returnedPoints.size());
        assertEquals(station1, returnedPoints.get(0).getStation());
        assertEquals(station2, returnedPoints.get(1).getStation());
    }

    @Test
    void getPoints_ShouldReturnSameAsPointsOfRoute() {
        assertEquals(route.getPointsOfRoute(), route.getPoints());
    }

    @Test
    void setAndGetTrain_ShouldWorkCorrectly() {
        assertNull(route.getTrain());
        route.setTrain(train);
        assertEquals(train, route.getTrain());
    }

    @Test
    void setMapName_ShouldUpdateMapNameAndId() {
        String newMapName = "Novo Mapa";
        route.setMapName(newMapName);
        assertEquals(newMapName, route.getMapName());
        assertTrue(route.getId().contains(newMapName));
    }

    @Test
    void setScenarioName_ShouldUpdateScenarioNameAndId() {
        String newScenarioName = "Novo Cenário";
        route.setScenarioName(newScenarioName);
        assertEquals(newScenarioName, route.getScenarioName());
        assertTrue(route.getId().contains(newScenarioName));
    }

    @Test
    void toString_ShouldContainAllRelevantInformation() {
        String str = route.toString();
        assertTrue(str.contains(route.getId()));
        assertTrue(str.contains(String.valueOf(points.size())));
        assertTrue(str.contains(playerEmail));
        assertTrue(str.contains(mapName));
        assertTrue(str.contains(scenarioName));
        assertTrue(str.contains("hasTrain=false"));

        route.setTrain(train);
        str = route.toString();
        assertTrue(str.contains("hasTrain=true"));
    }
}