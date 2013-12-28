package uk.co.stikman.dominion.cards;

import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsActionCard;
import uk.co.stikman.dominion.Turn;

public class ShantyTownCard extends BasicActionCard {

	public ShantyTownCard(DominionGame game, String name, String shortcut, int cost) {
		super(game, name, shortcut, cost, 0, 0, 0, 0);
	}

	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);
		output.soft(" (").out("Draw +2 cards if you have no other actions in your hand").soft(")");
	}

	@Override
	public void playOn(Turn turn, CardInstance instance) {
		this.cards = 0;
		super.playOn(turn, instance);
		int i = 0;
		for (CardInstance c : turn.getHand())
			if (c.getCard() instanceof IsActionCard)
				++i;
		if (i == 0)
			this.cards = 2;
		handleDrawCards();
	}

}
