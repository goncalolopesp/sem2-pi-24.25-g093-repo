# US009 - Buying a train

## 1. Requirements Engineering

### 1.1. User Story Description

As a Player, I want to buy a train.

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

> The player should choose a train from a list of available trains. <br>Different trains have different fuel consumption, namely electricity, steam and diesel. <br>Also, Some types of trains require special rails to operate.


### 1.3. Acceptance Criteria

* The player should choose the locomotive from a list of available locomotives for the scenario as well as a current date

### 1.4. Found out Dependencies

* This User Store requires the US01. In order to buy a train, the player needs to have a map and a railway.

### 1.5 Input and Output Data

**Input Data:**

- Train type

**Output Data:**

- Success Message

### 1.6. System Sequence Diagram (SSD)


![US09-SSD](svg/US009-SSD.svg)

### 1.7 Other Relevant Remarks