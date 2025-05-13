package domain;

public class User {

    String name;
    String email;
    String password;
    String roleId;
    Budget budget;

    public User(String name, String email, String password, String roleId, Budget Budget) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
        this.budget = Budget;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRoleId() { return roleId; }
    public int getBudgetValue() { return budget.getBudget(); }
    public Budget getBudget() { return budget; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
    public void setBudget(Budget budget) { this.budget = budget; }

}
