package domain;

import repository.LocomotiveRepository;

import java.util.Map;
import java.util.HashMap;

public class CurrentUser {
    private static CurrentUser session = null;

    private Map<String, User> usr;

    private CurrentUser() {
        usr = new HashMap<>();
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

    public void setUser(User user) {
        usr.put("currentUser", user);
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
