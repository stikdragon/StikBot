package uk.co.stikman.stikbot;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.co.stikman.stikbot.except.IRCException;
import uk.co.stikman.stikbot.except.ModuleError;
import uk.co.stikman.stikbot.util.RandomString;
import uk.co.stikman.stikbot.util.Utils;
import uk.co.stikman.tokeniser.TokenException;
import uk.co.stikman.tokeniser.TokenList;

/**
 * Provides standard functionality. Is treated specially by the bot in that it
 * doesn't need any qualification
 * 
 * @author Stik
 * 
 */
public class CoreModule extends BaseModule {
	private static final Logger	LOGGER	= Logger.getLogger(CoreModule.class.getName());
	public static final String	VERSION	= "1.3";
	private StikBot				bot;

	@Override
	public void shutdown() throws ModuleError {

	}

	@Override
	public void init(final StikBot gameBot) throws ModuleError {
		this.bot = gameBot;

		try {
			initDB();
		} catch (SQLException e1) {
			throw new ModuleError("Failed to init database", e1);
		}

		gameBot.registerHandler(new StandardCommandHandler(this, "quit", "ADMIN", "Quit") {
			@Override
			public void handle(String command, Nick nick, TokenList args, SendResponseHandler response) throws PermissionError {
				checkRole(nick, "ADMIN");
				response.send("Bye");
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
				gameBot.shutdown();
			}
		});

		
		gameBot.registerHandler(new StandardCommandHandler(this, "version", "Show version") {
			@Override
			public void handle(String command, Nick nick, TokenList args, SendResponseHandler response) throws IRCException {
				response.format().bold("Version: ").box("StikBot v" + StikBot.VERSION).send();
				List<String> lst = new ArrayList<>();
				for (BaseModule m : bot.getModules()) 
					lst.add(m.getName() + " v" + m.getVersion());
				response.format().bold("Modules: ").boxlist(lst).send();
			}
		});
		
		gameBot.registerHandler(new StandardCommandHandler(this, "listusers", "ADMIN", "List registered users") {
			@Override
			public void handle(String command, Nick nick, TokenList args, SendResponseHandler response) throws IRCException {
				checkRole(nick, "ADMIN");
				showUsers(response);
			}
		});
		
		gameBot.registerHandler(new StandardCommandHandler(this, "deleteuser", "ADMIN", "Delete a user: deleteuser <username>") {
			@Override
			public void handle(String command, Nick nick, TokenList args, SendResponseHandler response) throws IRCException {
				checkRole(nick, "ADMIN");
				try {
					deleteUser(response, args.nextString());
				} catch (TokenException e) {
					throw new IRCException("Invalid command, expected a name");
				}
			}
		});

		
		gameBot.registerHandler(new StandardCommandHandler(this, "grant", "ADMIN", "Grant a role to a user.  grant <user> <role>") {
			@Override
			public void handle(String command, Nick nick, TokenList args, SendResponseHandler response) throws IRCException {
				checkRole(nick, "ADMIN");
				try {
					String user = args.nextString();
					String role = args.nextString();
					
					grant(nick, user, role, response);
					
				} catch (Throwable th) {
					throw new RuntimeException(th);
				}
				
			}
		});

		gameBot.registerHandler(new StandardCommandHandler(this, "auth", "Identify yourself using your password. auth [<username>] <password>") {
			@Override
			public void handle(String command, Nick nick, TokenList args, SendResponseHandler response) throws IRCException {
				try {
					String s1 = args.nextString();
					String s2 = null;
					if (args.hasNext())
						s2 = args.nextString();

					if (s2 == null) {
						s2 = s1;
						s1 = nick.getName();
					}

					login(s1, s2, nick, response);

				} catch (IRCException ex) {
					throw ex;
				} catch (Throwable th) {
					throw new RuntimeException(th);
				}
			}
		});
		

	
		gameBot.registerHandler(new StandardCommandHandler(this, "register", "Signup as a new user: register [<name>] <password>.  Suggest you do it in a PM rather than a channel") {
			@Override
			public void handle(String command, Nick nick, TokenList args, SendResponseHandler response) throws IRCException {
				try {
					String s1 = args.nextString();
					String s2 = null;
					if (args.hasNext())
						s2 = args.nextString();

					if (s2 == null) {
						s2 = s1;
						s1 = nick.getName();
					}

					register(s1, s2, nick, response);

				} catch (IRCException ex) {
					throw ex;
				} catch (TokenException tokerr) {
					throw new ModuleError("Invalid command format");
				} catch (Throwable th) {
					throw new ModuleError(th);
				}
			}
		});

		gameBot.registerHandler(new StandardCommandHandler(this, "help", "List all commands") {
			@Override
			public void handle(String command, Nick nick, TokenList args, SendResponseHandler response) throws IRCException {
				try {
					if (args.hasNext())
						listCommands(nick, response, args.next().getVal());
					else
						listCommands(nick, response, null);
				} catch (Exception e) {
					throw new GameError("Unknown command");
				}
			}
		});

	}


	/**
	 * Special init() routine for this module because it gets loaded before the
	 * bot is connected
	 */
	public void botConnected() {
		for (int i = 0; i < bot.getOptions().getChannelCount(); ++i)
			bot.joinGameChannel(bot.getNick() + "-" + (i + 1));
	}

	private void initDB() throws SQLException {
		List<String> sql = new ArrayList<>();

		if (!bot.checkForTable("users")) {
			String salt = new RandomString(16).nextString();
			sql.add("CREATE TABLE users (username VARCHAR(50), salt VARCHAR(50), hash VARCHAR(50), PRIMARY KEY(username))");
			sql.add("INSERT INTO users	(username, salt, hash) VALUES ('root', '" + salt + "', '" + Utils.hashPassword("root", salt) + "')");
		}

		if (!bot.checkForTable("roles")) {
			sql.add("CREATE TABLE roles (username VARCHAR(50), role VARCHAR(50))");
			sql.add("INSERT INTO roles (username, role) VALUES ('root', 'ADMIN')");
		}

		for (String s : sql)
			bot.getConnection().prepareStatement(s).execute();
		bot.commitdb();

	}

	protected void register(String name, String pass, Nick nick, SendResponseHandler response) throws IRCException {
		try {
			name = name.toLowerCase();
			synchronized (bot.getConnection()) {
				ResultSet rs = bot.runQuery("SELECT username FROM users WHERE username = ?", name);
				if (rs.next())
					throw new IRCException("Username already registered: " + name);

				String salt = new RandomString(16).nextString();
				bot.runStatement("INSERT INTO users (username, salt, hash) VALUES (?, ?, ?)", name, salt, Utils.hashPassword(pass, salt));

				bot.commitdb();
			}
			login(name, pass, nick, response);
			response.format().out("Registered OK.  You are logged in.  In future, you can use").bold("auth " + name + " " + pass).out(" to log in.").send();
			
		} catch (SQLException ex) {
			bot.rollback();
			LOGGER.log(Level.SEVERE, "register failed", ex);
			throw new LoginError("Failed to register");
		}
	}
	
	

	protected void grant(Nick nick, String user, String role, SendResponseHandler response) throws IRCException {
		try {
			user = user.toLowerCase();
			role = role.toUpperCase();
			synchronized (bot.getConnection()) {
				ResultSet rs = bot.runQuery("SELECT username FROM users WHERE username = ?", user);
				if (!rs.next())
					throw new IRCException("Username doesn't exist: " + user);

				bot.runStatement("INSERT INTO roles (username, role) VALUES (?, ?)", user, role);
				User u = bot.getUsers().get(user);
				if (u != null && !u.getRoles().contains(role))
					u.getRoles().add(role);
				bot.commitdb();
			}
			response.send("Ok");
			
		} catch (SQLException ex) {
			bot.rollback();
			LOGGER.log(Level.SEVERE, "register failed", ex);
			throw new LoginError("Failed to register");
		}
	}

	

	protected void deleteUser(SendResponseHandler response, String name) throws IRCException {
		try {
			name = name.toLowerCase();
			synchronized (bot.getConnection()) {
				ResultSet rs = bot.runQuery("SELECT username FROM users WHERE username = ?", name);
				if (!rs.next())
					throw new IRCException("User does not exist, cannot delete: " + name);
				
				bot.runStatement("DELETE FROM users WHERE username = ?", name);
				bot.getConnection().commit();
				response.send("Deleted user " + name);
				
				bot.getUsers().remove(name);
				
			}

		} catch (SQLException ex) {
			bot.rollback();
			LOGGER.log(Level.SEVERE, "register failed", ex);
			throw new LoginError("Failed to register");
		}
	}

	protected void showUsers(SendResponseHandler response) throws IRCException {
		try {
			synchronized (bot.getConnection()) {
				ResultSet rs = bot.runQuery("SELECT username FROM users");
				List<String> lst = new ArrayList<>();
				while (rs.next())
					lst.add(rs.getString(1));
				Collections.sort(lst, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return o1.compareToIgnoreCase(o2);
					}
				});
				int cnt = lst.size();
				int idx = 0;
				while (cnt > 0) {
					int n = cnt;
					if (n > 10)
						n = 10;
					List<String> slice = lst.subList(idx, idx + n);
					response.format().boxlist(slice).send();
					cnt -= n;
					idx += n;
				}
				
			}
		} catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "showUsers error", ex);
			throw new IRCException("Database error");
		}
	}

	protected void login(String name, String pass, Nick nick, SendResponseHandler response) throws LoginError {
		name = name.toLowerCase();
		List<String> roles = new ArrayList<>();
		try {
			ResultSet rs = bot.runQuery("SELECT username, salt, hash FROM users WHERE username = ?", name);
			if (!rs.next())
				throw new LoginError("Unknown user: " + name);
			if (!Utils.hashPassword(pass, rs.getString(2)).equals(rs.getString(3)))
				throw new LoginError("Incorrect password");

			rs = bot.runQuery("SELECT username, role FROM roles WHERE username = ?", name);
			while (rs.next())
				roles.add(rs.getString(2).toUpperCase());

		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "login failed", e);
			throw new LoginError("Database error", e);
		}

		User user = bot.getUsers().get(name);
		if (user != null) {
			//
			// Already logged in under a different nick, so kick that one off
			//
			bot.onUserLogout(user);
			user.getNick().setUserName(null);
		} else {
			user = new User(name);
			bot.getUsers().put(user);
		}

		user.setNick(nick);
		nick.setUserName(name);
		user.setRoles(roles);
		bot.onUserLogin(user);
		response.send("Logged in okay.  Welcome, " + user.getName());
	}


	protected void listCommands(Nick nick, SendResponseHandler response, String specific) {
		if (specific == null) {
			response.format().out("Use ").bold("help ").c4("<").out("command").c4(">").out(" for help on a specific command").send();
			response.format().boxlist(bot.getHandlers(nick)).send();
		} else {
			boolean b = false;
			for (CommandHandler h : bot.getHandlers(nick)) {
				if (h.canHandle(specific)) {
					b = true;
					response.format().box(h.toString()).quiet(" - ").out(h.getDesc()).send();
				}
			}
			if (!b)
				response.format().c5("Unknown command: ").bold(specific).send();
		}
	}

	@Override
	public String getName() {
		return "core";
	}

	@Override
	public boolean isRoot() {
		return true;
	}

	@Override
	public String getVersion() {
		return VERSION;
	}

}
