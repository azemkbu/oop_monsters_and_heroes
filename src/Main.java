import game.GameLauncher;
import utils.EndOfInputException;

public class Main {
    public static void main(String[] args) {
        GameLauncher launcher = new GameLauncher();
        try {
        launcher.run();
        } catch (EndOfInputException e) {
            System.out.println("Input ended. Exiting game.");
        }
    }
}