package uk.co.stikman.dominion;

public interface IsActionCard {

	/**
	 * Should throw an exception if it can't be played. Note that at the time
	 * this is called the current turn's hand will still contain
	 * <code>instance</code> - the card that this method is called on. It hasn't
	 * been played yet
	 * 
	 * @param turn
	 * @param instance
	 * @throws DominionError
	 */
	void checkCanPlay(Turn turn, CardInstance instance) throws DominionError;

	void playOn(Turn turn, CardInstance instance);

}
