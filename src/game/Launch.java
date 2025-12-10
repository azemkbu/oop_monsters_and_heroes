package game;

import game.enums.GameType;
import java.util.Scanner;
import utils.ConsoleIOUtils;
import utils.IOUtils;

public class Launch {
    
    public void run(){

        IOUtils ioUtils = new ConsoleIOUtils(new Scanner(System.in));
        
        
        System.out.println("Which game would you like to play?");
        System.out.println("1. Monsters and Heroes");
        System.out.println("2. Legends of Valor");
        

        int decision = ioUtils.readIntInRange(1,2);

        if(decision - 1 == GameType.LEGENDSOFVALOR.ordinal()){
            LOVGameLauncher gameLauncher = new LOVGameLauncher();
            gameLauncher.run();
        }
        else if(decision - 1 == GameType.MONSTERSANDHEROES.ordinal()){
            MAHGameLauncher gameLauncher = new MAHGameLauncher();
            gameLauncher.run();
        }
        

    }

}
