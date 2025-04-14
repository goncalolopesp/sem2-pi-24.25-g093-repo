# US006 - Upgrade a Station

## 3. Design

### 3.1. Rationale

**The rationale grounds on the SSD interactions and the identified input/output data.**

| Interaction ID                              | Question: Which class is responsible for...                                                                                                                                                                                                                                                                    | Answer                   | Justification (with patterns)        |
|:--------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-------------------------|:-------------------------------------|
| Step 1 - Requests to upgrade a station      | ...instantiating the class that handles the UI?                                                                                                                                                                                                                                                                | UpgradeStationUI         | Pure Fabrication                     |
|                                               | ...coordinating the US?                              | UpgradeStationController  | Controller                           |
| Step 2 - Selects station and upgrade option | ...displaying form options to upgrade?                                                                                                                                                                                                                                  | UpgradeStationUI         | Pure Fabrication                     |
|                                               | ...validating selected building?                    | UpgradeStationUI         | Pure Fabrication                     |
|                                               | ...loading available building types?               | BuildingRepository        | Information Expert                   |
|                                               | ...checking building restrictions?                 | Building                 | Information Expert                   |
|                                               | ...ensuring building does not conflict (exclusivity)? | Station                | Information Expert                   |
|                                               | ...applying the upgrade?                            | StationRepository         | Creator                              |
|                                               | ...displaying success message?                      | UpgradeStationUI         | Pure Fabrication                     |

### Systematization ##

According to the taken rationale, the conceptual classes promoted to software classes are:

* Repositories
* StationRepository
* BuildingRepository
* Station
* Building

Other software classes (i.e. Pure Fabrication) identified:

* UpgradeStationUI
* UpgradeStationController


## 3.2. Sequence Diagram (SD)

_In this section, it is suggested to present an UML dynamic view representing the sequence of interactions between software objects that allows to fulfill the requirements._

### Full Diagram

This diagram shows the full sequence of interactions between the classes involved in the realization of this user story.

![US004-SD](svg/US004-SD.svg)

### Split Diagrams

The following diagram shows the same sequence of interactions between the classes involved in the realization of this user story, but it is split in partial diagrams to better illustrate the interactions between the classes.

It uses Interaction Occurrence (a.k.a. Interaction Use).

**Get Input Partial SD**

![Sequence Diagram - Partial - Get Input](svg/US004-SD-getInput.svg)

**Create Scenario Partial SD**

![Sequence Diagram - Partial - Create Scenario](svg/US004-SD-createScenario.svg)

**Verify if Scenario exists Partial SD**

![Sequence Diagram - Partial - Create Scenario](svg/US004-SD-verify.svg)

**Display Success Message Partial SD**

![Sequence Diagram - Partial - Create Scenario](svg/US004-SD-displaySuccess.svg)

## 3.3. Class Diagram (CD)

_In this section, it is suggested to present an UML static view representing the main related software classes that are involved in fulfilling the requirements as well as their relations, attributes and methods._

![US004-CD](svg/US004-CD.svg)