package market.ui;

import hero.Hero;
import market.command.MarketAction;
import market.command.MarketCommand;
import market.command.MarketCommandConfig;
import market.model.item.Item;
import market.service.MarketService;
import market.service.MarketResult;
import utils.MessageUtils;
import utils.GameConstants;
import utils.IOUtils;

import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of the {@link MarketMenu} interface
 */
public class MarketMenuImpl implements MarketMenu {

    private final MarketService marketService;
    private final IOUtils ioUtils;
    private final Map<Integer, MarketCommand> commands;

    public MarketMenuImpl(MarketService marketService, IOUtils ioUtils) {
        this.marketService = marketService;
        this.ioUtils = ioUtils;
        this.commands = MarketCommandConfig.createCommands(this);
    }

    @Override
    public void runMarketSession(Hero hero) {
        boolean exit = false;

        while (!exit) {
            printMarketHeader(hero);
            printMainMenu();

            int choice = ioUtils.readIntInRange(
                    GameConstants.MARKET_MENU_MIN_OPTION,
                    GameConstants.MARKET_MENU_MAX_OPTION
            );

            if (choice == MarketAction.LEAVE.getCode()) {
                ioUtils.printlnWarning(MessageUtils.LEAVE_MARKET_MESSAGE);
                exit = true;
                continue;
            }

            MarketCommand command = commands.get(choice);

            if (command != null) {
                command.execute(hero);
            } else {
                ioUtils.printlnFail(MessageUtils.INVALID_CHOICE);
                ioUtils.printlnFail(String.format(MessageUtils.CORRECT_RANGE_MESSAGE,
                        GameConstants.MARKET_MENU_MIN_OPTION ,
                        GameConstants.MARKET_MENU_MAX_OPTION));
            }
        }
    }


    @Override
    public void handleBuy(Hero hero) {
        List<Item> items = marketService.getItemsForSale();
        if (items == null || items.isEmpty()) {
            ioUtils.printlnWarning(MessageUtils.NO_ITEMS_FOR_SALE);
            return;
        }

        ioUtils.printlnTitle(MessageUtils.ITEMS_FOR_SALE_HEADER);
        int index = selectItem(items);

        if (index == -1) {
            ioUtils.printlnFail(MessageUtils.CANCELED);
            return;
        }

        Item selected = items.get(index);
        MarketResult result = marketService.buyItem(hero, selected);
        printResult(result);
    }

    @Override
    public void handleSell(Hero hero) {
        List<Item> inventory = hero.getInventory();
        if (inventory == null || inventory.isEmpty()) {
            ioUtils.printlnWarning(MessageUtils.HERO_NO_ITEMS_TO_SELL);
            return;
        }

        ioUtils.printlnHeader(MessageUtils.INVENTORY_MESSAGE);
        int index = selectItem(inventory);

        if (index == -1) {
            ioUtils.printlnFail(MessageUtils.CANCELED);
            return;
        }

        Item selected = inventory.get(index);
        MarketResult result = marketService.sellItem(hero, selected);
        printResult(result);
    }

    private void printResult(MarketResult result) {
        if (result == null) {
            ioUtils.printlnFail(MessageUtils.FAILED);
            return;
        }
        switch (result.getSeverity()) {
            case SUCCESS:
                ioUtils.printlnSuccess(result.getMessage());
                break;
            case WARNING:
                ioUtils.printlnWarning(result.getMessage());
                break;
            case FAIL:
            default:
                ioUtils.printlnFail(result.getMessage());
                break;
        }
    }

    @Override
    public void showHeroInventory(Hero hero) {
        ioUtils.printlnHeader(MessageUtils.HERO_INFORMATION_HEADER);
        ioUtils.printlnTitle(hero.toString());

        List<Item> inventory = hero.getInventory();
        if (inventory == null || inventory.isEmpty()) {
            ioUtils.printlnTitle(MessageUtils.INVENTORY_MESSAGE);
        } else {
            ioUtils.printlnTitle(MessageUtils.INVENTORY_MESSAGE);
            printItems(inventory);
        }
    }


    private void printMarketHeader(Hero hero) {
        ioUtils.printlnHeader(MessageUtils.MARKET_MENU_HEADER);
        ioUtils.printlnTitle("  Hero: " + hero.getName()
                + " | Level: " + hero.getLevel()
                + " | Gold: " + hero.getGold());
    }


    private void printMainMenu() {
        for (MarketAction action : MarketAction.values()) {
            ioUtils.printlnTitle(action.getActionLine());
        }

        ioUtils.printPrompt(MessageUtils.ENTER_CHOICE);
    }

    public void printItems(List<Item> items) {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            ioUtils.printlnTitle(String.format(
                    "  [%d] %s (Type: %s, Level %d, Price %d)",
                    i + 1,
                    item.getName(),
                    item.getItemType(),
                    item.getLevel(),
                    item.getPrice()
            ));
        }
        ioUtils.printlnTitle(MessageUtils.CANCEL_LINE);
    }

    private int selectItem(List<Item> items) {
        printItems(items);
        ioUtils.printPrompt(MessageUtils.ENTER_CHOICE);

        int choice = ioUtils.readIntInRange(0, items.size());
        if (choice == 0) {
            return -1;
        }
        return choice - 1;
    }
}
