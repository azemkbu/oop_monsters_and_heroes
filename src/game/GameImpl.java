package game;

import battle.engine.BattleEngine;
import hero.Hero;
import hero.Party;
import market.ui.MarketMenuImpl;
import market.model.Market;
import market.ui.MarketMenu;
import market.service.MarketService;
import market.service.MarketServiceImpl;
import utils.MessageUtils;
import utils.GameConstants;
import utils.IOUtils;
import worldMap.enums.Direction;
import worldMap.Tile;
import worldMap.enums.TileType;
import worldMap.WorldMap;

import java.util.Arrays;
import java.util.List;

import static utils.ConsoleColors.*;

/**
 * Implementation of the {@link Game} interface
 */
public class GameImpl implements Game {

    private final WorldMap worldMap;
    private final Party party;
    private final BattleEngine battleEngine;
    private final IOUtils io;
    private boolean running = false;

    public GameImpl(WorldMap worldMap,
                    Party party,
                    BattleEngine battleEngine,
                    IOUtils ioUtils) {
        this.worldMap = worldMap;
        this.party = party;
        this.battleEngine = battleEngine;
        this.io = ioUtils;
    }

    @Override
    public IOUtils getIo() {
        return io;
    }

    @Override
    public void start() {
        io.printlnHeader(MessageUtils.WELCOME_MESSAGE);
        printGameInstructions();

        running = true;

        while (running) {
            worldMap.printMap(party);
            printGameInstructions();
            io.printPrompt(MessageUtils.ENTER_GAME_COMMAND);

            char commandChar = readCommandChar();
            GameCommand command = GameCommand.fromChar(commandChar);

            if (command != null) {
                command.execute(this);
            } else {
                io.printlnFail(MessageUtils.UNKNOWN_COMMAND);
            }
        }
    }

    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public void handleMove(Direction direction) {
        boolean moved = worldMap.moveParty(party, direction);
        if (!moved) {
            io.printlnFail(MessageUtils.INCORRECT_MOVE_MESSAGE);
            return;
        }

        Tile currentTile = worldMap.getPartyTile(party);
        TileType tileType = currentTile.getType();

        switch (tileType) {
            case COMMON:
                startBattle();
                break;
            case MARKET:
                io.printlnHeader(MessageUtils.MARKET_ENTER_COMMAND);
                break;
            case INACCESSIBLE:
            default:
                break;
        }
    }

    private void startBattle() {
        double rollDice = Math.random();
        if (rollDice <= GameConstants.BATTLE_PROBABILITY) {
            io.printlnHeader(MessageUtils.BATTLE_START_MESSAGE);
            boolean heroesWon = battleEngine.runBattle(party);
            if (!heroesWon) {
                io.printlnFail(MessageUtils.PARTY_DEFEATED_MESSAGE);
                stop();
            }
        } else {
            io.printlnWarning(MessageUtils.NOTHING_ATTACKS_MESSAGE);
        }
    }

    @Override
    public void handleEnterMarket() {
        io.printlnHeader(MessageUtils.ENTER_MARKET_HEADER);
        Tile currentTile = worldMap.getPartyTile(party);
        Market market = currentTile.getMarket();
        if (market == null) {
            io.printlnFail(MessageUtils.NO_MARKET);
            return;
        }

        Hero hero = chooseHeroForMarket();
        if (hero == null) {
            io.printlnFail(MessageUtils.HERO_NOT_SELECTED);
            return;
        }

        MarketService marketService = new MarketServiceImpl(market, io);
        MarketMenu marketMenu = new MarketMenuImpl(marketService, io);
        marketMenu.runMarketSession(hero);
    }

    private Hero chooseHeroForMarket() {
        List<Hero> heroes = party.getHeroes();
        if (heroes.isEmpty()) {
            io.printlnFail(MessageUtils.PARTY_IS_EMPTY);
            return null;
        }

        io.printPrompt(MessageUtils.CHOOSE_HERO);
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            io.printlnTitle(String.format(
                    "  [%d] %s (Level %d, Gold %d)",
                    i + 1,
                    h.getName(),
                    h.getLevel(),
                    h.getGold()
            ));
        }
        io.printlnTitle(MessageUtils.CANCEL_LINE);

        int choice = io.readIntInRange(0, heroes.size());
        if (choice == 0) {
            return null;
        }
        return heroes.get(choice - 1);
    }

    @Override
    public void showPartyInfo() {
        io.printlnHeader(MessageUtils.PARTY_INFORMATION_HEADER);
        for (Hero hero : party.getHeroes()) {
            io.printlnTitle(hero.toString());
        }
    }

    private char readCommandChar() {
        String line = io.readLine();
        while (line == null || line.isEmpty()) {
            line = io.readLine();
        }
        return line.charAt(0);
    }

    private void printGameInstructions() {
        io.printlnHeader(CYAN  + MessageUtils.CONTROLS_HEADER);

        List<GameCommand> commands = Arrays.asList(GameCommand.values());

        int maxLabelLen = "Keys".length();
        int maxDescLen  = "Action".length();

        for (GameCommand cmd : commands) {
            if (cmd.getLabel().length() > maxLabelLen) {
                maxLabelLen = cmd.getLabel().length();
            }
            if (cmd.getDescription().length() > maxDescLen) {
                maxDescLen = cmd.getDescription().length();
            }
        }

        String headerRow = String.format(
                "| %-"+ maxLabelLen +"s | %-"+ maxDescLen +"s |",
                "Keys", "Action"
        );

        String border = "+" + repeat('-', headerRow.length() - 2) + "+";

        io.printlnTitle(CYAN + border);
        io.printlnTitle(CYAN + headerRow);
        io.printlnTitle(CYAN + border);

        for (GameCommand cmd : commands) {
            String row = String.format(
                    "| %-"+ maxLabelLen +"s | %-"+ maxDescLen +"s |",
                    cmd.getLabel(),
                    cmd.getDescription()
            );
            io.printlnTitle(CYAN + row);
        }

        io.printlnTitle(CYAN + border);
    }

    private String repeat(char c, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

}
