package monster;

import entity.GamePiece;
import monster.enums.MonsterAttribute;
import utils.GameConstants;

import java.util.Set;

import static utils.GameConstants.HERO_HP_PER_LEVEL;

/**
 * Abstract class representing a Monster.
 * Implements GamePiece interface for unified position tracking on the game board.
 * 
 * ==================== DESIGN CHANGE LOG ====================
 * 
 * ADDED: GamePiece interface implementation
 * 
 * REASON:
 * - Hero and Monster needed a common abstraction for the game board
 * - WorldMap can now manage all pieces uniformly via GamePiece interface
 * - Follows Interface Segregation and Dependency Inversion principles
 * 
 * NEW FIELDS:
 * - row, col: Position tracking for Legends of Valor game mode
 * 
 * ===========================================================
 */
public abstract class Monster implements GamePiece {

    private final String name;
    private final int level;

    private int hp;
    private int baseDamage;
    private int defense;
    private int dodgeAbility;

    // Position tracking for GamePiece interface (used in Legends of Valor)
    private int row;
    private int col;

    protected Monster(String name,
                      int level,
                      int baseDamage,
                      int defense,
                      int dodgeAbility) {

        this.name = name;
        this.level = level;
        this.baseDamage = baseDamage;
        this.defense = defense;
        this.dodgeAbility = dodgeAbility;

        this.hp = getHpByLevel(level);

        applyFavoredAttributes();
    }

    protected abstract Set<MonsterAttribute> getFavoredAttributes();


    protected boolean isDamageFavored() {
        return getFavoredAttributes().contains(MonsterAttribute.DAMAGE);
    }

    protected boolean isDefenseFavored() {
        return getFavoredAttributes().contains(MonsterAttribute.DEFENSE);
    }

    protected boolean isDodgeFavored() {
        return getFavoredAttributes().contains(MonsterAttribute.DODGE_ABILITY);
    }

    private int getHpByLevel(int level) {
        return level * HERO_HP_PER_LEVEL;
    }

    private void applyFavoredAttributes() {
        if (isDamageFavored()) {
            baseDamage = (int) Math.round(baseDamage * GameConstants.MONSTER_FAVORED_MULTIPLIER);
        }
        if (isDefenseFavored()) {
            defense = (int) Math.round(defense * GameConstants.MONSTER_FAVORED_MULTIPLIER);
        }
        if (isDodgeFavored()) {
            dodgeAbility = (int) Math.round(dodgeAbility * GameConstants.MONSTER_FAVORED_MULTIPLIER);
        }
    }

    public int getBaseDamage() {
        return baseDamage;
    }


    public int computeAttackDamage() {
        int dmg = (int) Math.round(baseDamage * GameConstants.MONSTER_ATTACK_MULTIPLIER);
        if (dmg < GameConstants.MONSTER_MIN_DAMAGE_ON_HIT) {
            dmg = GameConstants.MONSTER_MIN_DAMAGE_ON_HIT;
        }
        return dmg;
    }


    public int takeDamage(int incomingDamage) {
        if (incomingDamage <= 0) {
            return 0;
        }

        int defenseMitigation = defense / GameConstants.MONSTER_DEFENSE_DIVISOR;
        int effectiveDamage = incomingDamage - defenseMitigation;

        if (effectiveDamage < GameConstants.MONSTER_MIN_DAMAGE_ON_HIT) {
            effectiveDamage = GameConstants.MONSTER_MIN_DAMAGE_ON_HIT;
        }

        hp -= effectiveDamage;
        if (hp < 0) {
            hp = 0;
        }

        return effectiveDamage;
    }


    public boolean isAlive() {
        return hp > 0;
    }


    public double getDodgeChance() {
        double chance = dodgeAbility * GameConstants.MONSTER_DODGE_MULTIPLIER;

        if (chance < 0.0) {
            chance = 0.0;
        }
        if (chance > GameConstants.MAX_DODGE_CHANCE) {
            chance = GameConstants.MAX_DODGE_CHANCE;
        }
        return chance;
    }

    public boolean dodgesAttack() {
        return Math.random() < getDodgeChance();
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getHp() {
        return hp;
    }

    public int getDefense() {
        return defense;
    }

    public int getDodgeAbility() {
        return dodgeAbility;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setBaseDamage(double baseDamage) {
        this.baseDamage = (int) Math.round(baseDamage);
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setDodgeAbility(int dodgeAbility) {
        this.dodgeAbility = dodgeAbility;
    }

    @Override
    public String toString() {
        return String.format(
                "%s (%s) Level %d | HP: %d, Damage: %d, Defense: %d, Dodge_ability: %d, Dodge_chance%%: %.2f",
                name,
                getClass().getSimpleName().toUpperCase(),
                level,
                hp,
                baseDamage,
                defense,
                dodgeAbility,
                getDodgeChance() * 100.0
        );
    }

    // ==================== GamePiece Interface Implementation ====================

    /**
     * Gets the current row position on the map.
     * @return the row index
     */
    @Override
    public int getRow() {
        return row;
    }

    /**
     * Gets the current column position on the map.
     * @return the column index
     */
    @Override
    public int getCol() {
        return col;
    }

    /**
     * Sets the position of this monster on the map.
     * @param row the row index
     * @param col the column index
     */
    @Override
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Identifies this piece as a monster.
     * @return true (this is always a Monster)
     */
    @Override
    public boolean isMonster() {
        return true;
    }
}
