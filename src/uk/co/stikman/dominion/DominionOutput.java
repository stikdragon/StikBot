package uk.co.stikman.dominion;

public interface DominionOutput {
	DominionOutput out(String s);
	DominionOutput bold(String s);
	DominionOutput soft(String s);
	void send();
	DominionOutput card(Card card, boolean includeCost);
	void cardtype(CardType cardType);
	
	
}
