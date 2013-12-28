package uk.co.stikman.dominion;


public abstract class Card {
	private String			name;
	private int				cost;
	private String			shortcut;

	private DominionGame	game;
	private boolean			alwaysInclude;

	public Card(DominionGame game, String name, String shortcut, int cost) {
		super();
		this.name = name;
		this.cost = cost;
		this.game = game;
		this.shortcut = shortcut;
	}

	protected void describeNameType(DominionOutput output) {
		output.bold(getName());
		output.soft(" (");
		output.cardtype(getCardType());
		output.soft(") ");
	}

	
	public Card setAlwaysInclude(boolean always) {
		alwaysInclude = always;
		return this;
	}

	public String getName() {
		return name;
	}

	public DominionGame getGame() {
		return game;
	}

	public String getShortcut() {
		return shortcut;
	}

	public int getCost() {
		return cost;
	}

	public abstract void describe(DominionOutput output);

	public abstract CardType getCardType();

	@Override
	public String toString() {
		return getName();
	}

	@SuppressWarnings("unchecked")
	public <T> T as(Class<T> cls) {
		return (T) this;
	}

	public boolean isAlwaysInclude() {
		return alwaysInclude;
	}

	/**
	 * Throws {@link DominionError} if they can't buy this at this point
	 * 
	 * @param currentTurn
	 */
	public void canBuy(Turn currentTurn) {
		if (getCost() > currentTurn.calculateCoin())
			throw new DominionError("You can't afford that");
	}
	
	
	
	

}
