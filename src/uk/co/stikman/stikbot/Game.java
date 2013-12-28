package uk.co.stikman.stikbot;

import java.util.List;

import uk.co.stikman.stikbot.except.ModuleError;

public interface Game {
	String getName();

	GameState getState();

	boolean canJoin(User usr);
	
	Channel getChannel();
	void setChannel(Channel chnl);

	void addPlayer(User user) throws ModuleError;
	
	List<GamePlayer> getPlayers();

	/**
	 * Called when a user says something in the channel. It's up to you to check
	 * that they're one of the actual players in this game. Note that unauthed
	 * nicks won't fire this message at all.
	 * 
	 * @param user
	 * @param message
	 * @param response
	 * @throws GameError 
	 */
	void onMessage(User user, String message, SendResponseHandler response) throws GameError;
	
	/**
	 * Called when a {@link User} joins the game's channel. Only authenticated
	 * users will trigger this. Unauthed users won't trigger any methods of this
	 * <code>Game</code>
	 * 
	 * @param user
	 */
	void onUserJoin(User user);

	/**
	 * Called when a {@link User} leaves the game's channel. You should remove
	 * them from their player slot when this happens. It might be that they've
	 * forfeit the game here.
	 * 
	 * @param user
	 */
	void onUserPart(User user);

	

}
