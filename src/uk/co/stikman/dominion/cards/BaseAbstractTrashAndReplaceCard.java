package uk.co.stikman.dominion.cards;

import java.util.Iterator;

import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.CardList;
import uk.co.stikman.dominion.DominionError;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsInteractiveCard;
import uk.co.stikman.dominion.Player;
import uk.co.stikman.dominion.Turn;
import uk.co.stikman.tokeniser.NoMoreTokensError;
import uk.co.stikman.tokeniser.TokenList;

/**
 * Pick some number of cards from the player's hand (excluding this one) and
 * trash them. Pick some number of cards on the table to pick up. Abstract class
 * that allows complex behaviour
 * 
 * @author Stik
 * 
 */
public abstract class BaseAbstractTrashAndReplaceCard extends BasicActionCard implements IsInteractiveCard {

	protected enum State {
		WAIT_FOR_DISCARD_CHOICE, WAIT_FOR_PICKUP_CHOICE, FINISHED
	}

	protected State		state;
	private int			discardCount;
	private CardList	selectedDiscards	= null;
	private int			pickupCount;

	public BaseAbstractTrashAndReplaceCard(DominionGame game, String name, String shortcut, int cost, int spend, int actions, int buys, int cards) {
		super(game, name, shortcut, cost, spend, actions, buys, cards);
		discardCount = 1;
		pickupCount = 1;
	}

	@Override
	public boolean processResponse(Player player, Turn turn, TokenList tokens) {
		if (!player.equals(playedOn.getPlayer()))
			return false;

		if (state == State.WAIT_FOR_DISCARD_CHOICE) {

			selectedDiscards = new CardList();
			try {

				CardList allowed = getAllowedDiscards();

				int cnt = discardCount;
				if (discardCount < 0)
					cnt *= -1;
				for (int i = 0; i < cnt; ++i) {
					if (!tokens.hasNext() && discardCount < 0)
						break;
					selectedDiscards.add(allowed.findByTypeExclude(getGame().getCardSet().findCard(tokens.nextString()), false, selectedDiscards));
				}

				state = State.WAIT_FOR_PICKUP_CHOICE;

				CardList replaceWith = getAllowedReplaceWiths(selectedDiscards, turn);
				if (replaceWith.isEmpty()) {
					state = State.FINISHED;
					throw new DominionError("There are no cards you can choose, so that Action was wasted!");
				}

				CardList x = isThereAReplaceChoice(replaceWith);
				if (x != null) {
					//
					// There's no choice the player could make, so we take it
					// for them
					//
					execute(selectedDiscards, x, turn);
					state = State.FINISHED;
				}

				return true;

			} catch (NoMoreTokensError e) {
				throw new DominionError("Invalid command", e);
			}

		} else if (state == State.WAIT_FOR_PICKUP_CHOICE) {

			CardList selectedReplaceWiths = new CardList();
			try {

				CardList replaceWith = getAllowedReplaceWiths(selectedDiscards, turn);
				if (replaceWith.isEmpty()) {
					state = State.FINISHED;
					throw new DominionError("There are no cards you can choose, so that Action was wasted!");
				}

				for (int i = 0; i < pickupCount; ++i)
					selectedReplaceWiths.add(replaceWith.findByTypeExclude(getGame().getCardSet().findCard(tokens.nextString()), false, selectedReplaceWiths));

				execute(selectedDiscards, selectedReplaceWiths, turn);
				state = State.FINISHED;
				return true;

			} catch (NoMoreTokensError e) {
				throw new DominionError("Invalid command", e);
			}
		}
		return false;
	}

	/**
	 * Return a {@link CardList} of cards that the user is allowed to choose to
	 * discard from their hand
	 * 
	 * @return
	 */
	protected CardList getAllowedDiscards() {
		return new CardList(playedOn.getHand());
	}

	/**
	 * If the user has only one option available to them then this should return
	 * a list of the cards they would have to pick. If there is a choice then it
	 * must return <code>null</code>
	 * 
	 * @param replaceWith
	 * @return
	 */
	protected CardList isThereADiscardChoice(CardList allowedDiscards, Turn turn) {
		return null;
	}

	/**
	 * Return a list of cards that the user could select as replacements to the
	 * cards they've discarded
	 * 
	 * @param discards
	 * @param turn
	 * @return
	 */
	protected abstract CardList getAllowedReplaceWiths(CardList discards, Turn turn);

	/**
	 * If the user has only one option available to them then this should return
	 * a list of the cards they would have to pick. If there is a choice then it
	 * must return <code>null</code>
	 * 
	 * @param replaceWith
	 * @return
	 */
	protected CardList isThereAReplaceChoice(CardList replaceWith) {
		return null;
	}

	@Override
	public void playOn(Turn turn, CardInstance instance) {
		super.playOn(turn, instance);
		state = State.WAIT_FOR_DISCARD_CHOICE;
		selectedDiscards = null;

		//
		// Check if the user has a choice in what they trash
		//
		CardList x = isThereADiscardChoice(getAllowedDiscards(), turn);
		if (x != null) {
			selectedDiscards = x;
			state = State.WAIT_FOR_PICKUP_CHOICE;

			CardList replaceWith = getAllowedReplaceWiths(selectedDiscards, turn);
			if (replaceWith.isEmpty()) {
				state = State.FINISHED;
				throw new DominionError("There are no cards you can choose, so that Action was wasted!");
			}

			x = isThereAReplaceChoice(replaceWith);
			if (x != null) {
				//
				// There's no choice the player could make, so we take it
				// for them
				//
				execute(selectedDiscards, x, turn);
				state = State.FINISHED;
			}

		}

	}

	protected void execute(CardList discards, CardList replaceWith, Turn turn) {

		for (CardInstance ci : discards) {
			turn.removeFromHand(ci);
			getGame().getTrash().add(ci);
			turn.getInPlay().contains(ci); // if we're trashing ourself then
											// this will happen
		}
		getGame().getTableCards().remove(replaceWith);

		for (CardInstance ci : replaceWith)
			turn.getHand().add(ci);

		int cnt = 0;
		getGame().getOutput().out(turn.getPlayer().getName()).out(" trashed ");
		for (Iterator<CardInstance> i = discards.iterator(); i.hasNext();) {
			getGame().getOutput().out("a ").card(i.next().getCard(), false);
			if (i.hasNext())
				getGame().getOutput().out(" and ");
			++cnt;
		}

		getGame().getOutput().out(" card" + (cnt > 1 ? "s" : "") + " to gain ");

		for (Iterator<CardInstance> i = replaceWith.iterator(); i.hasNext();) {
			getGame().getOutput().card(i.next().getCard(), false);
			if (i.hasNext())
				getGame().getOutput().out(" and ");
		}
		getGame().getOutput().out(" into their hand").send();

	}

	public boolean needsInput() {
		return state != State.FINISHED;
	}

	@Override
	public void askQuestion(DominionOutput response) {
		if (state == State.WAIT_FOR_DISCARD_CHOICE) {
			if (discardCount == -100)
				response.out(playedOn.getPlayer().getName()).out(": Pick some cards to trash ");
			else
				response.out(playedOn.getPlayer().getName()).out(": Pick " + discardCount + " card" + (discardCount > 1 ? "s" : "") + " to trash ");
			getGame().showCardSet(getAllowedDiscards(), true, true);

		} else if (state == State.WAIT_FOR_PICKUP_CHOICE) {
			response.out(playedOn.getPlayer().getName()).out(": Pick " + pickupCount + " card" + (pickupCount > 1 ? "s" : "") + " to replace with ");
			getGame().showCardSet(getAllowedReplaceWiths(selectedDiscards, playedOn), true, true);
		}
	}

	protected int getDiscardCount() {
		return discardCount;
	}

	/**
	 * if negative then they can pick up to that many, but not required
	 * 
	 * @param discardCount
	 */
	protected void setDiscardCount(int discardCount) {
		this.discardCount = discardCount;
	}

	protected int getPickupCount() {
		return pickupCount;
	}

	protected void setPickupCount(int pickupCount) {
		this.pickupCount = pickupCount;
	}

}
