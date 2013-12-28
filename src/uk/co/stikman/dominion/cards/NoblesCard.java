package uk.co.stikman.dominion.cards;

import java.util.List;

import uk.co.stikman.dominion.CardList;
import uk.co.stikman.dominion.CardType;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsVictoryCard;
import uk.co.stikman.dominion.Turn;

public class NoblesCard extends ChoiceCard implements IsVictoryCard {

	private int	points;

	public NoblesCard(DominionGame game, String name, String shortcut, int cost, int points) {
		super(game, name, shortcut, cost);
		this.points = points;
	}

	@Override
	protected int getChoices(List<Choice> choices) {
		choices.add(new Choice("+3 Cards") {
			@Override
			public void action(Turn currentTurn) {
				currentTurn.getHand().add(currentTurn.getPlayer().draw());
				currentTurn.getHand().add(currentTurn.getPlayer().draw());
				currentTurn.getHand().add(currentTurn.getPlayer().draw());
			}
		});
		choices.add(new Choice("+2 Actions") {
			@Override
			public void action(Turn currentTurn) {
				currentTurn.setActions(currentTurn.getActions() + 2);
			}
		});
		return 1;
	}

	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);
		output.soft(" (").out("2 Victory Points.  Choose one of +3 Cards or +2 Actions").soft(")");
	}

	@Override
	public int getVictoryPoints(CardList playerCards) {
		return points;
	}

	@Override
	public CardType getCardType() {
		return CardType.ACTION_VICTORY;
	}
	
	

}
