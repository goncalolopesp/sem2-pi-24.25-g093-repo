package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CityTest {
    private City city;

    @BeforeEach
    void setUp() {
        city = new City("CidadeTeste", "MapaTeste", 3, 4);
    }

    @Test
    void getName_ReturnsCorrectValue() {
        assertEquals("CidadeTeste", city.getName());
    }

    @Test
    void getMapName_ReturnsCorrectValue() {
        assertEquals("MapaTeste", city.getMapName());
    }

    @Test
    void getCenterX_ReturnsCorrectValue() {
        assertEquals(3, city.getCenterX());
    }

    @Test
    void getCenterY_ReturnsCorrectValue() {
        assertEquals(4, city.getCenterY());
    }

    @Test
    void addBlockManual_AddsBlockSuccessfully() {
        HouseBlock block = new HouseBlock(1, 2);
        city.addBlockManual(block);
        List<HouseBlock> blocks = city.getBlocks();
        assertEquals(1, blocks.size());
        assertTrue(blocks.contains(block));
    }

    @Test
    void getBlocks_ReturnsUnmodifiableList() {
        List<HouseBlock> blocks = city.getBlocks();
        assertThrows(UnsupportedOperationException.class, () -> blocks.add(new HouseBlock(0, 0)));
    }

}