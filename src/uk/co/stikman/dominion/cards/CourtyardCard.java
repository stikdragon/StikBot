package uk.co.stikman.dominion.cards;

import uk.co.stikman.dominion.Card;
import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.DominionError;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsInteractiveCard;
import uk.co.stikman.dominion.Player;
import uk.co.stikman.dominion.Turn;
import uk.co.stikman.tokeniser.NoMoreTokensError;
import uk.co.stikman.tokeniser.TokenList;


/**
 * +3 Cards, then choose a card to put back on top of your deck
 * 
 * @author Stik
 *
 */
public class CourtyardCard extends BasicActionCard implements IsInteractiveCard {

	private enum State {
		WAIT_FOR_CHOICE,
		FINISHED
	}

	private State state; 

	
	
	public CourtyardCard(DominionGame game, String name, String shortcut, int cost, int spend, int actions, int buys, int cards) {
		super(game, name, shortcut, cost, spend, actions, buys, cards);
	}
	
	

	public boolean needsInput() {
		return state != State.FINISHED;
	}
	
	@Override
	public boolean processResponse(Player player, Turn turn, TokenList tokens) {
		if (!player.equals(playedOn.getPlayer()))
			return false;
		
				
		if (state == State.WAIT_FOR_CHOICE) {
			String card;
			try {
				card = tokens.nextString();
			} catch (NoMoreTokensError e) {
				throw new DominionError("Invalid command", e);
			}
			
			Card ct = getGame().getCardSet().findCard(card);

			CardInstance c = turn.findInHand(ct, false);
			if (c == null)
				throw new DominionError("You don't have that card in your hand");
			turn.removeFromHand(c);
			player.getDeck().add(0, c);
			state = State.FINISHED;
			return true;
		}
		return false;
	}
	
	public void describe(DominionOutput output) {
		describeNameType(output);

		output.soft(" (").out("+3 cards, then choose 1 card from your hand to put back on top of your deck");
		output.soft(")");
	}




	@Override
	public void playOn(Turn turn, CardInstance instance) {
		super.playOn(turn, instance);
		state = State.WAIT_FOR_CHOICE;
	}



	@Override
	public void askQuestion(DominionOutput response) {
		if (state == State.WAIT_FOR_CHOICE) { 
			response.out(playedOn.getPlayer().getName()).out(": pick a card from your hand to place on the top of your deck - ");
			getGame().showCardSet(playedOn.getHand(), true, false);
		}
	}
	
	
	
}
