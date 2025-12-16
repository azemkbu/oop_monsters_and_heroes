import game.GameLauncher;
import utils.BGMPlayer;
import utils.EndOfInputException;

public class Main {
    public static void main(String[] args) {
        // Start background music (non-blocking, does not affect game architecture)
        BGMPlayer.start("BGM.mp3");
        
        GameLauncher launcher = new GameLauncher();
        try {
            launcher.run();
        } catch (EndOfInputException e) {
            System.out.println("Input ended. Exiting game.");
        } finally {
            BGMPlayer.stop();
        }
    }
}