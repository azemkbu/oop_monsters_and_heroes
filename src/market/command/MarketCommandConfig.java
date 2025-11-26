package market.command;

import hero.Hero;
import market.ui.MarketMenuImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for mapping {@link MarketAction} and {@link MarketCommand}
 */
public final class MarketCommandConfig {

    private MarketCommandConfig() {
    }

    public static Map<Integer, MarketCommand> createCommands(MarketMenuImpl menu) {
        Map<Integer, MarketCommand> commands = new HashMap<>();

        commands.put(MarketAction.BUY.getCode(), new MarketCommand() {
            @Override
            public void execute(Hero hero) {
                menu.handleBuy(hero);
            }
        });

        commands.put(MarketAction.SELL.getCode(), new MarketCommand() {
            @Override
            public void execute(Hero hero) {
                menu.handleSell(hero);
            }
        });

        commands.put(MarketAction.VIEW_INFO.getCode(), new MarketCommand() {
            @Override
            public void execute(Hero hero) {
                menu.showHeroInventory(hero);
            }
        });

        return commands;
    }
}
