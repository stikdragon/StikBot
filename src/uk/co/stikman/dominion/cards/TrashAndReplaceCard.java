package uk.co.stikman.dominion.cards;

import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.CardList;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.Turn;

public class TrashAndReplaceCard extends BaseAbstractTrashAndReplaceCard {

	public enum PickupType {
		BY_VALUE, BY_NAME;
	}

	private PickupType	pickupType;
	private String		pickupCard;
	private int			pickupDifference;

	// <cards to trash>,<thing to replace with>
	// Left:Card to discard, one char each
	// Right:card to pick up. +n means "costing +n", +<name> means pick up a
	// name
	// *,+2
	// **,+silver

	public TrashAndReplaceCard(DominionGame game, String name, String shortcut, int cost, int actions, int buys, int cards, String operation) {
		super(game, name, shortcut, cost, 0, actions, buys, cards);

		if (!operation.matches("(?i)^\\*+\\,\\+([1-9]|[a-z ]+)$"))
			throw new IllegalArgumentException("Invalid operation for ConfigTrashAndReplaceCard");

		String[] lr = operation.split(",");
		setDiscardCount(lr[0].length());

		if (Character.isDigit(lr[1].charAt(1))) {
			pickupType = PickupType.BY_VALUE;
			pickupCard = null;
			pickupDifference = Integer.parseInt(lr[1].substring(1));
		} else {
			pickupType = PickupType.BY_NAME;
			pickupCard = lr[1].substring(1);
			pickupDifference = 0;
		}
	}

	public void describe(DominionOutput output) {
		describeNameType(output);
		if (pickupType == PickupType.BY_NAME)
			output.soft(" (").out("Trash " + getDiscardCount() + " card" + (getDiscardCount() > 1 ? "s" : "") + " from your hand and gain a " + pickupCard);
		else
			output.soft(" (").out("Trash " + getDiscardCount() + " card" + (getDiscardCount() > 1 ? "s" : "") + " from your hand and gain one costing up to " + pickupDifference + " coin more");
		output.soft(")");
	}

	@Override
	protected CardList getAllowedReplaceWiths(CardList discards, Turn turn) {
		CardList res = new CardList();
		if (pickupType == PickupType.BY_NAME) {
			res.add(turn.getGame().getTableCards().findByType(pickupCard, true));
		} else if (pickupType == PickupType.BY_VALUE) {
			int val = 0;
			for (CardInstance ci : discards)
				val += ci.getCard().getCost();
			val += pickupDifference;

			for (CardInstance ci : turn.getGame().getTableCards())
				if (ci.getCard().getCost() <= val)
					res.add(ci);

		}
		return res;

	}

	@Override
	protected CardList getAllowedDiscards() {
		//
		// Can discard anything in their hand
		//
		return new CardList(playedOn.getHand());
	}

	@Override
	protected CardList isThereADiscardChoice(CardList allowedDiscards, Turn turn) {
		if (turn.getHand().size() == getDiscardCount())
			return new CardList(turn.getHand());
		return null;
	}

	
	@Override
	protected CardList isThereAReplaceChoice(CardList replaceWith) {
		if (pickupType == PickupType.BY_NAME) {
			CardList res = new CardList();
			res.add(replaceWith.get(0));
			return res;
		}
		return null;
	}

}
