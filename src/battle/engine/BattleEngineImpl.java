package battle.engine;

import battle.heroAction.BattleContext;
import game.GameType;
import battle.menu.BattleMenu;
import battle.enums.HeroActionType;
import battle.heroAction.HeroActionStrategy;
import battle.heroAction.BattleActionsConfig;
import hero.Hero;
import hero.Party;
import monster.Monster;
import monster.MonsterFactory;
import utils.MessageUtils;
import utils.GameConstants;
import utils.IOUtils;
import worldMap.IWorldMap;
import worldMap.ILegendsWorldMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Implementation of {@link BattleEngine} interface
 **/

public class BattleEngineImpl implements BattleEngine {

    private final BattleMenu battleMenu;
    private final IOUtils ioUtils;
    private final MonsterFactory monsterFactory;

    public BattleEngineImpl(BattleMenu battleMenu, IOUtils ioUtils,
                            MonsterFactory monsterFactory) {
        this.battleMenu = battleMenu;
        this.ioUtils = ioUtils;
        this.monsterFactory = monsterFactory;
    }

    @Override
    public boolean runBattle(Party party, IWorldMap iWorldMap) {
        GameType gameType = (iWorldMap instanceof ILegendsWorldMap)
                ? GameType.LEGENDS_OF_VALOR
                : GameType.MONSTERS_AND_HEROES;
        HashMap<HeroActionType, HeroActionStrategy> actions = (HashMap<HeroActionType, HeroActionStrategy>)
                BattleActionsConfig.createActions(gameType, iWorldMap, ioUtils);

        List<Monster> monsters = monsterFactory.createMonstersForParty(party);

        List<Monster> originalMonsters = new ArrayList<>(monsters);

        List<Hero> heroes = new ArrayList<>(party.getHeroes());
        battleMenu.showBattleStatus(heroes, monsters);

        BattleContext context = new BattleContext(battleMenu);

        while (true) {
            for (Hero hero : heroes) {
                if (!hero.isAlive()) {
                    continue;
                }

                if (allMonstersDefeated(monsters)) {
                    ioUtils.printlnSuccess(MessageUtils.HEROES_DEFEAT_MONSTERS);
                    grantBattleRewards(party, originalMonsters);
                    applyPostBattleRecovery(party);
                    return true;
                }

                HeroActionType actionType = battleMenu.chooseActionForHero(hero, monsters);

                if (actionType == HeroActionType.SKIP) {
                    ioUtils.printlnWarning(String.format(MessageUtils.SKIP_TURN, hero.getName()));
                } else {
                    HeroActionStrategy strategy = actions.get(actionType);
                    if (strategy != null) {
                        strategy.execute(hero, monsters, context, ioUtils);
                    } else {
                        ioUtils.printlnFail(MessageUtils.UNKNOWN_COMMAND);
                    }
                }

                removeDeadMonsters(monsters);
                battleMenu.showBattleStatus(heroes, monsters);

                if (allMonstersDefeated(monsters)) {
                    ioUtils.printlnSuccess(MessageUtils.HEROES_DEFEAT_MONSTERS);
                    grantBattleRewards(party, originalMonsters);
                    applyPostBattleRecovery(party);
                    return true;
                }
            }

            // --- Monsters' turn ---
            for (Monster monster : monsters) {
                if (!monster.isAlive()) {
                    continue;
                }

                Hero hero = pickRandomAliveHero(heroes);
                if (hero == null) {
                    ioUtils.printlnFail(MessageUtils.MONSTERS_DEFEAT_HEROES);
                    applyPostBattleRecovery(party);
                    return false;
                }

                int damage = monster.computeAttackDamage();
                boolean dodged = hero.dodgesAttack();
                if (dodged) {
                    ioUtils.printlnWarning(String.format(MessageUtils.ATTACK_WAS_DODGED, monster.getName(), hero.getName()));
                } else {
                    hero.takeDamage(damage);
                    ioUtils.printlnWarning(String.format(MessageUtils.SUCCESSFUL_ATTACK,  monster.getName(), hero.getName(), damage));
                }

                if (!hero.isAlive()) {
                    ioUtils.printlnWarning(String.format(MessageUtils.CHARACTER_FAINTED, hero.getName()));
                }
            }

            if (party.allHeroesDefeated()) {
                ioUtils.printlnFail(MessageUtils.MONSTERS_DEFEAT_HEROES);
                applyPostBattleRecovery(party);
                return false;
            }

            for (Hero hero : heroes) {
                if (hero.isAlive()) {
                    hero.recoverAfterRound();
                }
            }

            battleMenu.showBattleStatus(heroes, monsters);
        }
    }

    private boolean allMonstersDefeated(List<Monster> monsters) {
        for (Monster m : monsters) {
            if (m.isAlive()) {
                return false;
            }
        }
        return true;
    }

    private void removeDeadMonsters(List<Monster> monsters) {
        monsters.removeIf(m -> !m.isAlive());
    }

    private Hero pickRandomAliveHero(List<Hero> heroes) {
        List<Hero> alive = new ArrayList<>();
        for (Hero h : heroes) {
            if (h.isAlive()) {
                alive.add(h);
            }
        }
        if (alive.isEmpty()) {
            return null;
        }
        int idx = (int) (Math.random() * alive.size());
        return alive.get(idx);
    }

    private void applyPostBattleRecovery(Party party) {
        for (Hero hero : party.getHeroes()) {
            if (hero.isAlive()) {
                continue;
            }
            int maxHp = hero.getLevel() * GameConstants.HERO_HP_PER_LEVEL;
            int revivedHp = maxHp / 2;
            if (revivedHp < 1) {
                revivedHp = 1;
            }
            hero.setHp(revivedHp);
        }
    }

    private void grantBattleRewards(Party party, List<Monster> originalMonsters) {
        int numberOfMonsters = originalMonsters.size();

        int highestMonsterLevel = 0;
        for (Monster m : originalMonsters) {
            if (m.getLevel() > highestMonsterLevel) {
                highestMonsterLevel = m.getLevel();
            }
        }

        for (Hero hero : party.getHeroes()) {
            boolean heroFainted = !hero.isAlive();
            hero.rewardFromBattle(highestMonsterLevel, numberOfMonsters, heroFainted);
        }
    }
}
