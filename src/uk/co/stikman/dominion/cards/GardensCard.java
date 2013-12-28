package uk.co.stikman.dominion.cards;

import uk.co.stikman.dominion.Card;
import uk.co.stikman.dominion.CardList;
import uk.co.stikman.dominion.CardType;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsVictoryCard;

public class GardensCard extends Card implements IsVictoryCard {

	public GardensCard(DominionGame game, String name, String shortcut, int cost) {
		super(game, name, shortcut, cost);
	}

	public void describe(DominionOutput output) {
		describeNameType(output);

		output.soft(" (").out("Worth 1 Victory Point for every 10 cards in your deck at the end of the game.  Rounded down").soft(")");
	}

	@Override
	public CardType getCardType() {
		return CardType.VICTORY;
	}

	@Override
	public int getVictoryPoints(CardList playerCards) {
		return playerCards.size() / 10;
	}
	


}
