package uk.co.stikman.dominion.cards;

import uk.co.stikman.dominion.Card;
import uk.co.stikman.dominion.CardList;
import uk.co.stikman.dominion.CardType;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsTreasureCard;
import uk.co.stikman.dominion.IsVictoryCard;
import uk.co.stikman.dominion.Turn;

public class HaremCard extends Card implements IsVictoryCard, IsTreasureCard {

	private int	coin;
	private int	points;

	public HaremCard(DominionGame game, String name, String shortcut, int cost, int coin, int victory) {
		super(game, name, shortcut, cost);
		this.points = victory;
		this.coin = coin;
	}

	
	public void describe(DominionOutput output) {
		describeNameType(output);

		output.soft(" (").out("+2 coin when in hand, 2 victory points at end of game");
		output.soft(")");
	}

	@Override
	public CardType getCardType() {
		return CardType.TREASURE_VICTORY;
	}

	@Override
	public int getVictoryPoints(CardList playerCards) {
		return points;
	}

	@Override
	public int getTreasureValue(Turn turn) {
		return coin;
	}

}
