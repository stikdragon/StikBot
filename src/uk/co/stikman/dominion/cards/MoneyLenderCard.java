package uk.co.stikman.dominion.cards;

import java.util.List;

import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.DominionError;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.Turn;

public class MoneyLenderCard extends BasicActionCard {

	private String	cardtype;
	private int	reward;

	public MoneyLenderCard(DominionGame game, String name, String shortcut, int cost, int spend, int actions, int buys, int cards, String cardtype) {
		super(game, name, shortcut, cost, 0, actions, buys, cards);
		this.reward = spend;
		this.cardtype = cardtype;
	}

	@Override
	protected void describeGetEffects(List<String> things) {
		things.add("Trash a " + cardtype + " from your hand to: ");
		super.describeGetEffects(things);
	}

	@Override
	public int getTreasureValue(Turn turn) {
		return super.getTreasureValue(turn) + reward;
	}

	@Override
	public void checkCanPlay(Turn turn, CardInstance instance) throws DominionError {
		//
		// Only if they have some copper
		//
		super.checkCanPlay(turn, instance);
		if (turn.findInHand(cardtype, true) == null) 
			throw new DominionError("You cannot play this unless you have a " + cardtype + " in your hand");
	}

	@Override
	public void playOn(Turn turn, CardInstance instance) {
		super.playOn(turn, instance);
		turn.getInPlay().remove(instance);
		getGame().getTrash().add(instance);
	}
	
	

}
