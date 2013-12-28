package uk.co.stikman.dominion.cards;

import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsActionCard;
import uk.co.stikman.dominion.IsTreasureCard;
import uk.co.stikman.dominion.IsVictoryCard;
import uk.co.stikman.dominion.Turn;

public class IronWorksCard extends GainCardCard {
	
	private int	extraSpend;

	public IronWorksCard(DominionGame game, String name, String shortcut, int cost, int spend, int actions, int buys, int cards, int upto) {
		super(game, name, shortcut, cost, spend, actions, buys, cards, upto);
	}


	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);
		output.soft(" (").out("Gain a card worth up to " + this.costUpTo + " coins.  If it's an… Action, +1 Action. Treasure, +1 coin. Victory, +1 Card").soft(")");
	}
	
	
	@Override
	protected void cardGained(CardInstance c) {
		super.cardGained(c);
		
		if (c.getCard() instanceof IsTreasureCard) 
			this.extraSpend = 1;
		if (c.getCard() instanceof IsVictoryCard)
			playedOn.getHand().add(playedOn.getPlayer().draw());
		if (c.getCard() instanceof IsActionCard)
			playedOn.setActions(playedOn.getActions() + 1);
	}


	@Override
	public void playOn(Turn turn, CardInstance instance) {
		this.extraSpend = 0;
		super.playOn(turn, instance);
	}


	@Override
	public int getTreasureValue(Turn turn) {
		return super.getTreasureValue(turn) + extraSpend;
	}
	
	
	


	

}
