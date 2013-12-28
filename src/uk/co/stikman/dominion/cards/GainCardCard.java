package uk.co.stikman.dominion.cards;

import uk.co.stikman.dominion.Card;
import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.CardList;
import uk.co.stikman.dominion.DominionError;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsInteractiveCard;
import uk.co.stikman.dominion.Player;
import uk.co.stikman.dominion.Turn;
import uk.co.stikman.tokeniser.NoMoreTokensError;
import uk.co.stikman.tokeniser.TokenList;

public class GainCardCard extends BasicActionCard implements IsInteractiveCard {
	
	private enum State {
		WAIT_FOR_CHOICE, FINISHED
	}

	private State	state;
	protected int	costUpTo;

	public GainCardCard(DominionGame game, String name, String shortcut, int cost, int spend, int actions, int buys, int cards, int upto) {
		super(game, name, shortcut, cost, spend, actions, buys, cards);
		this.costUpTo = upto;
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
			CardInstance c = getGame().getTableCards().findByType(ct, true);
			if (c == null)
				throw new DominionError("There are no cards of that type on the table");
			if (ct.getCost() > costUpTo)
				throw new DominionError("That card is too expensive.");
			getGame().getTableCards().remove(c);
			player.getDiscards().add(c);
			cardGained(c);
			getGame().getOutput().out(turn.getPlayer().getName()).out(" used " + getName() + " to gain a ").card(ct, false).out(" into their discard pile").send();
			state = State.FINISHED;
			return true;
		}
		return false;
	}

	protected void cardGained(CardInstance c) {
	}


	@Override
	public void playOn(Turn turn, CardInstance instance) {
		super.playOn(turn, instance);
		state = State.WAIT_FOR_CHOICE;
		turn.getInPlay().add(instance);
		turn.removeFromHand(instance);
	}


	@Override
	public void askQuestion(DominionOutput response) {
		if (state == State.WAIT_FOR_CHOICE) {
			response.out(playedOn.getPlayer().getName()).out(": pick a card worth up to " + costUpTo + " coins.  It will be put in your discard pile - ");
			CardList tmp = new CardList();
			for (CardInstance c : getGame().getTableCards()) 
				if (c.getCard().getCost() <= costUpTo)
					tmp.add(c);
			getGame().showCardSet(tmp, true, true);
		}
	}


	public void describe(DominionOutput output) {
		describeNameType(output);

		output.soft(" (").out("Gain a card costing up to " + costUpTo + " coin");
		output.soft(")");
	}

}
