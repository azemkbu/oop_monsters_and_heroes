package monster;

import monster.enums.MonsterAttribute;
import utils.RangeConstants;

import java.util.HashSet;
import java.util.Set;

/**
 * Concrete subclass of a {@link Monster} representing a Spirit monster
 */
public class Spirit extends Monster {

    public Spirit(String name,
                  int level,
                  int baseDamage,
                  int defense,
                  int dodgeAbility) {
        super(name, level, baseDamage, defense, dodgeAbility);
    }

    @Override
    protected Set<MonsterAttribute> getFavoredAttributes() {
        Set<MonsterAttribute> attributes = new HashSet<>();
        attributes.add(MonsterAttribute.DAMAGE);
        return attributes;
    }

    @Override
    public int getBaseAttackRange() {
        return RangeConstants.SPIRIT_RANGE;
    }
}
