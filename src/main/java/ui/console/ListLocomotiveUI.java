package ui.console;

import domain.CurrentUser;
import domain.Locomotive;
import domain.User;

public class ListLocomotiveUI implements Runnable {
    @Override public void run() {
        System.out.println("==========Locomotives List==========");
        CurrentUser session = CurrentUser.getInstance();
        User user = session.getUser();
        for (Locomotive locomotive : user.getLocomotives()) {
            System.out.println("*" + locomotive.getModelName());
        }
        System.out.println("==============================");
    }

}
