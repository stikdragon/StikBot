package uk.co.stikman.stikbot.except;


/**
 * Exception that  
 * 
 * @author frenchd
 *
 */
public class IRCException extends Exception {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public IRCException(String message) {
		super(message);
	}

	public IRCException(String message, Throwable cause) {
		super(message, cause);
	}

	public IRCException(Throwable e) {
		super(e);
	}

	public String formatForReply() {
		return getMessage();
	}

}
