package uk.co.stikman.dominion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import uk.co.stikman.stikbot.BaseModule;
import uk.co.stikman.stikbot.Game;
import uk.co.stikman.stikbot.GameState;
import uk.co.stikman.stikbot.IrcFormatter;
import uk.co.stikman.stikbot.Nick;
import uk.co.stikman.stikbot.PermissionError;
import uk.co.stikman.stikbot.SendResponseHandler;
import uk.co.stikman.stikbot.StandardCommandHandler;
import uk.co.stikman.stikbot.StikBot;
import uk.co.stikman.stikbot.User;
import uk.co.stikman.stikbot.except.IRCException;
import uk.co.stikman.stikbot.except.ModuleError;
import uk.co.stikman.tokeniser.TokenException;
import uk.co.stikman.tokeniser.TokenList;

public class DominionGameModule extends BaseModule {

	public static final String	VERSION	= "1.5";

	public class DominionOutputImpl implements DominionOutput {

		private SendResponseHandler	handler;
		private IrcFormatter		currentFormat;

		public DominionOutputImpl(SendResponseHandler srh) {
			this.handler = srh;
		}

		@Override
		public DominionOutput soft(String s) {
			getFormat().quiet(s);
			return this;
		}

		private IrcFormatter getFormat() {
			if (this.currentFormat == null)
				this.currentFormat = handler.format();
			return this.currentFormat;
		}

		@Override
		public void send() {
			getFormat().send();
			this.currentFormat = null;
		}

		@Override
		public DominionOutput out(String s) {
			getFormat().out(s);
			return this;
		}

		private int getColour(CardType cardType) {
			switch (cardType) {
				case ACTION:
					return 4;
				case TREASURE:
				case TREASURE_VICTORY:
					return 5;
				case VICTORY:
					return 6;
				case ACTION_VICTORY:
					return 4;
				case CURSE:
					return 2;
				default:
					return 1;
			}
		}

		@Override
		public DominionOutput card(Card card, boolean includeCost) {
			String shortcut = card.getShortcut();
			String s = card.getName();
			int colour = getColour(card.getCardType());

			String lcshort = shortcut.toLowerCase();
			String lcs = s.toLowerCase();
			int start = 0;
			int scp = 0;
			boolean live = false;
			if (lcshort.length() > 0) 
				live = lcs.charAt(0) == lcshort.charAt(scp);
			if (live)
				++scp;
			for (int i = 1; i < s.length(); ++i) {
				boolean b = (scp < lcshort.length()) && (lcs.charAt(i) == lcshort.charAt(scp));
				if (b)
					++scp;
				if (b ^ live) {
					getFormat().underline(live);
					getFormat().colour(colour, s.substring(start, i));
					live = b;
					start = i;
				}
			}
			getFormat().underline(live);
			getFormat().colour(colour, s.substring(start));

			if (includeCost)
				soft("(" + card.getCost() + ")");
			return this;
		}

		@Override
		public void cardtype(CardType cardType) {
			switch (cardType) {
				case ACTION:
					getFormat().colour(getColour(CardType.ACTION), "Action");
					break;
				case ACTION_VICTORY:
					getFormat().colour(getColour(CardType.ACTION), "Act");
					getFormat().quiet("/");
					getFormat().colour(getColour(CardType.VICTORY), "Vic");
					break;
				case TREASURE:
					getFormat().colour(getColour(CardType.TREASURE), "Treasure");
					break;
				case TREASURE_VICTORY:
					getFormat().colour(getColour(CardType.TREASURE), "Tre");
					getFormat().quiet("/");
					getFormat().colour(getColour(CardType.VICTORY), "Vic");
					break;
				case VICTORY:
					getFormat().colour(getColour(CardType.VICTORY), "Victory");
					break;
				case CURSE:
					getFormat().colour(getColour(CardType.CURSE), "Curse");
					break;
				default:
					break;
			}
		}

		@Override
		public DominionOutput bold(String s) {
			getFormat().bold(s);
			return this;
		}

	}

	private StikBot	bot;

	@Override
	public void init(StikBot gameBot) throws ModuleError {
		this.bot = gameBot;

		try {
			initDB();
		} catch (SQLException e1) {
			throw new ModuleError("Failed to initialise Dominion module", e1);
		}

		bot.registerHandler(new StandardCommandHandler(this, "newgame", "Create a game of Dominion.  \"newgame <gamename>\"") {
			@Override
			public void handle(String command, Nick nick, TokenList args, SendResponseHandler response) throws IRCException {
				try {
					newGame(nick, args.nextString(), response);
				} catch (TokenException e) {
					throw new ModuleError("Invalid command", e);
				}
			}
		});

		bot.registerHandler(new StandardCommandHandler(this, "join", "Join a game of Dominion.  \"join <gamename>\"") {
			@Override
			public void handle(String command, Nick nick, TokenList args, SendResponseHandler response) throws IRCException {
				try {
					join(nick, args.nextString(), response);
				} catch (TokenException e) {
					throw new ModuleError("Invalid command", e);
				}
			}
		});

	}

	private void initDB() throws SQLException {

		List<String> sql = new ArrayList<>();

		if (!bot.checkForTable("dominion_configs"))
			sql.add("CREATE TABLE dominion_configs (player VARCHAR(100), name VARCHAR(100), data VARCHAR(4096))");

		for (String s : sql)
			bot.getConnection().prepareStatement(s).execute();
		bot.commitdb();

	}

	@Override
	public String getName() {
		return "dominion";
	}

	protected void join(Nick nick, String name, SendResponseHandler response) throws IRCException {
		User usr = checkRole(nick, "");

		DominionGame g = bot.getGame(name, DominionGame.class);
		if (g.getState() != GameState.WAITING_FOR_PLAYERS)
			throw new PermissionError("This game is already running, or has finished.  You can still join and watch, though.  It's on channel " + g.getChannel());
		if (!g.canJoin(usr))
			throw new PermissionError("You aren't allowed to join that game");

		g.addPlayer(usr);
		response.format().out("You have been added to the game ").box(name).out(".  Please type ").bold("/join " + g.getChannel()).out(" to play").send();
	}

	protected void newGame(Nick nick, String name, SendResponseHandler response) throws IRCException {
		checkRole(nick, ""); // check they're logged in
		Game g = bot.getGame(name);
		if (g != null)
			throw new ModuleError("Game already exists");

		//
		// Create a new game
		//
		DominionGame game = new DominionGame(name, this);
		bot.addGame(game);

		final SendResponseHandler srh = bot.getChannelSender(game.getChannel().getName());
		game.setOutput(new DominionOutputImpl(srh));

		response.format().out("Game ").box(name).out(" created").send();
		join(nick, name, response);
	}

	@Override
	public void shutdown() throws ModuleError {

	}

	public void gameTerminated(DominionGame game) {
		bot.removeGame(game);
	}

	@Override
	public String getVersion() {
		return VERSION;
	}

	public void saveGameConfig(Player p, GameConfig config, String name) throws ModuleError {

		try {
			bot.runStatement("DELETE FROM dominion_configs WHERE player = ? AND name = ?", p.getName(), name);
			bot.runStatement("INSERT INTO dominion_configs (player, name, data) VALUES (?, ?, ?)", p.getName(), name, config.save());
			bot.commitdb();
		} catch (SQLException e) {
			bot.rollback();
			throw new ModuleError("Failed to save config", e);
		}

	}

	public GameConfig loadGameConfig(Player p, String name) throws ModuleError {
		try {
			ResultSet rs = bot.runQuery("SELECT data FROM dominion_configs WHERE player = ? AND name = ?", p.getName(), name);
			if (!rs.next())
				throw new ModuleError("Not found: " + name);
			return GameConfig.load(rs.getString(1));
		} catch (SQLException e) {
			throw new ModuleError("Failed to load config", e);
		}

	}

}
