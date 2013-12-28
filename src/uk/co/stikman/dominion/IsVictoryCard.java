package uk.co.stikman.dominion;

public interface IsVictoryCard {

	/**
	 * Return the value of this victory card. The value can depend on the
	 * player's entire deck, so you can calculate it from the
	 * <code>playerCards</code> list
	 * 
	 * @param playerCards
	 * @return
	 */
	int getVictoryPoints(CardList playerCards);

}
