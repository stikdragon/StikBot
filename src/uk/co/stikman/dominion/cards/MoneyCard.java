package uk.co.stikman.dominion.cards;

import uk.co.stikman.dominion.Card;
import uk.co.stikman.dominion.CardType;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsTreasureCard;
import uk.co.stikman.dominion.Turn;

public class MoneyCard extends Card implements IsTreasureCard {

	private int	value;

	public MoneyCard(DominionGame game, String name, String shortcut, int cost, int value, boolean alwaysinclude) {
		super(game, name, shortcut, cost);
		this.value = value;
		setAlwaysInclude(alwaysinclude);
	}

	@Override
	public int getTreasureValue(Turn turn) {
		return value;
	}

	
	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);

		output.soft(" (").out("Worth " + value + " coin when in your hand").soft(")");
	}

	@Override
	public CardType getCardType() {
		return CardType.TREASURE;
	}

}
