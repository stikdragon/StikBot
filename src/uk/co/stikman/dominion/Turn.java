package uk.co.stikman.dominion;

import java.util.ArrayList;
import java.util.List;

import uk.co.stikman.dominion.cards.RepeatPlayCard;
import uk.co.stikman.tokeniser.TokenException;
import uk.co.stikman.tokeniser.TokenList;

public class Turn {
	private Player				player;
	private CardList			hand;
	private CardList			inplay;
	private CardList			boughtCards;
	private DominionGame		game;
	private CardInstance		pendingCard;

	private int					buys;
	private int					actions;

	/**
	 * Note that <code>playList</code> is not a {@link CardList} because it
	 * needs to be able to contain duplicates for things like
	 * {@link RepeatPlayCard}
	 */
	private List<CardInstance>	playList	= new ArrayList<>();

	public Turn(DominionGame game, Player player) {
		super();
		this.game = game;
		this.player = player;
		this.hand = new CardList(player.getHand()); // make a copy
		this.inplay = new CardList();
		this.boughtCards = new CardList();
		this.buys = 1;
		this.actions = 1;
	}

	public CardInstance getPendingCard() {
		return pendingCard;
	}

	public void setPendingCard(CardInstance pendingCard) {
		this.pendingCard = pendingCard;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public CardList getHand() {
		return hand;
	}

	public CardList getInPlay() {
		return inplay;
	}

	public CardList getBoughtCards() {
		return boughtCards;
	}

	public int calculateCoin() {
		int i = 0;
		for (CardInstance c : getHand())
			if (c.getCard() instanceof IsTreasureCard)
				i += c.getCard().as(IsTreasureCard.class).getTreasureValue(this);
		for (CardInstance c : inplay)
			if (c.getCard() instanceof ProducesTreasure)
				i += c.getCard().as(ProducesTreasure.class).getTreasureValue(this);
		for (CardInstance c : boughtCards)
			i -= c.getCard().getCost();
		return i;
	}

	public int calculateActions() {
		return actions;
	}

	public int calculateBuys() {
		return buys;
	}

	public boolean validMovesLeft() {
		int actions = 0;
		for (CardInstance c : hand)
			if (c.getCard() instanceof IsActionCard)
				++actions;

		return (calculateActions() > 0 && actions > 0) || (calculateBuys() > 0);
	}

	public CardInstance findInHand(String name, boolean allownull) {
		Card cc = null;
		try {
			cc = game.getCardSet().findCard(name);

			for (CardInstance c : hand)
				if (c.getCard().equals(cc))
					return c;
		} catch (Exception e) {
			throw new DominionError("Unknown card", e);
		}
		if (allownull)
			return null;
		throw new DominionError("Card not in hand: " + cc.getName());
	}

	public DominionGame getGame() {
		return game;
	}

	/**
	 * Return a card that matches this class. Throws an exception if nothing
	 * 
	 * @param cc
	 * @return
	 */
	public CardInstance findInHand(Card cc, boolean allownull) {

		for (CardInstance c : hand)
			if (c.getCard().equals(cc))
				return c;
		if (allownull)
			return null;
		throw new DominionError("Card not in hand: " + cc.getName());
	}

	public int getBuys() {
		return buys;
	}

	public void setBuys(int buys) {
		this.buys = buys;
	}

	public int getActions() {
		return actions;
	}

	public void setActions(int actions) {
		this.actions = actions;
	}

	public void removeFromHand(CardInstance c) {
		getHand().remove(c);
	}

	public CardList extractCardsFromHand(TokenList tokens, int maxselect) {
		CardList res = new CardList();
		try {
			while (tokens.hasNext() && maxselect > 0) {
				CardInstance c = findInHand(tokens.nextString(), false);
				getHand().remove(c);
				res.add(c);
				--maxselect;
			}
		} catch (TokenException e) {
			throw new DominionError("Invalid command, select cards from your hand", e);
		}
		return res;
	}

	public void addToPlayList(CardInstance card) {
		this.playList.add(card);
	}

	public boolean isCardsToPlay() {
		return !this.playList.isEmpty();
	}

	public CardInstance extractNextPlayCard() {
		if (playList.isEmpty())
			throw new DominionError("There are no cards to play");
		return playList.remove(0);
	}

}
