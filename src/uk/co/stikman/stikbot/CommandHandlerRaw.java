package uk.co.stikman.stikbot;

import uk.co.stikman.stikbot.except.IRCException;


public interface CommandHandlerRaw extends CommandHandler {
	void handleRaw(String command, Nick gameNick, String args, SendResponseHandler response) throws IRCException;
}
