package uk.co.stikman.dominion;

public interface IsReactionCard {
	/**
	 * If this returns <code>true</code> then the player defended against the
	 * attack and it should have no effect
	 * 
	 * @param turn
	 * @param instance
	 * @return
	 */
	boolean reactToAttack(Turn turn, Player victim, CardInstance thisCard, CardInstance attackingCard);
}
