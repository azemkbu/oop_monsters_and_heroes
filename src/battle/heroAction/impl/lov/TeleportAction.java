package battle.heroAction.impl.lov;

import battle.heroAction.BattleContext;
import battle.heroAction.HeroActionStrategy;
import hero.Hero;
import monster.Monster;
import utils.IOUtils;
import utils.MessageUtils;
import worldMap.ILegendsWorldMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Teleport action for Legends of Valor
 */
public class TeleportAction implements HeroActionStrategy {

    private final ILegendsWorldMap worldMap;
    private final IOUtils io;

    public TeleportAction(ILegendsWorldMap worldMap, IOUtils io) {
        this.worldMap = worldMap;
        this.io = io;
    }

    @Override
    public void execute(Hero hero,
                        List<Monster> monsters,
                        BattleContext context,
                        IOUtils ignored) {
        worldMap.printMap();

        Hero targetHero = chooseTeleportTarget(hero);
        if (targetHero == null) {
            return;
        }

        boolean success = worldMap.teleportHero(hero, targetHero);
        if (!success) {
            io.printlnFail(MessageUtils.FAILED);
        } else {
            io.printlnSuccess(String.format(MessageUtils.TELEPORT_SUCCESS, hero.getName(), targetHero.getName()));
            worldMap.printMap();
        }
    }

    private Hero chooseTeleportTarget(Hero currentHero) {
        List<Hero> allHeroes = worldMap.getHeroes();
        int currentLane = worldMap.getHeroLane(currentHero);

        List<Hero> candidates = new ArrayList<>();
        for (Hero h : allHeroes) {
            if (h == currentHero || !h.isAlive()) {
                continue;
            }
            int lane = worldMap.getHeroLane(h);
            if (lane != -1 && lane != currentLane) {
                candidates.add(h);
            }
        }

        if (candidates.isEmpty()) {
            io.printlnFail(MessageUtils.TELEPORT_NO_VALID_HEROES);
            return null;
        }

        io.printlnTitle(MessageUtils.TELEPORT_CHOOSE_HERO);
        for (int i = 0; i < candidates.size(); i++) {
            Hero h = candidates.get(i);
            int[] pos = worldMap.getHeroPosition(h);
            int lane = worldMap.getHeroLane(h);

            io.printlnTitle(String.format( "  %d) %s  [lane=%d, row=%d, col=%d]",
                    i + 1,
                    h.getName(),
                    lane,
                    pos != null ? pos[0] : -1,
                    pos != null ? pos[1] : -1));
        }
        io.printlnTitle(MessageUtils.CANCEL_LINE);

        io.printPrompt(MessageUtils.ENTER_CHOICE);
        int choice = io.readIntInRange(0, candidates.size());

        if (choice == 0) {
            io.printlnFail(MessageUtils.CANCELED);
            return null;
        }

        return candidates.get(choice - 1);
    }
}

