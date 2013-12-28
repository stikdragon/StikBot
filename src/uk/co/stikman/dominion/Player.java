package uk.co.stikman.dominion;

import uk.co.stikman.stikbot.GamePlayer;
import uk.co.stikman.stikbot.User;

public class Player implements GamePlayer {
	private String			name;
	private DominionGame	game;
	private CardList		discards	= new CardList();
	private CardList		hand		= new CardList();
	private CardList		deck		= new CardList();
	private User			user;
	private boolean			ready;

	public Player(DominionGame game, String name, User user) {
		super();
		this.game = game;
		this.name = name;
		this.user = user;
		this.ready = false;
	}

	public DominionGame getGame() {
		return game;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public String getName() {
		return name;
	}

	// public void setName(String name) {
	// this.name = name;
	// }

	public CardList getDiscards() {
		return discards;
	}

	public CardList getHand() {
		return hand;
	}

	public CardList getDeck() {
		return deck;
	}

	public void shuffleDeck() {
		CardList tmp = new CardList();
		while (!deck.isEmpty()) {
			int i = (int) (Math.random() * deck.size());
			tmp.add(deck.remove(i));
		}
		deck = tmp;
	}

	public void deal(int count) {
		while (count-- > 0)
			hand.add(draw());
	}

	public CardInstance draw() {
		if (deck.isEmpty())
			restock();
		if (deck.isEmpty())
			throw new DominionError("Your deck is empty and you have no discards to recycle.  This shouldn't ever happen, theoretically.");
		return deck.remove(0);
	}

	private void restock() {
		deck.addAll(discards);
		discards.clear();
		shuffleDeck();
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public String toString() {
		return name + " (" + user + ")";
	}

	public CardInstance findInHand(Card ct) {
		for (CardInstance c : getHand())
			if (c.getCard().equals(ct))
				return c;
		return null;
	}

	/**
	 * Consolidate all cards into the deck
	 */
	public void consolidate() {
		deck.addAll(discards);
		deck.addAll(hand);
		hand.clear();
		discards.clear();
	}

	public boolean canAttack(Turn turn, CardInstance attackCard) {
		for (CardInstance ci : hand) {
			if (ci.getCard() instanceof IsReactionCard) {
				IsReactionCard rc = (IsReactionCard) ci.getCard();
				if (rc.reactToAttack(turn, this, ci, attackCard))
					return false;
			}
		}
		return true;
	}

}
