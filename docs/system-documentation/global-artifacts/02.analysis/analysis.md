# OO Analysis

The construction process of the domain model is based on the client specifications, especially the nouns (for _concepts_) and verbs (for _relations_) used. 

## Rationale to identify domain conceptual classes
To identify domain conceptual classes, start by making a list of candidate conceptual classes inspired by the list of categories suggested in the book "Applying UML and Patterns: An Introduction to Object-Oriented Analysis and Design and Iterative Development". 


### _Conceptual Class Category List_

**Business Transactions**

* Purchase of Trains
* Purchase of Locomotives
* Transport of Cargo
* Building of Station
* Station Upgrade
* Building of Railway Line

---

**Transaction Line Items**

* Shipment of Cargo

---

**Product/Service related to a Transaction or Transaction Line Item**

* Train Service
* Cargo Generation

---

**Transaction Records**

* Purchase of Trains
* Purchase of Locomotives
* Delivery of Cargo
* Building of Station
* Station Upgrade
* Building of Railway Line

---  

**Roles of People or Organizations**

* Editor
* Player

---

**Places**

* Map
* City
* Station
* Industry

---

**Noteworthy Events**

* Cargo Generation
* Historical Events

---

**Physical Objects**

* Train
* Locomotive
* Railway Line
* Station

---

**Descriptions of Things**

* City Description
* Industry Description
* Locomotive Specifications

---

**Catalogs**

* Locomotive Catalog
* Station Catalog

---

**Containers**

* Map
* Station
* Train

---

**Elements of Containers**

* Cities
* Industries
* Stations
* Cargo

---

**(Other) Organizations**

* 

---

**Other (External/Collaborating) Systems**

* 

---

**Records of finance, work, contracts, legal matters**

* Player Budget
* Train Pricings
* Locomotive Pricings
* Station Building Pricings
* Station Upgrade Pricings
* Railway Line Building Pricings

---

**Financial Instruments**

* Player Budget

---

**Documents mentioned/used to perform some work**

* 

---


## Rationale to identify associations between conceptual classes

An association is a relationship between instances of objects that indicates a relevant connection and that is worth of remembering, or it is derivable from the List of Common Associations: 

* **_A_** is physically or logically part of **_B_**
* **_A_** is physically or logically contained in/on **_B_**
* **_A_** is a description for **_B_**
* **_A_** known/logged/recorded/reported/captured in **_B_**
* **_A_** uses or manages or owns **_B_**
* **_A_** is related with a transaction (item) of **_B_**
* etc.


| Concept (A) 		|  Association   	|  Concept (B) |
|----------	   		|:-------------:		|------:       |
| C1  	| verb1    		 	| C2  |
| ...  	| ...    		 	| ...  |



## Domain Model

**Do NOT forget to identify concept atributes too.**

**Insert the Domain Model diagram in SVG format below.**

![Domain Model](svg/analysis.svg)