package controller;

import domain.CurrentUser;
import domain.Locomotive;
import domain.Scenario;
import repository.LocomotiveRepository;
import repository.Repositories;
import repository.ScenarioRepository;

import java.util.*;

public class BuyLocomotiveController {
    private LocomotiveRepository locomotiveRepository;
    CurrentUser session = CurrentUser.getInstance();

    public BuyLocomotiveController() {
        Repositories repositories = Repositories.getInstance();
        locomotiveRepository = repositories.getLocomotiveRepository();
    }

    public void createLocomotive(String modelName, int price, String type, int fuelCost, int maintenanceCost, int startYearOfOperation, int endYearOfOperation, int topSpeed) {
        locomotiveRepository.addLocomotive(modelName, price, type, fuelCost, maintenanceCost, startYearOfOperation, endYearOfOperation, topSpeed);
    }

    public List<Locomotive> getLocomotives(Scenario scenario) {
        Set<Locomotive> locomotives = locomotiveRepository.getLocomotives();
        List<Locomotive> locomotivesList = new ArrayList<>();
        for (Locomotive locomotive : locomotives) {
            if(locomotive.getStartYearOfOperation() <= scenario.getTimeRestrictions()[0] && locomotive.getEndYearOfOperation() >= scenario.getTimeRestrictions()[0]) {
                for (String techRestriction : scenario.getTechnologicalRestrictions()){
                    if (techRestriction.toLowerCase().equals(locomotive.getType().toLowerCase())) {
                        locomotivesList.add(locomotive);
                    }
                }
            }
        }
        return locomotivesList;
    }

    public List<Locomotive> getPlayerLocomotives() {
        return session.getUser().getLocomotives();
    }

    public boolean canBuyLocomotive(String modelName, Scenario scenario) {
        for (Locomotive locomotive : this.getLocomotives(scenario)){
            if (locomotive.getModelName().equals(modelName)){
                return true;
            }
        }
        return false;
    }

    public boolean buyLocomotive(String modelName, Scenario scenario){
        if (canBuyLocomotive(modelName, scenario)){
            return locomotiveRepository.buyLocomotive(modelName);
        }
        return false;
    }


}
