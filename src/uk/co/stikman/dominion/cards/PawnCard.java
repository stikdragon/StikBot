package uk.co.stikman.dominion.cards;

import java.util.List;

import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.ProducesTreasure;
import uk.co.stikman.dominion.Turn;

public class PawnCard extends ChoiceCard implements ProducesTreasure {

	private int	coin	= 0;

	public PawnCard(DominionGame game, String name, String shortcut, int cost) {
		super(game, name, shortcut, cost);
	}

	@Override
	public int getTreasureValue(Turn turn) {
		return coin;
	}

	@Override
	protected int getChoices(List<Choice> choices) {
		choices.add(new Choice("+1 Card") {
			@Override
			public void action(Turn currentTurn) {
				currentTurn.getHand().add(currentTurn.getPlayer().draw());
			}
		});
		choices.add(new Choice("+1 Action") {
			@Override
			public void action(Turn currentTurn) {
				currentTurn.setActions(currentTurn.getActions() + 1);
			}
		});
		choices.add(new Choice("+1 Buy") {
			@Override
			public void action(Turn currentTurn) {
				currentTurn.setBuys(currentTurn.getBuys() + 1);
			}
		});
		choices.add(new Choice("+1 Coin") {
			@Override
			public void action(Turn currentTurn) {
				coin = 1;
			}
		});
		return 2;
	}

	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);
		output.soft(" (").out("Choose two: +1 Card; +1 Action; +1 Buy; +$1.");
		output.soft(")");
	}

	@Override
	public void playOn(Turn turn, CardInstance instance) {
		super.playOn(turn, instance);
		coin = 0;
	}

}
