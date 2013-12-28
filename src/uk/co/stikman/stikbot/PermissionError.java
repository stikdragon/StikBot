package uk.co.stikman.stikbot;

import uk.co.stikman.stikbot.except.IRCException;

public class PermissionError extends IRCException {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public PermissionError(String message, Throwable cause) {
		super(message, cause);
	}

	public PermissionError(String string) {
		super(string);
	}

	@Override
	public String formatForReply() {
		return "You do not have permission to do this.  Role required: " + getMessage();
	}

}
