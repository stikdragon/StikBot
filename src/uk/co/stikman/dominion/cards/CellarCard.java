package uk.co.stikman.dominion.cards;

import uk.co.stikman.dominion.Card;
import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.CardList;
import uk.co.stikman.dominion.CardType;
import uk.co.stikman.dominion.DominionError;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsActionCard;
import uk.co.stikman.dominion.IsInteractiveCard;
import uk.co.stikman.dominion.Player;
import uk.co.stikman.dominion.Turn;
import uk.co.stikman.tokeniser.TokenList;

public class CellarCard extends Card implements IsActionCard, IsInteractiveCard {

	private Turn	playedOn;
	private boolean	choiceMade;

	public CellarCard(DominionGame game, String name, String shortcut, int cost) {
		super(game, name, shortcut, cost);
	}

	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);
		output.soft(" (").out("Discard any number of cards.  +1 card each time").soft(")");
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
		turn.getInPlay().add(instance);
		turn.removeFromHand(instance);
		choiceMade = false;
	}

	@Override
	public boolean needsInput() {
		return !choiceMade;
	}

	@Override
	public void askQuestion(DominionOutput response) {
		if (!choiceMade) {
			response.out(playedOn.getPlayer().getName()).out(": Choose a number of cards from your hand to discard: ");
			getGame().showCardSet(playedOn.getHand(), true, false);
			response.send();
		}
	}

	@Override
	public boolean processResponse(Player player, Turn turn, TokenList tokens) {
		if (!player.equals(playedOn.getPlayer()))
			return false;

		if (!choiceMade) {
			CardList selected = turn.extractCardsFromHand(tokens, 100);
			turn.getHand().addAll(selected);
			getGame().getOutput().out(player.getName()).out(" discarded " + selected.size() + " cards and drew ");
			CardList drew = new CardList();
			for (int i = 0; i < selected.size(); ++i) 
				drew.add(turn.getPlayer().draw());
			getGame().showCardSet(drew, true, false);
			getGame().getOutput().out(" into their hand").send();
			choiceMade = true;
			return true;
		}
		return false;
	}

}
