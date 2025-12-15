package ui.launcher;

import hero.Hero;
import hero.Party;

import java.util.List;

/**
 * View for launching the game (mode + party selection).
 */
public interface LauncherView {
    int promptGameMode();

    Party promptPartySelection(List<Hero> availableHeroes, int minSize, int maxSize);

    void showFail(String msg);
}


