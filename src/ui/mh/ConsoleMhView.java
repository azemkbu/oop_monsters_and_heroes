package ui.mh;

import game.GameCommand;
import hero.Hero;
import hero.Party;
import market.model.Market;
import market.service.MarketService;
import market.service.MarketServiceImpl;
import market.ui.MarketMenu;
import market.ui.MarketMenuImpl;
import ui.formatter.LineKind;
import ui.formatter.MhMapFormatter;
import ui.formatter.RenderedLine;
import utils.IOUtils;
import utils.MessageUtils;
import worldMap.IWorldMap;

import java.util.List;

import static utils.ConsoleColors.CYAN;

public final class ConsoleMhView implements MhView {
    private final IOUtils io;
    private final MhMapFormatter mapFormatter;

    public ConsoleMhView(IOUtils io) {
        this(io, new MhMapFormatter());
    }

    public ConsoleMhView(IOUtils io, MhMapFormatter mapFormatter) {
        this.io = io;
        this.mapFormatter = mapFormatter;
    }

    @Override
    public void showWelcome() {
        io.printlnHeader(MessageUtils.WELCOME_MESSAGE);
    }

    @Override
    public void showInstructions(List<GameCommand> commands) {
        io.printlnHeader(CYAN + MessageUtils.CONTROLS_HEADER);

        int maxLabelLen = "Keys".length();
        int maxDescLen  = "Action".length();

        for (GameCommand cmd : commands) {
            maxLabelLen = Math.max(maxLabelLen, cmd.getLabel().length());
            maxDescLen = Math.max(maxDescLen, cmd.getDescription().length());
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

    @Override
    public void renderMap(IWorldMap map, Party party) {
        for (RenderedLine line : mapFormatter.render(map, party)) {
            if (line.getKind() == LineKind.HEADER) {
                io.printlnHeader(line.getText());
            } else {
                io.printlnTitle(line.getText());
            }
        }
    }

    @Override
    public GameCommand promptCommand() {
        io.printPrompt(MessageUtils.ENTER_GAME_COMMAND);
        String line = io.readLine();
        while (line == null || line.isEmpty()) {
            line = io.readLine();
        }
        char commandChar = line.charAt(0);
        return GameCommand.fromChar(commandChar);
    }

    @Override
    public void showSuccess(String msg) {
        io.printlnSuccess(msg);
    }

    @Override
    public void showFail(String msg) {
        io.printlnFail(msg);
    }

    @Override
    public void showWarning(String msg) {
        io.printlnWarning(msg);
    }

    @Override
    public Hero promptHeroForMarket(Party party) {
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
    public void runMarketSession(Hero hero, Market market) {
        MarketService service = new MarketServiceImpl(market);
        MarketMenu menu = new MarketMenuImpl(service, io);
        menu.runMarketSession(hero);
    }

    private String repeat(char c, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}


