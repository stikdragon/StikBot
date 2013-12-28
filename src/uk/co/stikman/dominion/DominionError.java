package uk.co.stikman.dominion;

public class DominionError extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DominionError() {
		super();
	}

	public DominionError(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DominionError(String arg0) {
		super(arg0);
	}

	public DominionError(Throwable arg0) {
		super(arg0);
	}

	
	
}
