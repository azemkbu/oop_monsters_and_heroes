package hero;

import hero.enums.HeroSkill;
import hero.enums.HeroType;
import utils.RangeConstants;

import java.util.EnumSet;
import java.util.Set;

/**
 * Concrete subclass of a {@link Hero} representing a Sorcerer hero
 */
public class Sorcerer extends Hero {

    public Sorcerer(String name,
                    int level,
                    int strength,
                    int dexterity,
                    int agility,
                    int mp, Wallet wallet, int exp) {
        super(name, level, strength, dexterity, agility, mp, wallet, exp);
    }

    @Override
    protected Set<HeroSkill> getFavoredSkills() {
        return EnumSet.of(HeroSkill.AGILITY, HeroSkill.DEXTERITY);
    }

    @Override
    public String getHeroClassName() {
        return HeroType.SORCERER.name();
    }

    @Override
    public int getBaseAttackRange() {
        return RangeConstants.SORCERER_RANGE;
    }
}
