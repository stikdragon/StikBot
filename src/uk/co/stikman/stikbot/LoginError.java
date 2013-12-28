package uk.co.stikman.stikbot;

import uk.co.stikman.stikbot.except.IRCException;

public class LoginError extends IRCException {

	public LoginError(String message, Throwable cause) {
		super(message, cause);
	}

	public LoginError(String message) {
		super(message);
	}

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	@Override
	public String formatForReply() {
		return "Login failed: " + getMessage();
	}

}
