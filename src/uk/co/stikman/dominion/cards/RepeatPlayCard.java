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

public class RepeatPlayCard extends Card implements IsActionCard, IsInteractiveCard {

	private Turn	playedOn;
	private int		repeatcount;
	private boolean	cardChosen;

	public RepeatPlayCard(DominionGame game, String name, String shortcut, int cost, int repeatcount) {
		super(game, name, shortcut, cost);
		this.repeatcount = repeatcount;
	}

	@Override
	public void describe(DominionOutput output) {
		describeNameType(output);
		output.soft(" (").out("Choose an action from your hand, play it " + repeatcount + " times").soft(")");
	}

	@Override
	public CardType getCardType() {
		return CardType.ACTION;
	}

	@Override
	public void checkCanPlay(Turn turn, CardInstance instance) throws DominionError {
		int i = 0;
		for (CardInstance ci : turn.getHand())
			if (ci.getCard() instanceof IsActionCard && !ci.equals(instance))
				++i;
		if (i == 0)
			throw new DominionError("You don't have any action cards to play with this");
	}

	@Override
	public void playOn(Turn turn, CardInstance instance) {
		this.playedOn = turn;
		turn.removeFromHand(instance);
		turn.getInPlay().add(instance);
		this.cardChosen = false;
	}

	
	
	
	
	@Override
	public boolean needsInput() {
		return !cardChosen;
	}

	@Override
	public void askQuestion(DominionOutput response) {
		if (!cardChosen) {
			response.out(playedOn.getPlayer().getName()).out(": Choose an Action card from your hand to play: ");
			CardList lst = new CardList();
			for (CardInstance ci : playedOn.getHand())
				if (ci.getCard() instanceof IsActionCard)
					lst.add(ci);
			getGame().showCardSet(lst, true, false);
			response.send();
		}
	}

	@Override
	public boolean processResponse(Player player, Turn turn, TokenList tokens) {
		if (!player.equals(playedOn.getPlayer()))
			return false;
		
		//turn.getHand().add(getGame().getTableCards().findByType("Market", false));
		
		if (!cardChosen) {
			CardList selectedCards = turn.extractCardsFromHand(tokens, 1);
			if (selectedCards.size() != 1)
				throw new DominionError("Pick one Action card");
			CardInstance selected = selectedCards.get(0);
			for (int i = 0; i < repeatcount; ++i)
				turn.addToPlayList(selected);
			turn.setActions(turn.getActions() + repeatcount);
			cardChosen = true;
			return true;
		}
		return false;
	}

}
