package monster;

import hero.Party;
import upload.MonsterFileLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Factory responsible for creating list of {@link Monster} for a battle
 */
public class MonsterFactory implements IMonsterFactory {

    private final Random random = new Random();
    private final List<Monster> monsterPool;

    public MonsterFactory() {
        this.monsterPool = MonsterFileLoader.loadAllMonsters();
        if (monsterPool.isEmpty()) {
            throw new IllegalStateException("No monsters loaded from files");
        }
    }

    @Override
    public List<Monster> createMonstersForParty(Party party) {
        int count = party.getMonsterCountForBattle();
        int monsterLevel = party.getHighestLevel();

        List<Monster> monsters = new ArrayList<Monster>(count);
        for (int i = 0; i < count; i++) {
            Monster template = chooseRandomMonsterTemplate();
            Monster battleMonster = cloneMonsterForBattle(template, monsterLevel, i + 1);
            monsters.add(battleMonster);
        }

        return monsters;
    }

    private Monster chooseRandomMonsterTemplate() {
        int index = random.nextInt(monsterPool.size());
        return monsterPool.get(index);
    }


    private Monster cloneMonsterForBattle(Monster template, int targetLevel, int index) {
        String name = template.getName() + "#" + index;

        int level = targetLevel;

        if (template instanceof Dragon) {
            Dragon d = (Dragon) template;
            return new Dragon(
                    name,
                    level,
                    d.getBaseDamage(),
                    d.getDefense(),
                    d.getDodgeAbility()
            );
        } else if (template instanceof Exoskeleton) {
            Exoskeleton e = (Exoskeleton) template;
            return new Exoskeleton(
                    name,
                    level,
                    e.getBaseDamage(),
                    e.getDefense(),
                    e.getDodgeAbility()
            );
        } else if (template instanceof Spirit) {
            Spirit s = (Spirit) template;
            return new Spirit(
                    name,
                    level,
                    s.getBaseDamage(),
                    s.getDefense(),
                    s.getDodgeAbility()
            );
        } else {
            throw new IllegalArgumentException("Unknown monster type: " + template.getClass());
        }
    }
}
