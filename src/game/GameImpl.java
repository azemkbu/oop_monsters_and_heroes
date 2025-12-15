package game;

import battle.engine.BattleEngine;
import hero.Hero;
import hero.Party;
import market.model.Market;
import utils.GameConstants;
import utils.MessageUtils;
import ui.mh.MhView;
import worldMap.enums.Direction;
import worldMap.Tile;
import worldMap.enums.TileType;
import worldMap.IWorldMap;

import java.util.Arrays;

/**
 * Implementation of the {@link Game} interface
 */
public class GameImpl implements Game {

    private final IWorldMap worldMap;
    private final Party party;
    private final BattleEngine battleEngine;
    private final MhView view;
    private boolean running = false;

    public GameImpl(IWorldMap worldMap,
                    Party party,
                    BattleEngine battleEngine,
                    MhView view) {
        this.worldMap = worldMap;
        this.party = party;
        this.battleEngine = battleEngine;
        this.view = view;
    }

    @Override
    public void start() {
        view.showWelcome();
        view.showInstructions(Arrays.asList(GameCommand.values()));

        running = true;

        while (running) {
            view.renderMap(worldMap, party);
            view.showInstructions(Arrays.asList(GameCommand.values()));
            GameCommand command = view.promptCommand();

            if (command != null) {
                command.execute(this);
            } else {
                view.showFail(MessageUtils.UNKNOWN_COMMAND);
            }
        }
    }

    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public void handleQuit() {
        view.showSuccess("Quitting game. Goodbye!");
        stop();
    }

    @Override
    public void handleMove(Direction direction) {
        boolean moved = worldMap.moveParty(party, direction);
        if (!moved) {
            view.showFail(MessageUtils.INCORRECT_MOVE_MESSAGE);
            return;
        }

        Tile currentTile = worldMap.getPartyTile(party);
        TileType tileType = currentTile.getType();

        switch (tileType) {
            case COMMON:
                startBattle();
                break;
            case MARKET:
                view.showWarning(MessageUtils.MARKET_ENTER_COMMAND);
                break;
            case INACCESSIBLE:
            default:
                break;
        }
    }

    private void startBattle() {
        double rollDice = Math.random();
        if (rollDice <= GameConstants.BATTLE_PROBABILITY) {
            view.showWarning(MessageUtils.BATTLE_START_MESSAGE);
            boolean heroesWon = battleEngine.runBattle(party, worldMap);
            if (!heroesWon) {
                view.showFail(MessageUtils.PARTY_DEFEATED_MESSAGE);
                stop();
            }
        } else {
            view.showWarning(MessageUtils.NOTHING_ATTACKS_MESSAGE);
        }
    }

    @Override
    public void handleEnterMarket() {
        view.showWarning(MessageUtils.ENTER_MARKET_HEADER);
        Tile currentTile = worldMap.getPartyTile(party);
        Market market = currentTile.getMarket();
        if (market == null) {
            view.showFail(MessageUtils.NO_MARKET);
            return;
        }

        Hero hero = view.promptHeroForMarket(party);
        if (hero == null) {
            view.showFail(MessageUtils.HERO_NOT_SELECTED);
            return;
        }

        view.runMarketSession(hero, market);
    }

    @Override
    public void showPartyInfo() {
        view.showWarning(MessageUtils.PARTY_INFORMATION_HEADER);
        for (Hero hero : party.getHeroes()) {
            view.showWarning(hero.toString());
        }
    }

}
