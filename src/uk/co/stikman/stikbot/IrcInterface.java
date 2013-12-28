package uk.co.stikman.stikbot;

import java.io.IOException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.User;

public interface IrcInterface {

	public interface IrcEventsHandler {
		void onPrivateMessage(final String sender, String login, String hostname, String message);
		void onMessage(String channel, String sender, String login, String hostname, String message);
		void onJoin(String channel, String sender, String login, String hostname);
		void onUserList(String channel, User[] users);
		void onPart(String channel, String sender, String login, String hostname);
		void onNickChange(String oldNick, String login, String hostname, String newNick);
		void onDisconnect();

	}

	void setLogin(String string);

	void joinChannel(String channel);
	void partChannel(String channel);
	
	String getNick();

	void setName(String nick);

	void connect(String server) throws NickAlreadyInUseException, IOException, IrcException;

	void setIrcEventsHandler(IrcEventsHandler handler);

	void sendMessage(String target, String message);

	void terminate();

	void quitServer(String reason);



}
