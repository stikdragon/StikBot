package uk.co.stikman.dominion.cards;

import uk.co.stikman.dominion.Card;
import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.CardType;
import uk.co.stikman.dominion.DominionError;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsActionCard;
import uk.co.stikman.dominion.IsInteractiveCard;
import uk.co.stikman.dominion.IsTreasureCard;
import uk.co.stikman.dominion.Player;
import uk.co.stikman.dominion.ProducesTreasure;
import uk.co.stikman.dominion.Turn;
import uk.co.stikman.tokeniser.NoMoreTokensError;
import uk.co.stikman.tokeniser.TokenList;

public class LoanCard extends Card implements IsActionCard, ProducesTreasure, IsInteractiveCard {

	private boolean	choiceMade;
	private CardInstance treasureCard;
	private Turn	playedOn;

	public LoanCard(DominionGame game, String name, String shortcut, int cost) {
		super(game, name, shortcut, cost);
	}

	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);
		output.soft(" (").out("+1 coin. Reveal cards from your deck until you reveal a Treasure. Discard it or trash it. Discard the other cards.").soft(")");
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
		this.playedOn = turn;
		choiceMade = false;
		treasureCard = null;
		
		//
		// Find the next treasure in their deck
		//
		while (treasureCard == null) {
			if (turn.getPlayer().getDeck().isEmpty())
				break;
			CardInstance ci = turn.getPlayer().draw();
			if (ci.getCard() instanceof IsTreasureCard) 
				treasureCard = ci;
			else
				turn.getPlayer().getDiscards().add(ci);
		}
		turn.removeFromHand(instance);
		turn.getInPlay().add(instance);
		if (treasureCard == null)
			getGame().getOutput().out(turn.getPlayer().getName()).out(" did not have any Treasure cards in their deck.  Lucky.");
	}

	@Override
	public int getTreasureValue(Turn turn) {
		return 1;
	}

	
	
	@Override
	public boolean needsInput() {
		return !choiceMade;
	}

	@Override
	public void askQuestion(DominionOutput response) {
		response.out(playedOn.getPlayer().getName()).out(": The next Treasure in your deck is ").card(treasureCard.getCard(), false).out(". ").bold("[").out("D").bold("]").out("iscard or ").bold("[").out("T").bold("]").out("rash?").send();
	}

	@Override
	public boolean processResponse(Player player, Turn turn, TokenList tokens) {
		if (!player.equals(turn.getPlayer()))
			return false;
			
		try {
			String s = tokens.nextString().toUpperCase().trim();
			if (s.length() == 0) 
				throw new DominionError("Invalid response, pick D[iscard] or T[rash]");
			boolean trash;
			if (s.startsWith("T"))
				trash = true;
			else if (s.startsWith("D"))
				trash = false;
			else 
				throw new DominionError("Invalid response, pick D[iscard] or T[rash]");
			
			if (trash)
				getGame().getTrash().add(treasureCard);
			else
				player.getDiscards().add(treasureCard);
			
			getGame().getOutput().out(player.getName()).out(trash ? " trashed " : " discarded ").out("a ").card(treasureCard.getCard(), false);
			choiceMade = true;
				
		} catch (NoMoreTokensError e) {
			throw new DominionError("Invalid response, pick D[iscard] or T[rash]", e);
		}
		
		return true;
	}

}
