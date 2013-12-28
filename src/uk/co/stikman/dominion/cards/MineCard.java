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

public class MineCard extends BasicActionCard implements IsInteractiveCard {

	private enum State {
		WAIT_FOR_CHOICE, FINISHED
	}

	private State	state;

	public MineCard(DominionGame game, String name, String shortcut, int cost, int spend, int actions, int buys, int cards) {
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
			if (!ct.getName().equalsIgnoreCase("Copper") && !ct.getName().equalsIgnoreCase("Silver"))
				throw new DominionError("You must pick Silver or Copper to trade up");

			CardInstance c = turn.findInHand(ct, false);
			tradeup(c, turn);
			state = State.FINISHED;
			return true;
		}
		return false;
	}

	@Override
	public void playOn(Turn turn, CardInstance instance) {
		super.playOn(turn, instance);

		//
		// If they've only got one money card then play that
		//
		CardInstance c1 = turn.findInHand("Copper", true);
		CardInstance c2 = turn.findInHand("Silver", true);

		if ((c1 == null) ^ (c2 == null)) {
			if (c1 == null)
				c1 = c2;

			tradeup(c1, turn);

			state = State.FINISHED;
		} else {
			state = State.WAIT_FOR_CHOICE;
		}
	}

	private void tradeup(CardInstance c1, Turn turn) {
		Card ct = null;
		turn.removeFromHand(c1);
		getGame().getTrash().add(c1);
		if (c1.getCard().getName().equalsIgnoreCase("Copper"))
			ct = turn.getGame().getCardSet().findCard("Silver");
		else if (c1.getCard().getName().equalsIgnoreCase("Silver"))
			ct = turn.getGame().getCardSet().findCard("Gold");
		CardInstance c = turn.getGame().getTableCards().findByType(ct, true);
		if (c == null) {
			state = State.FINISHED;
			throw new DominionError("There's no " + ct.getName() + " cards left on the table, so this action has had no effect.  Sorry :)");
		}
		turn.getHand().add(c);
		turn.getGame().getOutput().out(turn.getPlayer().getName()).out(" trashed a ").card(c1.getCard(), false).out(" from their hand for a ").card(ct, false).send();
	}

	@Override
	public void askQuestion(DominionOutput response) {
		if (state == State.WAIT_FOR_CHOICE) {
			response.out(playedOn.getPlayer().getName()).out(": pick a money card from your hand to trade up - ");
			getGame().showCardSet(playedOn.getHand(), true, false);
		}
	}

	@Override
	public void checkCanPlay(Turn turn, CardInstance instance) {
		super.checkCanPlay(turn, instance);
		CardInstance c1 = turn.findInHand("Copper", true);
		CardInstance c2 = turn.findInHand("Silver", true);
		if (c1 == null && c2 == null)
			throw new DominionError("You don't have any Copper or Silver in your hand to exchange");
	}

	public void describe(DominionOutput output) {
		describeNameType(output);

		output.soft(" (").out("trash a Copper or Silver for the next one up");
		output.soft(")");
	}

}
