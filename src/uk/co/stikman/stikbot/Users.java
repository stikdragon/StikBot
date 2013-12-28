package uk.co.stikman.stikbot;

import java.util.Map;

import uk.co.stikman.stikbot.util.CaseInsensitiveMap;

public class Users {

	private Map<String, User>	users	= new CaseInsensitiveMap<>();
	private StikBot				bot;

	public Users(StikBot bot) {
		this.bot = bot;
	}

	public User get(String name) {
		return users.get(name);
	}

	public User get(Nick nick) {
		return get(nick.getUserName());
	}

	public void put(User user) {
		if (users.containsKey(user.getName()))
			throw new RuntimeException("User already exists");
		users.put(user.getName(), user);
	}

	public void remove(String name) {
		users.remove(name);
	}
	
	public StikBot getBot() {
		return bot;
	}
}
