# US004 - Create a Scenario

## 3. Design

### 3.1. Rationale

**The rationale grounds on the SSD interactions and the identified input/output data.**

| Interaction ID                           | Question: Which class is responsible for...                                                                                                                                                                                                                                                                   | Answer                                                                                                                                                            | Justification (with patterns)                                                                                |
|:-----------------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-------------------------------------------------------------------------------------------------------------|
| Step 1 - Asks to create a new scenario		 | ...instantiating the class that handles the UI?						                                                                                                                                                                                                                                                         | CreateScenarioUI                                                                                                                                                  | Pure Fabrication                                                                                             |
| Step 2 - Requests user data		            | ...displaying the form for the actor to input data?						                                                                                                                                                                                                                                                     | CreateScenarioUI                                                                                                                                                  | Pure Fabrication                                                                                             |
| Step 3 - Types/selects user data  		     | ...validating typed/selected data?<br/>...temporarily keeping the data?<br/>...get instances of repositories?<br/>...get scenario repository?<br/>...creating the Scenario Object?<br/>...validating the data locally?<br/>...return the scenario's data?<br/>...displaying operation success message?							 | CreateScenarioUI<br/>CreateScenarioUI<br/>Repositories<br/>Repositories<br/>ScenarioRepository<br/>Scenario<br/>ScenarioRepository<br/>CreateScenarioUI | Pure Fabrication<br/>Pure Fabrication<br/><br/><br/>Creator<br/>Information Expert<br/><br/>Pure Fabrication |

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

![USXXX-SD](svg/USXXX-SD.svg)

## 3.3. Class Diagram (CD)

_In this section, it is suggested to present an UML static view representing the main related software classes that are involved in fulfilling the requirements as well as their relations, attributes and methods._

![USXXX-CD](svg/USXXX-CD.svg)