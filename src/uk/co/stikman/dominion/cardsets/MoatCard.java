package uk.co.stikman.dominion.cardsets;

import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.IsReactionCard;
import uk.co.stikman.dominion.Player;
import uk.co.stikman.dominion.Turn;
import uk.co.stikman.dominion.cards.BasicActionCard;

public class MoatCard extends BasicActionCard implements IsReactionCard {

	public MoatCard(DominionGame game, String name, String shortcut, int cost, int spend, int actions, int buys, int cards) {
		super(game, name, shortcut, cost, spend, actions, buys, cards);
	}

	@Override
	public boolean reactToAttack(Turn turn, Player victim, CardInstance thisCard, CardInstance attackingCard) {
		getGame().getOutput().out(victim.getName()).out(" used ").card(thisCard.getCard(), false).out(" to defend. ").send();
		return true;
	}

}
