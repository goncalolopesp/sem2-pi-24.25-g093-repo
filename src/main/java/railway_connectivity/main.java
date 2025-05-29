package railway_connectivity;

import guru.nidi.graphviz.attribute.Color;
import static guru.nidi.graphviz.attribute.Attributes.attr;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import static guru.nidi.graphviz.engine.Engine.DOT;
import static guru.nidi.graphviz.model.Factory.*;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.*;
import java.util.*;

public class main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        String csvDir = (args.length > 0 ? args[0] : ".");
        File cwd = new File(csvDir);
        System.out.println("Trying to find .csv files in: " + cwd.getAbsolutePath());
        if (!cwd.exists() || !cwd.isDirectory()) {
            System.err.println("Directory does not exist or isn't a valid directory: " + cwd.getAbsolutePath());
            System.exit(1);
        }
        File[] csvFiles = cwd.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (csvFiles == null || csvFiles.length < 2) {
            System.err.println("There needs to be at least 2 .csv here (1 for stations, 1 for lines): " + cwd.getAbsolutePath());
            System.exit(1);
        }

        System.out.println("CSV Files Found:");
        for (File f : csvFiles) {
            System.out.println(" - " + f.getName());
        }

        String linesFile = askFileName(sc, csvFiles, "Enter lines CSV file name: ");
        String stationsFile = askFileName(sc, csvFiles, "Enter station CSV file name: ");

        File lines = new File(cwd, linesFile);
        File stations = new File(cwd, stationsFile);

        if (!lines.exists() || !stations.exists()) {
            System.err.println("One of the selected files does not exist. Exiting...");
            System.exit(1);
        }

        while (true) {
            System.out.println("\n=== Menu ===");
            System.out.println("1. Verify if a train can travel between two stations (US13)");
            System.out.println("2. Verify if a route that passes once, and only once, by each railway line exists (US14)");
            System.out.println("3. Find shortest route through specified stations (US27)");
            System.out.println("4. Performance Testing for Variable-Sized Inputs (US29)");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            String op = sc.nextLine();
            System.out.println();

            if (op.equals("1")) {
                implementUS13(lines);
                break;
            } else if (op.equals("2")) {
                implementUS14(lines);
                break;
            } else if (op.equals("3")) {
                implementUS27(lines, stations);
                break;
            } else if (op.equals("4")) {
                implementUS29();
                break;
            } else if (op.equals("5")) {
                System.out.println("Closing...");
                break;
            } else {
                System.out.println("Invalid option. Please, try another one.");
            }
        }
        sc.close();
    }

    private static void implementUS13(File linesFile) throws IOException {
        // Load railway data
        List<String[]> edges = loadEdges(linesFile);

        // Menu for train type selection
        System.out.println("Select train type:");
        System.out.println("1. Steam");
        System.out.println("2. Diesel");
        System.out.println("3. Electric");
        System.out.print("Train type: ");
        String trainType = sc.nextLine().trim();

        // Input source and destination stations
        System.out.print("Enter source station: ");
        String source = sc.nextLine().trim();
        System.out.print("Enter destination station: ");
        String destination = sc.nextLine().trim();

        if (source.split("_")[0].equals(destination.split("_")[0])) {
            List<String> vertices = new ArrayList<>();
            for (String[] e : edges) {
                if (!vertices.contains(e[0])) vertices.add(e[0]);
                if (!vertices.contains(e[1])) vertices.add(e[1]);
            }

            // Build adjacency list
            Map<String, List<String>> graph = buildGraph(edges, trainType);

            // Check connectivity
            boolean canTravel = isConnected(graph, source, destination);
            if (canTravel) {
                System.out.println("The train can travel between " + source + " and " + destination + ".");
            } else {
                System.out.println("The train cannot travel between " + source + " and " + destination + ".");
            }

            // Build adjacency list
            Map<String, MutableNode> nodeMap = new HashMap<>();
            for (String v : vertices) nodeMap.put(v, mutNode(v).add(Color.BLACK));
            // build graph
            MutableGraph g = mutGraph("MapaDeEstacoes").setDirected(false)
                    .add(nodeMap.values().toArray(new MutableNode[0]));
            for (String[] e : edges) {
                String a = e[0], b = e[1];
                Color c = e[2].equals("1") ? Color.YELLOW : Color.RED;
                g.add(nodeMap.get(a).addLink(to(nodeMap.get(b)).with(c)));
            }

            Graphviz.fromGraph(g).engine(DOT).render(Format.SVG).toFile(new File("station-map.svg"));
            System.out.println("Graph saved as station-map.svg");
        } else {
            System.out.println("The train cannot travel between " + source + " and " + destination + ": the type of the stations are different.");
        }
    }

    private static Map<String, List<String>> buildGraph(List<String[]> edges, String trainType) {
        Map<String, List<String>> graph = new HashMap<>();
        for (String[] edge : edges) {
            String from = edge[0];
            String to = edge[1];
            String type = edge[2]; // 0 = non-electrified, 1 = electrified

            // Filter edges based on train type
            if (trainType.equals("3") && type.equals("0")) continue; // Electric trains can't use non-electrified lines

            graph.putIfAbsent(from, new ArrayList<>());
            graph.putIfAbsent(to, new ArrayList<>());
            graph.get(from).add(to);
            graph.get(to).add(from);
        }
        return graph;
    }

    private static boolean isConnected(Map<String, List<String>> graph, String source, String destination) {
        Set<String> visited = new HashSet<>();
        return dfs(graph, source, destination, visited);
    }

    private static boolean dfs(Map<String, List<String>> graph, String current, String destination, Set<String> visited) {
        if (current.equals(destination)) return true;
        visited.add(current);

        for (String neighbor : graph.getOrDefault(current, new ArrayList<>())) {
            if (!visited.contains(neighbor)) {
                if (dfs(graph, neighbor, destination, visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void implementUS14(File linesF) throws IOException {
        try {
            // edges will store the lines in order from csv file
            // edges.get(i)[0] - "from"
            // edges.get(i)[1] - "to"
            // edges.get(i)[2] - type (1 = electrified; 0 = not)
            List<String[]> allEdges = loadEdges(linesF);

            // choice of maintenance type
            System.out.println("Select maintenance type:");
            System.out.println("1. All The Lines");
            System.out.println("2. Only Electrified Lines");
            System.out.print("Maintenance type: ");
            boolean onlyElec = sc.nextLine().trim().equals("2");

            // filter edges
            List<String[]> edges = new ArrayList<>();
            for (String[] e : allEdges) {
                boolean elec = e[2].equals("1");
                if (!onlyElec || elec) edges.add(e);
            }
            if (edges.isEmpty()) {
                System.out.println("There are no lines that meet the requirements.");
                return;
            }

            // build vertices & adjacency
            List<String> vertices = new ArrayList<>();
            for (String[] e : edges) {
                if (!vertices.contains(e[0])) vertices.add(e[0]);
                if (!vertices.contains(e[1])) vertices.add(e[1]);
            }
            List<List<Integer>> adj = new ArrayList<>();
            for (int i = 0; i < vertices.size(); i++) adj.add(new ArrayList<>());
            for (int i = 0; i < edges.size(); i++) {
                String[] e = edges.get(i);
                int u = vertices.indexOf(e[0]);
                int v = vertices.indexOf(e[1]);
                adj.get(u).add(i);
                adj.get(v).add(i);
            }

            // find non-zero degree vertices
            List<Integer> nonZero = new ArrayList<>();
            for (int i = 0; i < vertices.size(); i++) if (!adj.get(i).isEmpty()) nonZero.add(i);

            // check connectivity
            List<Boolean> visited = new ArrayList<>();
            for (int i = 0; i < vertices.size(); i++) visited.add(false);
            dfs(nonZero.get(0), adj, edges, vertices, visited);
            for (int idx : nonZero)
                if (!visited.get(idx)) {
                    System.out.println("Graph is disconnected. No Eulerian route exists.");
                    return;
                }

            // count odd degree vertices
            List<Integer> odd = new ArrayList<>();
            for (int i : nonZero) if (adj.get(i).size() % 2 != 0) odd.add(i);

            if (odd.size() != 0 && odd.size() != 2) {
                System.out.println("No Eulerian route (" + odd.size() + " odd-degree vertices).");
                return;
            }

            System.out.println("Eulerian route is possible!");

            // Find the complete Eulerian route
            List<String> completeRoute;
            if (odd.size() == 0) {
                // Eulerian cycle - start from any vertex
                completeRoute = findEulerianRoute(nonZero.get(0), vertices, adj, edges);
            } else {
                // Eulerian path - start from one odd vertex
                completeRoute = findEulerianRoute(odd.get(0), vertices, adj, edges);
            }

            System.out.println("\n=== COMPLETE EULERIAN ROUTE ===");
            System.out.println("Full maintenance route: " + completeRoute);
            System.out.println("Total stations in route: " + completeRoute.size());

            // Ask user if they want to see complete or partial route
            System.out.println("\nRoute Options:");
            System.out.println("1. Generate graph for complete route");
            System.out.println("2. Generate graph for partial route (choose start and end stations)");
            System.out.print("Choose option: ");
            String routeChoice = sc.nextLine().trim();

            if (routeChoice.equals("1")) {
                // Generate complete route visualization
                generateCompleteRouteVisualization(completeRoute, edges, vertices);
            } else if (routeChoice.equals("2")) {
                // Get valid start and end stations for partial route
                System.out.println("\nAvailable stations: " + getNames(vertices, nonZero));

                int start = getValidStation(vertices, nonZero, "Enter start station for the route: ");
                int end = getValidStation(vertices, nonZero, "Enter end station for the route: ", start);

                // Extract the partial route from start to end
                List<String> partialRoute = extractPartialRoute(completeRoute, vertices.get(start), vertices.get(end));

                if (partialRoute == null || partialRoute.isEmpty()) {
                    System.out.println("Could not find a route from " + vertices.get(start) + " to " + vertices.get(end));
                    return;
                }

                System.out.println("\n=== PARTIAL ROUTE ===");
                System.out.println("Maintenance route (" + vertices.get(start) + " -> " + vertices.get(end) + "): " + partialRoute);
                System.out.println("Stations in partial route: " + partialRoute.size());

                // Generate partial route visualization
                generatePartialRouteVisualization(partialRoute, edges, vertices, start, end);
            } else {
                System.out.println("Invalid option selected.");
                return;
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void implementUS29() throws IOException {
        System.out.println("=== US29 - Performance Testing for Variable-Sized Inputs ===");
        System.out.println("Testing asymptotic behavior of US13 and US14 procedures\n");

        // Directory containing test files
        System.out.print("Insert directory path for test files: ");
        String testDataDirectory = sc.nextLine(); // Change this to your directory path

        // Discover all CSV files in the directory
        File directory = new File(testDataDirectory);
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Error: Directory '" + testDataDirectory + "' does not exist or is not a directory");
            return;
        }

        // Get all scenario files and station files
        List<File> scenarioFiles = new ArrayList<>();
        List<File> stationFiles = new ArrayList<>();

        File[] csvFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (csvFiles == null || csvFiles.length == 0) {
            System.err.println("No CSV files found in directory: " + testDataDirectory);
            return;
        }

        // Separate scenario files from station files
        for (File file : csvFiles) {
            String fileName = file.getName().toLowerCase();
            if (fileName.contains("scenario") && !fileName.contains("station")) {
                scenarioFiles.add(file);
            } else if (fileName.contains("station")) {
                stationFiles.add(file);
            }
        }

        // Sort files by size/name for consistent ordering
        scenarioFiles.sort((f1, f2) -> {
            // Extract numbers from filename for natural sorting
            int num1 = extractNumberFromFilename(f1.getName());
            int num2 = extractNumberFromFilename(f2.getName());
            return Integer.compare(num1, num2);
        });

        stationFiles.sort((f1, f2) -> {
            int num1 = extractNumberFromFilename(f1.getName());
            int num2 = extractNumberFromFilename(f2.getName());
            return Integer.compare(num1, num2);
        });

        System.out.println("Found " + scenarioFiles.size() + " scenario files and " + stationFiles.size() + " station files\n");

        // Test US13 (Connectivity Check) performance
        System.out.println("=== TESTING US13 (Train Connectivity) PERFORMANCE ===");
        testUS13Performance(scenarioFiles, stationFiles);

        System.out.println("\n" + "=".repeat(63) + "\n");

        // Test US14 (Eulerian Route) performance
        System.out.println("=== TESTING US14 (Eulerian Route) PERFORMANCE ===");
        testUS14Performance(scenarioFiles);
    }

    private static int extractNumberFromFilename(String filename) {
        // Extract the first number found in the filename
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d+");
        java.util.regex.Matcher matcher = pattern.matcher(filename);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return 0; // Default if no number found
    }

    private static void testUS13Performance(List<File> scenarioFiles, List<File> stationFiles) throws IOException {
        List<PerformanceResult> us13Results = new ArrayList<>();

        for (File scenarioFile : scenarioFiles) {
            // Find matching station file
            File matchingStationFile = findMatchingStationFile(scenarioFile, stationFiles);

            if (matchingStationFile == null) {
                System.out.println("Warning: No matching station file found for " + scenarioFile.getName());
                continue;
            }

            if (!scenarioFile.exists() || !matchingStationFile.exists()) {
                System.out.println("Warning: Files not found - " + scenarioFile.getName() + " or " + matchingStationFile.getName());
                continue;
            }

            // Load data to get size information
            List<String[]> edges = loadEdges(scenarioFile);
            List<String> stations = loadStations(matchingStationFile);

            // Test multiple scenarios for US13
            String[] trainTypes = {"1", "2", "3"}; // Steam, Diesel, Electric
            String[] trainTypeNames = {"Steam", "Diesel", "Electric"};

            for (int t = 0; t < trainTypes.length; t++) {
                // Test connectivity between random station pairs
                List<Long> executionTimes = new ArrayList<>();
                int testCases = Math.min(10, Math.max(1, stations.size() / 2)); // Ensure at least 1 test case

                for (int test = 0; test < testCases; test++) {
                    // Select random source and destination from same scenario
                    String source = stations.get(test * 2 % stations.size());
                    String destination = stations.get((test * 2 + 1) % stations.size());

                    long startTime = System.nanoTime();

                    // Execute US13 core logic
                    Map<String, List<String>> graph = buildGraph(edges, trainTypes[t]);
                    boolean connected = isConnected(graph, source, destination);

                    long endTime = System.nanoTime();
                    long executionTime = endTime - startTime;
                    executionTimes.add(executionTime);
                }

                // Calculate statistics
                double avgTime = executionTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);

                us13Results.add(new PerformanceResult(
                        "US13_" + trainTypeNames[t] + "_" + stations.size(),
                        stations.size(),
                        edges.size(),
                        avgTime
                ));
            }
        }

        // Print US13 performance summary
        printPerformanceSummary("US13", us13Results);
    }

    private static File findMatchingStationFile(File scenarioFile, List<File> stationFiles) {
        String scenarioName = scenarioFile.getName();

        // Extract identifying part of scenario filename (e.g., "50", "100", etc.)
        int scenarioNumber = extractNumberFromFilename(scenarioName);

        // Find station file with matching number
        for (File stationFile : stationFiles) {
            int stationNumber = extractNumberFromFilename(stationFile.getName());
            if (stationNumber == scenarioNumber) {
                return stationFile;
            }
        }

        // If no exact match, try to find by similar naming pattern
        String scenarioBaseName = scenarioName.toLowerCase()
                .replace("scenario_", "")
                .replace("_lines.csv", "")
                .replace(".csv", "");

        for (File stationFile : stationFiles) {
            String stationName = stationFile.getName().toLowerCase();
            if (stationName.contains(scenarioBaseName)) {
                return stationFile;
            }
        }

        return null; // No matching file found
    }

    private static void testUS14Performance(List<File> scenarioFiles) throws IOException {
        List<PerformanceResult> us14Results = new ArrayList<>();

        for (File scenarioFile : scenarioFiles) {
            if (!scenarioFile.exists()) {
                System.out.println("Warning: File not found - " + scenarioFile.getName());
                continue;
            }

            List<String[]> allEdges = loadEdges(scenarioFile);

            // Test both maintenance types
            boolean[] maintenanceTypes = {false, true}; // All lines, Only electrified
            String[] typeNames = {"All Lines", "Electrified Only"};

            for (int m = 0; m < maintenanceTypes.length; m++) {
                boolean onlyElec = maintenanceTypes[m];

                long startTime = System.nanoTime();

                // Execute US14 core logic
                List<String[]> edges = new ArrayList<>();
                for (String[] e : allEdges) {
                    boolean elec = e.length > 2 && e[2].equals("1");
                    if (!onlyElec || elec) edges.add(e);
                }

                if (edges.isEmpty()) {
                    System.out.println("  " + typeNames[m] + ": No applicable lines");
                    continue;
                }

                // Build graph structure
                List<String> vertices = new ArrayList<>();
                for (String[] e : edges) {
                    if (!vertices.contains(e[0])) vertices.add(e[0]);
                    if (!vertices.contains(e[1])) vertices.add(e[1]);
                }

                List<List<Integer>> adj = new ArrayList<>();
                for (int i = 0; i < vertices.size(); i++) adj.add(new ArrayList<>());
                for (int i = 0; i < edges.size(); i++) {
                    String[] e = edges.get(i);
                    int u = vertices.indexOf(e[0]);
                    int v = vertices.indexOf(e[1]);
                    adj.get(u).add(i);
                    adj.get(v).add(i);
                }

                // Check Eulerian properties
                List<Integer> nonZero = new ArrayList<>();
                for (int i = 0; i < vertices.size(); i++) if (!adj.get(i).isEmpty()) nonZero.add(i);

                // Connectivity check
                List<Boolean> visited = new ArrayList<>();
                for (int i = 0; i < vertices.size(); i++) visited.add(false);

                if (!nonZero.isEmpty()) {
                    dfs(nonZero.get(0), adj, edges, vertices, visited);
                }

                // Count odd degree vertices
                List<Integer> odd = new ArrayList<>();
                for (int i : nonZero) if (adj.get(i).size() % 2 != 0) odd.add(i);

                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;

                us14Results.add(new PerformanceResult(
                        "US14_" + typeNames[m].replace(" ", "_") + "_" + vertices.size(),
                        vertices.size(),
                        edges.size(),
                        executionTime
                ));
            }
        }

        // Print US14 performance summary
        printPerformanceSummary("US14", us14Results);
    }

    private static void printPerformanceSummary(String usName, List<PerformanceResult> results) {
        System.out.println("=== " + usName + " PERFORMANCE SUMMARY ===");
        System.out.printf("%-25s %10s %10s %15s%n",
                "Test Case", "Vertices", "Edges", "Avg Time (ms)");
        System.out.println("-".repeat(63));

        for (PerformanceResult result : results) {
            System.out.printf("%-25s %10d %10d %15.3f%n",
                    result.testName, result.vertexCount, result.edgeCount,
                    result.avgTime / 1_000_000.0);
        }
    }

    // Helper class to store performance results
    private static class PerformanceResult {
        String testName;
        int vertexCount;
        int edgeCount;
        double avgTime;

        PerformanceResult(String testName, int vertexCount, int edgeCount,
                          double avgTime) {
            this.testName = testName;
            this.vertexCount = vertexCount;
            this.edgeCount = edgeCount;
            this.avgTime = avgTime;
        }
    }

    private static int getValidStation(List<String> vertices, List<Integer> nonZero, String prompt) {
        return getValidStation(vertices, nonZero, prompt, -1);
    }

    private static int getValidStation(List<String> vertices, List<Integer> nonZero, String prompt, int excludeStation) {
        int station;
        do {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            station = vertices.indexOf(s);

            if (station < 0) {
                System.out.println("Station not found. Choose from available stations: " + getNames(vertices, nonZero));
            } else if (!nonZero.contains(station)) {
                System.out.println("Invalid station (not in the maintenance network). Choose from: " + getNames(vertices, nonZero));
            } else if (station == excludeStation) {
                System.out.println("Start and end stations cannot be the same. Please choose a different station.");
            } else {
                break; // Valid station found
            }
            station = -1; // Reset to continue loop
        } while (true);

        return station;
    }

    private static void generateCompleteRouteVisualization(List<String> completeRoute, List<String[]> edges, List<String> vertices) throws IOException {
        // Create nodes for all stations in the complete route
        Set<String> routeStations = new HashSet<>(completeRoute);
        Map<String, MutableNode> nodeMap = new HashMap<>();

        // Color the first and last stations differently
        String firstStation = completeRoute.get(0);
        String lastStation = completeRoute.get(completeRoute.size() - 1);

        for (String station : routeStations) {
            Color nodeColor = Color.BLACK;
            if (station.equals(firstStation)) {
                nodeColor = Color.GREEN;
            } else if (station.equals(lastStation) && !station.equals(firstStation)) {
                nodeColor = Color.RED;
            }
            nodeMap.put(station, mutNode(station).add(nodeColor)
                    .add(attr("shape", "circle"))
                    .add(attr("width", "1.2"))
                    .add(attr("height", "1.2"))
                    .add(attr("fontsize", "10")));
        }

        // Build graph with better layout attributes
        MutableGraph g = mutGraph("CompleteEulerianRoute").setDirected(false)
                .graphAttrs().add(attr("layout", "neato"))
                .graphAttrs().add(attr("overlap", "false"))
                .graphAttrs().add(attr("splines", "true"))
                .graphAttrs().add(attr("sep", "2.0"))
                .graphAttrs().add(attr("esep", "1.5"))
                .graphAttrs().add(attr("mindist", "2.0"))
                .graphAttrs().add(attr("K", "3.0"))
                .add(nodeMap.values().toArray(new MutableNode[0]));

        // Add all edges that are part of the complete route with better styling
        Set<String> addedEdges = new HashSet<>(); // To avoid duplicate edges
        for (int i = 0; i < completeRoute.size() - 1; i++) {
            String from = completeRoute.get(i);
            String to = completeRoute.get(i + 1);
            String edgeKey = from.compareTo(to) < 0 ? from + "-" + to : to + "-" + from;

            if (!addedEdges.contains(edgeKey)) {
                // Find the edge between these two vertices
                for (String[] e : edges) {
                    if ((e[0].equals(from) && e[1].equals(to)) || (e[0].equals(to) && e[1].equals(from))) {
                        Color c = e[2].equals("1") ? Color.YELLOW : Color.RED;
                        g.add(nodeMap.get(from).addLink(to(nodeMap.get(to))
                                .with(c)
                                .with(attr("penwidth", "2.5"))
                                .with(attr("len", "2.0"))));
                        addedEdges.add(edgeKey);
                        break;
                    }
                }
            }
        }

        Graphviz.fromGraph(g).engine(DOT).render(Format.SVG).toFile(new File("complete-eulerian-route.svg"));
        System.out.println("Complete route graph saved as complete-eulerian-route.svg");
    }

    private static void generatePartialRouteVisualization(List<String> partialRoute, List<String[]> edges,
                                                          List<String> vertices, int start, int end) throws IOException {
        // Create nodes (only those in the partial route)
        Set<String> routeStations = new HashSet<>(partialRoute);
        Map<String, MutableNode> nodeMap = new HashMap<>();
        for (String station : routeStations) {
            Color nodeColor = Color.BLACK;
            if (station.equals(vertices.get(start))) nodeColor = Color.GREEN;
            else if (station.equals(vertices.get(end))) nodeColor = Color.RED;
            nodeMap.put(station, mutNode(station).add(nodeColor)
                    .add(attr("shape", "circle"))
                    .add(attr("width", "1.2"))
                    .add(attr("height", "1.2"))
                    .add(attr("fontsize", "10")));
        }

        // Build graph with better layout for partial route
        MutableGraph g = mutGraph("PartialEulerianRoute").setDirected(false)
                .graphAttrs().add(attr("layout", "neato"))
                .graphAttrs().add(attr("overlap", "false"))
                .graphAttrs().add(attr("splines", "true"))
                .graphAttrs().add(attr("sep", "1.8"))
                .graphAttrs().add(attr("esep", "1.2"))
                .graphAttrs().add(attr("mindist", "1.5"))
                .graphAttrs().add(attr("K", "2.5"))
                .add(nodeMap.values().toArray(new MutableNode[0]));

        // Add only edges that are part of the partial route with sequence numbering
        for (int i = 0; i < partialRoute.size() - 1; i++) {
            String from = partialRoute.get(i);
            String to = partialRoute.get(i + 1);

            // Find the edge between these two vertices
            for (String[] e : edges) {
                if ((e[0].equals(from) && e[1].equals(to)) || (e[0].equals(to) && e[1].equals(from))) {
                    Color c = e[2].equals("1") ? Color.YELLOW : Color.RED;
                    g.add(nodeMap.get(from).addLink(to(nodeMap.get(to))
                            .with(c)
                            .with(attr("penwidth", "3.0"))
                            .with(attr("len", "2.0"))
                            .with(attr("fontsize", "8"))
                            .with(attr("labelfontcolor", "blue"))));
                    break;
                }
            }
        }

        Graphviz.fromGraph(g).engine(DOT).render(Format.SVG).toFile(new File("partial-eulerian-route.svg"));
        System.out.println("Partial route graph saved as partial-eulerian-route.svg");
        System.out.println("(Green = Start station, Red = End station");
        System.out.println("(Yellow = Electrified lines, Red lines = Non-electrified)");
    }

    private static void dfs(int u, List<List<Integer>> adj, List<String[]> edges,
                            List<String> vertices, List<Boolean> visited) {
        visited.set(u, true);
        for (int ei : adj.get(u)) {
            String[] e = edges.get(ei);
            int v = vertices.indexOf(e[0].equals(vertices.get(u)) ? e[1] : e[0]);
            if (!visited.get(v)) dfs(v, adj, edges, vertices, visited);
        }
    }

    private static List<String> findEulerianRoute(int start, List<String> vertices,
                                                  List<List<Integer>> adj, List<String[]> edges) {
        int m = edges.size();
        boolean[] used = new boolean[m];
        List<String> route = new ArrayList<>();
        int u = start;
        route.add(vertices.get(u));
        for (int step = 0; step < m; step++) {
            int nextEdge = -1;
            for (int ei : adj.get(u)) if (!used[ei]) { nextEdge = ei; break; }
            if (nextEdge == -1) break;
            used[nextEdge] = true;
            String[] e = edges.get(nextEdge);
            u = vertices.indexOf(e[0].equals(vertices.get(u)) ? e[1] : e[0]);
            route.add(vertices.get(u));
        }
        return route;
    }

    private static List<String> extractPartialRoute(List<String> completeRoute, String startStation, String endStation) {
        int startIndex = -1;
        int endIndex = -1;

        // Find the first occurrence of startStation and the first occurrence of endStation after that
        for (int i = 0; i < completeRoute.size(); i++) {
            if (completeRoute.get(i).equals(startStation) && startIndex == -1) {
                startIndex = i;
            } else if (completeRoute.get(i).equals(endStation) && startIndex != -1 && endIndex == -1) {
                endIndex = i;
                break;
            }
        }

        // If we didn't find the pattern startStation -> ... -> endStation,
        // try the reverse: endStation -> ... -> startStation
        if (startIndex == -1 || endIndex == -1) {
            startIndex = -1;
            endIndex = -1;
            for (int i = 0; i < completeRoute.size(); i++) {
                if (completeRoute.get(i).equals(endStation) && startIndex == -1) {
                    startIndex = i;
                } else if (completeRoute.get(i).equals(startStation) && startIndex != -1 && endIndex == -1) {
                    endIndex = i;
                    break;
                }
            }

            // If found in reverse, extract and reverse the sublist
            if (startIndex != -1 && endIndex != -1) {
                List<String> partialRoute = new ArrayList<>(completeRoute.subList(startIndex, endIndex + 1));
                Collections.reverse(partialRoute);
                return partialRoute;
            }
        } else {
            // Found in normal order
            return new ArrayList<>(completeRoute.subList(startIndex, endIndex + 1));
        }

        return null; // Could not find a path from start to end
    }


    private static void implementUS27(File linesFile, File stationsFile) throws IOException {
        List<String[]> edges = loadEdges(linesFile);
        List<String> allStations = loadStations(stationsFile);

        System.out.println("\nShortest Route Through Specified Stations");

        System.out.println("\nAvailable stations:");
        for (int i = 0; i < allStations.size(); i++) {
            System.out.println((i + 1) + ". " + allStations.get(i));
        }

        String sourceStation = getValidStationFromList(allStations, "Enter the name of the source station: ");

        String destinationStation;
        do {
            destinationStation = getValidStationFromList(allStations, "Enter the name of the destination station: ");
            if (sourceStation.equals(destinationStation)) {
                System.out.println("Source and destination cannot be the same. Please choose a different destination.");
            }
        } while (sourceStation.equals(destinationStation));

        List<String> intermediateStations = new ArrayList<>();
        System.out.println("\n Add intermediate stations in the order you want to visit them.");

        while (true) {
            System.out.print("Enter intermediate station (or 'done' to finish): ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("done") || input.isEmpty()) {
                break;
            }

            if (!allStations.contains(input)) {
                System.out.println("Station '" + input + "' not found. Please choose from the available stations.");
                continue;
            }
            if (input.equals(sourceStation)) {
                System.out.println("This is already your source station. Please choose a different station.");
                continue;
            }

            if (input.equals(destinationStation)) {
                System.out.println("This is already your destination station. Please choose a different station.");
                continue;
            }
            if (intermediateStations.contains(input)) {
                System.out.println("Station '" + input + "' is already in your intermediate stations list.");
                continue;
            }

            intermediateStations.add(input);
            System.out.println("Added '" + input + "' to intermediate stations.");
        }
        List<String> routeSequence = new ArrayList<>();
        routeSequence.add(sourceStation);
        routeSequence.addAll(intermediateStations);
        routeSequence.add(destinationStation);
        ShortestPathResult result = findShortestPathThroughStations(edges, allStations, routeSequence);

        if (result == null || result.path.isEmpty()) {
            System.out.println("No valid route found through the specified stations.");
            return;
        }
        System.out.println("\nShortest Route Found:");
        System.out.println("Complete path: " + String.join(" -> ", result.path));
        generateShortestRouteVisualization(result.path, edges, allStations, routeSequence);
    }

    private static ShortestPathResult findShortestPathThroughStations(List<String[]> edges,
                                                                      List<String> allStations,
                                                                      List<String> routeSequence) {
        List<String> completePath = new ArrayList<>();
        int totalDistance = 0;
        for (int i = 0; i < routeSequence.size() - 1; i++) {
            String from = routeSequence.get(i);
            String to = routeSequence.get(i + 1);

            DijkstraResult segmentResult = dijkstraShortestPath(edges, allStations, from, to);

            if (segmentResult == null || segmentResult.path.isEmpty()) {
                return null;
            }
            if (i == 0) {
                completePath.addAll(segmentResult.path);
            } else {
                for (int j = 1; j < segmentResult.path.size(); j++) {
                    completePath.add(segmentResult.path.get(j));
                }
            }

            totalDistance += segmentResult.distance;
        }

        return new ShortestPathResult(completePath, totalDistance);
    }

    private static class DijkstraResult {
        List<String> path;
        int distance;

        DijkstraResult(List<String> path, int distance) {
            this.path = path;
            this.distance = distance;
        }
    }

    private static DijkstraResult dijkstraShortestPath(List<String[]> edges, List<String> allStations,
                                                       String source, String destination) {
        Map<String, List<Edge>> graph = new HashMap<>();
        for (String station : allStations) {
            graph.put(station, new ArrayList<>());
        }

        for (String[] edge : edges) {
            String from = edge[0];
            String to = edge[1];
            int weight = Integer.parseInt(edge[3]);

            graph.get(from).add(new Edge(to, weight));
            graph.get(to).add(new Edge(from, weight));
        }

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> unvisited = new HashSet<>();

        for (String station : allStations) {
            distances.put(station, Integer.MAX_VALUE);
            unvisited.add(station);
        }
        distances.put(source, 0);

        while (!unvisited.isEmpty()) {
            String current = null;
            int minDistance = Integer.MAX_VALUE;
            for (String station : unvisited) {
                if (distances.get(station) < minDistance) {
                    minDistance = distances.get(station);
                    current = station;
                }
            }

            if (current == null || distances.get(current) == Integer.MAX_VALUE) {
                break;
            }

            unvisited.remove(current);

            if (current.equals(destination)) {
                break;
            }

            for (Edge edge : graph.get(current)) {
                if (unvisited.contains(edge.to)) {
                    int newDistance = distances.get(current) + edge.weight;
                    if (newDistance < distances.get(edge.to)) {
                        distances.put(edge.to, newDistance);
                        previous.put(edge.to, current);
                    }
                }
            }
        }

        List<String> path = new ArrayList<>();
        String current = destination;

        if (!previous.containsKey(destination) && !source.equals(destination)) {
            return null;
        }

        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }

        return new DijkstraResult(path, distances.get(destination));
    }

    private static class Edge {
        String to;
        int weight;

        Edge(String to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }


    private static void generateShortestRouteVisualization(List<String> shortestPath, List<String[]> edges,
                                                           List<String> allStations, List<String> routeSequence) throws IOException {
        Map<String, MutableNode> nodeMap = new HashMap<>();

        // Create nodes with different colors and shapes for different types of stations
        for (String station : shortestPath) {
            Color nodeColor = Color.BLACK;
            String shape = "circle";
            double size = 1.2;

            if (station.equals(routeSequence.get(0))) {
                nodeColor = Color.GREEN;
                size = 1.5;
                shape = "doublecircle";
            } else if (station.equals(routeSequence.get(routeSequence.size() - 1))) {
                nodeColor = Color.RED;
                size = 1.5;
                shape = "doublecircle";
            } else if (routeSequence.contains(station)) {
                // Intermediate stations
                nodeColor = Color.BLUE;
                size = 1.3;
                shape = "diamond";
            }

            nodeMap.put(station, mutNode(station).add(nodeColor)
                    .add(attr("shape", shape))
                    .add(attr("width", String.valueOf(size)))
                    .add(attr("height", String.valueOf(size)))
                    .add(attr("fontsize", "10"))
                    .add(attr("fontname", "Arial Bold")));
        }

        // Create directed graph for better path visualization
        MutableGraph g = mutGraph("ShortestRouteVisualization").setDirected(true)
                .graphAttrs().add(attr("layout", "dot"))
                .graphAttrs().add(attr("rankdir", "TB"))
                .graphAttrs().add(attr("overlap", "false"))
                .graphAttrs().add(attr("splines", "true"))
                .graphAttrs().add(attr("sep", "2.0"))
                .graphAttrs().add(attr("nodesep", "1.5"))
                .graphAttrs().add(attr("ranksep", "2.0"))
                .add(nodeMap.values().toArray(new MutableNode[0]));

        // Add edges with sequence numbers and directions
        Map<String, Integer> edgeCount = new HashMap<>(); // Track multiple edges between same nodes

        for (int i = 0; i < shortestPath.size() - 1; i++) {
            String from = shortestPath.get(i);
            String to = shortestPath.get(i + 1);

            // Create unique edge key for tracking multiple edges
            String edgeKey = from + "->" + to;
            int edgeNumber = edgeCount.getOrDefault(edgeKey, 0) + 1;
            edgeCount.put(edgeKey, edgeNumber);

            // Find the corresponding edge in the original data to get type and weight
            String edgeType = "0"; // default
            String weight = "1";   // default

            for (String[] edge : edges) {
                if ((edge[0].equals(from) && edge[1].equals(to)) ||
                        (edge[0].equals(to) && edge[1].equals(from))) {
                    edgeType = edge[2];
                    weight = edge[3];
                    break;
                }
            }

            // Color based on electrification
            Color edgeColor = edgeType.equals("1") ? Color.YELLOW : Color.RED;

            // Create label with step number and weight
            String edgeLabel = String.format("%d", i + 1);

            // Add different styling if this is a repeated edge
            String penwidth = edgeNumber > 1 ? "4.0" : "3.0";
            String style = edgeNumber > 1 ? "dashed" : "solid";

            g.add(nodeMap.get(from).addLink(to(nodeMap.get(to))
                    .with(edgeColor)
                    .with(attr("penwidth", penwidth))
                    .with(attr("style", style))
                    .with(attr("label", edgeLabel))
                    .with(attr("fontsize", "12"))
                    .with(attr("fontcolor", "black"))
                    .with(attr("fontname", "Arial Bold"))
                    .with(attr("labelfontcolor", "black"))
                    .with(attr("arrowsize", "1.2"))
                    .with(attr("arrowhead", "normal"))));
        }

        Graphviz.fromGraph(g).engine(DOT).render(Format.SVG).toFile(new File("shortest-route.svg"));
        System.out.println("\nGraph saved as shortest-route.svg");
    }

    private static String getValidStationFromList(List<String> stations, String prompt) {
        String station;
        do {
            System.out.print(prompt);
            station = sc.nextLine().trim();

            if (!stations.contains(station)) {
                System.out.println("Station '" + station + "' not found. Please choose from the available stations listed above.");
                station = null;
            }
        } while (station == null);

        return station;
    }

    private static List<String> loadStations(File stationsFile) throws FileNotFoundException {
        Scanner scf = new Scanner(stationsFile);
        List<String> stations = new ArrayList<>();

        if (scf.hasNextLine()) {
            String line = scf.nextLine().trim();
            String[] stationArray = line.split(";");
            for (String station : stationArray) {
                stations.add(station.trim());
            }
        }

        scf.close();
        return stations;
    }

    private static class ShortestPathResult {
        List<String> path;
        int totalDistance;

        ShortestPathResult(List<String> path, int totalDistance) {
            this.path = path;
            this.totalDistance = totalDistance;
        }
    }

    private static List<String[]> loadEdges(File f) throws FileNotFoundException {
        Scanner scf = new Scanner(f);
        List<String[]> edges = new ArrayList<>();
        while (scf.hasNextLine()) {
            String line = scf.nextLine().trim();
            if (!line.isEmpty()) edges.add(line.split(";"));
        }
        scf.close();
        return edges;
    }

    private static String askFileName(Scanner sc, File[] files, String prompt) {
        String name;
        boolean found;
        do {
            System.out.print(prompt);
            name = sc.nextLine().trim();
            found = false;
            for (File f : files) if (f.getName().equals(name)) { found = true; break; }
            if (!found) System.out.println("File not found. Please enter one of the listed names.");
        } while (!found);
        return name;
    }

    private static String getNames(List<String> vertices, List<Integer> idxs) {
        List<String> names = new ArrayList<>();
        for (int i : idxs) names.add(vertices.get(i));
        return names.toString();
    }
}