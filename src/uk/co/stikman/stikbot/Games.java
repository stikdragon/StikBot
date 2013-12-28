package uk.co.stikman.stikbot;

import java.util.HashMap;
import java.util.Map;

public class Games {
	private Map<String, Game>	games	= new HashMap<>();

	public Games(StikBot stikBot) {
	}

	public void addGame(Game g) {
		games.put(g.getName(), g);
	}

	public Game find(String name, boolean allownull) {
		Game g = games.get(name);
		if (g == null && !allownull)
			throw new RuntimeException("Game " + name + " not found");
		return g;
	}

	public void removeGame(Game game) {
		games.remove(game.getName());
	}

}
