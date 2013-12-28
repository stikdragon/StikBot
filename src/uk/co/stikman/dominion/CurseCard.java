package uk.co.stikman.dominion;

public class CurseCard extends Card implements IsCurseCard {

	private int	points;

	public CurseCard(DominionGame game, String name, String shortcut, int cost, int points, boolean alwaysinclude) {
		super(game, name, shortcut, cost);
		this.points = points;
		setAlwaysInclude(alwaysinclude);		
	}

	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);
		output.soft(" (").out("-1 Victory point at the end of the game").soft(")");
	}

	@Override
	public CardType getCardType() {
		return CardType.CURSE;
	}

	@Override
	public int getVictoryPoints(CardList playerCards) {
		return points;
	}

}
