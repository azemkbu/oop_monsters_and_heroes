package monster;

import monster.enums.MonsterAttribute;

import java.util.HashSet;
import java.util.Set;

/**
 * Concrete subclass of a {@link Monster} representing an Exoskeleton monster
 */
public class Exoskeleton extends Monster {

    public Exoskeleton(String name,
                       int level,
                       int baseDamage,
                       int defense,
                       int dodgeAbility) {
        super(name, level, baseDamage, defense, dodgeAbility);
    }

    @Override
    protected Set<MonsterAttribute> getFavoredAttributes() {
        Set<MonsterAttribute> attributes = new HashSet<>();
        attributes.add(MonsterAttribute.DEFENSE);
        return attributes;
    }
}
