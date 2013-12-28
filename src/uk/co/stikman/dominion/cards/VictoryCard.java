package uk.co.stikman.dominion.cards;

import uk.co.stikman.dominion.Card;
import uk.co.stikman.dominion.CardList;
import uk.co.stikman.dominion.CardType;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsVictoryCard;

public class VictoryCard extends Card implements IsVictoryCard {

	private int	value;

	public VictoryCard(DominionGame game, String name, String shortcut, int cost, int value, boolean alwaysinclude) {
		super(game, name, shortcut, cost);
		this.value = value;
		setAlwaysInclude(alwaysinclude);
	}

	
	@Override
	public int getVictoryPoints(CardList playerCards) {
		return value;
	}



	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);

		output.soft(" (").out("Worth " + value + " victory points at the end of the game").soft(")");
	}

	@Override
	public CardType getCardType() {
		return CardType.VICTORY;
	}


	
}
