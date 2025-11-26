package hero;

import utils.GameConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the group of {@link Hero} together on the map
 */
public class Party {

    private final List<Hero> heroes = new ArrayList<>();
    private final int maxSize;

    private int row;
    private int col;


    public Party() {
        this(GameConstants.PARTY_DEFAULT_MAX_SIZE);
    }

    public Party(int maxSize) {
        this.maxSize = maxSize;
    }

    public void addHero(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException("hero must not be null");
        }
        if (heroes.contains(hero)) {
            throw new IllegalArgumentException("hero is already in the party");
        }
        if (heroes.size() >= maxSize) {
            throw new IllegalStateException("party is full (max " + maxSize + " heroes)");
        }
        heroes.add(hero);
    }


    public boolean isEmpty() {
        return heroes.isEmpty();
    }


    public List<Hero> getHeroes() {
        return Collections.unmodifiableList(heroes);
    }


    public int getHighestLevel() {
        int max = 0;
        for (Hero hero : heroes) {
            if (hero.getLevel() > max) {
                max = hero.getLevel();
            }
        }
        return max;
    }

    public boolean allHeroesDefeated() {
        if (heroes.isEmpty()) {
            return false;
        }
        for (Hero hero : heroes) {
            if (hero.isAlive()) {
                return false;
            }
        }
        return true;
    }


    public int getMonsterCountForBattle() {
        return heroes.size();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }


    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Party at (")
                .append(row).append(", ").append(col).append(") with ")
                .append(heroes.size()).append(" hero(es):\n");

        for (Hero hero : heroes) {
            sb.append("  - ").append(hero.toString()).append("\n");
        }
        return sb.toString();
    }
}
