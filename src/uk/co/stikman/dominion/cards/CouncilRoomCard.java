package uk.co.stikman.dominion.cards;

import java.util.ArrayList;
import java.util.List;

import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.Player;
import uk.co.stikman.dominion.Turn;
import uk.co.stikman.stikbot.GamePlayer;
import uk.co.stikman.stikbot.util.Utils;

public class CouncilRoomCard extends BasicActionCard {

	public CouncilRoomCard(DominionGame game, String name, String shortcut, int cost, int spend, int actions, int buys, int cards) {
		super(game, name, shortcut, cost, spend, actions, buys, cards);
	}

	public void describe(DominionOutput output) {
		describeNameType(output);

		output.soft(" (").out("+4 cards, +1 buy.  All other players also draw a card");
		output.soft(")");
	}

	@Override
	public void playOn(Turn turn, CardInstance instance) {
		super.playOn(turn, instance);

		//
		// draw a card for all other players
		//
		List<Player> lst = new ArrayList<>();
		for (GamePlayer p : getGame().getPlayers()) {
			if (p.equals(turn.getPlayer()))
				continue;
			Player a = (Player) p;
			a.getHand().add(a.draw());
			lst.add(a);
		}
		
		switch (lst.size()) {
		case 1:
			getGame().getOutput().out(lst.get(0).getName()).out(" has also drawn an extra card. ").send();
			break;
		case 2:
			getGame().getOutput().out(Utils.join(lst, ", ", " and ")).out(" have also drawn an extra card each. ").send();
		}

	}

}
