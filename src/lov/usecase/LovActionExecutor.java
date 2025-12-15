package lov.usecase;

import battle.enums.EquipChoice;
import battle.enums.HeroActionType;
import lov.usecase.helper.LovRangeUtils;
import hero.Hero;
import market.model.item.Item;
import market.model.item.ItemType;
import market.model.item.Potion;
import market.model.item.Spell;
import market.model.item.Weapon;
import monster.Monster;
import utils.GameConstants;
import utils.MessageUtils;
import worldMap.ILegendsWorldMap;
import worldMap.Tile;
import worldMap.enums.Direction;

import lov.usecase.requests.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * UseCase layer executor for Legends of Valor actions.
 * No input/output here; returns {@link LovActionResult} for View to display.
 */
public final class LovActionExecutor {
    private final ILegendsWorldMap worldMap;

    public LovActionExecutor(ILegendsWorldMap worldMap) {
        this.worldMap = worldMap;
    }

    public LovActionResult execute(HeroActionType type, Hero hero, List<Monster> monsters, LovActionRequest request) {
        if (type == null) {
            return LovActionResult.builder().success(false).addFail(MessageUtils.UNKNOWN_COMMAND).build();
        }
        switch (type) {
            case MOVE:
                return doMove(hero, (MoveRequest) request);
            case TELEPORT:
                return doTeleport(hero, (TeleportRequest) request);
            case RECALL:
                return doRecall(hero);
            case REMOVE_OBSTACLE:
                return doRemoveObstacle(hero, (RemoveObstacleRequest) request);
            case ATTACK:
                return doAttack(hero, monsters, (AttackRequest) request);
            case CAST_SPELL:
                return doCastSpell(hero, monsters, (CastSpellRequest) request);
            case USE_POTION:
                return doUsePotion(hero, (UsePotionRequest) request);
            case EQUIP:
                return doEquip(hero, (EquipRequest) request);
            case SKIP:
                return LovActionResult.builder()
                        .success(true)
                        .addWarning(String.format(MessageUtils.SKIP_TURN, hero.getName()))
                        .build();
            default:
                return LovActionResult.builder().success(false).addFail(MessageUtils.UNKNOWN_COMMAND).build();
        }
    }

    private LovActionResult doMove(Hero hero, MoveRequest request) {
        Direction dir = request == null ? null : request.getDirection();
        if (dir == null) {
            return LovActionResult.builder().success(false).addFail(MessageUtils.CANCELED).build();
        }

        boolean moved = worldMap.moveHero(hero, dir);
        if (!moved) {
            return LovActionResult.builder()
                    .success(false)
                    .addFail(String.format(MessageUtils.TRY_ANOTHER_DIRECTION, dir))
                    .build();
        }

        return LovActionResult.builder()
                .success(true)
                .shouldRenderMap(true)
                .addSuccess(String.format(MessageUtils.SUCCESS_MOVE, hero.getName(), dir))
                .build();
    }

    private LovActionResult doTeleport(Hero hero, TeleportRequest request) {
        if (request == null || request.getTargetHero() == null) {
            return LovActionResult.builder().success(false).addFail(MessageUtils.CANCELED).build();
        }
        boolean ok = worldMap.teleportHero(hero, request.getTargetHero());
        if (!ok) {
            return LovActionResult.builder().success(false).addFail("Teleport failed.").build();
        }
        return LovActionResult.builder()
                .success(true)
                .shouldRenderMap(true)
                .addSuccess(String.format(MessageUtils.TELEPORT_SUCCESS, hero.getName(), request.getTargetHero().getName()))
                .build();
    }

    private LovActionResult doRecall(Hero hero) {
        worldMap.recallHero(hero);
        int lane = worldMap.getHeroLane(hero);
        int[] pos = worldMap.getHeroPosition(hero);
        return LovActionResult.builder()
                .success(true)
                .shouldRenderMap(true)
                .addSuccess(String.format(
                        MessageUtils.MSG_RECALL_SUCCESS,
                        hero.getName(),
                        lane,
                        pos == null ? -1 : pos[0],
                        pos == null ? -1 : pos[1]
                ))
                .build();
    }

    private LovActionResult doRemoveObstacle(Hero hero, RemoveObstacleRequest request) {
        Direction dir = request == null ? null : request.getDirection();
        if (dir == null) {
            return LovActionResult.builder().success(false).addFail(MessageUtils.CANCELED).build();
        }

        int newRow = hero.getRow() + dir.getRow();
        int newCol = hero.getCol() + dir.getCol();

        if (!worldMap.checkBounds(newRow, newCol)) {
            return LovActionResult.builder().success(false).addFail(String.format(MessageUtils.TRY_ANOTHER_DIRECTION, dir)).build();
        }

        Tile tile = worldMap.getTile(newRow, newCol);
        if (tile == null || !tile.isObstacle()) {
            return LovActionResult.builder().success(false).addFail(String.format(MessageUtils.TRY_ANOTHER_DIRECTION, dir)).build();
        }

        boolean removed = tile.removeObstacle();
        if (!removed) {
            return LovActionResult.builder().success(false).addFail(String.format(MessageUtils.TRY_ANOTHER_DIRECTION, dir)).build();
        }

        return LovActionResult.builder()
                .success(true)
                .shouldRenderMap(true)
                .addSuccess("Obstacle removed.")
                .build();
    }

    private LovActionResult doAttack(Hero hero, List<Monster> monsters, AttackRequest request) {
        if (monsters == null || monsters.isEmpty()) {
            return LovActionResult.builder().success(false).addWarning(MessageUtils.NO_MONSTERS_TO_ATTACK).build();
        }
        if (request == null || request.getTarget() == null) {
            return LovActionResult.builder().success(false).addWarning(MessageUtils.NO_MONSTER_SELECTED).build();
        }

        Monster monster = request.getTarget();
        if (!monster.isAlive()) {
            return LovActionResult.builder().success(false).addWarning(MessageUtils.NO_MONSTER_SELECTED).build();
        }
        if (!LovRangeUtils.isWithinRangeToAttack(hero, monster)) {
            return LovActionResult.builder().success(false).addWarning(MessageUtils.NO_ENEMIES_IN_RANGE).build();
        }

        LovActionResult.Builder res = LovActionResult.builder();

        int weaponDamage = 0;
        Weapon weapon = hero.getEquippedWeapon();
        if (weapon != null) {
            weaponDamage = weapon.getDamage();
            if (weapon.getHandsRequired() == 1) {
                Integer hands = request.getHandsForOneHandedWeapon();
                if (hands != null && hands == 2) {
                    weaponDamage = (int) Math.round(weaponDamage * GameConstants.ONE_HANDED_WEAPON_BONUS_MULTIPLIER);
                }
            }
        }

        double strengthMultiplier = 1.0;
        Tile tile = worldMap.getTile(hero.getRow(), hero.getCol());
        if (tile != null) {
            strengthMultiplier = tile.getStrengthMultiplier();
        }
        int effectiveStrength = (int) Math.round(hero.getStrength() * strengthMultiplier);
        int damage = (int) Math.round((effectiveStrength + weaponDamage) * GameConstants.HERO_ATTACK_MULTIPLIER);

        if (monster.dodgesAttack()) {
            return res.success(false)
                    .addWarning(String.format(MessageUtils.ATTACK_WAS_DODGED, hero.getName(), monster.getName()))
                    .build();
        }

        int dealtDamage = monster.takeDamage(damage);
        res.addWarning(String.format(MessageUtils.SUCCESSFUL_ATTACK, hero.getName(), monster.getName(), dealtDamage));

        if (weapon != null) {
            weapon.consumeUse();
            if (!weapon.isUsable()) {
                res.addFail(String.format(MessageUtils.ITEM_CAN_NO_LONGER_BE_USED, weapon.getName()));
            } else {
                res.addWarning(String.format(MessageUtils.REMAINING_ITEM_USES, weapon.getName(), weapon.getUsesRemaining()));
            }
        }

        if (!monster.isAlive()) {
            res.addWarning(String.format(MessageUtils.CHARACTER_DEFEATED, monster.getName()));
        }

        return res.success(true).build();
    }

    private LovActionResult doCastSpell(Hero hero, List<Monster> monsters, CastSpellRequest request) {
        LovActionResult.Builder res = LovActionResult.builder();

        if (request == null || request.getSpell() == null) {
            return res.success(false).addWarning(String.format(MessageUtils.NO_ITEM_SELECTED, ItemType.SPELL)).build();
        }
        Spell spell = request.getSpell();

        if (hero.getMp() < spell.getManaCost()) {
            return res.success(false)
                    .addWarning(String.format(MessageUtils.NOT_ENOUGH_MP_TO_CAST_SPELL, hero.getName(), spell.getName()))
                    .build();
        }

        if (monsters == null || monsters.isEmpty()) {
            return res.success(false).addWarning(MessageUtils.NO_MONSTERS_TO_CAST_SPELL).build();
        }

        Monster target = request.getTarget();
        if (target == null) {
            return res.success(false).addWarning(MessageUtils.NO_MONSTER_SELECTED).build();
        }
        if (!target.isAlive()) {
            return res.success(false).addWarning(MessageUtils.NO_MONSTER_SELECTED).build();
        }
        if (!LovRangeUtils.isWithinRangeToAttack(hero, target)) {
            return res.success(false).addWarning(MessageUtils.NO_ENEMIES_IN_RANGE).build();
        }

        // Consume MP + spell item, regardless of dodge (consistent with current behavior)
        hero.setMp(hero.getMp() - spell.getManaCost());
        hero.getInventory().remove(spell);

        int finalDamage = calculateSpellDamageWithTerrain(hero, target, spell);

        // Dodge check
        double dodgeProb = target.getDodgeChance() * GameConstants.MONSTER_DODGE_MULTIPLIER;
        double roll = ThreadLocalRandom.current().nextDouble();
        if (roll < dodgeProb) {
            res.addWarning(String.format(MessageUtils.MONSTER_DODGED_SPELL, target.getName()));
            return res.success(true).build();
        }

        target.setHp(Math.max(0, target.getHp() - finalDamage));
        applySpellDebuff(target, spell);

        res.addSuccess(String.format(
                MessageUtils.SUCCESSFUL_SPELL_CAST,
                hero.getName(),
                spell.getName(),
                target.getName(),
                finalDamage
        ));

        if (target.getHp() == 0) {
            res.addSuccess(MessageUtils.HEROES_DEFEAT_MONSTERS);
        }

        return res.success(true).build();
    }

    private int calculateSpellDamageWithTerrain(Hero hero, Monster monster, Spell spell) {
        double dexMultiplier = 1.0;
        Tile tile = worldMap.getTile(hero.getRow(), hero.getCol());
        if (tile != null) {
            dexMultiplier = tile.getDexterityMultiplier();
        }

        double baseDamage = spell.getDamage();
        double dexterity = hero.getDexterity() * dexMultiplier;
        double spellDamage = baseDamage + (dexterity / GameConstants.HERO_SPELL_DEX_DIVISOR) * baseDamage;

        double effectiveDamage = spellDamage - monster.getDefense();
        return (int) Math.max(0, Math.round(effectiveDamage));
    }

    private void applySpellDebuff(Monster monster, Spell spell) {
        double remainingFactor = 1.0 - GameConstants.MONSTER_SKILL_LOSS_MULTIPLIER;
        switch (spell.getType()) {
            case ICE: {
                double damage = monster.getBaseDamage();
                monster.setBaseDamage(damage * remainingFactor);
                break;
            }
            case FIRE: {
                double defense = monster.getDefense();
                monster.setDefense((int) (defense * remainingFactor));
                break;
            }
            case LIGHTNING: {
                int dodge = (int) Math.round(monster.getDodgeAbility() * remainingFactor);
                monster.setDodgeAbility(dodge);
                break;
            }
            default:
                break;
        }
    }

    private LovActionResult doUsePotion(Hero hero, UsePotionRequest request) {
        LovActionResult.Builder res = LovActionResult.builder();

        Potion potion = request == null ? null : request.getPotion();
        if (potion == null) {
            return res.success(false).addWarning(String.format(MessageUtils.NO_ITEM_SELECTED, ItemType.POTION)).build();
        }

        // Validate potion exists in inventory
        boolean found = false;
        for (Item item : hero.getInventory()) {
            if (item == potion) {
                found = true;
                break;
            }
        }
        if (!found) {
            return res.success(false).addWarning(String.format(MessageUtils.NO_ITEM_SELECTED, ItemType.POTION)).build();
        }

        double amount = potion.getEffectAmount();
        switch (potion.getStatType()) {
            case HP:
                hero.setHp((int) (hero.getHp() + amount));
                break;
            case MP:
                hero.setMp((int) (hero.getMp() + amount));
                break;
            case STRENGTH:
                hero.setStrength((int) (hero.getStrength() + amount));
                break;
            case DEXTERITY:
                hero.setDexterity((int) (hero.getDexterity() + amount));
                break;
            case AGILITY:
                hero.setAgility((int) (hero.getAgility() + amount));
                break;
            default:
                break;
        }

        hero.getInventory().remove(potion);

        res.addSuccess(String.format(
                MessageUtils.SUCCESSFUL_POTION_USE_MESSAGE,
                hero.getName(),
                potion.getName(),
                potion.getStatType().name(),
                (int) amount
        ));

        return res.success(true).build();
    }

    private LovActionResult doEquip(Hero hero, EquipRequest request) {
        LovActionResult.Builder res = LovActionResult.builder();

        if (request == null || request.getChoice() == null) {
            return res.success(false).addFail(MessageUtils.CANCELED).build();
        }

        EquipChoice choice = request.getChoice();
        switch (choice) {
            case WEAPON:
                if (request.getWeapon() == null) {
                    return res.success(false).addWarning(String.format(MessageUtils.NO_ITEM_SELECTED, ItemType.WEAPON.name())).build();
                }
                hero.equipWeapon(request.getWeapon());
                return res.success(true).addSuccess(String.format(MessageUtils.SUCCESSFUL_EQUIP, hero.getName(), request.getWeapon().getName())).build();
            case ARMOR:
                if (request.getArmor() == null) {
                    return res.success(false).addWarning(String.format(MessageUtils.NO_ITEM_SELECTED, ItemType.ARMOR.name())).build();
                }
                hero.equipArmor(request.getArmor());
                return res.success(true).addSuccess(String.format(MessageUtils.SUCCESSFUL_EQUIP, hero.getName(), request.getArmor().getName())).build();
            case CANCEL:
            default:
                return res.success(false).addFail(MessageUtils.CANCELED).build();
        }
    }
}


