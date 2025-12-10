package game;

import hero.*;
import java.util.List;
import utils.IOUtils;

/**
 * Entry point for the game that wires up the game components and starts the game loop
 */
public interface GameLauncher {

    /**
     * Build dependencies and start the game
     */
    public void run();
        


    public Party chooseParty(List<Hero> availableHeroes, IOUtils ioUtils);
    
}
