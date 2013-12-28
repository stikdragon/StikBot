package uk.co.stikman.stikbot;

import java.io.IOException;
import java.util.logging.Logger;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

public class PircIrcInterface implements IrcInterface {

	public static class ConcretePircBot extends PircBot {

		private static final Logger	LOGGER	= Logger.getLogger(ConcretePircBot.class.getName());
		private IrcEventsHandler	handler;

		public ConcretePircBot(PircIrcInterface pircIrcInterface) {
		}

		@Override
		public void log(String line) {
			LOGGER.info(line);
		}

		public void setLoginName(String name) {
			super.setLogin(name);
		}

		public void setBotName(String nick) {
			super.setName(nick);
		}

		@Override
		protected void onDisconnect() {
			super.onDisconnect();
			if (handler != null)
				handler.onDisconnect();
		}

		@Override
		protected void onUserList(String channel, User[] users) {
			super.onUserList(channel, users);
			if (handler != null)
				handler.onUserList(channel, users);
		}

		@Override
		protected void onMessage(String channel, String sender, String login, String hostname, String message) {
			super.onMessage(channel, sender, login, hostname, message);
			if (handler != null)
				handler.onMessage(channel, sender, login, hostname, message);
		}

		@Override
		protected void onPrivateMessage(String sender, String login, String hostname, String message) {
			super.onPrivateMessage(sender, login, hostname, message);
			if (handler != null)
				handler.onPrivateMessage(sender, login, hostname, message);
		}

		@Override
		protected void onJoin(String channel, String sender, String login, String hostname) {
			super.onJoin(channel, sender, login, hostname);
			if (handler != null)
				handler.onJoin(channel, sender, login, hostname);
		}

		@Override
		protected void onPart(String channel, String sender, String login, String hostname) {
			super.onPart(channel, sender, login, hostname);
			if (handler != null)
				handler.onPart(channel, sender, login, hostname);
		}

		@Override
		protected void onNickChange(String oldNick, String login, String hostname, String newNick) {
			super.onNickChange(oldNick, login, hostname, newNick);
			if (handler != null)
				handler.onNickChange(oldNick, login, hostname, newNick);
		}

		public IrcEventsHandler getHandler() {
			return handler;
		}

		public void setHandler(IrcEventsHandler handler) {
			this.handler = handler;
		}

	}

	private ConcretePircBot		bot;
	private IrcEventsHandler	handler;

	public PircIrcInterface() {
		bot = new ConcretePircBot(this);
		bot.setMessageDelay(750);
	}

	@Override
	public void setLogin(String string) {
		bot.setLoginName(string);
	}

	@Override
	public void joinChannel(String channel) {
		bot.joinChannel(channel);
	}

	@Override
	public String getNick() {
		return bot.getNick();
	}

	@Override
	public void setName(String nick) {
		bot.setBotName(nick);
	}

	@Override
	public void connect(String server) throws NickAlreadyInUseException, IOException, IrcException {
		bot.connect(server);
	}

	@Override
	public void setIrcEventsHandler(IrcEventsHandler handler) {
		this.bot.setHandler(handler);
	}

	@Override
	public void sendMessage(String target, String message) {
		bot.sendMessage(target, message);
	}

	@Override
	public void terminate() {
		bot.dispose();
	}

	@Override
	public void quitServer(String reason) {
		bot.quitServer(reason);
	}

	@Override
	public void partChannel(String channel) {
		bot.partChannel(channel);
	}

}
