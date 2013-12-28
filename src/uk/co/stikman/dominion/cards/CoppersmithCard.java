package uk.co.stikman.dominion.cards;

import uk.co.stikman.dominion.Card;
import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.CardType;
import uk.co.stikman.dominion.DominionError;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsActionCard;
import uk.co.stikman.dominion.ProducesTreasure;
import uk.co.stikman.dominion.Turn;

public class CoppersmithCard extends Card implements IsActionCard, ProducesTreasure {

	public CoppersmithCard(DominionGame game, String name, String shortcut, int cost) {
		super(game, name, shortcut, cost);
	}

	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);
		output.soft(" (").out("Copper is worth 2 coin when you play this").soft(")");
	}

	@Override
	public CardType getCardType() {
		return CardType.ACTION;
	}

	@Override
	public void checkCanPlay(Turn turn, CardInstance instance) throws DominionError {
	}

	@Override
	public void playOn(Turn turn, CardInstance instance) {
		turn.removeFromHand(instance);
		turn.getInPlay().add(instance);
	}


	@Override
	public int getTreasureValue(Turn turn) {
		int i = 0;
				
		for (CardInstance c : turn.getHand())
			if (c.getCard().getName().equalsIgnoreCase("Copper"))
				++i;
		return i;
	}

}
