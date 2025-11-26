package market.ui;

import hero.Hero;

/**
 * User interface for interacting with {@link market.model.Market}
 */
public interface MarketMenu {

    /**
     * Starts a market session for the given hero and keeps running until
     * the player chooses to leave the market
     *
     * @param hero the hero who is currently trading in the market
     */
    void runMarketSession(Hero hero);

    void handleBuy(Hero hero);

    void handleSell(Hero hero);

    void showHeroInventory(Hero hero);

}
