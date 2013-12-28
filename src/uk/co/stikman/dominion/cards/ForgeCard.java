package uk.co.stikman.dominion.cards;

import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.CardList;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.Turn;

public class ForgeCard extends BaseAbstractTrashAndReplaceCard {

	public ForgeCard(DominionGame game, String name, String shortcut, int cost, int spend, int actions, int buys, int cards) {
		super(game, name, shortcut, cost, spend, actions, buys, cards);
		setDiscardCount(-100);
	}

	@Override
	protected CardList getAllowedReplaceWiths(CardList discards, Turn turn) {
		CardList lst = new CardList();
		int i = 0;
		for (CardInstance ci : discards)
			i += ci.getCard().getCost();
		for (CardInstance ci : getGame().getTableCards())
			if (ci.getCard().getCost() == i)
				lst.add(ci);
		return lst;
	}

	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);
		output.soft(" (").out("Discard any number of cards from your hand and choose a card with exactly the same value").soft(")");
	}
	
	

}
