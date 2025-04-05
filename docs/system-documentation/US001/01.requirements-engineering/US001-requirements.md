# US001 - Create a Map

## 1. Requirements Engineering

### 1.1. User Story Description
As an Editor, I want to create a map with a size and a name.

### 1.2. Customer Specifications and Clarifications

#### From the customer's clarifications:
> Question: Are there minimum and maximum values for size?
>
> Answer: The number needs to be a positive on; there is no maximum

> Question: Are there standard ratio or max/min ratio for map size?
>
> Answer: It's up to the editor to decide.

> Question: Should map names be unique within the system?
>
> Answer: yes.

> Question: Can the names of scenarios and maps be the same?
>
> Answer: Yes, they are different objects.

> Question: A route is a list of stations where the train passes. If there are many stations but no railway lines, are we able to create a route?
>
> Answer: If there is no path between any pair of consecutive points of the route, the player should be warned, then the player can opt between cancel/proceed.

> Question: Can multiple cargo types be assigned to the same train? Does a route have a limit of stations that can be assigned?
>
> Answer: Yes; No.
 
#### From the specifications document:
- A map must have a unique name.
- A map must have defined dimensions (width x height).
- The editor should be able to specify these attributes during creation.

### 1.3. Acceptance Criteria
- **AC1:** The maps dimensions are positive integers.
- **AC2:** Map name should be a valid file name.

### 1.4. Found Dependencies
- None.

### 1.5. Input and Output Data

#### Input Data:
- Typed data:
  - Map name (unique)
  - Map size (width x height)

#### Output Data:
- Confirmation of successful map creation
- Error message if creation fails (e.g., duplicate name, invalid size)

### 1.6. System Sequence Diagram (SSD)
![System Sequence Diagram](svg/US001-SSD.svg)
**_Other alternatives might exist._**

### 1.7. Other Relevant Remarks
- None.