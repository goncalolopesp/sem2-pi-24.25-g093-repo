# US13 - Documentation (1222123 - 1241232)

The objective of this document is to explain how the US13 was created. The source code can be found in the `main` file in the `railway_connectivity` package. The US13 is based on a Constrained Pathfinding algorithm (A way to find a path between two nodes, but only if certain conditions are met).

---

## Table of Contents

1. [Overview](#overview)
2. [Code Structure](#code-structure)
3. [Execution Flow](#execution-flow)
4. [Conclusion](#conclusion)

---

## Overview

This User Story's purpose is to determine paths between 2 stations of the same type.

---

## Code Structure

1. **main**
   - Scans the current directory for `.csv` files;
   - Prompts the user to select both the lines and stations file;
   - Displays the menu;

2. **implementUS13(File linesFile)**
   - Loads all edges via function `loadEdges()`
   - Prompts the user as to what type of locomotive the code is going to verify
   - Prompts the user about the source and destination stations
   - Verifies if the stations belong to the same category
   - Builds the adjacency map
   - Does the graphical representation of the map, using Graphviz
3. **buildGraph(List edges, String trainType)**
   - Checks the locomotive type
   - Creates the basis for the graph
4. **dfs(Map graph, String source, String destination, Set visited)**
   - Checks if there is a path between the source station and the destination station

---

## Execution Flow
1. Lists all the `.csv` files present in the directory
2. User selects the `.csv` files that are going to be used by the program
3. User selects the type of locomotive to be checked
4. User inputs the source station
5. User inputs the destination station
6. Graph is built
7. Verifies if a station has a reachable adjacent station
8. Does the graphical representation of the Map, using GraphViz

---

## Conclusion

To conclude, our code generates a map with all the routes between stations, given the lines of a railway system.