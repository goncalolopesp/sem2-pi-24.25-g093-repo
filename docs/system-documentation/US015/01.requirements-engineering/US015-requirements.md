# US015 - Statistical analysis of the average annual profit of a user-specified railway station
## 1. Requirements Engineering

### 1.1. User Story Description
As a Player, I want to perform a statistical analysis of the aver-
age annual profit of a user-specified railway station. In order to achieve
this, a table with key descriptive measures: mean, standard deviation,
median, and mode will be generated. Additionally, the analysis aims
to identify outlier profits as well as to determine the most and least
profitable years.

### 1.2. Customer Specifications and Clarifications

#### From the customer's clarifications:

#### From the specifications document:
> Conducting a statistical analysis of railway station data is crucial for op-
timizing operations, improving efficiency, and maximizing profitability. By
analyzing train traffic, cargo, passengers, and mail volumes, companies can
enhance scheduling, resource allocation, customer satisfaction, and logistics.
Additionally, reviewing revenue and expenses over time helps identify prof-
itability trends, manage costs, and support strategic decision-making. This
data-driven approach ensures long-term financial sustainability, operational
improvements, and adaptability to changing demands. The best practices
concerning statistical analysis, referring to stations, should be considered to
develop the following US. The ”Stations Data.csv” file provides the necessary
data to carry out the study.

### 1.3. Acceptance Criteria
There is no AC for this US.

### 1.4. Found Dependencies
- This user story has a dependency with US005 - "Create a Station" as there has to exist a station for there to be a statistical analysis of the average annual profit.

### 1.5. Input and Output Data

#### Input Data:
- Selected Data:
    - Station

#### Output Data:
- Mean, standard deviation, median, and mode.
- Identification of outlier profits.
- Most and least profitable years.

### 1.6. System Sequence Diagram (SSD)
Successful System Sequence Diagram
![System Sequence Diagram](svg/USXXX-SSD.svg)

**_Other alternatives might exist._**

### 1.7. Other Relevant Remarks
- None.