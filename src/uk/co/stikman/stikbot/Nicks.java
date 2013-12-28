package uk.co.stikman.stikbot;
import java.util.HashMap;
import java.util.Map;

import uk.co.stikman.stikbot.except.UnknownUserError;


public class Nicks {
	private Map<String, Nick> nicks = new HashMap<>();
	private StikBot	owner;
	
	
	public Nicks(StikBot owner) {
		this.owner = owner;
	}
	
	public Nick get(String name) throws UnknownUserError {
		Nick n = nicks.get(name);
		if (n == null) 
			throw new UnknownUserError(name);
		return n;
	}
	
	public Nick getOpt(String name) {
		return nicks.get(name);
	}

	boolean canChangeNickTo(String name) {
		return !nicks.containsKey(name);			
	}

	public void changeNickTo(Nick gameNick, String name) {
		if (canChangeNickTo(name))
			throw new RuntimeException("Nick " + name + " alread exists");
		gameNick.setNameInternal(name);
	}


	public Nick join(String sender, String channel) {
		if (!nicks.containsKey(sender)) {
			Nick n = new Nick(this, sender);
			n.setCurrentModuleScope(owner.getRootModule());
			nicks.put(sender, n);
		}
		Nick n = nicks.get(sender);
		n.addChannel(channel);
		return n;
	}

	public Nick part(String sender, String channel) throws UnknownUserError {
		Nick u = get(sender);
		u.removeChannel(channel);
		if (u.getChannelCount() == 0)
			nicks.remove(u.getName());
		return u;
	}

	public StikBot getOwner() {
		return owner;
	}
	
	
	
}
