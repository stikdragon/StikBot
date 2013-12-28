package uk.co.stikman.stikbot;

import java.util.ArrayList;
import java.util.List;

public class Channel {
	private final String	name;
	private List<Nick>		nicks	= new ArrayList<>();
	private GameChannelStatus	status;
	private Game	game;

	public String getName() {
		return name;
	}

	public Channel(String name) {
		super();
		this.name = name;
	}
	
	public List<Nick> getNicks() {
		return nicks;
	}

	public void setStatus(GameChannelStatus status) {
		this.status = status;
	}

	public GameChannelStatus getStatus() {
		return status;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Game getGame() {
		return game;
	}
	
	
	
	@Override
	public String toString() {
		return name;
	}
	
	

}
