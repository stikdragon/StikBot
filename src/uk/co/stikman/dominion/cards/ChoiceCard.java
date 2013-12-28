package uk.co.stikman.dominion.cards;

import java.util.ArrayList;
import java.util.List;

import uk.co.stikman.dominion.Card;
import uk.co.stikman.dominion.CardInstance;
import uk.co.stikman.dominion.CardType;
import uk.co.stikman.dominion.DominionError;
import uk.co.stikman.dominion.DominionGame;
import uk.co.stikman.dominion.DominionOutput;
import uk.co.stikman.dominion.IsActionCard;
import uk.co.stikman.dominion.IsInteractiveCard;
import uk.co.stikman.dominion.Player;
import uk.co.stikman.dominion.Turn;
import uk.co.stikman.tokeniser.TokenException;
import uk.co.stikman.tokeniser.TokenList;

public abstract class ChoiceCard extends Card implements IsActionCard, IsInteractiveCard {

	public abstract class Choice {
		private String	text;

		public Choice(String text) {
			super();
			this.text = text;
		}

		public String getText() {
			return text;
		}

		public abstract void action(Turn currentTurn);

	}

	private boolean			choiceMade	= false;
	private List<Choice>	choices;
	private int				choiceCount;
	protected Turn			playedOn;

	public ChoiceCard(DominionGame game, String name, String shortcut, int cost) {
		super(game, name, shortcut, cost);
	}

	@Override
	public void checkCanPlay(Turn turn, CardInstance instance) throws DominionError {
	}

	@Override
	public void playOn(Turn turn, CardInstance instance) {
		this.playedOn = turn;
		choiceMade = false;
		choices = new ArrayList<>();
		choiceCount = getChoices(choices);
		turn.removeFromHand(instance);
		turn.getInPlay().add(instance);
	}

	/**
	 * Should fill <code>choices</code> with a number of {@link Choice} which
	 * will be offered to the player. It should return the number of choices the
	 * player is expected to make
	 * 
	 * @param choices
	 * @return
	 */
	protected abstract int getChoices(List<Choice> choices);

	@Override
	public boolean needsInput() {
		return !choiceMade;
	}

	@Override
	public void askQuestion(DominionOutput response) {
		response.out(playedOn.getPlayer().getName()).out(": choose ").bold(Integer.toString(choiceCount)).out(" of the following: ");
		int i = (int) 'A';
		for (Choice choice : choices) {
			// there must be a better way to do this in java
			byte[] b = new byte[1];
			b[0] = (byte) i;
			String ch = new String(b);
			response.bold("[").out(ch).bold("] ").out(choice.getText()).out(". ");
			++i;
		}
	}

	@Override
	public boolean processResponse(Player player, Turn turn, TokenList tokens) {
		if (!player.equals(turn.getPlayer()))
			return false;
		List<String> choices = new ArrayList<>();
		try {
			while (tokens.hasNext()) {
				String s = tokens.nextString().toUpperCase();
				if (s.length() != 1) 
					throw new DominionError("Choose with single letters, eg. A B D");
				if (choices.contains(s))
					throw new DominionError("Your choices must be distinct");
				choices.add(s);
			}
		} catch (TokenException e) {
			throw new DominionError("Invalid input", e);
		}
		if (choices.size() != choiceCount)
			throw new DominionError("You must choose " + choiceCount + " of the options.  Try again");

		List<Choice> chosen = new ArrayList<>();
		for (String s : choices) {
			char ch = s.charAt(0);
			int i = (int)ch - (int)'A';
			if (i < 0 || i >= this.choices.size())
				throw new DominionError("Invalid choice, please choose from the " + this.choices.size() + " options");
			chosen.add(this.choices.get(i));
		}
		
		for (Choice c : chosen)
			c.action(turn);
		choiceMade = true;
		return true;
	}

	@Override
	public CardType getCardType() {
		return CardType.ACTION;
	}

}
