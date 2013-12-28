package uk.co.stikman.dominion;

public interface ProducesTreasure {
	/**
	 * Indicates that this card produces some value when it's in play. 
	 * 
	 * <p><code>turn</code> will be the current turn. If it's <code>null</code>
	 * then it's up the card to decide what it's going to return, generally if
	 * the card relies on the current state of a player's hand then it won't be
	 * able to return a meaningful number anyhow, in which case it should return
	 * <code>-1</code>
	 * 
	 * @param turn
	 * @return
	 */
	int getTreasureValue(Turn turn);
}
