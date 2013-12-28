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

public class FeastCard extends BasicActionCard implements IsInteractiveCard {

	private enum State {
		WAIT_FOR_CHOICE, FINISHED
	}

	private State	state;

	public FeastCard(DominionGame game, String name, String shortcut, int cost, int spend, int actions, int buys, int cards) {
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
			CardInstance c = getGame().getTableCards().findByType(ct, true);
			if (c == null)
				throw new DominionError("There are no cards of that type on the table");
			if (ct.getCost() > 5)
				throw new DominionError("That card is too expensive.");
			getGame().getTableCards().remove(c); // it'll be on the table, so remove it
			getGame().getTrash().add(c);
			player.getDiscards().add(c);
			getGame().getOutput().out(turn.getPlayer().getName()).out(" trashed a Feast card to gain a ").card(ct, false).out(" into their discard pile").send();
			state = State.FINISHED;
			return true;
		}
		return false;
	}

	@Override
	public void playOn(Turn turn, CardInstance instance) {
		super.playOn(turn, instance);
		//
		// The super call will have put this card in the play pile, so remove it again
		//
		turn.getInPlay().remove(instance);
		state = State.WAIT_FOR_CHOICE;
	}

	@Override
	public void askQuestion(DominionOutput response) {
		if (state == State.WAIT_FOR_CHOICE) {
			response.out(playedOn.getPlayer().getName()).out(": pick a card worth up to 5 coin.  It will be put in your discard pile - ");
			CardList tmp = new CardList();
			for (CardInstance c : getGame().getTableCards())
				if (c.getCard().getCost() <= 5)
					tmp.add(c);
			getGame().showCardSet(tmp, true, true);
		}
	}

	public void describe(DominionOutput output) {
		describeNameType(output);

		output.soft(" (").out("Trash this card and gain a card costing up to 5 coin");
		output.soft(")");
	}

}
