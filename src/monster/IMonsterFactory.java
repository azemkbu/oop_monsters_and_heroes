package monster;

import hero.Party;

import java.util.List;

/**
 * Port for creating monsters (may be random / data-driven).
 * Allows deterministic implementations for tests.
 */
public interface IMonsterFactory {
    List<Monster> createMonstersForParty(Party party);
}


