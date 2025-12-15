package hero;

import entity.GamePiece;
import hero.enums.HeroSkill;
import java.util.List;
import java.util.Set;
import market.model.item.Armor;
import market.model.item.Item;
import market.model.item.Weapon;
import utils.GameConstants;
import static utils.GameConstants.*;

/**
 * Abstract class representing a Hero.
 * Implements GamePiece interface for unified position tracking on the game board.
 */
public abstract class Hero implements GamePiece {

    private final String name;
    private int level;
    private int experience;

    private int hp;
    private int mp;
    private int maxMp;

    private int strength;
    private int dexterity;
    private int agility;

    private final Wallet wallet;
    private final Inventory inventory;

    private Weapon equippedWeapon;
    private Armor equippedArmor;

    // Position tracking for GamePiece interface (used in Legends of Valor)
    private int row;
    private int col;

    protected Hero(String name,
                   int level,
                   int strength,
                   int dexterity,
                   int agility,
                   int mp,
                   Wallet wallet,
                   int experience) {
        this.name = name;
        this.level = level;
        this.strength = strength;
        this.dexterity = dexterity;
        this.agility = agility;
        this.maxMp = Math.max(0, mp);
        this.mp = this.maxMp;
        this.hp = computeHpForLevel(level);
        this.experience = experience;
        this.wallet = wallet;
        this.inventory = new Inventory();
        applyInitialFavoredSkills();
    }


    protected abstract Set<HeroSkill> getFavoredSkills();

    public abstract String getHeroClassName();

    private int computeHpForLevel(int lvl) {
        return lvl * HERO_HP_PER_LEVEL;
    }

    private int experienceToNextLevel() {
        return level * HERO_EXP_PER_LEVEL_MULTIPLIER;
    }

    protected boolean isStrengthFavored() {
        return getFavoredSkills().contains(HeroSkill.STRENGTH);
    }

    protected boolean isDexterityFavored() {
        return getFavoredSkills().contains(HeroSkill.DEXTERITY);
    }

    protected boolean isAgilityFavored() {
        return getFavoredSkills().contains(HeroSkill.AGILITY);
    }


    private void applyInitialFavoredSkills() {
        if (isStrengthFavored()) {
            strength = (int) Math.round(
                    strength * (HERO_BASE_LEVEL_UP_MULTIPLIER * HERO_FAVORED_EXTRA_MULTIPLIER));
        }
        if (isDexterityFavored()) {
            dexterity = (int) Math.round(
                    dexterity * (HERO_BASE_LEVEL_UP_MULTIPLIER * HERO_FAVORED_EXTRA_MULTIPLIER));
        }
        if (isAgilityFavored()) {
            agility = (int) Math.round(
                    agility * (HERO_BASE_LEVEL_UP_MULTIPLIER * HERO_FAVORED_EXTRA_MULTIPLIER));
        }
    }

    public void gainExperience(int amount) {
        if (amount <= 0) {
            return;
        }

        experience += amount;
        while (experience >= experienceToNextLevel()) {
            experience -= experienceToNextLevel();
            levelUp();
        }
    }

    private void levelUp() {
        level++;

        strength = applyLevelUpToStat(strength, isStrengthFavored());
        dexterity = applyLevelUpToStat(dexterity, isDexterityFavored());
        agility = applyLevelUpToStat(agility, isAgilityFavored());

        hp = computeHpForLevel(level);
        maxMp = (int) Math.round(maxMp * HERO_LEVEL_UP_MP_MULTIPLIER);
        if (maxMp < 0) maxMp = 0;
        mp = maxMp;
    }

    private int applyLevelUpToStat(int current, boolean favored) {
        double multiplier = HERO_BASE_LEVEL_UP_MULTIPLIER;

        if (favored) {
            multiplier *= HERO_FAVORED_EXTRA_MULTIPLIER;
        }

        return (int) Math.round(current * multiplier);
    }

    public void recoverAfterRound() {
        int maxHp = level * HERO_HP_PER_LEVEL;

        hp = (int) Math.round(hp * HERO_ROUND_RECOVERY_MULTIPLIER);
        if (hp > maxHp) {
            hp = maxHp;
        }

        mp = (int) Math.round(mp * HERO_ROUND_RECOVERY_MULTIPLIER);
        if (mp > maxMp) {
            mp = maxMp;
        }
    }

    public int getMaxHp() {
        return level * HERO_HP_PER_LEVEL;
    }

    public int getMaxMp() {
        return maxMp;
    }


    public double getDodgeChance() {
        double chance = agility * GameConstants.HERO_DODGE_MULTIPLIER;

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

    public boolean isAlive() {
        return hp > 0;
    }

    public void takeDamage(int incomingDamage) {
        int effectiveDamage = incomingDamage;

        Armor armor = getEquippedArmor();
        if (armor != null && armor.isUsable()) {
            effectiveDamage -= armor.getDamageReduction();
            if (effectiveDamage < 0) {
                effectiveDamage = 0;
            }

            // Armor durability is consumed on each hit.
            armor.consumeUse();
            if (!armor.isUsable()) {
                // Broken armor no longer provides protection on subsequent hits.
                this.equippedArmor = null;
            }
        }

        hp = Math.max(0, hp - effectiveDamage);
    }


    public void rewardFromBattle(int monsterLevel,
                                 int numberOfMonsters,
                                 boolean heroFainted) {
        if (!heroFainted) {
            wallet.addGold(monsterLevel * HERO_GOLD_PER_MONSTER_LEVEL);
        }
        gainExperience(numberOfMonsters * HERO_EXP_PER_MONSTER);
    }

    public List<Item> getInventory() {
        return inventory.getItems();
    }

    public boolean hasItem(Item item) {
        return inventory.hasItem(item);
    }

    public void addItem(Item item) {
        inventory.addItem(item);
    }

    public void removeItem(Item item) {
        inventory.removeItem(item);
    }


    public int getGold() {
        return wallet.getGold();
    }

    public void addGold(int amount) {
        wallet.addGold(amount);
    }


    public void spendGold(int amount) {
        wallet.spendGold(amount);
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

    public int getMp() {
        return mp;
    }

    public int getStrength() {
        return strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getAgility() {
        return agility;
    }

    public void setMp(int mp) {
        if (mp < 0) {
            this.mp = 0;
            return;
        }
        this.mp = Math.min(mp, maxMp);
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public Armor getEquippedArmor() {
        return equippedArmor;
    }


    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
    }


    public void equipArmor(Armor armor) {
        this.equippedArmor = armor;
    }

    @Override
    public String toString() {
        return String.format(
                "%s (%s) Level: %d | HP: %d, MP: %d, Strength: %d, Dexterity: %d, Agility: %d, Gold: %d, Experience : %d",
                name, getHeroClassName(), level, hp, mp, strength, dexterity, agility, wallet.getGold(), experience
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
     * Sets the position of this hero on the map.
     * @param row the row index
     * @param col the column index
     */
    @Override
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Identifies this piece as a hero.
     * @return true (this is always a Hero)
     */
    @Override
    public boolean isHero() {
        return true;
    }

}
