package uk.co.stikman.dominion;


public interface CardSet {

	String getName();

	String getDesc();
	
	void addCards(DominionGame game, CardTypes res);
}