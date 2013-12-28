package uk.co.stikman.stikbot;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.hsqldb.server.Server;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import uk.co.stikman.stikbot.IrcInterface.IrcEventsHandler;
import uk.co.stikman.stikbot.except.IRCException;
import uk.co.stikman.stikbot.except.ModuleError;
import uk.co.stikman.stikbot.except.UnknownInstruction;
import uk.co.stikman.stikbot.util.LoggerWriter;
import uk.co.stikman.stikbot.util.Utils;
import uk.co.stikman.tokeniser.TokenList;

public class StikBot {

	public static final String		VERSION				= "1.3";
	private static final Logger		LOGGER				= Logger.getLogger(StikBot.class.getName());
	public static final int			DB_VERSION			= 1;
	public static final int			MAX_MESSAGE_LENGTH	= 460;
	public static final String		NEWLINE_SEQ			= System.getProperty("line.separator");

	private Users					users				= new Users(this);
	private Nicks					nicks				= new Nicks(this);
	private Games					games				= new Games(this);
	private Channels				gameChannels		= new Channels(this);
	private List<CommandHandler>	commandHandlers		= new ArrayList<>();
	private List<BaseModule>		modules				= new ArrayList<>();
	private Connection				connection;

	private Server					hsqlServer;
	private IrcInterface			intf;
	private CoreModule				rootModule;
	private List<String>			botNames;
	private BotOptions	options;

	public enum DatabaseState {
		EMPTY, OLDVERSION, OK
	}

	public StikBot(final IrcInterface intf) {
		this.intf = intf;

		intf.setIrcEventsHandler(new IrcEventsHandler() {
			@Override
			public void onPrivateMessage(String sender, String login, String hostname, String message) {
				StikBot.this.onPrivateMessage(sender, login, hostname, message);
			}

			@Override
			public void onMessage(final String channel, final String sender, String login, String hostname, String message) {
				StikBot.this.onMessage(channel, sender, login, hostname, message);
			}

			@Override
			public void onJoin(String channel, String sender, String login, String hostname) {
				StikBot.this.onJoin(channel, sender, login, hostname);
			}

			@Override
			public void onUserList(String channel, org.jibble.pircbot.User[] users) {
				StikBot.this.onUserList(channel, users);
				Channel ch = gameChannels.get(channel);
				if (ch != null) {
					if (users.length == 1) { // us
						ch.setStatus(GameChannelStatus.IDLE);
					} else {
						intf.partChannel(channel);
						gameChannels.remove(ch);
						LOGGER.info("Parted channel " + channel);
					}
				}
			}

			@Override
			public void onPart(String channel, String sender, String login, String hostname) {
				StikBot.this.onPart(channel, sender, login, hostname);
			}

			@Override
			public void onNickChange(String oldNick, String login, String hostname, String newNick) {
				StikBot.this.onNickChange(oldNick, login, hostname, newNick);
			}

			@Override
			public void onDisconnect() {
				StikBot.this.onDisconnect();
			}
		});

	}

	public void setBotNames(List<String> nicks) {
		this.botNames = nicks;
	}

	public void begin(BotOptions opts) throws IOException, IrcException {
		this.options = opts;
		
		try {
			startDB();
		} catch (ClassNotFoundException | SQLException e1) {
			throw new RuntimeException("Could not start database", e1);
		}

		try {
			rootModule = (CoreModule) loadModule("uk.co.stikman.stikbot.CoreModule");
		} catch (ModuleError e) {
			LOGGER.log(Level.SEVERE, "Failed to load root", e);
			throw new RuntimeException(e);
		}

		intf.setLogin("derp");

		List<String> names = new ArrayList<>();
		if (botNames == null) {
			for (int i = 0; i < 10; ++i)
				names.add("stikbot" + i);
		} else {
			names.addAll(botNames);
		}

		int i = 0;
		for (;;) {
			if (i >= names.size())
				throw new IrcException("Cannot find nick to use");
			if (tryConnectAs(opts.getServer(), names.get(i)))
				break;
			++i;
		}

		LOGGER.info("Connected as " + intf.getNick());
		intf.joinChannel(opts.getChannel());

		rootModule.botConnected();

		//
		// Load other modules
		//
		try {
			//		loadModule("uk.co.stikman.ttt.TTTGameModule");
			loadModule("uk.co.stikman.dominion.DominionGameModule");
			//		loadModule("uk.co.stikman.worldgame.WorldGame");
		} catch (ModuleError e) {
			LOGGER.log(Level.SEVERE, "Failed to load module", e);
		}
	}

	private void startDB() throws ClassNotFoundException, SQLException {
		hsqlServer = new org.hsqldb.server.Server();
		hsqlServer.setLogWriter(new LoggerWriter(Logger.getLogger("database"), Level.INFO));
		hsqlServer.setAddress("localhost");
		hsqlServer.setPort(12345);
		hsqlServer.setDatabaseName(0, "stikbot");
		hsqlServer.setDatabasePath(0, "db" + System.getProperty("file.separator") + "bot");
		hsqlServer.start();

		LOGGER.info("Connecting to database...");
		Class.forName("org.hsqldb.jdbc.JDBCDriver");
		String connStr = "jdbc:hsqldb:hsql://localhost:" + Integer.toString(12345) + "/stikbot";
		connection = DriverManager.getConnection(connStr, "SA", "");
		connection.setAutoCommit(false);
		LOGGER.info("...connected");

		//
		// Check version and upgrade if necessary
		//
		switch (checkValidDatabase()) {
		case EMPTY:
			LOGGER.info("Database does not exist, creating a new one...");
			createNewDatabase();
			LOGGER.info("...done.");
			break;

		case OLDVERSION:
			LOGGER.info("Database is an old version attempting upgrade...");
			updateDatabase();
			LOGGER.info("...done.");
			break;

		case OK:
			break;
		}

	}

	private void updateDatabase() {
	}

	public DatabaseState checkValidDatabase() {
		synchronized (connection) {
			try {

				DatabaseMetaData meta = connection.getMetaData();
				ResultSet res = meta.getTables(null, null, null, new String[] { "TABLE" });
				boolean found = false;
				while (res.next())
					found = found | res.getString("TABLE_NAME").equalsIgnoreCase("global");
				if (!found)
					return DatabaseState.EMPTY;

				PreparedStatement pst = connection.prepareStatement("SELECT val FROM global WHERE key = 'db_vers'");
				pst.clearParameters();
				ResultSet rs = pst.executeQuery();
				if (rs.next()) {
					int vers = Integer.parseInt(rs.getString(1));
					if (vers == DB_VERSION)
						return DatabaseState.OK;
				} else {
					return DatabaseState.OLDVERSION;
				}
			} catch (Throwable th) {
				throw new RuntimeException(th);
			}
		}

		return DatabaseState.EMPTY;
	}

	public void createNewDatabase() {
		synchronized (connection) {
			try {
				List<String> sql = new ArrayList<>();
				sql.add("CREATE TABLE global   			(key VARCHAR(50),  val VARCHAR(255), PRIMARY KEY(key))");

				for (String s : sql)
					connection.prepareStatement(s).execute();

				connection.commit();

			} catch (Throwable th) {
				rollback();
				throw new RuntimeException("Could not create a new database", th);
			}
		}
	}

	public BaseModule loadModule(String string) throws ModuleError {
		BaseModule mod;
		try {
			mod = (BaseModule) Class.forName(string).newInstance();
			mod.init(this);
			modules.add(mod);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new ModuleError("Failed to load module " + string + " because " + e.getMessage(), e);
		}
		return mod;
	}

	private boolean tryConnectAs(String server, String nick) throws IOException, IrcException {
		intf.setName(nick);
		try {
			intf.connect(server);
		} catch (NickAlreadyInUseException e) {
			return false;
		}
		return true;
	}

	private void formatErrorForDisplay(Throwable th, SendResponseHandler response) {
		if (th instanceof IRCException)
			response.format().error(((IRCException) th).formatForReply()).send();
		else
			response.format().error("Internal error").send();
	}

	protected void onPrivateMessage(final String sender, String login, String hostname, String message) {
		SendResponseHandler response = new FormatterSendResponseHandler() {
			@Override
			public void send(String message) {
				intf.sendMessage(sender, message);
			}

			@Override
			public Type getType() {
				return Type.PRIVMSG;
			}

			@Override
			public String getChannelName() {
				return null;
			}
		};
		try {
			String msg = message.trim();
			handleUserMessage(nicks.get(sender), msg, response);
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, "onMessage", th);
			formatErrorForDisplay(th, response);
		}
	}

	protected void onMessage(final String channel, final String sender, String login, String hostname, String message) {
		SendResponseHandler responder = new FormatterSendResponseHandler() {
			@Override
			public void send(String message) {
				intf.sendMessage(channel, sender + ": " + message);
			}

			@Override
			public Type getType() {
				return Type.CHANNEL;
			}

			@Override
			public String getChannelName() {
				return channel;
			}
		};

		try {
			//
			// If it's in a game channel then we route messages to there first
			//
			Channel c = gameChannels.get(channel);
			if (c != null && c.getStatus() == GameChannelStatus.INUSE && c.getGame() != null) {
				Nick n = nicks.getOpt(sender);
				if (n != null && n.getUser() != null)
					c.getGame().onMessage(n.getUser(), message, responder);
			}

			//
			// Only respond if it's aimed at us
			//
			if (message.matches("^ *" + Pattern.quote(intf.getNick()) + "[,: ].*$")) {
				String msg = message.substring(intf.getNick().length() + 1).trim();
				handleUserMessage(nicks.get(sender), msg, responder);
			}
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "onMessage", e);
			formatErrorForDisplay(e, responder);
		}
	}

	protected void onJoin(String channel, String sender, String login, String hostname) {
		try {
			Nick n = null;

			if (sender.equals(intf.getNick())) // ignore us
				return;

			n = nicks.join(sender, channel);

			//
			// If this is a game channel then we trigger the onJoin event for that game
			//
			Channel c = gameChannels.get(channel);
			if (c != null && c.getGame() != null && n.getUser() != null)
				c.getGame().onUserJoin(n.getUser());

		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "onJoin", e);
		}
	}

	protected void onUserList(String channel, org.jibble.pircbot.User[] users) {
		for (org.jibble.pircbot.User u : users)
			nicks.join(u.getNick(), channel);
	}

	protected void onPart(String channel, String sender, String login, String hostname) {
		try {
			if (sender.equals(intf.getNick())) // ignore us
				return;
			Nick n = nicks.part(sender, channel);
			Channel c = gameChannels.get(channel);
			if (c != null && c.getGame() != null && n.getUser() != null)
				c.getGame().onUserPart(n.getUser());
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "onPart", e);
		}
	}

	protected void onNickChange(String oldNick, String login, String hostname, String newNick) {
		try {
			nicks.get(oldNick).setName(newNick);
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "onNickChange", e);
		}
	}

	protected void handleUserMessage(Nick nick, String message, SendResponseHandler response) throws IRCException {
		int pos = message.indexOf(' ');
		String command = message;
		String args = null;
		if (pos != -1) {
			command = message.substring(0, pos);
			args = message.substring(pos + 1);
		}

		CommandHandler handler = findHandlerFor(nick, command);

		if (handler instanceof CommandHandlerRaw)
			((CommandHandlerRaw) handler).handleRaw(command, nick, args, response);

		TokenList lst = Utils.tokenise(args);
		try {
			handler.handle(command, nick, lst, response);
		} catch (IRCException ex) {
			throw ex;
		} catch (Throwable th) {
			throw new RuntimeException("Failed", th);
		}

		LOGGER.fine(nick + " - " + command + "/" + args);
	}

	public void registerHandler(CommandHandler handler) {
		commandHandlers.add(handler);
	}

	protected void onDisconnect() {
		intf.terminate();
	}

	public Connection getConnection() {
		return connection;
	}

	public ResultSet runQuery(String sql, Object... args) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		for (int i = 0; i < args.length; ++i) {
			if (args[i] instanceof String)
				stmt.setString(i + 1, (String) args[i]);
			else if (args[i] instanceof Integer)
				stmt.setInt(i + 1, ((Integer) args[i]).intValue());
			else if (args[i] instanceof Float)
				stmt.setFloat(i + 1, ((Float) args[i]).floatValue());
			else
				throw new RuntimeException("Unsupported argument");

		}
		return stmt.executeQuery();
	}

	public boolean runStatement(String sql, Object... args) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		for (int i = 0; i < args.length; ++i) {
			if (args[i] instanceof String)
				stmt.setString(i + 1, (String) args[i]);
			else if (args[i] instanceof Integer)
				stmt.setInt(i + 1, ((Integer) args[i]).intValue());
			else if (args[i] instanceof Float)
				stmt.setFloat(i + 1, ((Float) args[i]).floatValue());
			else
				throw new RuntimeException("Unsupported argument");

		}
		return stmt.execute();
	}

	public void shutdown() {
		hsqlServer.shutdown();
		intf.quitServer("");
	}

	public boolean checkForTable(String name) throws SQLException {
		DatabaseMetaData meta = connection.getMetaData();
		ResultSet res = meta.getTables(null, null, null, new String[] { "TABLE" });
		boolean found = false;
		while (res.next())
			found = found | res.getString("TABLE_NAME").equalsIgnoreCase(name);
		return found;
	}

	public void commitdb() throws SQLException {
		getConnection().commit();
	}

	public void rollback() {
		try {
			connection.rollback();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Rollback failed", e);
		}
	}

	public Collection<CommandHandler> getHandlers(Nick nick) {
		ArrayList<CommandHandler> lst = new ArrayList<>();
		for (CommandHandler h : commandHandlers)
			if (h.getRequiredRole() == null || (nick.getUser() != null && nick.getUser().hasRole(h.getRequiredRole())))
				lst.add(h);
		return lst;
	}

	public abstract class FormatterSendResponseHandler implements SendResponseHandler {

		@Override
		public IrcFormatter format() {
			return new Formatter(this);
		}

	}

	public CommandHandler findHandlerFor(Nick nick, String command) throws UnknownInstruction {
		List<CommandHandler> lst = new ArrayList<>();
		for (CommandHandler h : commandHandlers)
			if (h.canHandle(command))
				lst.add(h);
		if (lst.size() == 0)
			throw new UnknownInstruction(command);
		if (lst.size() == 1)
			return lst.get(0);

		//
		// Otherwise look for the current scoped module
		//
		for (CommandHandler h : lst)
			if (h.getModule().equals(nick.getCurrentModuleScope()))
				return h;

		throw new UnknownInstruction(command + " is ambiguous - pick one: " + Utils.join(lst, ", "));
	}

	public BaseModule getRootModule() {
		return rootModule;
	}

	public SendResponseHandler getChannelSender(final String channelName) {
		return new FormatterSendResponseHandler() {
			@Override
			public void send(String message) {
				intf.sendMessage(channelName, message);
			}

			@Override
			public Type getType() {
				return Type.CHANNEL;
			}

			@Override
			public String getChannelName() {
				return channelName;
			}
		};
	}

	public Users getUsers() {
		return users;
	}

	public void onUserLogout(User user) {
		LOGGER.info("User " + user + " logged out");
	}

	public void onUserLogin(User user) {
		LOGGER.info("User " + user + " logged in");
	}

	public Game getGame(String name) {
		return games.find(name, true);
	}

	/**
	 * Add a new game and assign it to a free channel
	 * 
	 * @param game
	 */
	public void addGame(Game game) {
		Channel chnl = getFreeChannel();
		chnl.setStatus(GameChannelStatus.INUSE);
		game.setChannel(chnl);
		chnl.setGame(game);
		games.addGame(game);
	}

	/**
	 * Remove an existing game and free up its channel
	 * 
	 * @param game
	 */
	public void removeGame(Game game) {
		game.getChannel().setGame(null);
		game.getChannel().setStatus(GameChannelStatus.IDLE);
		games.removeGame(game);
	}

	protected Channel getFreeChannel() {
		for (Channel ch : gameChannels)
			if (ch.getStatus() == GameChannelStatus.IDLE)
				return ch;

		// TODO: encourage bot to join some more channels

		throw new RuntimeException("Cannot find a channel to join");
	}

	@SuppressWarnings("unchecked")
	public <T> T getGame(String name, Class<T> type) throws GameError {
		Game g = getGame(name);
		if (g == null)
			throw new GameError("Game " + name + " does not exist");
		if (!g.getClass().equals(type))
			throw new GameError("Game " + name + " is not a " + type.getSimpleName());
		return (T) g;
	}

	public String getNick() {
		return intf.getNick();
	}

	/**
	 * Joins a channel and makes it available for the game modules. If it's not
	 * available (bot can't get an @) then it will log a warning and not add it.
	 * Since this is an async operation the {@link Channel} is added here with a
	 * status of "pending" until the join handler picks it up again
	 * 
	 * @param name
	 */
	public void joinGameChannel(String name) {
		if (!name.startsWith("#"))
			name = "#" + name;
		Channel ch = new Channel(name);
		ch.setStatus(GameChannelStatus.PENDING_JOIN);
		gameChannels.add(ch);
		intf.joinChannel(name);
		LOGGER.info("Joined " + name);
	}

	public List<BaseModule> getModules() {
		return modules;
	}

	public BotOptions getOptions() {
		return options;
	}
	
	
	



}
