package uk.co.stikman.dominion.cardsets;

import uk.co.stikman.dominion.CardSet;
import uk.co.stikman.dominion.CardTypes;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.cards.BankCard;
import uk.co.stikman.dominion.cards.BasicActionCard;
import uk.co.stikman.dominion.cards.CountingHouseCard;
import uk.co.stikman.dominion.cards.ForgeCard;
import uk.co.stikman.dominion.cards.GrandMarketCard;
import uk.co.stikman.dominion.cards.LoanCard;
import uk.co.stikman.dominion.cards.MoneyCard;
import uk.co.stikman.dominion.cards.RepeatPlayCard;
import uk.co.stikman.dominion.cards.TrashAndReplaceCard;
import uk.co.stikman.dominion.cards.VictoryCard;

public class ProsperityCardSet implements CardSet {

	@Override
	public String getName() {
		return "Prosperity";
	}

	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public String getDesc() {
		return "Prosperity cards";
	}

	//@formatter:off
	@Override
	public void addCards(DominionGame game, CardTypes res) {
		res.addCardType(new MoneyCard(game,   			"Platinum",   		"Pt", 9, 5, false));
		res.addCardType(new VictoryCard(game, 			"Colony",     		"Cy", 11, 10, false));
		res.addCardType(new TrashAndReplaceCard(game, 	"Expand",     		"Ex", 7,    0, 0, 0, "*,+3")); // trash a card, replace with +3
		res.addCardType(new GrandMarketCard(game, 		"Grand Market", 	"GM", 6, 2, 1, 1, 1)); // can only buy if no copper in hand
		res.addCardType(new BasicActionCard(game, 		"Worker's Village", "WV", 4, 0, 2, 1, 1));
		res.addCardType(new CountingHouseCard(game, 	"Countinghouse", 	"Cth",5)); // get coppers from your discard back into hand
		res.addCardType(new BankCard(game, 				"Bank", 			"Ba", 7)); // +1 for each treasure in play
		res.addCardType(new LoanCard(game,         		"Loan", 			"Lo", 3)); // +1 coin, go through deck and discard or trash first treasure.  discard others.
		res.addCardType(new RepeatPlayCard(game,		"Kings Court",  	"KC", 7, 3)); // choose an action card, play it three times
		res.addCardType(new ForgeCard(game,				"Forge", 			"Fg", 7, 0, 0, 0, 0)); // trash anynumber of cards, replace with one of exact total value

	}
	//@formatter:on

}
