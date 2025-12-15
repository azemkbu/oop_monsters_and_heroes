package hero;

import hero.enums.HeroSkill;
import hero.enums.HeroType;
import utils.RangeConstants;

import java.util.EnumSet;
import java.util.Set;

/**
 * Concrete subclass of a {@link Hero} representing a Paladin hero
 */
public class Paladin extends Hero {

    public Paladin(String name,
                   int level,
                   int strength,
                   int dexterity,
                   int agility,
                   int mp,
                   Wallet wallet,
                   int experience) {
        super(name, level, strength, dexterity, agility, mp, wallet, experience);
    }


    @Override
    protected Set<HeroSkill> getFavoredSkills() {
        return EnumSet.of(HeroSkill.STRENGTH, HeroSkill.DEXTERITY);
    }

    @Override
    public String getHeroClassName() {
        return HeroType.PALADIN.name();
    }

    @Override
    public int getBaseAttackRange() {
        return RangeConstants.PALADIN_RANGE;
    }
}
