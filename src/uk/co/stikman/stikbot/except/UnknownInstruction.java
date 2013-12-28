package uk.co.stikman.stikbot.except;

public class UnknownInstruction extends IRCException {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public UnknownInstruction(String command) {
		super(command);
	}

	
	@Override
	public String formatForReply() {
		return "Unknown command: " + getMessage();
	}

}
