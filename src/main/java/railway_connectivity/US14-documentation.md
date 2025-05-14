# US14 - Documentation (1241229 - 1241234)

The main objective of this document is to explain how the work was developed in order to create the US14. The program can be found in this Java file `main` in the `railway_connectivity` package. The User Case 14 consists in finding an Eulerian circuit (a path that travels each railway line exactly once) given a set of stations and connections.

---

## Table of Contents

1. [Overview](#overview)
2. [Code Structure](#code-structure)
3. [Execution Flow](#exectution-flow)
4. [Conclusion](#conclusion)

---

## Overview

The purpose of this User Story is to determine an **Eulerian circuit**—a cycle that goes through every edge exactly once—in an undirected graph representing railway stations (vertices) and lines (edges). It uses a simplified Fleury’s algorithm: repeatedly remove the first available edge until none remain.

---

## Code Structure

1. **main**
    - Scans the current directory for `.csv` files;
    - Prompts the user to select both the lines and stations file;
    - Displays the menu;

2. **implementUS14(File linesF)**
    - Loads all the edges via function `loadEdges`
    - Allows to choose the maintenance of All the Lines or only Electrified ones
    - Builds adjacency map
    - Checks if every vertex has an even degree
    - Runs a simplified version of the Fleury Algorithm: removes the first edge until none remain
    - Does the graphical representation of the circuit, using Graphviz

3. **verifyConnected(String v, List vertices, List<List> adjLists, List vis, List<String[]> edges)**
    - Ensures that the graph is a connected one

4. **loadEdges(File f)**
    - Reads each `from;to;flag` line present on the `.csv` files into a `String[]`

5. **askFileName(...)**
    - Validates the user input regarding the name of the file that is going to be used as an input

---

## Exectution Flow

1. Lists all the `.csv` files present the directory
2. User selects the `.csv` we wants to use as an input
3. User chooses maintenance type (All Lines or Only Electrified)
4. The graph is built: each station maps to a list of stations that are near them
5. Verifies if all vertices have an even degree (necessary for an Eulerian circuit)
6. verifyConnected is called to ensure the graph is connected
7. Chooses any starting station with edges
8. For each remaining edge:
    1. Selects the first neighbor in the adjacency list
    2. Removes that edge from both lists
    3. Moves to the neighbor station
9. Does the graphical representation of the circuit, using Graphviz  

---

## Conclusion

To conclude, our code successfully implements a simplified version of Fleury’s algorithm to find an Eulerian circuit, given the lines of the railway system.

