package uk.co.stikman.dominion.cards;

import java.util.ArrayList;
import java.util.List;

import uk.co.stikman.dominion.Card;
import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.DominionError;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsInteractiveCard;
import uk.co.stikman.dominion.Player;
import uk.co.stikman.dominion.Turn;
import uk.co.stikman.stikbot.util.Utils;
import uk.co.stikman.tokeniser.NoMoreTokensError;
import uk.co.stikman.tokeniser.TokenList;

public class ChapelCard extends BasicActionCard implements IsInteractiveCard {
	

	private enum State {
		WAIT_FOR_CHOICE, FINISHED
	}

	private State	state;


	public ChapelCard(DominionGame game, String name, String shortcut, int cost, int spend, int actions, int buys, int cards) {
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
			List<Card> cards = new ArrayList<>();
			try {
				while (tokens.hasNext()) 
					cards.add(getGame().getCardSet().findCard(tokens.nextString()));
			} catch (NoMoreTokensError e) {
				throw new DominionError("Invalid command", e);
			}
			
			while (cards.size() > 4)
				cards.remove(0);
			
			List<CardInstance> trashed = new ArrayList<>();
			for (Card cc : cards) {
				CardInstance c = turn.findInHand(cc, true);
				if (c != null) {
					trashed.add(c);
					turn.removeFromHand(c);
				}
			}
			if (!trashed.isEmpty()) 
				turn.getGame().getOutput().out(turn.getPlayer().getName()).out(" trashed ").out(Utils.join(trashed, ", ", " and ")).out(" from their hand.").send();
			
			state = State.FINISHED;
			return true;
		}
		return false;
	}


	@Override
	public void playOn(Turn turn, CardInstance instance) {
		super.playOn(turn, instance);
		state = State.WAIT_FOR_CHOICE;
	}



	@Override
	public void askQuestion(DominionOutput response) {
		if (state == State.WAIT_FOR_CHOICE) {
			response.out(playedOn.getPlayer().getName()).out(": pick up to 4 cards from your hand to trash.  List them on one line - ");
			getGame().showCardSet(playedOn.getHand(), true, false);
		}
	}

	
	public void describe(DominionOutput output) {
		describeNameType(output);

		output.soft(" (").out("trash up to 4 cards from your hand");
		output.soft(")");
	}


	
}
