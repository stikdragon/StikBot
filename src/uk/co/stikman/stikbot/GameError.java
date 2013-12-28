package uk.co.stikman.stikbot;


import uk.co.stikman.stikbot.except.IRCException;

public class GameError extends IRCException {

	public GameError(String message) {
		super(message);
	}

	public GameError(String string, Throwable e) {
		super(string, e);
	}

	public GameError(Throwable de) {
		super(de);
	}

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	@Override
	public String formatForReply() {
		return getMessage();
	}

}
