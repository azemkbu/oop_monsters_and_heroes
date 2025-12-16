package game;

import hero.Hero;
import hero.Party;
import utils.IOUtils;
import utils.MessageUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PartyFactoryUtil {
    private PartyFactoryUtil() {}

    public static Party chooseParty(List<Hero> availableHeroes,
                                   IOUtils ioUtils,
                                   int minSize,
                                   int maxSize) {

        ioUtils.printlnTitle(MessageUtils.LIST_OF_HEROES_HEADER);

        for (int i = 0; i < availableHeroes.size(); i++) {
            Hero hero = availableHeroes.get(i);
            ioUtils.printlnTitle(String.format(" [%d] %s%n", i + 1, hero.toString()));
        }

        if (minSize == maxSize) {
            ioUtils.printlnTitle("Party size for this mode is fixed at " + minSize + ".");
        } else {
            ioUtils.printPrompt(String.format(MessageUtils.CHOOSE_YOUR_PARTY_MESSAGE, minSize, maxSize));
        }

        int partySize = (minSize == maxSize)
                ? minSize
                : ioUtils.readIntInRange(minSize, maxSize);

        Party party = new Party();
        Set<Hero> chosen = new HashSet<>();

        for (int i = 0; i < partySize; i++) {
            while (true) {
                ioUtils.printPrompt(String.format(MessageUtils.SELECT_HERO_BY_NUMBER, i + 1));
                int choice = ioUtils.readIntInRange(1, availableHeroes.size());
                Hero hero = availableHeroes.get(choice - 1);
                if (chosen.contains(hero)) {
                    ioUtils.printlnFail("You already selected that hero. Choose a different one.");
                    continue;
                }
                chosen.add(hero);
                party.addHero(hero);
                break;
            }
        }

        return party;
    }
}
