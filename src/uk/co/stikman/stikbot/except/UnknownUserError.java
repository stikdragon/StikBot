package uk.co.stikman.stikbot.except;

public class UnknownUserError extends IRCException {

	public UnknownUserError(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	@Override
	public String formatForReply() {
		return "Unknown user: " + getMessage() + ".  Are you in one of my channels?";
	}

}
