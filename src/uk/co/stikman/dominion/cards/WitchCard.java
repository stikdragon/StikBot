package uk.co.stikman.dominion.cards;

import java.util.ArrayList;
import java.util.List;

import uk.co.stikman.dominion.Card;
import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.CardList;
import uk.co.stikman.dominion.CardType;
import uk.co.stikman.dominion.DominionError;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsActionCard;
import uk.co.stikman.dominion.IsAttackCard;
import uk.co.stikman.dominion.Player;
import uk.co.stikman.dominion.Turn;
import uk.co.stikman.stikbot.GamePlayer;
import uk.co.stikman.stikbot.util.Utils;

public class WitchCard extends Card implements IsActionCard, IsAttackCard {

	private int	cards;

	public WitchCard(DominionGame game, String name, String shortcut, int cost, int cards) {
		super(game, name, shortcut, cost);
		this.cards = cards;
	}

	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);
		output.soft(" (").out("+" + cards + " cards.  Other players draw a Curse into their hand").soft(")");
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
		turn.removeFromHand(instance);
		turn.getInPlay().add(instance);
		CardList lst = new CardList();
		for (int i = 0; i < cards; ++i) {
			CardInstance c = turn.getPlayer().draw();
			lst.add(c);
			turn.getHand().add(c);
		}

		CardInstance curse = null;
		List<Player> players = new ArrayList<>();
		for (GamePlayer p : getGame().getPlayers()) {
			if (p.equals(turn.getPlayer()))
				continue;
			Player a = (Player) p;
			
			if (a.canAttack(turn, instance)) {			
				curse = getGame().getTableCards().findByType("Curse", true);
				if (curse == null)
					break; // lucky :)
				getGame().getTableCards().remove(curse);
				a.getHand().add(curse);
				players.add(a);
			}
		}

		getGame().getOutput().out(turn.getPlayer().getName()).out(" drew ");
		getGame().showCardSet(lst, false, false);
		getGame().getOutput().out(" into their hand. ");
		if (players.isEmpty())
			getGame().getOutput().out("Other players were lucky, since the game is out of curses");
		else
			getGame().getOutput().out(Utils.join(players, ", ", " and ")).out(" have gained a ").card(curse.getCard(), false).send();
	}

}
