package uk.co.stikman.stikbot;

import java.util.ArrayList;
import java.util.List;

public class Nick {
	private String			name;
	private Nicks			owner;
	private List<String>	channels	= new ArrayList<>();
	private String			userName;
	private BaseModule		currentModule;

	public Nick(Nicks gameNicks, String sender) {
		this.owner = gameNicks;
		this.name = sender;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		owner.changeNickTo(this, name);
	}

	void setNameInternal(String name) {
		this.name = name;
	}

	void setOwnerInternal(Nicks owner) {
		this.owner = owner;
	}

	public int getChannelCount() {
		return channels.size();
	}

	public String getChannel(int idx) {
		return channels.get(idx);
	}

	void addChannel(String ch) {
		if (channels.indexOf(ch) != -1)
			return;
		channels.add(ch);
	}

	void removeChannel(String ch) {
		channels.remove(ch);
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	@Override
	public String toString() {
		return name;
	}

	public BaseModule getCurrentModuleScope() {
		return currentModule;
	}

	public void setCurrentModuleScope(BaseModule m) {
		this.currentModule = m;
	}

	public User getUser() {
		return owner.getOwner().getUsers().get(this);
	}

}
