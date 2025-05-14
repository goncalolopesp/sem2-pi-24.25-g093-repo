import controller.AuthController;
import controller.BuyLocomotiveController;
import controller.CreateMapController;
import controller.CreateScenarioController;
import domain.Budget;
import domain.CurrentUser;
import repository.AuthRepository;
import repository.LocomotiveRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Bootstrap {
    public void run() {
        addUsers(); addMaps(); addScenarios(); createLocomotives();
    }

    private void addUsers() {
        //TODO: add Authentication users here: should be created for each user in the organization
        //get authentication repository
        AuthController authController = new AuthController();

        authController.createNewUser("Main Editor", "editor@this.app", "admin", authController.ROLE_EDITOR, Optional.empty());
        authController.createNewUser("Player 1", "Player@this.app", "pwd", authController.ROLE_PLAYER, Optional.of(new Budget(1000)));
    }

    private void createLocomotives() {
        //TODO: add Locomotive models here: should be created for each locomotive in the game
        //get locomotive repository

        LocomotiveRepository lr = new LocomotiveRepository();
        CurrentUser session = CurrentUser.getInstance();
        session.setLocomotiveRepository(lr);

        BuyLocomotiveController buyLocomotiveController = new BuyLocomotiveController();
        buyLocomotiveController.createLocomotive("Trevithick-1", 10, "Steam", 8, 6, 1800, 1835, 10);
        buyLocomotiveController.createLocomotive("Stephenson Rocket", 16, "Steam", 8, 6, 1829, 1851, 16);
        buyLocomotiveController.createLocomotive("John Bull", 23, "Steam", 9, 7, 1831, 1853, 25);
        buyLocomotiveController.createLocomotive("Dewitt Clinton", 18, "Steam", 7, 5, 1833, 1855, 20);
        buyLocomotiveController.createLocomotive("Prussian", 35, "Steam", 11, 8, 1837, 1857, 30);
        buyLocomotiveController.createLocomotive("American-C", 46, "Steam", 13, 5, 1848, 1885, 40);
        buyLocomotiveController.createLocomotive("Iron Duke", 78, "Steam", 31, 9, 1855, 1894, 54);
        buyLocomotiveController.createLocomotive("Eight Wheeler", 59, "Steam", 18, 5, 1868, 1903, 48);
        buyLocomotiveController.createLocomotive("Vulcan", 32, "Steam", 6, 4, 1872, 1900, 30);
        buyLocomotiveController.createLocomotive("Consolidation", 51, "Steam", 20, 8, 1877, 1905, 40);
        buyLocomotiveController.createLocomotive("3-Truck Shay", 43, "Steam", 18, 17, 1882, 1930, 15);
        buyLocomotiveController.createLocomotive("Mastodon", 60, "Steam", 23, 13, 1890, 1911, 45);
        buyLocomotiveController.createLocomotive("Ten Wheeler", 66, "Steam", 22, 11, 1892, 1911, 50);
        buyLocomotiveController.createLocomotive("Mogul", 83, "Steam", 25, 12, 1895, 1915, 48);
        buyLocomotiveController.createLocomotive("Atlantic", 93, "Steam", 44, 18, 1902, 1929, 80);
        buyLocomotiveController.createLocomotive("Camelback", 75, "Steam", 15, 9, 1905, 1932, 30);
        buyLocomotiveController.createLocomotive("Pacific", 119, "Steam", 63, 21, 1908, 1945, 87);
        buyLocomotiveController.createLocomotive("Class G10", 98, "Steam", 51, 38, 1910, 1939, 50);
        buyLocomotiveController.createLocomotive("Prairie", 85, "Steam", 34, 11, 1912, 1953, 60);
        buyLocomotiveController.createLocomotive("D16sb", 65, "Steam", 21, 9, 1914, 1934, 45);
        buyLocomotiveController.createLocomotive("Class 13H", 102, "Steam", 46, 36, 1917, 1942, 40);
        buyLocomotiveController.createLocomotive("USRA", 90, "Steam", 30, 13, 1918, 1942, 40);
        buyLocomotiveController.createLocomotive("Mikado", 133, "Steam", 51, 32, 1919, 1950, 55);
        buyLocomotiveController.createLocomotive("Class B12", 146, "Steam", 32, 14, 1923, 1939, 71);
        buyLocomotiveController.createLocomotive("USRA II", 98, "Steam", 32, 18, 1930, 1948, 45);
        buyLocomotiveController.createLocomotive("Class A4 Mallard", 200, "Steam", 55, 19, 1935, 1955, 126);
        buyLocomotiveController.createLocomotive("Hudson", 210, "Steam", 54, 25, 1937, 1953, 90);
        buyLocomotiveController.createLocomotive("Daylight", 230, "Steam", 64, 30, 1937, 1952, 80);
        buyLocomotiveController.createLocomotive("J3A Streamliner", 255, "Steam", 112, 28, 1938, 1955, 103);
        buyLocomotiveController.createLocomotive("Big Boy", 375, "Steam", 84, 75, 1941, 1955, 68);
        buyLocomotiveController.createLocomotive("T1", 284, "Steam", 78, 28, 1945, 99999, 85);
        buyLocomotiveController.createLocomotive("DB 18 201", 400, "Steam", 145, 75, 1998, 99999, 120);

        buyLocomotiveController.createLocomotive("F3A+B", 265, "Diesel", 52, 16, 1945, 1968, 85);
        buyLocomotiveController.createLocomotive("Alco PA-1", 210, "Diesel", 53, 16, 1946, 1990, 90);
        buyLocomotiveController.createLocomotive("F9", 337, "Diesel", 63, 18, 1949, 1970, 110);
        buyLocomotiveController.createLocomotive("GP9", 235, "Diesel", 48, 11, 1954, 1980, 71);
        buyLocomotiveController.createLocomotive("GP18", 245, "Diesel", 55, 15, 1958, 1975, 83);
        buyLocomotiveController.createLocomotive("V200", 160, "Diesel", 54, 19, 1959, 1985, 87);
        buyLocomotiveController.createLocomotive("Class 55 Deltic", 480, "Diesel", 52, 15, 1961, 1981, 100);
        buyLocomotiveController.createLocomotive("FP45", 366, "Diesel", 66, 14, 1968, 99999, 106);
        buyLocomotiveController.createLocomotive("SD45", 280, "Diesel", 36, 21, 1972, 1994, 65);
        buyLocomotiveController.createLocomotive("SDP40", 292, "Diesel", 62, 18, 1973, 1989, 103);
        buyLocomotiveController.createLocomotive("Dash-9", 478, "Diesel", 68, 18, 1993, 99999, 70);
        buyLocomotiveController.createLocomotive("AMD-103", 425, "Diesel", 88, 25, 1993, 99999, 105);
        buyLocomotiveController.createLocomotive("Class 232", 492, "Diesel", 73, 35, 1997, 99999, 75);

        buyLocomotiveController.createLocomotive("1-3 Bobo", 85, "Electric", 33, 6, 1895, 1935, 54);
        buyLocomotiveController.createLocomotive("Be 4/6 II", 61, "Electric", 15, 11, 1920, 1950, 35);
        buyLocomotiveController.createLocomotive("Ee 3/3", 47, "Electric", 12, 7, 1932, 1948, 30);
        buyLocomotiveController.createLocomotive("Class 1045", 95, "Electric", 12, 6, 1927, 1950, 40);
        buyLocomotiveController.createLocomotive("GG1", 285, "Electric", 43, 19, 1935, 1970, 100);
        buyLocomotiveController.createLocomotive("Class E18", 93, "Electric", 27, 16, 1936, 1966, 93);
        buyLocomotiveController.createLocomotive("Ae 8/14", 210, "Electric", 48, 24, 1939, 1955, 68);
        buyLocomotiveController.createLocomotive("Class 1020", 119, "Electric", 22, 18, 1941, 1981, 56);
        buyLocomotiveController.createLocomotive("E69", 86, "Electric", 13, 8, 1955, 1970, 31);
        buyLocomotiveController.createLocomotive("TGVx", 850, "Electric", 71, 80, 1957, 1973, 155); // second-to-last row, left as is
        buyLocomotiveController.createLocomotive("Penn. E44", 370, "Electric", 38, 22, 1960, 1985, 70);
        buyLocomotiveController.createLocomotive("Shinkansen Bullet", 650, "Electric", 67, 66, 1966, 2002, 130);
        buyLocomotiveController.createLocomotive("E60CP", 310, "Electric", 38, 19, 1973, 1998, 85);
        buyLocomotiveController.createLocomotive("TGV", 750, "Electric", 82, 72, 1973, 99999, 155);
        buyLocomotiveController.createLocomotive("Class E111", 390, "Electric", 38, 17, 1974, 99999, 85);
        buyLocomotiveController.createLocomotive("E656 FS", 226, "Electric", 38, 17, 1975, 1995, 93);
        buyLocomotiveController.createLocomotive("Thalys Bullet", 1000, "Electric", 112, 78, 1994, 99999, 186);
        buyLocomotiveController.createLocomotive("Eurostar", 1600, "Electric", 96, 65, 1994, 99999, 175);
        buyLocomotiveController.createLocomotive("Brenner", 1250, "Electric", 69, 175, 1999, 99999, 140);
        buyLocomotiveController.createLocomotive("Mag-Lev TBX-1", 2500, "Electric", 306, 200, 2008, 99999, 280);

    }

    private void addMaps() {
        CreateMapController createMapController = new CreateMapController();
        createMapController.createMap("exampleMap", 50, 50);
    }

    private void addScenarios() {
        CreateScenarioController createScenarioController = new CreateScenarioController();
        int[] timeRestrictions = {1974, 1978};
        List<String> historicalRestrictions = new ArrayList<>();
        historicalRestrictions.add("none");
        List<String> techRestrictions = new ArrayList<>();
        techRestrictions.add("none");
        createScenarioController.createScenario("exampleScenario", "exampleMap", timeRestrictions, techRestrictions, historicalRestrictions);
    }


}