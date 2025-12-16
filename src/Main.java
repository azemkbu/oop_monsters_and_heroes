import game.GameLauncher;
import utils.BGMPlayer;
import utils.EndOfInputException;

public class Main {
    public static void main(String[] args) {
        // Start background music (WAV format preferred, MP3 not supported by Java AudioSystem)
        if (new java.io.File("BGM.wav").exists()) {
            BGMPlayer.start("BGM.wav");
        } else if (new java.io.File("BGM.mp3").exists()) {
            BGMPlayer.start("BGM.mp3");  // Will show error message if format unsupported
        }
        
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