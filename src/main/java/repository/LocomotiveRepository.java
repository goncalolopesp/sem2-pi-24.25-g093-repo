package repository;

import java.util.*;

import domain.Budget;
import domain.CurrentUser;
import domain.Locomotive;
import domain.User;

public class LocomotiveRepository {

    Set<Locomotive> locomotives = new HashSet<>();
    CurrentUser session = CurrentUser.getInstance();

    public boolean addLocomotive(Locomotive locomotive) {

        if (!checkIfLocomotiveExists(locomotive)) {
            locomotives.add(locomotive);
            return true;
        }

        return false;

    }

    public boolean addLocomotive(String modelName, int price, String type, int fuelCost, int maintenanceCost, int startYearOfOperation, int endYearOfOperation, int topSpeed) {
        Locomotive l1 = new Locomotive(modelName, price, type, fuelCost, maintenanceCost, startYearOfOperation, endYearOfOperation, topSpeed);
        if (!checkIfLocomotiveExists(l1)){
            locomotives.add(l1);
            return true;
        }
        return false;
    }

    public boolean checkIfLocomotiveExists(Locomotive locomotive) {
        for (Locomotive b : locomotives) {
            if (b.getModelName().equals(locomotive.getModelName())) {
                return true;
            }
        }
        return false;
    }

    public boolean buyLocomotive(Locomotive locomotive){
        User user = session.getUser();
        Budget money = user.getBudget();
        if (money.getBudget() < locomotive.getPrice()) {
            return false;
        }
        List<Locomotive> boughtLocomotives = user.getLocomotives();
        boughtLocomotives.add(locomotive);
        user.setLocomotives(boughtLocomotives);
        money.subtractBudget(locomotive.getPrice());
        return true;
    }

    public boolean buyLocomotive(String modelName){
        Optional<Locomotive> locomotive = getLocomotive(modelName);
        if (locomotive.isPresent()) {
            return buyLocomotive(locomotive.get());
        }else{
            return false;
        }
    }

    public boolean deleteLocomotive(Locomotive locomotive){
        if (checkIfLocomotiveExists(locomotive)) {
            locomotives.remove(locomotive);
            return true;
        }
        return false;
    }

    public Optional<Locomotive> getLocomotive(String modelName){
        for (Locomotive l : locomotives) {
            if (l.getModelName().equals(modelName)) {
                return Optional.of(l);
            }
        }
        return Optional.empty();
    }

    public Set<Locomotive> getLocomotives() {
        return locomotives;
    }

}
