package uk.co.stikman.dominion.cards;

import java.util.ArrayList;
import java.util.List;

import uk.co.stikman.dominion.Card;
import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.DominionError;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.Player;
import uk.co.stikman.dominion.ProducesTreasure;
import uk.co.stikman.dominion.Turn;
import uk.co.stikman.stikbot.util.Utils;
import uk.co.stikman.tokeniser.NoMoreTokensError;
import uk.co.stikman.tokeniser.TokenList;

public class StewardCard extends ChoiceCard implements ProducesTreasure {

	private int		coin	= 0;
	private boolean	pickTrashCards;

	public StewardCard(DominionGame game, String name, String shortcut, int cost) {
		super(game, name, shortcut, cost);
	}

	@Override
	public int getTreasureValue(Turn turn) {
		return coin;
	}

	@Override
	protected int getChoices(List<Choice> choices) {
		choices.add(new Choice("+2 Card") {
			@Override
			public void action(Turn currentTurn) {
				currentTurn.getHand().add(currentTurn.getPlayer().draw());
				currentTurn.getHand().add(currentTurn.getPlayer().draw());
			}
		});
		choices.add(new Choice("+2 Coin") {
			@Override
			public void action(Turn currentTurn) {
				coin = 2;
			}
		});
		choices.add(new Choice("Trash 2 cards") {
			@Override
			public void action(Turn currentTurn) {
				pickTrashCards = true;
			}
		});
		return 1;
	}

	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);
		output.soft(" (").out("Choose one: +2 cards; +2 coin; trash 2 cards.");
		output.soft(")");
	}

	@Override
	public void playOn(Turn turn, CardInstance instance) {
		super.playOn(turn, instance);
		coin = 0;
		pickTrashCards = false;
	}

	@Override
	public boolean needsInput() {
		if (pickTrashCards)
			return true;
		return super.needsInput();
	}

	@Override
	public void askQuestion(DominionOutput response) {
		if (pickTrashCards) {
			response.out(playedOn.getPlayer().getName()).out(": pick one or two cards to trash - ");
			getGame().showCardSet(playedOn.getHand(), true, false);
		} else {
			super.askQuestion(response);
		}
	}

	@Override
	public boolean processResponse(Player player, Turn turn, TokenList tokens) {
		if (!player.equals(turn.getPlayer()))
			return false;
		if (pickTrashCards) {

			List<Card> cards = new ArrayList<>();
			try {
				while (tokens.hasNext())
					cards.add(getGame().getCardSet().findCard(tokens.nextString()));
			} catch (NoMoreTokensError e) {
				throw new DominionError("Invalid command", e);
			}

			while (cards.size() > 2)
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

			pickTrashCards = false;
			
			return true;
		} else {
			return super.processResponse(player, turn, tokens);
		}
	}

}
