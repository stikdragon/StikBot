package uk.co.stikman.dominion.cards;

import java.util.List;

import uk.co.stikman.dominion.CardList;
import uk.co.stikman.dominion.CardType;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.IsVictoryCard;

public class GreatHallCard extends BasicActionCard implements IsVictoryCard {

	private int	points;

	public GreatHallCard(DominionGame game, String name, String shortcut, int cost, int spend, int actions, int buys, int cards, int points) {
		super(game, name, shortcut, cost, spend, actions, buys, cards);
		this.points = points;
	}

	@Override
	public int getVictoryPoints(CardList playerCards) {
		return points;
	}

	@Override
	public CardType getCardType() {
		return CardType.ACTION_VICTORY;
	}

	
	protected void describeGetEffects(List<String> things) {
		super.describeGetEffects(things);
		things.add("+" + points + " victory points");
	}

		
	
	
}
