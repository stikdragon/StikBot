package uk.co.stikman.dominion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import uk.co.stikman.stikbot.AbstractGame;
import uk.co.stikman.stikbot.Game;
import uk.co.stikman.stikbot.GameError;
import uk.co.stikman.stikbot.GamePlayer;
import uk.co.stikman.stikbot.GameState;
import uk.co.stikman.stikbot.SendResponseHandler;
import uk.co.stikman.stikbot.User;
import uk.co.stikman.stikbot.except.ModuleError;
import uk.co.stikman.stikbot.util.MutableInteger;
import uk.co.stikman.stikbot.util.Pair;
import uk.co.stikman.stikbot.util.Utils;
import uk.co.stikman.tokeniser.TokenException;
import uk.co.stikman.tokeniser.TokenList;
import uk.co.stikman.tokeniser.TokenType;
import uk.co.stikman.tokeniser.Tokeniser;

public class DominionGame extends AbstractGame implements Game {

	private List<Player>		players		= new ArrayList<Player>();
	private Turn				currentTurn	= null;
	private DominionOutput		output;
	private GameConfig			config		= new GameConfig();
	private CardList			tableCards	= new CardList();
	private CardTypes			cardSet		= null;
	private GameState			state		= GameState.WAITING_FOR_PLAYERS;
	private DominionGameModule	module;
	private CardList			trash		= new CardList();

	public DominionGame(String name, DominionGameModule module) {
		super(name);
		this.module = module;
	}

	@Override
	public void onMessage(User user, String message, SendResponseHandler response) throws GameError {
		String player = user.getName();
		message = message.replace(',', ' ');
		TokenList toks = Tokeniser.tokenise(message);
		try {
			boolean handled = false;
			if (currentTurn != null && currentTurn.getPendingCard() != null)
				handled = handlePendingCard(player, toks);

			if (!handled) {
				String cmd = toks.next(TokenType.NORMAL);
				if (cmd.equalsIgnoreCase("withdraw"))
					withdraw(player);
				// else if (cmd.equalsIgnoreCase("join"))
				// join(user, player);
				else if (cmd.equalsIgnoreCase("play"))
					play(player, toks);
				else if (cmd.equalsIgnoreCase("buy"))
					buy(player, toks);
				else if (cmd.equalsIgnoreCase("show"))
					show(player, toks);
				else if (cmd.equalsIgnoreCase("skip"))
					skip(player, toks);
				else if (cmd.equalsIgnoreCase("stats"))
					statistics(player, toks);
				else if (cmd.equalsIgnoreCase("describe") || cmd.equalsIgnoreCase("desc"))
					describe(player, toks);
				else if (cmd.equalsIgnoreCase("startgame"))
					startGame(player, toks);
				else if (cmd.equalsIgnoreCase("config"))
					configure(player, toks);
			}
			


		} catch (DominionError de) {
			throw new GameError(de.getMessage(), de);
		} catch (TokenException e) {
			throw new GameError("Invalid command", e);
		}
	}

	private void configure(String player, TokenList toks) throws TokenException {
		Player p = findPlayer(player);
		if (p == null)
			return;

		if (!toks.hasNext()) {
			showConfig();
		} else {
			String cmd = toks.nextString();
			if (cmd.equalsIgnoreCase("addset")) {
				String s = toks.nextString();
				if (s.equalsIgnoreCase("all"))
					config.addAll();
				else
					config.addSet(s);
				showConfig();
			} else if (cmd.equalsIgnoreCase("removeset")) {
				config.removeSet(toks.nextString());
				showConfig();
			} else if (cmd.equalsIgnoreCase("setscale")) {
				config.setCardScale(Float.parseFloat(toks.nextString()));
				showConfig();
			} else if (cmd.equalsIgnoreCase("load")) {
				try {
					config = module.loadGameConfig(p, toks.nextString());
				} catch (ModuleError e) {
					throw new DominionError(e);
				}
				showConfig();
			} else if (cmd.equalsIgnoreCase("save")) {
				try {
					module.saveGameConfig(p, config, toks.nextString());
				} catch (ModuleError e) {
					throw new DominionError(e);
				}
				getOutput().out("OK").send();
			} else if (cmd.equalsIgnoreCase("help")) {
				//@formatter:off
				getOutput().out("Available commands: ")
					.bold("addset").soft(", ")
					.bold("removeset").soft(", ")
					.bold("setscale").soft(", ")
					.bold("setcardcount").soft(", ")
					.bold("save").soft(", ")
					.bold("load").soft(", ")
					.bold("help").soft(", ")
					.bold("listsets").out(".").send();
				//@formatter:on
			} else if (cmd.equalsIgnoreCase("listsets")) {
				listAvailableSets();
			} else if (cmd.equalsIgnoreCase("setcardcount")) {
				config.setNumtypes(Integer.parseInt(toks.nextString()));
				showConfig();
			} else {
				throw new DominionError("Unknown config command: " + cmd);
			}
		}
	}

	private void listAvailableSets() {
		for (CardSet cs : config.getAvailableSets()) {
			getOutput().bold(cs.getName()).soft(" - ");
			CardTypes lst = new CardTypes();
			cs.addCards(null, lst);
			for (Iterator<Card> i = lst.iterator(); i.hasNext();) {
				Card x = i.next();
				getOutput().card(x, false);
				if (i.hasNext())
					getOutput().soft(", ");
			}
			getOutput().send();
		}
	}

	private void showConfig() {
		getOutput().out("Game config - Sets: ").bold(Utils.join(config, ", "));
		getOutput().out(".  Card Scale: ").bold(Float.toString(config.getCardScale()));
		getOutput().out(".  Card Count: ").bold(Integer.toString(config.getNumtypes()));
		getOutput().send();
	}

	private void describe(String player, TokenList toks) throws TokenException {
		while (toks.hasNext()) {
			String s = toks.nextString();
			if (s.equalsIgnoreCase("all")) {
				
				Set<Card> ingame = new HashSet<>();
				for (CardInstance ci : getTableCards()) 
					ingame.add(ci.getCard());
				List<Card> list = new ArrayList<>(ingame);
				Collections.sort(list, new Comparator<Card>() {
					@Override
					public int compare(Card o1, Card o2) {
						int r = o1.getCardType().compareTo(o2.getCardType());
						if (r != 0)
							return r;
						r = o1.getCost() - o2.getCost();
						if (r != 0)
							return r;
						return o1.getName().compareTo(o2.getName());
					}
				});
			
				
				for (Card c : list) {
					c.describe(getOutput());
					getOutput().send();
				}
				return;
			}
				
			config.getSelectedCards().findCard(s).describe(getOutput());
			getOutput().send();
		}
	}

	private void statistics(String player, TokenList toks) {
		checkPlayer(player, false);
		getOutput().bold("STATISTICS:  ").out("Cards played: ").bold(Integer.toString(-1)).out(",  ");
		for (Player p : players)
			getOutput().out(p.getName()).out("'s cards: ").bold(Integer.toString(p.getDeck().size() + p.getDiscards().size() + p.getHand().size())).out(",  ");
		getOutput().send();
	}

	private void skip(String player, TokenList toks) {
		checkPlayer(player, true);
		finishTurn();
	}

	private void show(String player, TokenList toks) {
		String s;
		try {
			s = toks.nextString();

			if (s.equalsIgnoreCase("table")) {
				checkPlayer(player, false);
				showTable();
			}

			if (s.equalsIgnoreCase("hand")) {
				checkPlayer(player, true);
				showCurrentTurn();
			}

		} catch (TokenException e) {
			throw new DominionError("Invalid command");
		}
	}

	private void buy(String player, TokenList toks) {

		Player p = checkPlayer(player, true);
		try {
			String s = toks.nextString();
			Card ct = cardSet.findCard(s);
			//
			// Check they can afford it
			//
			if (currentTurn.calculateBuys() < 1)
				throw new DominionError("You have no buy actions left");
			ct.canBuy(currentTurn);

			currentTurn.getBoughtCards().add(pickTableCard(ct));
			currentTurn.setBuys(currentTurn.getBuys() - 1);
			getOutput().out(p.getName());
			getOutput().out(" purchased ");
			ct.describe(getOutput());
			getOutput().out(" for " + ct.getCost() + " coin.  ");
			if (currentTurn.validMovesLeft()) {
				getOutput().out("Remaining - C/A/B = ").bold(Integer.toString(currentTurn.calculateCoin())).out("/").bold(Integer.toString(currentTurn.calculateActions())).out("/").bold(Integer.toString(currentTurn.calculateBuys()));
			}
			getOutput().send();
			if (!currentTurn.validMovesLeft())
				finishTurn();
		} catch (TokenException e) {
			throw new DominionError("Invalid command", e);
		}
	}

	private CardInstance pickTableCard(Card type) {
		CardInstance res = null;
		for (CardInstance c : tableCards) {
			if (c.getCard().equals(type)) {
				res = c;
				break;
			}
		}
		if (res == null)
			throw new DominionError("No cards of that type left");
		tableCards.remove(res);
		return res;
	}

	/**
	 * Play an action
	 * 
	 * @param player
	 * @param toks
	 */
	private void play(String player, TokenList toks) {
		checkPlayer(player, true);

		//
		// Build list of the cards they want to play
		//
		while (toks.hasNext()) {
			try {
				String s = toks.nextString();
				currentTurn.addToPlayList(currentTurn.findInHand(s, false));
			} catch (TokenException e) {
				throw new DominionError("Invalid command. List cards separated with spaces, eg. Vi Wo Wo", e);
			}
		}

		
		runPlayList();
		
	}

	public void runPlayList() {
		if (currentTurn.isCardsToPlay()) {
			CardInstance c = currentTurn.extractNextPlayCard();
			
			if (!(c.getCard() instanceof IsActionCard))
				throw new DominionError("You can only play Action cards");

			IsActionCard bac = (IsActionCard) c.getCard();
			bac.checkCanPlay(currentTurn, c);

			if (currentTurn.calculateActions() == 0)
				throw new DominionError("You have no actions left to play this card");

			getOutput().out(currentTurn.getPlayer().getName()).out(" played ");
			c.getCard().describe(getOutput());
			getOutput().out(". ");

			currentTurn.getInPlay().add(c);
			currentTurn.getHand().remove(c);
			currentTurn.setActions(currentTurn.getActions() - 1);
			bac.playOn(currentTurn, c);
			getOutput().send();

			if (c.getCard() instanceof IsInteractiveCard) {
				IsInteractiveCard ic = (IsInteractiveCard) c.getCard();

				if (ic.needsInput()) {
					currentTurn.setPendingCard(c);
					ic.askQuestion(getOutput());
					getOutput().send();

					//
					// If this card requires further input then we can't do anymore
					// in a batch
					//
					return;
				}
			}
			
			runPlayList();
			
		} else {
			if (currentTurn.validMovesLeft())
				showCurrentTurn();
			else
				finishTurn();
		}
		
	}

	/**
	 * Returns <code>true</code> if the input was handled, false if not
	 * 
	 * @param player
	 * @param toks
	 * @return
	 */
	private boolean handlePendingCard(String player, TokenList toks) {
		Player p = checkPlayer(player, false);

		IsInteractiveCard pend = currentTurn.getPendingCard().getCard().as(IsInteractiveCard.class);
		boolean handled = pend.processResponse(p, currentTurn, toks);
		if (!handled)
			return false;

		if (pend.needsInput()) {
			pend.askQuestion(getOutput());
			getOutput().send();
		} else {
			//
			// It's finished
			//
			currentTurn.setPendingCard(null);
			runPlayList();
		}
		return true;
	}

	private Player checkPlayer(String player, boolean yourturn) {
		Player p = findPlayer(player);
		if (p == null)
			throw new DominionError("You're not in the game.");
		if (currentTurn == null)
			throw new DominionError("The game is not in session");
		if (yourturn && !p.equals(currentTurn.getPlayer()))
			throw new DominionError("It's not your turn.  Wait.");
		return p;
	}

	private void finishTurn() {
		currentTurn.setPendingCard(null);

		Player p = currentTurn.getPlayer();
		p.getDiscards().addAll(currentTurn.getHand());
		p.getHand().clear();
		p.deal(5);
		p.getDiscards().addAll(currentTurn.getBoughtCards());
		p.getDiscards().addAll(currentTurn.getInPlay());

		int cnt = 0;
		for (CardInstance c : tableCards)
			if (c.getCard().getName().equalsIgnoreCase("Province"))
				++cnt;

		if (cnt == 0) {
			gameOver();

		} else {

			int i = players.indexOf(currentTurn.getPlayer());
			if (i == -1)
				throw new DominionError("No current player");
			if (++i == players.size())
				i = 0;
			setCurrentTurn(players.get(i));
		}
	}

	private void gameOver() {
		this.state = GameState.FINISHED;
		getOutput().out("Game over!").send();
		Map<Player, MutableInteger> scores = new HashMap<>();
		for (Player ply : players)
			scores.put(ply, new MutableInteger());
		for (Player ply : players) {
			Map<Card, Pair<MutableInteger, MutableInteger>> breakdown = new HashMap<>();
			ply.consolidate();
			for (CardInstance c : ply.getDeck()) {
				if (c.getCard() instanceof IsVictoryCard) {
					int x = ((IsVictoryCard) c.getCard()).getVictoryPoints(ply.getDeck());
					scores.get(ply).add(x);
					if (!breakdown.containsKey(c.getCard()))
						breakdown.put(c.getCard(), new Pair<>(new MutableInteger(), new MutableInteger()));
					breakdown.get(c.getCard()).getB().add(x);
					breakdown.get(c.getCard()).getA().add(1);
				}
			}
			getOutput().bold(ply.getName()).soft(" - ").bold(Integer.toString(scores.get(ply).value));
			List<String> tmp = new ArrayList<>();
			for (Entry<Card, Pair<MutableInteger, MutableInteger>> e : breakdown.entrySet())
				tmp.add(e.getValue().getA().value + "x" + e.getKey().getName() + "=" + e.getValue().getB().value);
			getOutput().soft(" (").soft(Utils.join(tmp, " + ")).soft(")").send();
			
		}

		Player bestP = players.get(0);
		int bestScore = 0;
		for (Player ply : players) {
			if (scores.get(ply).value > bestScore) {
				bestScore = scores.get(ply).value;
				bestP = ply;
			}
		}

		getOutput().bold(bestP.getName()).bold(" wins!").send();
		getOutput().out("Please leave the channel.").send();
		setCurrentTurn(null);
	}

	private void withdraw(String player) {
		Player p = findPlayer(player);
		if (p == null) // not of interest since they weren't a real player
			return;

		players.remove(p);

		if (getState() == GameState.RUNNING) {
			getOutput().bold("<!> ").bold(p.getName()).bold(" has forfeit the game.  ");
			if (players.size() < 2)
				getOutput().bold("There are no longer enough players to continue.");
			getOutput().send();
			if (players.size() < 2)
				gameOver();
			else if (currentTurn.getPlayer().equals(p))
				setCurrentTurn(players.get(0)); // TODO: pick the next player in
												// sequence
		}

		if (players.isEmpty()) {
			state = GameState.TERMINATED;
			module.gameTerminated(this);
		}
	}

	@Override
	public void addPlayer(User user) {
		join(user, user.getNick().getName());
	}

	private void join(User user, String player) throws DominionError {
		Player p = findPlayer(user.getName());
		if (p != null)
			throw new DominionError("You are already in the game");
		p = new Player(this, player, user);
		players.add(p);
	}

	private void startGame(String player, TokenList toks) {
		Player x = findPlayer(player);
		if (x == null)
			throw new DominionError("You're not in the game");
		if (this.state == GameState.RUNNING)
			throw new DominionError("This game is already running");
		if (this.state != GameState.READY_TO_START)
			throw new DominionError("Game is not ready to start yet");
		if (players.size() < 2)
			throw new DominionError("Cannot start game yet, not enough players joined");

		getOutput().out("Everybody's ready, starting game!  The game will end when all the Provinces have been purchased").send();

		cardSet = CardTypes.createSet(this, this.config);
		cardSet.generateCards(tableCards, config);

		state = GameState.RUNNING;

		//
		// Put the dealt cards in the discard pile so that it'll get shuffled
		// for us
		//
		for (Player p : players) {
			for (int i = 0; i < 7; ++i)
				p.getDiscards().add(new CardInstance(cardSet.findCard("Copper")));
			for (int i = 0; i < 3; ++i)
				p.getDiscards().add(new CardInstance(cardSet.findCard("Estate")));
			p.deal(5);
		}

		showTable();

		setCurrentTurn(players.get(0));
	}

	private void setCurrentTurn(Player player) {
		if (player == null) {
			currentTurn = null;
			return;
		}
		if (currentTurn != null && player.equals(currentTurn.getPlayer()))
			return;
		currentTurn = new Turn(this, player);
		startTurn();
		showCurrentTurn();
	}

	private void startTurn() {

	}

	private void showCurrentTurn() {
		getOutput().out(currentTurn.getPlayer().getName()).out(": It's your turn.  Your hand: ");
		showCardSet(currentTurn.getHand(), false, false);
		getOutput().out(".  Coin: ").bold(Integer.toString(currentTurn.calculateCoin())).out(", Actions: ").bold(Integer.toString(currentTurn.calculateActions())).out(", Buys: ").bold(Integer.toString(currentTurn.calculateBuys()));
		getOutput().send();

	}

	public void showCardSet(Iterable<CardInstance> set, boolean compact, boolean showCost) {
		if (compact) {
			Map<Card, List<CardInstance>> groups = new HashMap<Card, List<CardInstance>>();
			for (CardInstance c : set) {
				if (!groups.containsKey(c.getCard()))
					groups.put(c.getCard(), new ArrayList<CardInstance>());
				groups.get(c.getCard()).add(c);
			}

			List<Entry<Card, List<CardInstance>>> tmp = new ArrayList<>(groups.entrySet());
			Collections.sort(tmp, new Comparator<Entry<Card, List<CardInstance>>>() {
				@Override
				public int compare(Entry<Card, List<CardInstance>> o1, Entry<Card, List<CardInstance>> o2) {
					int r = o1.getKey().getCardType().compareTo(o2.getKey().getCardType());
					if (r != 0)
						return r;
					r = o1.getKey().getCost() - o2.getKey().getCost();
					if (r != 0)
						return r;
					return o1.getKey().getName().compareTo(o2.getKey().getName());
				}
			});

			for (Iterator<Entry<Card, List<CardInstance>>> i = tmp.iterator(); i.hasNext();) {
				Entry<Card, List<CardInstance>> e = i.next();
				getOutput().card(e.getKey(), showCost).soft("x" + e.getValue().size());
				if (i.hasNext())
					getOutput().out(", ");
			}

		} else {
			List<CardInstance> sorted = new ArrayList<CardInstance>();
			for (CardInstance c : set)
				sorted.add(c);
			Collections.sort(sorted, new Comparator<CardInstance>() {
				public int compare(CardInstance o1, CardInstance o2) {
					int r = o1.getCard().getCardType().compareTo(o2.getCard().getCardType());
					if (r != 0)
						return r;
					r = o1.getCard().getCost() - o2.getCard().getCost();
					if (r != 0)
						return r;

					return o1.getCard().getName().compareTo(o2.getCard().getName());
				}
			});
			for (Iterator<CardInstance> i = sorted.iterator(); i.hasNext();) {
				getOutput().card(i.next().getCard(), showCost);
				if (i.hasNext())
					getOutput().out(", ");
			}

		}
	}

	private void showTable() {
		getOutput().out("Table: ");
		showCardSet(tableCards, true, true);
		getOutput().send();
	}

	public DominionOutput getOutput() {
		return output;
	}

	private void testReady() {
		int cnt = 0;
		for (Player p : players)
			if (p.isReady())
				++cnt;
		if (cnt >= 2)
			this.state = GameState.READY_TO_START;
		else
			this.state = GameState.WAITING_FOR_PLAYERS;

	}

	/**
	 * Returns <code>null</code> if this name is not a player
	 * 
	 * @param name
	 * @return
	 */
	private Player findPlayer(String name) {
		for (Player p : players)
			if (p.getUser().getName().equals(name))
				return p;
		return null;
	}

	public Turn getCurrentTurn() {
		return currentTurn;
	}

	public CardTypes getCardSet() {
		return cardSet;
	}

	@Override
	public GameState getState() {
		return state;
	}

	@Override
	public List<GamePlayer> getPlayers() {
		List<GamePlayer> lst = new ArrayList<>();
		for (Player p : players)
			lst.add(p);
		return lst;
	}

	@Override
	public void onUserJoin(User user) {
		Player p = findPlayer(user.getName());
		if (p != null) {
			output.out("Welcome to the game, ").bold(user.getNick().getName()).out(". You're logged in as ").out(user.getName());
			output.out(", it will begin when there's enough players and someone types ").bold("startgame.  ");
			output.out("Type ").bold("config").soft(" [help]").out(" to configure the game before starting it.").send();
			p.setReady(true);
			testReady();
		}
	}

	@Override
	public void onUserPart(User user) {
		withdraw(user.getName());
		testReady();
	}

	public void setOutput(DominionOutput output) {
		this.output = output;
	}

	public CardList getTableCards() {
		return tableCards;
	}

	public CardList getTrash() {
		return this.trash;
	}

	public GameConfig getConfig() {
		return config;
	}

}
