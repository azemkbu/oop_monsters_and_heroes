package ui.launcher;

import hero.Hero;
import hero.Party;
import utils.IOUtils;
import utils.MessageUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Console implementation of {@link LauncherView}.
 */
public final class ConsoleLauncherView implements LauncherView {
    private final IOUtils io;

    public ConsoleLauncherView(IOUtils io) {
        this.io = io;
    }

    @Override
    public int promptGameMode() {
        io.printlnHeader("Choose game mode:");
        io.printlnTitle("  1) Monsters and Heroes");
        io.printlnTitle("  2) Legends of Valor");
        io.printPrompt("Enter choice (1-2): ");
        return io.readIntInRange(1, 2);
    }

    @Override
    public Party promptPartySelection(List<Hero> availableHeroes, int minSize, int maxSize) {
        io.printlnTitle(MessageUtils.LIST_OF_HEROES_HEADER);
        for (int i = 0; i < availableHeroes.size(); i++) {
            Hero hero = availableHeroes.get(i);
            io.printlnTitle(String.format(" [%d] %s%n", i + 1, hero.toString()));
        }

        if (minSize == maxSize) {
            io.printlnTitle("Party size for this mode is fixed at " + minSize + ".");
        } else {
            io.printPrompt(String.format(MessageUtils.CHOOSE_YOUR_PARTY_MESSAGE, minSize, maxSize));
        }

        int partySize = (minSize == maxSize)
                ? minSize
                : io.readIntInRange(minSize, maxSize);

        Party party = new Party();
        Set<Hero> chosen = new HashSet<>();

        for (int i = 0; i < partySize; i++) {
            while (true) {
                io.printPrompt(String.format(MessageUtils.SELECT_HERO_BY_NUMBER, i + 1));
                int choice = io.readIntInRange(1, availableHeroes.size());
                Hero hero = availableHeroes.get(choice - 1);
                if (chosen.contains(hero)) {
                    io.printlnFail("You already selected that hero. Choose a different one.");
                    continue;
                }
                chosen.add(hero);
                party.addHero(hero);
                break;
            }
        }

        return party;
    }

    @Override
    public void showFail(String msg) {
        io.printlnFail(msg);
    }
}


