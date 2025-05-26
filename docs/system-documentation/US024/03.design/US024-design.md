# US023 - Save Simulation in File

## 3. Design

### 3.1. Rationale

**The rationale grounds on the SSD interactions and the identified input/output data.**

| Interaction ID | Question: Which class is responsible for...      | Answer                   | Justification (with patterns)                                                                               |
|:---------------|:-------------------------------------------------|:-------------------------|:------------------------------------------------------------------------------------------------------------|
| Step 1         | ... asking to load a simulation in file?         | SimulationUI             | **Pure Fabrication**: UI initiation is not a responsibility of any domain class. It fabricates interaction. |
|                | ... coordinating the US?                         | SimulationController     | **Controller**: Handles system operations initiated by the user (Player).                                   |
|                | ... instantiating the class that handles the UI? | SimulationUI             | **Pure Fabrication**: No domain object is responsible for UI instantiation.                                 |
| Step 2         | ... loading the simulation in the file?          | SimulationFile              | **Information Expert**: Repository has the knowledge to save simulations to files.                          |
| Step 3         | ... showing the operation success?               | SimulationUI         | **Pure Fabrication**: UI class presents success messages to the user.                                       |
| Step 4         | ... coordinating between UI and Domain for flow? | SimulationController | **Controller**: Central coordinator for user-system interaction.                                            |

### Systematization ##

According to the taken rationale, the conceptual classes promoted to software classes are:

* Simulation
* SimulationRepository
* Player

Other software classes (i.e. Pure Fabrication) identified:

* SimulationUI
* SimulationController
* Repositories
* SimulationFile

## 3.2. Sequence Diagram (SD)

![Sequence Diagram - Full](svg/US024-SD.svg)

## 3.3. Class Diagram (CD)

![Class Diagram](svg/US024-CD.svg)