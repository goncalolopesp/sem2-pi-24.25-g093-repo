# US05 - Build a Station

## 1. Requirements Engineering

### 1.1. User Story Description

As a Player, I want to build a station (Depot, Station or Terminal) in the current map. The system should
also propose the name for the station based on the closest city and type of station. Not only that, if the station's type if either Depot or Terminal, the center of the station should be defined by the player (NE,SE,NW,SW).

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

> The player should be able to build a station at any location on the map (the location should be validated beforehand). Overbuilding is not allowed (a station cannot be built on top of another station). The station's type have different costs and economic radius. Stations should also be able to connect to other stations by routes.

**From the customer's clarifications:**
> - Overbuilding in the Editor is possible, the existing building is replaced for a new one.
> - Overbuilding in the Simulator isn't possible, a warning needs to appear and the player continues to play.
> - The only types of stations the player can build are depots, stations and terminal, nothing more.
> - The budget for the building option represents just a generic currency.

### 1.3. Acceptance Criteria
A.C 1 : Overbuilding is not possible.

### 1.4. Found out Dependencies

* This User Story has a dependency with US01 "Create a Map" because it is necessary for a map to exist for there to be a station.

### 1.5 Input and Output Data

**Input Data:**

- The coordinates in which the station must be built (x,y);
- Information about which type of station must be built (between Depots, Stations and Terminals);
- The definition of the center (if the station is either a Depot or a Terminal);

**Output Data:**

- A success notification saying that the station was built;
- An error notification if the location was invalid, or there isn't enought budget to buy;
- Update the map with the newly placed station;
- A system-generated name for the newly built station, based on its type and nearest city name;

### 1.6. System Sequence Diagram (SSD)


![US005-SSD](svg/US005-SSD.svg)

### 1.7 Other Relevant Remarks
- The map should offer real-time feedback regarding the 'building station' option;
- The software should correctly generate the station's name;