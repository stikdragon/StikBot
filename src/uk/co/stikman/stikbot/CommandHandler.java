package uk.co.stikman.stikbot;

import uk.co.stikman.stikbot.except.IRCException;
import uk.co.stikman.tokeniser.TokenList;

public interface CommandHandler {
	void handle(String command, Nick nick, TokenList args, SendResponseHandler response) throws IRCException;
	String getDesc();
	String getName();
	String getRequiredRole(); 
	boolean canHandle(String command);
	BaseModule getModule();
}
