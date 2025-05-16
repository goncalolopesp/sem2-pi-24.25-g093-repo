package railway_connectivity;

import guru.nidi.graphviz.attribute.Color;
import static guru.nidi.graphviz.attribute.Attributes.attr;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import static guru.nidi.graphviz.engine.Engine.DOT;
import static guru.nidi.graphviz.model.Factory.*;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        // Searches all CSV files in the current directory
        String csvDir = (args.length > 0 ? args[0] : ".");
        File cwd = new File(csvDir);
        System.out.println("Trying to find .csv files in: " + cwd.getAbsolutePath());

        // Verifies the existence of the directory and if it's a directory
        if (!cwd.exists() || !cwd.isDirectory()) {
            System.err.println("Directory does not exist or isn't a valid directory: " + cwd.getAbsolutePath());
            System.exit(1);
        }

        // Lists all the files in the directory and verifies if they are CSV files
        File[] csvFiles = cwd.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (csvFiles == null || csvFiles.length < 2) {
            System.err.println("There needs to be at least 2 .csv here (1 for stations, 1 for lines): " + cwd.getAbsolutePath());
            System.exit(1);
        }

        System.out.println("CSV Files Found:");
        for (File f : csvFiles) {
            System.out.println(" - " + f.getName());
        }

        // Ask the name of the CSV files
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
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String op = sc.nextLine();

            if (op.equals("1")) {
                implementUS13(lines);
                break;
            } else if (op.equals("2")) {
                implementUS14(lines);
                break;
            } else if (op.equals("3")) {
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

        List<String> vertices = new ArrayList<>();
        for (String[] e : edges) {
            if (!vertices.contains(e[0])) vertices.add(e[0]);
            if (!vertices.contains(e[1])) vertices.add(e[1]);
        }

        // Build adjacency list
        Map<String, List<String>> graph = buildGraph(edges, trainType);

        List<String> route = new ArrayList<>(graph.keySet());

        // Check connectivity
        boolean canTravel = isConnected(graph, source, destination);
        if (canTravel) {
            System.out.println("The train can travel between " + source + " and " + destination + ".");
        } else {
            System.out.println("The train cannot travel between " + source + " and " + destination + ".");
        }

        // Build adjacency list
        Map<String, MutableNode> nodeMap = new HashMap<>();
        for (String v : vertices) nodeMap.put(v, mutNode(v).add(Color.WHITE));
        // build graph
        MutableGraph g = mutGraph("MapaDeEstacoes").setDirected(false)
                .add(nodeMap.values().toArray(new MutableNode[0]));
        for (String[] e : edges) {
            String a = e[0], b = e[1];
            Color c = e[2].equals("1") ? Color.YELLOW : Color.RED;
            g.add(nodeMap.get(a).addLink(to(nodeMap.get(b)).with(c)));
        }
        for (int i = 0; i < route.size() - 1; i++) {
            g.add(nodeMap.get(route.get(i)).addLink(
                    to(nodeMap.get(route.get(i + 1))).with(attr("penwidth", "4"))));
        }
        Graphviz.fromGraph(g).engine(DOT).render(Format.SVG).toFile(new File("station-map.svg"));
        System.out.println("Graph saved as station-map.svg");
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
            for (int idx : nonZero) if (!visited.get(idx)) {
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

            // determine start points
            List<Integer> starts = odd.isEmpty() ? nonZero : odd;
            System.out.println((odd.isEmpty() ? "Any station with edges is a valid start: " : "Stations which the maintenance can start: ")
                    + getNames(vertices, starts));

            // ask for start
            int start;
            do {
                System.out.print("Enter start station from list: ");
                String s = sc.nextLine().trim();
                start = vertices.indexOf(s);
                if (start < 0 || !starts.contains(start))
                    System.out.println("Invalid start. Choose from: " + getNames(vertices, starts));
            } while (!starts.contains(start));

            // find Eulerian route
            List<String> route = findEulerianRoute(start, vertices, adj, edges);
            System.out.println("Maintenance route (start -> finish): " + route);

            // --- Visualization ---
            // create nodes
            Map<String, MutableNode> nodeMap = new HashMap<>();
            for (String v : vertices) nodeMap.put(v, mutNode(v).add(Color.BLACK));
            // build graph
            MutableGraph g = mutGraph("RotaEuleriana").setDirected(false)
                    .add(nodeMap.values().toArray(new MutableNode[0]));
            for (String[] e : edges) {
                String a = e[0], b = e[1];
                Color c = e[2].equals("1") ? Color.YELLOW : Color.RED;
                g.add(nodeMap.get(a).addLink(to(nodeMap.get(b)).with(c)));
            }
            for (int i = 0; i < route.size() - 1; i++) {
                g.add(nodeMap.get(route.get(i)).addLink(
                        to(nodeMap.get(route.get(i + 1))).with(attr("penwidth", "4"))));
            }
            Graphviz.fromGraph(g).engine(DOT).render(Format.SVG).toFile(new File("eulerian-route.svg"));
            System.out.println("Graph saved as eulerian-route.svg");

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
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
