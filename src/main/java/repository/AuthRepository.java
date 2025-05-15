package repository;

import domain.User;
import domain.CurrentUser;

import java.util.*;

public class AuthRepository {

    Set<User> users = new HashSet<>();
    CurrentUser session = CurrentUser.getInstance();

    public boolean createNewUser(User user) {
        if (!doesUserExist(user)) {
            users.add(user);
            return true;
        }
        return false;
    }

    public List<User> getAllUsers(){

        List<User> userList = new ArrayList<>();
        for (User user : users) {
            userList.add(user);
        }

        return userList;
    }

    public Optional<User> getUserByEmail(String email) {

        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    private boolean doesUserExist(User user){
        for (User user1 : users) {
            if (user1.getEmail().equals(user.getEmail())) {
                return true;
            }
        }
        return false;
    }

    public boolean doesUserExist(String email) {
        for (User user1 : users) {
            if (user1.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPasswordValid(String email, String password) {
        if (doesUserExist(email)) {
            Optional<User> usr = getUserByEmail(email);
            String userPwd = usr.get().getPassword();
            return userPwd.equals(password.toString());
        }else{
            return false;
        }
    }

    public boolean deleteUser(String email) {
        if (doesUserExist(email)) {
            Optional<User> usr = getUserByEmail(email);
            users.remove(usr.get());
            if (session.getUser().equals(usr.get())) {
                doLogout();
            }
            return true;
        }
        return false;
    }

    public boolean deleteUser(User user) {
        if (doesUserExist(user.getEmail())) {
            users.remove(user);
            if (session.getUser().equals(user)) {
                doLogout();
            }
            return true;
        }
        return false;
    }

    public boolean changeRole(String email, String roleId) {
        if (doesUserExist(email)) {
            Optional<User> usr = getUserByEmail(email);
            usr.get().setRoleId(roleId);
            return true;
        }
        return false;
    }

    public boolean changeRole(User user, String roleId) {
        if (doesUserExist(user)) {
            user.setRoleId(roleId);
            return true;
        }
        return false;
    }

    public boolean doLogin(String email, String password) {
        if (isPasswordValid(email, password)){
            Optional<User> usr = getUserByEmail(email);
            return session.doLogin(usr.get());
        }
        return false;
    }

    public boolean doLogout() {
        session.doLogout();
        return true;
    }

    public String getRole(String email) {
        Optional<User> usr = getUserByEmail(email);
        return usr.get().getRoleId();
    }

    public String getCurrentUserEmail() {
        return session.getUser().getEmail();
    }

}
