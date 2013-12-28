package uk.co.stikman.dominion.cardsets;

import uk.co.stikman.dominion.BasicCardSet;
import uk.co.stikman.dominion.CardSet;
import uk.co.stikman.dominion.CardTypes;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.cards.BasicActionCard;
import uk.co.stikman.dominion.cards.CellarCard;
import uk.co.stikman.dominion.cards.ChancellorCard;
import uk.co.stikman.dominion.cards.ChapelCard;
import uk.co.stikman.dominion.cards.CouncilRoomCard;
import uk.co.stikman.dominion.cards.FeastCard;
import uk.co.stikman.dominion.cards.GainCardCard;
import uk.co.stikman.dominion.cards.GardensCard;
import uk.co.stikman.dominion.cards.MilitiaCard;
import uk.co.stikman.dominion.cards.MineCard;
import uk.co.stikman.dominion.cards.MoneyLenderCard;
import uk.co.stikman.dominion.cards.RepeatPlayCard;
import uk.co.stikman.dominion.cards.TrashAndReplaceCard;
import uk.co.stikman.dominion.cards.WitchCard;

public class DominionCardSet extends BasicCardSet implements CardSet {

	@Override
	public String getName() {
		return "Dominion";
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getDesc() {
		return "Standard Dominion cards";
	}

	//@formatter:off
	@Override
	public void addCards(DominionGame game, CardTypes res) {
		//                       		                                    cost 
		//																	  |	spend 
		//																	  |  | action 
		//																	  |	 |	| buy 
		//																	  |	 |	|  | cards
		//          														  |  |  |  |  |
		res.addCardType(new BasicActionCard(game,		"Woodcutter", 	"Wo", 3, 2, 0, 1, 0));
		res.addCardType(new BasicActionCard(game, 		"Village", 		"Vi", 3, 0, 2, 0, 1));
		res.addCardType(new BasicActionCard(game, 		"Market", 		"Ma", 5, 1, 1, 1, 1));
		res.addCardType(new BasicActionCard(game, 		"Festival", 	"Fs", 5, 2, 2, 1, 0));
		res.addCardType(new CouncilRoomCard(game, 		"Council Room", "CR", 5, 0, 0, 1, 4));  // +4 cards, everyone else +1 card
		res.addCardType(new BasicActionCard(game, 		"Laboratory", 	"La", 5, 0, 1, 0, 2));
		res.addCardType(new BasicActionCard(game, 		"Smithy", 		"Sm", 4, 0, 0, 0, 3));
		res.addCardType(new MineCard(game, 				"Mine", 		"Mi", 5, 0, 0, 0, 0));  // turn copper->silver, silver->gold
		res.addCardType(new ChapelCard(game, 			"Chapel", 		"Ch", 2, 0, 0, 0, 0));  // trash up to 4 cards
		res.addCardType(new GainCardCard(game, 			"Workshop", 	"sh", 3, 0, 0, 0, 0, 4));  // gain card costing up to 4
		res.addCardType(new FeastCard(game, 			"Feast", 		"fe", 4, 0, 0, 0, 0));  // trash card and gain up to 5
		res.addCardType(new MilitiaCard(game, 			"Militia", 		"Mt", 4, 2, 0, 0, 0));  // +2, everyone else reduced to 3 cards
		res.addCardType(new TrashAndReplaceCard(game, 	"Remodel", 		"Rm", 4,    0, 0, 0, "*,+2"));  // trash a card from hand, replace with one +2$
		res.addCardType(new MoneyLenderCard(game,       "Moneylender",  "Ml", 4, 3, 0, 0, 0, "Copper")); // trash a copper from hand, gain 3 coin
		res.addCardType(new CellarCard(game,			"Cellar", 		"Ce", 2)); // discard any number of cards, gain another for each
		res.addCardType(new ChancellorCard(game, 	 	"Chancellor",   "Cc", 3,          2)); // +2 cards, option to discard entire deck
		res.addCardType(new WitchCard(game, 			"Witch", 		"Wi", 5,          2)); // +2 cards, other players get a Curse
		res.addCardType(new MoatCard(game,				"Moat",			"Mo", 2, 0, 0, 0, 2)); // +2cards, reveal to be unaffected by an attack
		
		res.addCardType(new RepeatPlayCard(game,		"Throne Room",  "TR", 4, 2)); // choose an action card, play it twice

		res.addCardType(new GardensCard(game, 			"Gardens", 		"Ga", 4));  // worth 1 victory point for each 10 cards
	}
	//@formatter:on

	
	
	
}
