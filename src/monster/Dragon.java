package monster;

import monster.enums.MonsterAttribute;

import java.util.HashSet;
import java.util.Set;

/**
 * Concrete subclass of a {@link Monster} representing a Dragon monster
 */
public class Dragon extends Monster {

    public Dragon(String name,
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
}
