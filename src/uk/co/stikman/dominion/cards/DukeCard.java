package uk.co.stikman.dominion.cards;

import uk.co.stikman.dominion.Card;
import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.CardList;
import uk.co.stikman.dominion.CardType;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsVictoryCard;

public class DukeCard extends Card implements IsVictoryCard {

	public DukeCard(DominionGame game, String name, String shortcut, int cost) {
		super(game, name, shortcut, cost);
	}

	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);

		output.soft(" (").out("Gives 1 victory point for every Duchy you own").soft(")");
	}

	@Override
	public CardType getCardType() {
		return CardType.VICTORY;
	}

	@Override
	public int getVictoryPoints(CardList playerCards) {
		int p = 0;
		for (CardInstance c : playerCards)
			if (c.getCard().getName().equalsIgnoreCase("Duchy"))
				++p;
		return p;
	}

}
