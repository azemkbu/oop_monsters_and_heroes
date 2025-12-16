package game.mh;

import worldMap.enums.Direction;

import java.util.function.Consumer;

/**
 * Represents a command the player can choose in the main game loop
 */
public enum GameCommand {

    MOVE_UP(
            'w',
            "W/w",
            "move up",
            game -> game.handleMove(Direction.UP)
    ),
    MOVE_LEFT(
            'a',
            "A/a",
            "move left",
            game -> game.handleMove(Direction.LEFT)
    ),
    MOVE_DOWN(
            's',
            "S/s",
            "move down",
            game -> game.handleMove(Direction.DOWN)
    ),
    MOVE_RIGHT(
            'd',
            "D/d",
            "move right",
            game -> game.handleMove(Direction.RIGHT)
    ),
    ENTER_MARKET(
            'm',
            "M/m",
            "enter market (if on market tile)",
            MHCommandGame::handleEnterMarket
    ),
    SHOW_PARTY(
            'i',
            "I/i",
            "show party info",
            MHCommandGame::showPartyInfo
    ),
    QUIT(
            'q',
            "Q/q",
            "quit game",
            game -> {
                game.getIo().printlnSuccess("Quitting game. Goodbye!");
                game.stop();
            }
    );

    private final char key;
    private final String label;
    private final String description;
    private final Consumer<MHCommandGame> action;


    GameCommand(char key, String description, String label, Consumer<MHCommandGame> action) {
        this.key = key;
        this.description = description;
        this.label = label;
        this.action = action;
    }

    public char getKey() {
        return key;
    }
    public String getLabel() {
        return label;
    }
    public String getDescription() {
        return description;
    }

    public void execute(MHCommandGame game) {
        action.accept(game);
    }

    public static GameCommand fromChar(char c) {
        char lowerChar = Character.toLowerCase(c);
        for (GameCommand cmd : values()) {
            if (cmd.key == lowerChar) {
                return cmd;
            }
        }
        return null;
    }
}
