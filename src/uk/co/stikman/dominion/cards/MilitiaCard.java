package uk.co.stikman.dominion.cards;

import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsAttackCard;
import uk.co.stikman.dominion.Player;
import uk.co.stikman.dominion.Turn;
import uk.co.stikman.stikbot.GamePlayer;

public class MilitiaCard extends BasicActionCard implements IsAttackCard {

	public MilitiaCard(DominionGame game, String name, String shortcut, int cost, int spend, int actions, int buys, int cards) {
		super(game, name, shortcut, cost, spend, actions, buys, cards);
	}

	public void describe(DominionOutput output) {
		describeNameType(output);

		output.soft(" (").out("+2 coin.  All other players discard down to 3 cards in their hand");
		output.soft(")");
	}

	@Override
	public void playOn(Turn turn, CardInstance instance) {
		super.playOn(turn, instance);

		//
		// Cripple the other other players
		//
		getGame().getOutput().out("All other players now have 3 cards in their next hand.").send();
		
		for (GamePlayer p : getGame().getPlayers()) {
			if (p.equals(turn.getPlayer()))
				continue;
			Player a = (Player) p;
			
			if (a.canAttack(turn, instance)) 
				while (a.getHand().size() > 3)
					a.getDiscards().add(a.getHand().remove(0));
		}


	}

}
