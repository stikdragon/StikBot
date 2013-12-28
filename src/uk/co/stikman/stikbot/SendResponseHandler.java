package uk.co.stikman.stikbot;

public interface SendResponseHandler {
	public enum Type {
		CHANNEL, PRIVMSG
	}
	
	void send(String message);
	IrcFormatter format();
	Type getType();
	String getChannelName();
}
