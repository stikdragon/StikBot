package uk.co.stikman.dominion.cards;

import java.util.List;

import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.CardList;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.Turn;

public class ChancellorCard extends ChoiceCard {

	private int	cards;

	public ChancellorCard(DominionGame game, String name, String shortcut, int cost, int cards) {
		super(game, name, shortcut, cost);
		this.cards = cards;
	}

	@Override
	protected int getChoices(List<Choice> choices) {
		choices.add(new Choice("Do nothing") {
			@Override
			public void action(Turn currentTurn) {
				// do nothing
			}
		});
		choices.add(new Choice("Discard my deck") {
			@Override
			public void action(Turn currentTurn) {
				playedOn.getPlayer().getDiscards().addAll(playedOn.getPlayer().getDeck());
				getGame().getOutput().out(playedOn.getPlayer().getName()).out(" discarded their entire deck").send();
			}
		});
		
		return 1;
	}

	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);
		output.soft(" (").out("+" + cards + " cards.  You have the option to discard your entire deck").soft(")");
	}

	@Override
	public void playOn(Turn turn, CardInstance instance) {
		super.playOn(turn, instance);
		CardList lst = new CardList();
		for (int i = 0; i < cards; ++i) {
			CardInstance c = turn.getPlayer().draw();
			lst.add(c);
			turn.getHand().add(c);
		}
		getGame().getOutput().out(turn.getPlayer().getName()).out(" drew ");
		getGame().showCardSet(lst, false, false);
		getGame().getOutput().out(" into their hand.").send();
	}

	
	
}
