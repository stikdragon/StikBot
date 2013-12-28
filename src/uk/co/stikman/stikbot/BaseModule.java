package uk.co.stikman.stikbot;

import uk.co.stikman.stikbot.except.ModuleError;

public abstract class BaseModule {
	public abstract void init(StikBot gameBot) throws ModuleError;

	public abstract void shutdown() throws ModuleError;
	
	public abstract String getVersion();

	protected User checkRole(Nick nick, String role) throws PermissionError {
		if (nick.getUser() == null)
			throw new PermissionError("Be Logged In");
		nick.getUser().checkRole(role);
		return nick.getUser();
	}

	/**
	 * You can override this to detect when a nick has dissappeared from view
	 * 
	 * @param nick
	 */
	public void nickLeft(Nick nick) {

	}

	public abstract String getName();

	/**
	 * Only {@link CoreModule} must return <code>true</code> here
	 * 
	 * @return
	 */
	public boolean isRoot() {
		return false;
	}

}
