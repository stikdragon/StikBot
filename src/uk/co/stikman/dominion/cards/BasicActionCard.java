package uk.co.stikman.dominion.cards;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.stikman.dominion.Card;
import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.CardType;
import uk.co.stikman.dominion.DominionError;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsActionCard;
import uk.co.stikman.dominion.ProducesTreasure;
import uk.co.stikman.dominion.Turn;
import uk.co.stikman.stikbot.util.Utils;

public class BasicActionCard extends Card implements IsActionCard, ProducesTreasure {

	protected int		spend;
	protected int		actions;
	protected int		buys;
	protected int		cards;

	protected Turn	playedOn;

	public BasicActionCard(DominionGame game, String name, String shortcut, int cost, int spend, int actions, int buys, int cards) {
		super(game, name, shortcut, cost);
		this.spend = spend;
		this.buys = buys;
		this.cards = cards;
		this.actions = actions;

	}

	public void describe(DominionOutput output) {
		describeNameType(output);
		
		List<String> things = new ArrayList<>();
		describeGetEffects(things);

		if (!things.isEmpty())
			output.soft(" (");
		for (Iterator<String> i = things.iterator(); i.hasNext();) {
			output.out(i.next());
			if (i.hasNext())
				output.soft(", ");
		}
		if (!things.isEmpty())
			output.soft(")");
	}


	protected void describeGetEffects(List<String> things) {
		if (spend != 0)
			things.add("+" + spend + " coin");
		if (buys != 0)
			things.add("+" + buys + " buying actions");
		if (actions != 0)
			things.add("+" + actions + " additional actions");
		if (cards != 0)
			things.add("+" + cards + " cards");
	}

	@Override
	public int getTreasureValue(Turn turn) {
		return spend;
	}

	public int getActions() {
		return actions;
	}

	public int getBuys() {
		return buys;
	}

	public int getCards() {
		return cards;
	}

	@Override
	public CardType getCardType() {
		return CardType.ACTION;
	}


	@Override
	public void checkCanPlay(Turn turn, CardInstance instance) throws DominionError {

	}


	/**
	 * Called when the card is played
	 */
	@Override
	public void playOn(Turn turn, CardInstance instance) {
		this.playedOn = turn;
		
		turn.setActions(turn.getActions() + getActions());
		turn.setBuys(turn.getBuys() + getBuys());

		handleDrawCards();
	}

	/**
	 * If {@link #getCards} returns > 0 then this will draw them from the deck
	 * into the current turn's hand
	 * 
	 */
	protected void handleDrawCards() {
		List<String> tmp = new ArrayList<>();
		int cnt = getCards();
		for (int i = 0; i < cnt; ++i) {
			CardInstance c = playedOn.getPlayer().draw();
			playedOn.getHand().add(c);
			tmp.add(c.getCard().getName());
		}
		if (!tmp.isEmpty())
			playedOn.getGame().getOutput().out(" to draw ").out(Utils.join(tmp, ", ", " and ")).out(" into their hand. ");
	}


}
