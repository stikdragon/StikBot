package uk.co.stikman.dominion;

public class CardInstance {
	private Card type;

	public CardInstance(Card type) {
		super();
		this.type = type;
	}

	public Card getCard() {
		return type;
	}
	
	@Override
	public String toString() {
		return type.toString();
	}

}
