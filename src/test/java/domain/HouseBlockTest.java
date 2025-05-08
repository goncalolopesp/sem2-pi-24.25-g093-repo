package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HouseBlockTest {
    HouseBlock hb;
    HouseBlock hb2;
    @BeforeEach
    void setUp() {
         hb = new HouseBlock(3, 4);
    }

    @Test
    void getX_ReturnsCorrectValue() {
        assertEquals(3, hb.getX());
    }

    @Test
    void getY_ReturnsCorrectValue() {
        assertEquals(4, hb.getY());
    }

    @Test
    void equals_SameCoordinates_ReturnsTrue() {
        hb2 = new HouseBlock(3, 4);
        assertEquals(hb, hb2);
    }

    @Test
    void equals_SameCoordinates_ReturnsFlase() {
        hb2 = new HouseBlock(5, 6);
        assertNotEquals(hb, hb2);
    }

    @Test
    void hashCode_EqualsObjects_SameHash(){
        hb2 = new HouseBlock(3, 4);
        assertEquals(hb.hashCode(), hb2.hashCode());
    }
}