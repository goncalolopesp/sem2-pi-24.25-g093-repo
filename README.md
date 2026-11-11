# Projeto Integrador — 2.º Semestre LEI 2024/25 (G093)

Este é o Projeto Integrador do 2.º semestre da Licenciatura em Engenharia Informática do ISEP. É um **simulador de redes ferroviárias**: os editores criam mapas e cenários, e os jogadores constroem estações, compram locomotivas e atribuem comboios a rotas. Ao mesmo tempo, o simulador vai gerando carga nas estações conforme as cidades e as indústrias que a rede serve.

Equipa: Diogo Silva (1241229), Gonçalo Pinto (1241232), Gonçalo Tavares (1241234) e João Sousa (1222123).

## Funcionalidades (User Stories)

| US | Ator | Descrição |
|---|---|---|
| US01 | Editor | Criar um mapa com nome e dimensão |
| US02 | Editor | Adicionar uma indústria ao mapa |
| US03 | Editor | Adicionar uma cidade ao mapa |
| US04 | Editor | Criar um cenário para um mapa |
| US05 | Jogador | Construir uma estação (Depot, Station ou Terminal) |
| US06 | Jogador | Melhorar uma estação com um edifício |
| US07 | Jogador | Listar as estações e ver os seus detalhes |
| US09 | Jogador | Comprar uma locomotiva |
| US10 | Jogador | Atribuir um comboio a uma rota |
| US12 | Product Owner | Simulador que gera carga nas estações (start/pause) |
| US13–US14, US26–US29 | — | Conectividade da rede ferroviária e análise de desempenho (grafos) |
| US30–US31 | — | Análise de dados das estações ([README_MATCP](README_MATCP.md)) |

Cada US tem a sua documentação (requisitos, análise, design e testes) em [`docs/system-documentation`](docs/system-documentation). O manual do utilizador está em [`user-manual/G093_UserManual.pdf`](user-manual/G093_UserManual.pdf).

## Estrutura

```
src/main/java/
  Main.java, Bootstrap.java   ponto de entrada da aplicação (consola)
  domain/ controller/ dto/ repository/   camadas da aplicação
  ui/console/                 interface de consola
  ui/gui/                     interface gráfica JavaFX
  railway_connectivity/       ferramenta de conectividade/grafos (US13, US14, US26–US29)
src/test/java/                testes unitários (JUnit 5)
docs/                         documentação de engenharia (US, modelo de domínio, glossário)
scenario*_lines.csv / scenario*_stations.csv   cenários para a ferramenta de conectividade
users.usr                     utilizadores guardados (carregados no arranque)
```

## Compilar e executar pelo terminal

Requisitos: **JDK 11 ou superior** e **Maven 3**. Todos os comandos correm a partir da raiz do repositório. Na primeira build, o Maven descarrega as dependências (incluindo a `AuthLib` do repositório Repsy do ISEP), por isso é preciso ligação à internet.

### 1. Compilar e gerar o JAR

```bash
mvn clean package -DskipTests
```

Este comando gera `target/project-template-1.0-SNAPSHOT-jar-with-dependencies.jar`.

### 2. Executar a aplicação (consola)

```bash
java -cp target/project-template-1.0-SNAPSHOT-jar-with-dependencies.jar Main
```

No menu inicial podes fazer login ou criar um utilizador novo (opção 2).

> O `java -jar` sobre o mesmo ficheiro arranca a classe `pt.ipp.isep.dei.Main`, que é a aplicação de exemplo do template original. Para correr o projeto, usa o comando acima com a classe `Main`.

### 3. Executar a interface gráfica (JavaFX)

```bash
mvn javafx:run
```

### 4. Ferramenta de conectividade ferroviária

Esta ferramenta lê um par de CSV (estações e linhas) da pasta indicada e pergunta quais usar:

```bash
java -cp target/project-template-1.0-SNAPSHOT-jar-with-dependencies.jar railway_connectivity.main .
```

### Testes, Javadoc e cobertura

```bash
mvn clean test               # testes unitários
mvn javadoc:javadoc          # Javadoc do código
mvn test jacoco:report       # relatório de cobertura (target/site/jacoco)
mvn test jacoco:check        # verifica os limites de cobertura
```
