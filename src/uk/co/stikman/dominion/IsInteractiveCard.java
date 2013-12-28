package uk.co.stikman.dominion;

import uk.co.stikman.tokeniser.TokenList;

public interface IsInteractiveCard {

	boolean needsInput();

	void askQuestion(DominionOutput response);

	/**
	 * Can be called with different players. <code>turn</code> contains a
	 * reference to the player that owns the current turn, but other player's
	 * text will come here too. Returns whether or not it was handled.
	 * 
	 * <p>
	 * Generally if <code>player.equals(turn.getPlayer())</code> then the method
	 * should return <code>true</code> (if it's an interactive card)
	 * 
	 * @param player
	 * @param turn
	 * @param tokens
	 * @return
	 */
	boolean processResponse(Player player, Turn turn, TokenList tokens);
}
