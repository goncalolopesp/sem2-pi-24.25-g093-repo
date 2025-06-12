package repository;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the RouteRepository class.
 * Tests the functionality of saving and retrieving routes.
 */
class RouteRepositoryTest {

    private RouteRepository routeRepository;
    private Route testRoute1;
    private Route testRoute2;
    private Route testRoute3;

    @BeforeEach
    void setUp() {
        routeRepository = new RouteRepository();

        // Create test stations with proper constructor parameters
        Station station1 = new Station(
                Station.Type.STATION,
                "Central Station",
                "TestMap",
                "TestScenario",
                "player1@example.com",
                1000,
                100,
                100,
                200,
                Station.Orientation.NE
        );

        Station station2 = new Station(
                Station.Type.STATION,
                "North Station",
                "TestMap",
                "TestScenario",
                "player1@example.com",
                1500,
                150,
                150,
                250,
                Station.Orientation.SE
        );

        Station station3 = new Station(
                Station.Type.TERMINAL,
                "South Station",
                "TestMap",
                "TestScenario",
                "player2@example.com",
                800,
                80,
                50,
                150,
                null // Terminal stations don't have orientation
        );

        // Create test cargo types
        List<Carriage.Type> cargoTypes1 = List.of(Carriage.Type.PEOPLE, Carriage.Type.MAIL);
        List<Carriage.Type> cargoTypes2 = List.of(Carriage.Type.PEOPLE);
        List<Carriage.Type> cargoTypes3 = List.of(Carriage.Type.MAIL);

        // Create test points of route
        PointOfRoute point1 = new PointOfRoute(station1, cargoTypes1, CargoMode.FULL);
        PointOfRoute point2 = new PointOfRoute(station2, cargoTypes2, CargoMode.FULL);
        PointOfRoute point3 = new PointOfRoute(station3, cargoTypes3, CargoMode.FULL);

        // Create test routes
        testRoute1 = new Route(
                List.of(point1, point2),
                "player1@example.com",
                "TestMap1",
                "TestScenario1"
        );

        testRoute2 = new Route(
                List.of(point2, point3),
                "player2@example.com",
                "TestMap2",
                "TestScenario2"
        );

        testRoute3 = new Route(
                List.of(point1, point3),
                "player1@example.com",
                "TestMap1",
                "TestScenario3"
        );
    }

    @Test
    void testSaveSingleRoute() {
        // When
        routeRepository.save(testRoute1);
        List<Route> routes = routeRepository.getRoutes();

        // Then
        assertEquals(1, routes.size());
        assertEquals(testRoute1.getId(), routes.get(0).getId());
        assertEquals(testRoute1.getPlayerEmail(), routes.get(0).getPlayerEmail());
        assertEquals(testRoute1.getMapName(), routes.get(0).getMapName());
        assertEquals(testRoute1.getScenarioName(), routes.get(0).getScenarioName());
    }

    @Test
    void testSaveMultipleRoutes() {
        routeRepository.save(testRoute1);
        routeRepository.save(testRoute2);
        routeRepository.save(testRoute3);

        List<Route> routes = routeRepository.getRoutes();

        assertEquals(3, routes.size());

        List<String> routeIds = routes.stream().map(Route::getId).collect(Collectors.toList());

        assertTrue(routeIds.contains(testRoute1.getId()));
        assertTrue(routeIds.contains(testRoute2.getId()));
        assertTrue(routeIds.contains(testRoute3.getId()));
    }

    @Test
    void testGetRoutesWhenEmpty() {
        // When
        List<Route> routes = routeRepository.getRoutes();

        // Then
        assertNotNull(routes);
        assertTrue(routes.isEmpty());
        assertEquals(0, routes.size());
    }

    @Test
    void testGetRoutesReturnsDefensiveCopy() {
        // Given
        routeRepository.save(testRoute1);
        routeRepository.save(testRoute2);

        // When
        List<Route> routes1 = routeRepository.getRoutes();
        List<Route> routes2 = routeRepository.getRoutes();

        // Then - Should be different instances but same content
        assertNotSame(routes1, routes2);
        assertEquals(routes1.size(), routes2.size());

        // Modifying returned list should not affect repository
        routes1.clear();
        List<Route> routes3 = routeRepository.getRoutes();
        assertEquals(2, routes3.size());
    }

    @Test
    void testRouteOrder() {
        // Given
        List<Route> expectedOrder = List.of(testRoute1, testRoute2, testRoute3);

        // When
        for (Route route : expectedOrder) {
            routeRepository.save(route);
        }

        List<Route> actualRoutes = routeRepository.getRoutes();

        // Then
        assertEquals(expectedOrder.size(), actualRoutes.size());
        for (int i = 0; i < expectedOrder.size(); i++) {
            assertEquals(expectedOrder.get(i).getId(), actualRoutes.get(i).getId());
        }
    }

    @Test
    void testSaveDuplicateRoutes() {
        // When
        routeRepository.save(testRoute1);
        routeRepository.save(testRoute1); // Same route instance

        List<Route> routes = routeRepository.getRoutes();

        // Then
        assertEquals(2, routes.size());
        assertEquals(testRoute1.getId(), routes.get(0).getId());
        assertEquals(testRoute1.getId(), routes.get(1).getId());
        assertSame(routes.get(0), routes.get(1)); // Same instance
    }

    @Test
    void testSaveRoutesWithSameData() {
        // Given - Create routes with identical data but different instances
        Station station = new Station(
                Station.Type.STATION,
                "Test Station",
                "TestMap",
                "TestScenario",
                "test@example.com",
                1000,
                100,
                100,
                200,
                Station.Orientation.NW
        );

        List<Carriage.Type> cargoTypes = List.of(Carriage.Type.PEOPLE);
        PointOfRoute point = new PointOfRoute(station, cargoTypes, CargoMode.FULL);

        Route route1 = new Route(List.of(point), "test@example.com", "TestMap", "TestScenario");
        Route route2 = new Route(List.of(point), "test@example.com", "TestMap", "TestScenario");

        // When
        routeRepository.save(route1);
        routeRepository.save(route2);

        List<Route> routes = routeRepository.getRoutes();

        // Then
        assertEquals(2, routes.size());
        assertEquals(route1.getId(), route2.getId()); // Should have same generated ID
        assertNotSame(route1, route2); // But different instances
    }

    @Test
    void testConcurrentAccess() {
        // Given
        routeRepository.save(testRoute1);

        // When - Simulate concurrent access
        List<Route> routes = routeRepository.getRoutes();
        routeRepository.save(testRoute2); // Modify repository while holding reference

        // Then - Original list should remain unaffected
        assertEquals(1, routes.size());

        // New call should return updated list
        List<Route> updatedRoutes = routeRepository.getRoutes();
        assertEquals(2, updatedRoutes.size());
    }

    @Test
    void testRouteDataIntegrity() {
        // Given
        String originalPlayerEmail = testRoute1.getPlayerEmail();
        String originalMapName = testRoute1.getMapName();
        String originalScenarioName = testRoute1.getScenarioName();
        int originalPointsCount = testRoute1.getPointsOfRoute().size();

        // When
        routeRepository.save(testRoute1);
        List<Route> routes = routeRepository.getRoutes();
        Route retrievedRoute = routes.get(0);

        // Then
        assertEquals(originalPlayerEmail, retrievedRoute.getPlayerEmail());
        assertEquals(originalMapName, retrievedRoute.getMapName());
        assertEquals(originalScenarioName, retrievedRoute.getScenarioName());
        assertEquals(originalPointsCount, retrievedRoute.getPointsOfRoute().size());

        // Verify it's the same instance (reference equality)
        assertSame(testRoute1, retrievedRoute);
    }
}