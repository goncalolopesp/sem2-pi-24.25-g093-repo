package controller;

import controller.CreateCityController;
import domain.City;
import domain.HouseBlock;
import domain.Map;
import repository.Repositories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateCityControllerTest {
    private CreateCityController controller;

    @BeforeEach
    void setUp() {
        var repos = Repositories.getInstance();
        repos.getMapRepository();
        repos.getCityRepository();

        repos.getMapRepository().add(new Map("TestMap", 100, 100));
        repos.getCityRepository().add(new City("ExistingCity", "TestMap", 2, 2));

        controller = new CreateCityController();
    }

    @Test
    void validateNameMap_MapNotFound() {
        assertEquals(CreateCityController.NameMapError.MAP_NOT_FOUND,
                controller.validateNameMap("CityA", "NoMap"));
    }

    @Test
    void validateNameMap_NameDuplicate() {
        assertEquals(CreateCityController.NameMapError.NAME_DUPLICATE,
                controller.validateNameMap("ExistingCity", "TestMap"));
    }

    @Test
    void validateNameMap_OK() {
        assertEquals(CreateCityController.NameMapError.OK,
                controller.validateNameMap("NewCity", "TestMap"));
    }

    @Test
    void validateCenter_OutOfBounds() {
        assertEquals(CreateCityController.CenterError.OUT_OF_BOUNDS,
                controller.validateCenter("NewCity", "TestMap", 0, 11));
    }

    @Test
    void validateCenter_PositionTaken() {
        assertEquals(CreateCityController.CenterError.POSITION_TAKEN,
                controller.validateCenter("NewCity", "TestMap", 2, 2));
    }

    @Test
    void validateCenter_OK() {
        assertEquals(CreateCityController.CenterError.OK,
                controller.validateCenter("NewCity", "TestMap", 5, 5));
    }

    @Test
    void validateBlock_CollidesCityCenter() {
        assertEquals(CreateCityController.BlockError.COLLIDES_CITY_CENTER,
                controller.validateBlock("TestMap", new HouseBlock(2, 2)));
    }

    @Test
    void validateBlock_OK() {
        assertEquals(CreateCityController.BlockError.OK,
                controller.validateBlock("TestMap", new HouseBlock(3, 3)));
    }

    @Test
    void createCity_SuccessfulCreation() {
        var c = controller.createCity("CityA", "TestMap", 4, 4);
        assertEquals("CityA", c.getName());
        assertTrue(Repositories.getInstance().getCityRepository().getCities().contains(c));
    }

    @Test
    void addBlockManual_SucceedsAndFails() {
        var repos = Repositories.getInstance();
        var c = new City("CityB", "TestMap", 6, 6);
        repos.getCityRepository().add(c);
        assertTrue(controller.addBlockManual(c, new HouseBlock(7, 7)));
        assertFalse(controller.addBlockManual(c, new HouseBlock(6, 6)));
    }

    @Test
    void validateCenter_BlockPresent() {
        var repos = Repositories.getInstance();
        var c = new City("CityC", "TestMap", 1, 1);
        repos.getCityRepository().add(c);
        c.addBlockManual(new HouseBlock(15, 15));
        assertEquals(CreateCityController.CenterError.BLOCK_PRESENT,
                controller.validateCenter("OK", "TestMap", 15, 15));
    }

    @Test
    void validateBlock_OutOfBoundsAndMapNotFound() {
        assertEquals(CreateCityController.BlockError.OUT_OF_BOUNDS,
                controller.validateBlock("TestMap", new HouseBlock(0, 11)));
        assertEquals(CreateCityController.BlockError.OUT_OF_BOUNDS,
                controller.validateBlock("NoMap", new HouseBlock(1, 1)));
    }

    @Test
    void validateBlock_CollidesBlock() {
        var repos = Repositories.getInstance();
        var c = new City("CityD", "TestMap", 2, 3);
        repos.getCityRepository().add(c);
        c.addBlockManual(new HouseBlock(47, 47));
        assertEquals(CreateCityController.BlockError.COLLIDES_BLOCK,
                controller.validateBlock("TestMap", new HouseBlock(47, 47)));
    }

    @Test
    void createCity_ThrowsOnDuplicateName() {
        assertThrows(IllegalStateException.class, () ->
                controller.createCity("ExistingCity", "TestMap", 5, 5)
        );
    }

    @Test
    void createCity_ThrowsOnInvalidCenter() {
        assertThrows(IllegalStateException.class, () ->
                controller.createCity("CityE", "TestMap", -1, -1)
        );
    }

    @Test
    void assignBlocksAuto_AddsBlocksSuccessfully() {
        var repos = Repositories.getInstance();
        var city = new City("AutoCity", "TestMap", 5, 5);
        repos.getCityRepository().add(city);
        controller.assignBlocksAuto(city, 5, 1.0);
        assertEquals(5, city.getBlocks().size());
    }

}