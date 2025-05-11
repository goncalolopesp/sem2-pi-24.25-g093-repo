# US002 - Add an Industry to the Map

## 4. Tests

**Test 1:** Tests map creation.

```java
@Test
void testIndustryConstructorAndGetters() {
    IndustryType type = IndustryType.AUTOMOBILE;
    int x = 10;
    int y = 20;

    Industry industry = new Industry(type, x, y);

    assertEquals(type, industry.getType());
    assertEquals(new Location(x, y), industry.getLocation());
}
```
	

**Test 2:** Tests Industry setter. 

```java
 @Test
void testSetType() {
    Industry industry = new Industry(IndustryType.AUTOMOBILE, 0, 0);
    industry.setType(IndustryType.BAKERY);

    assertEquals(IndustryType.BAKERY, industry.getType());
}

```

**Test 3:** Tests location setter.

```java
 @Test
void testSetLocation() {
    Industry industry = new Industry(IndustryType.COAL_MINE, 0, 0);
    Location newLocation = new Location(50, 60);

    industry.setLocation(newLocation);

    assertEquals(newLocation, industry.getLocation());
}

```

**Test 4:** Tests toString method.

```java
 @Test
void testToString() {
    Industry industry = new Industry(IndustryType.COFFEE_FARM, 5, 15);
    String expected = "Industry{type=COFFEE_FARM, location=" + new Location(5, 15).toString() + "}";

    assertEquals(expected, industry.toString());
}
```
## 5. Construction (Implementation)

### Class PlaceIndustryController

```java
package controller;


import domain.Industry;

import domain.Map;
import repository.IndustryRepository;
import repository.MapRepository;


import java.util.List;

public class PlaceIndustryController {

    private  IndustryRepository instanceIndustryRepository;
    private MapRepository instanceMapRepository;

    public PlaceIndustryController() {
        this.instanceIndustryRepository = repository.Repositories.getInstance().getIndustryRepository();
        this.instanceMapRepository = repository.Repositories.getInstance().getMapRepository();
    }

    public PlaceIndustryController(IndustryRepository instanceIndustryRepository, MapRepository instanceMapRepository) {
        this.instanceIndustryRepository = instanceIndustryRepository;
        this.instanceMapRepository = instanceMapRepository;
    }

    public List<Map> getMaps() {
        return instanceMapRepository.getMaps();
    }
    public List<Industry> getIndustries() {
        return instanceIndustryRepository.getIndustries();
    }

    public boolean placeIndustryOnMap(Industry industry, int positionX,int positionY, Map map) {
        return map.placeIndustry(industry, positionX, positionY);
    }
}


```

### Class IndustryRepository

```java
package repository;

import domain.Industry;

import java.util.ArrayList;
import java.util.List;

public class IndustryRepository {
    private static IndustryRepository instance;
    private List<Industry> industries;

    public IndustryRepository() {
        industries = new ArrayList<>();
    }

    public static IndustryRepository getInstance() {
        if (instance == null) {
            instance = new IndustryRepository();
        }
        return instance;
    }

    public void addIndustry(Industry industry) {
        industries.add(industry);
    }

    public List<Industry> getIndustries() {
        return List.copyOf(industries);
    }

    public void clearIndustries() {
        industries.clear();
    }
}
```


