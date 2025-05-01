# US012 - Create a simulation

## 3. Design

### 3.1. Rationale

**The rationale grounds on the SSD interactions and the identified input/output data.**

| Interaction ID                           | Question: Which class is responsible for...                | Answer                   | Justification (with patterns)        |
|:-----------------------------------------|:-----------------------------------------------------------|:-------------------------|:-------------------------------------|
| Step 1 - Asks to assign train to route		 | ...instantiating the class that handles the UI?							     | SimulationUI             | Pure Fabrication                     |
| 		                                       | ...coordinating the US?							                             | SimulationController    | Controller                           |
| Step 2 - Requests user data  		          | ...displaying the form for the actor to input data?							 | SimulationUI            | Pure Fabrication                     |
| Step 3 - Types/selects user data  		                               | ...validating typed/selected data?							                  | SimulationUI            | Pure Fabrication                     |
| 		                               | ...temporarily keeping the data?							                    | SimulationUI            | Pure Fabrication                     |
|  		                               | ...getting instances of repositories?							               | Repositories             | Information Expert, Pure Fabrication |              
|   		                               | ...get simulation repository?							                       | Repositories             | Information Expert, Pure Fabrication |
|   		                               | ...creating the simulation Object?							                  | SimulationRepository | Creator                              |
|  		                               | ...validating the data locally?							                     | Simulation            | Information Expert                   |
|  		                              | ...return the simulation's data?							                    | SimulationRepository | Information Expert, Pure Fabrication |  
|  		                              | ...displaying operation success message?							            | SimulationUI            | Pure Fabrication |  

### Systematization ##

According to the taken rationale, the conceptual classes promoted to software classes are:

* Repositories
* SimulationRepository
* Simulation

Other software classes (i.e. Pure Fabrication) identified:

* SimulationUI
* SimulationController

## 3.2. Sequence Diagram (SD)

_In this section, it is suggested to present an UML dynamic view representing the sequence of interactions between software objects that allows to fulfill the requirements._

### Full Diagram

This diagram shows the full sequence of interactions between the classes involved in the realization of this user story.

![US012-SD](svg/US012-SD.svg)

### Split Diagrams

The following diagram shows the same sequence of interactions between the classes involved in the realization of this user story, but it is split in partial diagrams to better illustrate the interactions between the classes.

It uses Interaction Occurrence (a.k.a. Interaction Use).

**Get Input Partial SD**

![Sequence Diagram - Partial - Get Input](svg/US012-SD-getInput.svg)

**Start Simulation Partial SD**

![Sequence Diagram - Partial - Start Simulation](svg/US012-SD-startSimulation.svg)

**Verify if simulation exists Partial SD**

![Sequence Diagram - Partial - Verification](svg/US012-SD-verify.svg)

**Display Success Message Partial SD**

![Sequence Diagram - Partial - Display Success](svg/US012-SD-displaySuccess.svg)

## 3.3. Class Diagram (CD)

_In this section, it is suggested to present an UML static view representing the main related software classes that are involved in fulfilling the requirements as well as their relations, attributes and methods._

![US012-CD](svg/US012-CD.svg)