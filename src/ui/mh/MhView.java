package ui.mh;

import game.GameCommand;
import hero.Hero;
import hero.Party;
import market.model.Market;
import worldMap.IWorldMap;

import java.util.List;

/**
 * View layer for Monsters and Heroes (all interactive I/O belongs here).
 */
public interface MhView {
    void showWelcome();

    void showInstructions(List<GameCommand> commands);

    void renderMap(IWorldMap map, Party party);

    GameCommand promptCommand();

    void showSuccess(String msg);

    void showFail(String msg);

    void showWarning(String msg);

    Hero promptHeroForMarket(Party party);

    void runMarketSession(Hero hero, Market market);
}


