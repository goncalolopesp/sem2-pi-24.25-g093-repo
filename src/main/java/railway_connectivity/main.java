package railway_connectivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class main {
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) throws FileNotFoundException {
        // Searches all CSV files in the current directory
        String csvDir = (args.length > 0 ? args[0] : ".");
        File cwd = new File(csvDir);
        System.out.println("Trying to find .csv files in: " + cwd.getAbsolutePath());

        // Verifies the existance of the directory and if it's a directory
        if (!cwd.exists() || !cwd.isDirectory()) {
            System.err.println("Directory do not exist or isn't a valid directory: " + cwd.getAbsolutePath());
            System.exit(1);
        }

        // Lists all the files in the directory and verifies if they are CSV files
        File[] csvFiles = cwd.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (csvFiles == null || csvFiles.length < 2) {
            System.err.println("There needs to be at least 2 .csv here (1 for stations,1 for lines): " + cwd.getAbsolutePath());
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
            System.err.println("Um dos ficheiros selecionados não existe. Saindo...");
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
                System.out.println("Implementar aqui US13");
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

    private static void implementUS14(File linesF) throws FileNotFoundException {

        try{
            //edges will store the lines in order from csv file
            //edges.get(i)[0] - will store the station that represents the "from"
            //edges.get(i)[1] - will store the station that represents the "to"
            //edges.get(i)[2] - will store the type of the line (1 - if the line is electrified ; 0 - not electrified)
            List<String[]> allEdges = loadEdges(linesF);

            //choice of the type of maintenance
            System.out.println("Select maintenance type:");
            System.out.println("1. All The Lines");
            System.out.println("2. Only Electrified Lines ");
            System.out.print("Maintenance type: ");
            String answer = sc.nextLine().trim();
            boolean onlyElec = answer.equals("2");

            //Filter the lines according to the type of maintenance
            List<String[]> edges = new ArrayList<>();
            for (int i = 0; i < allEdges.size(); i++) {
                String[] e = allEdges.get(i);
                boolean elec = e[2].equals("1");
                if (!onlyElec || elec) {
                    edges.add(e);
                }
            }
            if (edges.size() == 0) {
                System.out.println("There are no lines that meet the requirements.");
                return;
            }

            int size = edges.size();
            boolean[] used = new boolean[size];

            //Using vertices (vertices is, in a short way, the stations) list + adjacency lists
            List<String> vertices = new ArrayList<>();
            //Gathers the unique stations on the edges
            for(int i = 0; i < size; i++){
                String from = edges.get(i)[0];
                String to = edges.get(i)[1];
                //Adds the "from" station if she isn't on the list
                boolean hasFrom = false;
                for (int j = 0; j < vertices.size(); j++){
                    if(vertices.get(j).equals(from)){
                        hasFrom = true;
                        break;
                    }
                }
                if(!hasFrom){
                    vertices.add(from);
                }
                //Adds the "to" station if she isn't on the list'
                boolean hasTo = false;
                for(int j = 0; j<vertices.size();j++){
                    if(vertices.get(j).equals(to)){
                        hasTo = true;
                        break;
                    }
                }
                if(!hasTo){
                    vertices.add(to);
                }

            }

            //Each index 'i' in vertices.get(i) corresponds to a sub-list List<Integer> in adj.Lists.get(i)
            //That sub-list List<Integer> contains the indexes of the edges that connect with that station
            //Since there is "vertices", vertices.get(fi) is the name of the station on the position 'fi'
            //and so we are able to add the edges of a certain station


            List<List<Integer>> adjLists = new ArrayList<>();
            for (int i = 0; i < vertices.size(); i++) {
                adjLists.add(new ArrayList<>());
            }
            for (int i = 0; i < size; i++) {
                String f = edges.get(i)[0];
                String t = edges.get(i)[1];
                int fi = -1, ti = -1;
                //Searches the indexes of the stations in the vertices list
                for (int j = 0; j < vertices.size(); j++) {
                    if (vertices.get(j).equals(f)) {
                        fi = j;
                    }
                    if (vertices.get(j).equals(t)){
                        ti = j;
                    }
                }
                adjLists.get(fi).add(i);
                adjLists.get(ti).add(i);
            }

            //Find the vertices(stations) that have at least one edge (connection)
            List<String> nonZero = new ArrayList<>();
            for (int i = 0; i < vertices.size(); i++) {
                if (!adjLists.get(i).isEmpty()) {
                    nonZero.add(vertices.get(i));
                }
            }

            //Verify if the graph is connected
            List<String> vis = new ArrayList<>();
            verifyConnected(nonZero.get(0), vertices, adjLists, vis, edges);
            if (vis.size() != nonZero.size()) {
                System.out.println("Graph is disconnected. No Eulerian route exists.");
                return;
            }

            //Counts how many vertices have an odd degree
            //If a graph has 0 vertices with odd degree - Eulerian Circuit (Starts and finishes in the same point)
            //If a graph has 2 vertices with odd degree - Eulerian Cycle (Starts on a odd degree vertex and finishes in another)
            List<String> odd = new ArrayList<>();
            for (int i = 0; i < nonZero.size(); i++) {
                String v = nonZero.get(i);
                int vId = -1;
                for (int j = 0; j < vertices.size(); j++){
                    if (vertices.get(j).equals(v)) {
                        vId = j;
                        break;
                    }
                }
                if (adjLists.get(vId).size() % 2 != 0) {
                    odd.add(v);
                }
            }
            if (odd.size() != 0 && odd.size() != 2) {
                System.out.println("No Eulerian route (" + odd.size() + " odd-degree vertices).");
                return;
            }

            //Determines all the valid start stations
            List<String> starts;
            if (odd.size() == 2) {
                starts = odd;
                System.out.println("Stations which the maintenance can start: " + starts);
            } else {
                starts = nonZero;
                System.out.println("Any station with edges is a valid start to the maintenance " + starts);
            }

            //Ask the user to select a start station
            String startStation;
            boolean validStart;

            do {
                System.out.print("Enter start station from list: ");
                startStation = sc.nextLine().trim();
                int index = 0;
                while (index < starts.size() && !starts.get(index).equals(startStation)) {
                    index++;
                }
                validStart = index < starts.size();
                if (!validStart) {
                    System.out.println("Invalid start. Choose from: " + starts);
                }
            } while (!validStart);

            //Prepares the function 'findEulerianRoute', which will try to find a Euliran Route
            List<String> eurelianRoute = findEulerianRoute(startStation, vertices, adjLists, edges);
            if (eurelianRoute == null){
                System.out.println("Failed to find a route.");
            }
            else {
                System.out.println("Maintenance route (start -> finish):");
                for (int i = 0; i < eurelianRoute.size(); i++) {
                    System.out.print(eurelianRoute.get(i));
                    if (i < eurelianRoute.size()-1) System.out.print(" -> ");
                }
                System.out.println();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    //Function that finds an Eulerian Route, given a start and finishing point, all the vertices, adjacency lists and edges
    private static List<String> findEulerianRoute(String start, List<String> vertices, List<List<Integer>> adjLists, List<String[]> edges) {
        int m = edges.size();
        boolean[] used = new boolean[m];

        List<String> route = new ArrayList<>();
        route.add(start);
        String current = start;
        int step = 0;

        while (step < m) {
            // Index of the current vertex
            int vId = 0;
            while (!vertices.get(vId).equals(current)) {
                vId++;
            }

            // Select all candidate edges
            List<Integer> cand = new ArrayList<>();
            for (int eIdx : adjLists.get(vId)) {
                if (!used[eIdx]) {
                    cand.add(eIdx);
                }
            }

            int chosen;
            if (cand.size() == 1) {
                chosen = cand.get(0);
            } else {
                // Count the number of nodes that are visited befora removing the edge
                boolean[] visited = new boolean[vertices.size()];
                List<Integer> stackNodes = new ArrayList<>();
                stackNodes.add(vId);
                int countBefore = 0;
                while (!stackNodes.isEmpty()) {
                    int u = stackNodes.remove(stackNodes.size() - 1);
                    if (visited[u]) continue;
                    visited[u] = true;
                    countBefore++;
                    for (int e : adjLists.get(u)) {
                        if (!used[e]) {
                            String[] ed = edges.get(e);
                            int w = ed[0].equals(vertices.get(u))
                                    ? vertices.indexOf(ed[1])
                                    : vertices.indexOf(ed[0]);
                            if (!visited[w]) {
                                stackNodes.add(w);
                            }
                        }
                    }
                }

                // Selecting the edge that doesn't disconnect the graph
                chosen = -1;
                for (int eTest : cand) {
                    used[eTest] = true;
                    boolean[] visited2 = new boolean[vertices.size()];
                    List<Integer> stack2 = new ArrayList<>();
                    stack2.add(vId);
                    int countAfter = 0;
                    while (!stack2.isEmpty()) {
                        int u2 = stack2.remove(stack2.size() - 1);
                        if (visited2[u2]) continue;
                        visited2[u2] = true;
                        countAfter++;
                        for (int e2 : adjLists.get(u2)) {
                            if (!used[e2]) {
                                String[] ed2 = edges.get(e2);
                                int w2 = ed2[0].equals(vertices.get(u2))
                                        ? vertices.indexOf(ed2[1])
                                        : vertices.indexOf(ed2[0]);
                                if (!visited2[w2]) {
                                    stack2.add(w2);
                                }
                            }
                        }
                    }
                    used[eTest] = false;
                    if (countAfter == countBefore) {
                        chosen = eTest;
                        break;
                    }
                }
                if (chosen == -1) {
                    chosen = cand.get(0);
                }
            }

            // Using the selected edge
            used[chosen] = true;
            String[] ed = edges.get(chosen);
            current = ed[0].equals(current) ? ed[1] : ed[0];
            route.add(current);
            step++;
        }

        return route;
    }

    //Function that verifies if a graph is connected
    private static void verifyConnected(String v, List<String> vertices, List<List<Integer>> adjLists, List<String> vis, List<String[]> edges) {
        // Adds vertex to visited list
        vis.add(v);

        // Finds index of current vertex
        int idxV = 0;
        while (idxV < vertices.size() && !vertices.get(idxV).equals(v)) {
            idxV++;
        }

        // Get list of edge indices for this vertex
        List<Integer> list = adjLists.get(idxV);

        // Search them using a loop
        int i = 0;
        while (i < list.size()) {
            int eIdx = list.get(i);
            String[] e = edges.get(eIdx);

            // Determine the other vertex
            String w;
            if (e[0].equals(v)) w = e[1];
            else w = e[0];

            // Check if w was visited
            int j = 0;
            while (j < vis.size() && !vis.get(j).equals(w)) {
                j++;
            }
            boolean wasVis = j < vis.size();

            // Recall the same function if w wasn't visited
            if (!wasVis) {
                verifyConnected(w, vertices, adjLists, vis, edges);
            }
            i++;
        }
    }

    // Method that loads the lines from the CSV file
    private static List<String[]> loadEdges(File f) throws FileNotFoundException {
        Scanner sc = new Scanner(f);
        List<String[]> edges = new ArrayList<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.length() == 0) continue;
            String[] p = line.split(";");
            edges.add(new String[]{p[0], p[1], p[2]});
        }
        sc.close();
        return edges;
    }

    // Method that creates the questions and gets the user's response
    private static String askFileName(Scanner sc, File[] files, String prompt) {
        String name;
        boolean found;
        do {
            System.out.print(prompt);
            name = sc.nextLine().trim();
            int idx = 0;
            while (idx < files.length && !files[idx].getName().equals(name)) {
                idx++;
            }
            found = idx < files.length;
            if (!found) {
                System.out.println("File not found. Please enter one of the listed names.");
            }
        } while (!found);

        return name;
    }


}