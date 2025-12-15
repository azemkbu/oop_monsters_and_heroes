package utils;

/**
 * Constants for attack range values.
 * Different hero classes and monster types have different base attack ranges.
 */
public final class RangeConstants {

    private RangeConstants() {}

    // Hero class ranges
    public static final int WARRIOR_RANGE = 1;    // Melee fighter
    public static final int PALADIN_RANGE = 1;    // Melee fighter
    public static final int SORCERER_RANGE = 2;   // Caster has slightly longer range

    // Monster type ranges
    public static final int DRAGON_RANGE = 2;       // Dragons have ranged fire breath
    public static final int SPIRIT_RANGE = 2;       // Spirits can attack from distance
    public static final int EXOSKELETON_RANGE = 1;  // Exoskeletons are melee
}

