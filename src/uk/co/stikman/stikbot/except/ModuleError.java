package uk.co.stikman.stikbot.except;


public class ModuleError extends IRCException {

	public ModuleError(String message) {
		super(message);
	}

	public ModuleError(String string, Throwable cause) {
		super(string, cause);
	}


	public ModuleError(Throwable e) {
		super(e);
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
