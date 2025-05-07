import ui.console.menu.InitialMenuUI;

public class Main {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.run();
        try {
            InitialMenuUI menu = new InitialMenuUI();
            menu.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}