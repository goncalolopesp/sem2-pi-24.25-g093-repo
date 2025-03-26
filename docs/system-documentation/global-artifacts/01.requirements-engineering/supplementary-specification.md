# Supplementary Specification (FURPS+)

## Functionality

_Specifies functionalities that:  
&nbsp; &nbsp; (i) are common across several US/UC;  
&nbsp; &nbsp; (ii) are not related to US/UC, namely: Audit, Reporting and Security._

- **Audit:** The system keeps log of important/critical actions took by the users while they are playing the simulator, such as map modifications and other sorts of critical factors of the simulator;
- **Reporting:** Provides information on some features, such as cargo movement and economic options;
- **Security:** User authentication with password rules (seven alphanumeric characters, including three capital
  letters and two digits);
## Usability

_Evaluates the user interface. It has several subcategories,
among them: error prevention; interface aesthetics and design; help and
documentation; consistency and standards._

- **User Interface:** The user interface must be simple and intuitive, in order to provide the best user-friendly experience.
- **Error Prevention:** Validates certain aspects before proceeding with certain actions (for example, before placing a station, the software must check if there is any other station there, to prevent overbuilding);
- **Consistency and Standards:** The project will follow Object-Oriented (OO) principles and Java best practices.
- **Documentation:** The documentation must be all written in english;
- **Accessibility:** The project will ensure easy navigation and interaction with the user, to give the best experience possible;

## Reliability

_Refers to the integrity, compliance and interoperability of the software. The requirements to be considered are: frequency and severity of failure, possibility of recovery, possibility of prediction, accuracy, average time between failures._

- **Failure Recovery:** Data will persist through object serialization for continuous system use;
- **Unit Testing:** Using JUnit 5 framework, unit tests will be created to verify if the software is working as intended to;
- **Security:** The software will use user authentication in order to increase secyrity;

## Performance

_Evaluates the performance requirements of the software, namely: response time, start-up time, recovery time, memory consumption, CPU usage, load capacity and application availability._

**N/A**

## Supportability

_The supportability requirements gathers several characteristics, such as:
testability, adaptability, maintainability, compatibility,
configurability, installability, scalability and more._

- **Testability:** Unit tests will be implemented, using JaCoCo coverage reports.
- **Maintainability:** The design of the project must be made, in order to easily implement future changes;

## +

### Design Constraints

_Specifies or constraints the system design process. Examples may include: programming languages, software process, mandatory standards/patterns, use of development tools, class library, etc._

- **Programming Language:** Java is mandatory to program the software;
- **Development Methodology:** Usage of SCRUM famework;
- **Coding Standards:** Usage of CamelCase and JavaDoc for documentation;
- **Version Control:** Mandatory use of Github repository feature;

### Implementation Constraints

_Specifies or constraints the code or construction of a system such
such as: mandatory standards/patterns, implementation languages,
database integrity, resource limits, operating system._

- **Testing Framework:** Using JUnit5 in order to create unit tests;
- **Data Persistance:** By using data serialization, the software will be able to store data between sessions;

### Interface Constraints

_Specifies or constraints the features inherent to the interaction of the
system being developed with other external systems._

**N/A**

### Physical Constraints

_Specifies a limitation or physical requirement regarding the hardware used to house the system, as for example: material, shape, size or weight._

**N/A**