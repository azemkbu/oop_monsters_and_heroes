package utils;

/**
 * Utility class that stores all constants of the game
 */
public final class GameConstants {

    private GameConstants() {
    }

    // Constants for Hero
    public static final int HERO_HP_PER_LEVEL = 100;
    public static final int HERO_EXP_PER_LEVEL_MULTIPLIER = 10;
    public static final double HERO_BASE_LEVEL_UP_MULTIPLIER = 1.05;
    public static final double HERO_FAVORED_EXTRA_MULTIPLIER = 1.05;
    public static final double HERO_LEVEL_UP_MP_MULTIPLIER = 1.10;
    public static final double HERO_ROUND_RECOVERY_MULTIPLIER = 1.10;
    public static final double HERO_ATTACK_MULTIPLIER = 0.05;
    public static final double HERO_DODGE_MULTIPLIER = 0.0005;
    public static final double HERO_SPELL_DEX_DIVISOR = 10000.0;
    public static final int HERO_GOLD_PER_MONSTER_LEVEL = 100;
    public static final int HERO_EXP_PER_MONSTER = 2;
    public static final int PARTY_DEFAULT_MAX_SIZE = 3;
    public static final int PARTY_DEFAULT_MIN_SIZE = 1;
    public static final int PARTY_INITIAL_COL_POSITION = 0;
    public static final int PARTY_INITIAL_ROW_POSITION = 0;

    // One-handed weapon used with both hands gets +50% weapon damage
    public static final double ONE_HANDED_WEAPON_BONUS_MULTIPLIER = 1.5;

    // Constants for Monster
    public static final double MONSTER_DODGE_MULTIPLIER = 0.003;
    public static final double MONSTER_FAVORED_MULTIPLIER = 1.10;
    public static final double MAX_DODGE_CHANCE = 0.6;
    public static final double MONSTER_SKILL_LOSS_MULTIPLIER = 0.10;
    public static final double MONSTER_ATTACK_MULTIPLIER = 0.05;
    public static final int MONSTER_DEFENSE_DIVISOR = 100;
    public static final int MONSTER_MIN_DAMAGE_ON_HIT = 1;

    // Battle
    public static final double BATTLE_PROBABILITY = 0.3;

    // Market
    public static final double SELL_PRICE_MULTIPLIER = 0.5;
    public static final int MARKET_MENU_MIN_OPTION = 1;
    public static final int MARKET_MENU_MAX_OPTION = 4;


    // World Map (Monsters and Heroes)
    public static final double WORLD_MAP_INACCESSIBLE_RATIO = 0.20;
    public static final double WORLD_MAP_MARKET_RATIO = 0.30;
    public static final double WORLD_MAP_COMMON_RATIO = 0.50;
    public static final Integer WORLD_MAP_SIZE = 8;

    // Legends of Valor Map
    public static final int LOV_MAP_SIZE = 8;
    public static final int LOV_NUM_LANES = 3;
    public static final int LOV_HEROES_PER_TEAM = 3;
    public static final double LOV_TERRAIN_BONUS_MULTIPLIER = 0.10;  // 10% stat bonus
    public static final double LOV_BUSH_RATIO = 0.20;   // 20% Bush tiles
    public static final double LOV_CAVE_RATIO = 0.20;   // 20% Cave tiles
    public static final double LOV_KOULOU_RATIO = 0.20; // 20% Koulou tiles
    public static final double LOV_PLAIN_RATIO = 0.40;  // 40% Plain tiles

    // Legends of Valor Game Rules
    public static final int LOV_MONSTER_SPAWN_INTERVAL = 8;  // Spawn new monsters every N rounds
    public static final int LOV_GOLD_PER_MONSTER_LEVEL = 500; // Gold dropped by monsters
    public static final int LOV_EXP_PER_MONSTER = 2;          // Experience per monster kill

    //Data upload
    public static final String BASE_DIR_TO_UPLOAD_FILES = "files";
}