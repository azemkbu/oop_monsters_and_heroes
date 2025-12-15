package hero;

import hero.enums.HeroSkill;
import hero.enums.HeroType;
import utils.RangeConstants;

import java.util.EnumSet;
import java.util.Set;

/**
 * Concrete subclass of a {@link Hero} representing a Warrior hero
 */
public class Warrior extends Hero {

    public Warrior(String name,
                   int level,
                   int strength,
                   int dexterity,
                   int agility,
                   int mp, Wallet wallet, int exp) {
        super(name, level, strength, dexterity, agility, mp, wallet, exp);
    }

    @Override
    protected Set<HeroSkill> getFavoredSkills() {
        return EnumSet.of(HeroSkill.STRENGTH, HeroSkill.AGILITY);
    }

    @Override
    public String getHeroClassName() {
        return HeroType.WARRIOR.name();
    }

    @Override
    public int getBaseAttackRange() {
        return RangeConstants.WARRIOR_RANGE;
    }
}
