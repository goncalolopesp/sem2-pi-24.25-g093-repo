package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IndustryTest {

    @Test
    void testIndustryConstructorAndGetters() {
        IndustryType type = IndustryType.AUTOMOBILE;
        int x = 10;
        int y = 20;

        Industry industry = new Industry("exampleMap", type, x, y);

        assertEquals(type, industry.getType());
        assertEquals(new Location(x, y), industry.getLocation());
    }

    @Test
    void testSetType() {
        Industry industry = new Industry("exampleMap", IndustryType.AUTOMOBILE, 0, 0);
        industry.setType(IndustryType.BAKERY);

        assertEquals(IndustryType.BAKERY, industry.getType());
    }

    @Test
    void testSetLocation() {
        Industry industry = new Industry("exampleMap", IndustryType.COAL_MINE, 0, 0);
        Location newLocation = new Location(50, 60);

        industry.setLocation(newLocation);

        assertEquals(newLocation, industry.getLocation());
    }

    @Test
    void testToString() {
        Industry industry = new Industry("exampleMap", IndustryType.COFFEE_FARM, 5, 15);
        String expected = "Industry{type=COFFEE_FARM, location=" + new Location(5, 15).toString() + "}";

        assertEquals(expected, industry.toString());
    }
}
