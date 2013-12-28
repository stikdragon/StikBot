package uk.co.stikman.dominion.cards;

import java.util.ArrayList;
import java.util.List;

import uk.co.stikman.dominion.Card;
import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.CardType;
import uk.co.stikman.dominion.DominionError;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsActionCard;
import uk.co.stikman.dominion.Turn;

public class CountingHouseCard extends Card implements IsActionCard {

	public CountingHouseCard(DominionGame game, String name, String shortcut, int cost) {
		super(game, name, shortcut, cost);
	}

	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);
		output.soft(" (").out("Go through your discard pile and put all Copper into your hand").soft(")");
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
		List<CardInstance> coppers = new ArrayList<>();
		turn.getInPlay().add(instance);
		for (CardInstance ci : turn.getPlayer().getDiscards())
			if (ci.getCard().getName().equalsIgnoreCase("Copper"))
				coppers.add(ci);
		for (CardInstance ci : coppers) {
			turn.getPlayer().getDiscards().remove(ci);
			turn.getHand().add(ci);
		}
		turn.removeFromHand(instance);
		getGame().getOutput().out(turn.getPlayer().getName()).out(" returned " + coppers.size()).out(" Coppers to their hand").send();
	}

}
