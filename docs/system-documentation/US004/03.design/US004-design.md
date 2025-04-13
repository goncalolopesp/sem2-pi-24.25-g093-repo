# US004 - Create a Scenario

## 3. Design

### 3.1. Rationale

**The rationale grounds on the SSD interactions and the identified input/output data.**

| Interaction ID                              | Question: Which class is responsible for...                                                                                                                                                                                                                                                                   | Answer                   | Justification (with patterns)        |
|:--------------------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-------------------------|:-------------------------------------|
| Step 1 - Asks to create a new scenario		    | ...instantiating the class that handles the UI?						                                                                                                                                                                                                                                                         | CreateScenarioUI         | Pure Fabrication                     |
| 		                                          |... coordinating the US?							 | CreateScenarioController | Controller                           |
| Step 2 - Requests user data		               | ...displaying the form for the actor to input data?						                                                                                                                                                                                                                                                     | CreateScenarioUI         | Pure Fabrication                     |
| Step 3 - Types/selects user data  		        | ...validating typed/selected data?| CreateScenarioUI         | Pure Fabrication                     |
| 		                                          |...temporarily keeping the data?							 | CreateScenarioUI         | Pure Fabrication                     |
| 		                                          |...get instances of repositories?							 | Repositories             | Information Expert, Pure Fabrication |
| 		                                          |...get scenario repository?							 | Repositories             | Information Expert, Pure Fabrication |
| 		                                          |...creating the Scenario Object?							 | ScenarioRepository       | Creator                              |
| 		                                          |...validating the data locally?							 | Scenario                 | Information Expert                   |
| 		                                          |...return the scenario's data?							 | ScenarioRepository       | Information Expert, Pure Fabrication |
| 		                                          |...displaying operation success message?							 | CreateScenarioUI         | Pure Fabrication                     |

### Systematization ##

According to the taken rationale, the conceptual classes promoted to software classes are:

* Repositories
* ScenarioRepository
* Scenario

Other software classes (i.e. Pure Fabrication) identified:

* CreateScenarioUI
* CreateScenarioController

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

## 3.3. Class Diagram (CD)

_In this section, it is suggested to present an UML static view representing the main related software classes that are involved in fulfilling the requirements as well as their relations, attributes and methods._

![US004-CD](svg/US004-CD.svg)