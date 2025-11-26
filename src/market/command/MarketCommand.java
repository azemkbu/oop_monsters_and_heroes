package market.command;

import hero.Hero;

/**
 * Defines an action that can be executed in the market
 */
public interface MarketCommand {
    void execute(Hero hero);
}

