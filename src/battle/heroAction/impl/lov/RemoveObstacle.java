package battle.heroAction.impl.lov;

import battle.heroAction.BattleContext;
import battle.heroAction.HeroActionStrategy;
import hero.Hero;
import java.util.List;
import monster.Monster;
import utils.IOUtils;
import utils.MessageUtils;
import worldMap.ILegendsWorldMap;
import worldMap.Tile;
import worldMap.enums.Direction;

public class RemoveObstacle implements HeroActionStrategy {

    private final ILegendsWorldMap worldMap;
    private final IOUtils io;

    public RemoveObstacle(ILegendsWorldMap worldMap, IOUtils io) {
        this.worldMap = worldMap;
        this.io = io;
    }

    @Override
    public void execute(Hero hero,
                        List<Monster> monsters,
                        BattleContext context,
                        IOUtils ignored) {

        worldMap.printMap();

        Direction direction = chooseRemoveDirection(hero);
        if (direction == null) {
            io.printlnFail(MessageUtils.CANCELED);
            return;
        }


        int currentRow = hero.getRow();
        int currentCol = hero.getCol();

        int newRow = currentRow + direction.getRow();
        int newCol = currentCol + direction.getCol();


        Tile obstacle = worldMap.getTile(newRow, newCol);

        boolean removed = obstacle.removeObstacle();

        if (removed) {
            io.printlnFail(String.format(MessageUtils.TRY_ANOTHER_DIRECTION, direction));
        } else {
            io.printlnSuccess(String.format(MessageUtils.SUCCESS_MOVE, hero.getName(), direction));
            worldMap.printMap();
        }
    }


    private Direction chooseRemoveDirection(Hero hero) {
        while (true) {
            io.printlnTitle("Choose where  " + hero.getName() + " would like to remove an obstacle?");
            io.printlnTitle("  W = Up");
            io.printlnTitle("  S = Down");
            io.printlnTitle("  A = Left");
            io.printlnTitle("  D = Right");
            io.printlnTitle("  Q = Cancel");

            io.printPrompt("Enter direction (W/A/S/D or Q): ");
            String line = io.readLine();
            if (line == null || line.trim().isEmpty()) {
                io.printlnFail("Invalid input, please use W/A/S/D or Q.");
                continue;
            }
            char input = Character.toUpperCase(line.trim().charAt(0));

            switch (input) {
                case 'W':
                    return Direction.UP;
                case 'S':
                    return Direction.DOWN;
                case 'A':
                    return Direction.LEFT;
                case 'D':
                    return Direction.RIGHT;
                case 'Q':
                    return null;
                default:
                    io.printlnFail("Invalid input, please use W/A/S/D or Q.");
            }
        }
    }
}
