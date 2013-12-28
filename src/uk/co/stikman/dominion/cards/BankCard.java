package uk.co.stikman.dominion.cards;

import uk.co.stikman.dominion.Card;
import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.CardType;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsTreasureCard;
import uk.co.stikman.dominion.Turn;

public class BankCard extends Card implements IsTreasureCard {

	public BankCard(DominionGame game, String name, String shortcut, int cost) {
		super(game, name, shortcut, cost);
	}

	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);
		output.soft(" (").out(" worth +1 coin for every Treasure Card in play, including itself").soft(")");
	}

	@Override
	public CardType getCardType() {
		return CardType.TREASURE;
	}

	@Override
	public int getTreasureValue(Turn turn) {
		int i = 0;
		for (CardInstance ci : turn.getHand())
			if (ci.getCard() instanceof IsTreasureCard)
				++i;
		for (CardInstance ci : turn.getInPlay())
			if (ci.getCard() instanceof IsTreasureCard)
				++i;
		return i;
	}

}
