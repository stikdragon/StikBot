package uk.co.stikman.stikbot;



public abstract class AbstractGame implements Game {
	private String	name;
	private Channel	channel;

	public AbstractGame(String name) {
		super();
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canJoin(User user) {
		return true;
	}

	@Override
	public Channel getChannel() {
		return channel;
	}

	@Override
	public void setChannel(Channel chnl) {
		this.channel = chnl;
	}
	
	

}
