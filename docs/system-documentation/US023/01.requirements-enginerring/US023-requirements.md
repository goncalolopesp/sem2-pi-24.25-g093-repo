# US023 - Save Simulation in File

## 1. Requirements Engineering

### 1.1. User Story Description

As a Player, I want to Save a Simulation (Game), in a file, for later use.

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

> The simulator will enable the manager (or, from the playful perspective, the player) to create a railway network
> composed of stations that serve cities and industries, lines connecting those stations, and trains that transport
> cargo between stations, according to a route defined by the manager. It is crucial to highlight that cities and
> industries only generate or transform cargo if they have a station; when the cargo is generated, it is then
> available to be collected at the station.
>
> In the simulator, the player/user acquires trains within the available budget and can put the train into service
> on a specific route.
>
> In addition to creating the dynamic elements already described, the player/user can play or pause the simulator.
> During the simulator’s operation, the evolution of events should be displayed, namely the trains’ journeys.

### 1.3. Acceptance Criteria

### 1.4. Found out Dependencies

* There is a dependency with US012 - "Create a Simulator", given that to save a simulation there needs to be one.

### 1.5 Input and Output Data

**Output Data:**
* **Confirmation of operation:**
    - A message confirming that the simulation has been saved successfully.

### 1.6. System Sequence Diagram (SSD)

![System Sequence Diagram](svg/US023-SSD.svg)

### 1.7 Other Relevant Remarks

* No other relevant remarks 