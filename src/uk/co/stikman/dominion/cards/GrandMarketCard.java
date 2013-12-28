package uk.co.stikman.dominion.cards;

import java.util.List;

import uk.co.stikman.dominion.DominionError;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.Turn;

public class GrandMarketCard extends BasicActionCard {

	public GrandMarketCard(DominionGame game, String name, String shortcut, int cost, int spend, int actions, int buys, int cards) {
		super(game, name, shortcut, cost, spend, actions, buys, cards);
	}

	@Override
	public void canBuy(Turn currentTurn) {
		if (currentTurn.getHand().findByType("Copper", true) != null)
			throw new DominionError("You can only buy a Grand Market when you have no Copper in play");
		super.canBuy(currentTurn);
	}

	@Override
	protected void describeGetEffects(List<String> things) {
		super.describeGetEffects(things);
		things.add("Can only buy when no Copper in play");
	}
	
	
	
}
