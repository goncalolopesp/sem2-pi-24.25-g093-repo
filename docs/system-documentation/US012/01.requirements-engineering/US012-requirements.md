# US012 - Create a simulator

## 1. Requirements Engineering

### 1.1. User Story Description
As a Product Owner, I want to create a simulator that generates cargoes at current stations, automatically, considering the cities and industries that the railway network serves. AC1: This simulator should provide options for start/pause.

### 1.2. Customer Specifications and Clarifications

#### From the customer's clarifications:
> Question: A simulação é corrida mapa a mapa ou para todos os mapas existentes? Exemplo: Escolhe correr isoladamente a simulação no mapa da cidade de Lisboa mas não corre no mapa de Paris. Ou teria que correr necessariamente em todos os mapas?
Se for mapa a mapa, como é que isso se processa? Corre no mapa em que o Product Owner está? O Product Owner seleciona em que mapas quer correr a simulação?
> Answer: No contexto da simulation o jogador escolhe um cenário para jogar, associado ao cenário existe obrigatoriamente um mapa.
>
> Question: Quando o Product Owner pausa: esta pausa é local para um cenário específico ou para todos os cenários? esta pausa é para um mapa ou para todos?
> Answer:
>
> Question: Is there a limit to cargo storage?
> Answer: 24
>
> Question: How should cargo generation be done? At fixed time intervals or based on specific events (train arrival)?
> Answer: Accordingly to the frequency defined for the industry and house blocks by the station (the distribution along the year can be fixed or random)
>
> Question: Should cargo generation dynamically update as the railway network expands or changes?
> Answer: Yes; the generation is done for the industries and house blocks served by the stations present in the network.
>
> Question: Will the user have customization options for cargo generation rules, or will it be fully automated?
> Answer: Just in the edition of the scenario.
>
> Question: How should cargo stockpiling be managed at stations to prevent excessive accumulation?
> Answer: A maximum number can be considered per cargo type (e.g. 30)

#### From the specifications document:
> The simulator will enable the manager (or from the playful perspective, the
player) to create a railway network composed of stations that serve cities
and industries, lines connecting those stations, and trains that transport
cargo between stations, according to a route defined by the manager. It
is crucial to highlight that cities and industries only generate or transform
cargo if they have a station; when the cargo is generated, it is then available
to be collected at the station.
> 
> In addition to creating the dynamic elements already described, the player/user
can play or pause the simulator. During the simulator’s operation, the evolution of events should be displayed, namely the trains’ journeys (start and
end).
> 
> This system may be used by different users, namely:
> 
>• Editor - a person who can create maps and scenarios that can be provided in a bundle with the game or sold separately.
> 
>• Player – a person who wants to play a game; in advanced versions, a
scenario can be played by multiple players simultaneously.

### 1.3. Acceptance Criteria
-  AC1: This simulator should provide options for start/pause.

### 1.4. Found Dependencies
- This user story has dependencies with all user stories as they're all needed to create the simulator.

### 1.5. Input and Output Data

#### Input Data:
- Existing railway network data
- Scenario configurations
- Simulation controls

#### Output Data:
- Cargo generation at stations
- Simulation operation feedback

### 1.6. System Sequence Diagram (SSD)
![System Sequence Diagram](svg/US012-SSD.svg)
**_Other alternatives might exist._**

### 1.7. Other Relevant Remarks
- None.