package uk.co.stikman.dominion.cardsets;

import uk.co.stikman.dominion.CardSet;
import uk.co.stikman.dominion.CardTypes;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.cards.CoppersmithCard;
import uk.co.stikman.dominion.cards.CourtyardCard;
import uk.co.stikman.dominion.cards.DukeCard;
import uk.co.stikman.dominion.cards.GreatHallCard;
import uk.co.stikman.dominion.cards.HaremCard;
import uk.co.stikman.dominion.cards.IronWorksCard;
import uk.co.stikman.dominion.cards.NoblesCard;
import uk.co.stikman.dominion.cards.PawnCard;
import uk.co.stikman.dominion.cards.ShantyTownCard;
import uk.co.stikman.dominion.cards.StewardCard;
import uk.co.stikman.dominion.cards.TrashAndReplaceCard;

public class IntrigueCardSet implements CardSet {

	@Override
	public String getName() {
		return "Intrigue";
	}

	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public String getDesc() {
		return "Intrigue cards";
	}

	//@formatter:off
	@Override
	public void addCards(DominionGame game, CardTypes res) {
		//                          	               	                  	cost 
		//																	  |	spend 
		//																	  |  | action 
		//																	  |	 |	| buy 
		//																	  |	 |	|  | cards
		//          														  |  |  |  |  |
		res.addCardType(new CourtyardCard(game, 		"Courtyard", 	"Co", 2, 0, 0, 0, 3));  // +3 cards, put card on deck
		res.addCardType(new GreatHallCard(game,  	 	"Great Hall",   "GH", 3, 0, 1, 0, 1, 1)); // 1 victory point, +1 action, +1 card
		res.addCardType(new TrashAndReplaceCard(game, 	"Upgrade", 		"Up", 5,    1, 0, 1, "*,+2"));  // trash a card from hand, replace with one +2$.  +1 card, +1 action
		res.addCardType(new TrashAndReplaceCard(game, 	"Trading Post",	"TP", 5,    0, 0, 0, "**,+Silver"));  // trash 2 cards, take a Silver
		res.addCardType(new IronWorksCard(game,      	"Ironworks", 	"Iw", 4, 0, 0, 0, 0, 4)); // Gain a card costing up to $4. If it is an… Action card, +1 Action. Treasure card, +$1. Victory card, +1 Card.
		res.addCardType(new PawnCard(game, 				"Pawn", 		"Pa", 2)); // Choose two: +1 card, +1 cash, +1 action, +1 buy
		res.addCardType(new StewardCard(game, 			"Steward", 		"Sw", 3)); // Choose one: +2 Cards; or +$2; or trash 2 cards from your hand.
		res.addCardType(new ShantyTownCard(game, 		"Shanty Town",	"ST", 3)); // +2 cards if you have no other actions
		res.addCardType(new CoppersmithCard(game, 		"Coppersmith",  "Cs", 4)); // Copper is worth 2 this turn

		res.addCardType(new DukeCard(game,   		"Duke",   		"Du", 5));  // worth 1 victory point for each duchy
		res.addCardType(new HaremCard(game,  		"Harem",   		"Ha", 6, 2, 2));  // worth 2 victory points and +2coin
		res.addCardType(new NoblesCard(game,  		"Nobles",  		"No", 6, 2));  // worth 2 victory points.  Choose +3 cards or +2 actions
	
	}
	//@formatter:on

}
