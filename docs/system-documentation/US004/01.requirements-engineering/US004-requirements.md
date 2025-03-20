# US004 - Create a Scenario

## 1. Requirements Engineering

### 1.1. User Story Description
As an Editor, I want to create a scenario for a selected map.

### 1.2. Customer Specifications and Clarifications

#### From the specifications document:
> The Scenario Editor must allow the user to define the restrictions and
conditions for using a given map, namely:
>
>• Time Restrictions - the period in which the simulation will take place;
>
>• Technological Restrictions - for example, which locomotives or indus-
tries are available in the scenario;
>
>• Historical Restrictions - historical events that make sense in the sce-
nario and change the operating conditions of the simulator. For in-
stance, a war increases the steel demand, and a vaccination campaign
or a schooling program encourages mail and/or passenger traffic. In
this context, the goods that the Ports import and export are also de-
fined.

### 1.3. Acceptance Criteria
- In this version there is no acceptance criteria.

### 1.4. Found Dependencies
- This user story has a dependency with US001 - "Create a map" as there has to exist a map for a scenario to be made.

### 1.5. Input and Output Data

#### Input Data:
- Map selection
- Time Period
- Technological Restrictions
- Historical Restrictions

#### Output Data:
- Confirmation of successful scenario creation
- Error message if creation fails (e.g., invalid map)

### 1.6. System Sequence Diagram (SSD)
![System Sequence Diagram](svg/US004-SSD.svg)
**_Other alternatives might exist._**

### 1.7. Other Relevant Remarks
- None.