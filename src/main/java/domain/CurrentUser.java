package domain;

import repository.LocomotiveRepository;

import java.util.Map;
import java.util.HashMap;

public class CurrentUser {
    private static CurrentUser session = null;

    private Map<String, User> usr;
    private Map<String, LocomotiveRepository> lr;

    private CurrentUser() {
        usr = new HashMap<>();
        lr = new HashMap<>();
    }

    public static CurrentUser getInstance() {
        if (session == null) {
            session = new CurrentUser();
        }
        return session;
    }

    public User getUser() {
        return usr.get("currentUser");
    }

    public LocomotiveRepository getLocomotiveRepository() {
        return lr.get("locomotiveRepository");
    }

    public void setUser(User user) {
        usr.put("currentUser", user);
    }

    public void setLocomotiveRepository(LocomotiveRepository locomotiveRepository) {
        lr.put("locomotiveRepository", locomotiveRepository);
    }

    public boolean doLogin(User user) {
        setUser(user);
        return true;
    }

    public void clear(){
        usr.clear();
    }

    public boolean doLogout() {
        clear();
        return true;
    }

}
