package uk.co.stikman.dominion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import uk.co.stikman.dominion.cards.MoneyCard;
import uk.co.stikman.dominion.cards.VictoryCard;

public class CardTypes implements Iterable<Card> {

	private List<Card>	types	= new ArrayList<Card>();
	private static final Logger LOGGER = Logger.getLogger(CardTypes.class.getName());


	public static CardTypes createSet(DominionGame game, GameConfig config) {
		CardTypes res = new CardTypes();
		
		addBaseCards(game, res);
		for (CardSet cs : config) 
			cs.addCards(game, res);
		
		//
		// Check for conflicting shortcuts
		//
		Map<String, Card> inuse = new HashMap<>();
		for (Card c : res) {
			if (inuse.containsKey(c.getShortcut().toLowerCase()))
				LOGGER.warning("Card " + c.getName() + " has a conflicting shortcut with " + inuse.get(c.getShortcut().toLowerCase()).getName());
			else
				inuse.put(c.getShortcut().toLowerCase(), c);
		}
		
		return res;
	}
	

	
	//@formatter:off
	private static void addBaseCards(DominionGame game, CardTypes res) {
		//                                             cost value
		res.types.add(new MoneyCard(game, "Gold",   "G", 6, 3, true));
		res.types.add(new MoneyCard(game, "Silver", "S", 3, 2, true));
		res.types.add(new MoneyCard(game, "Copper", "C", 0, 1, true));

		//                                                 cost value alwaysinclude
		res.types.add(new VictoryCard(game, "Province", "P", 8, 6, true));
		res.types.add(new VictoryCard(game, "Duchy",    "D", 5, 3, true));
		res.types.add(new VictoryCard(game, "Estate",   "E", 3, 1, true));
		
		res.types.add(new CurseCard(game, "Curse", "", 0, -1, true));
	}
	//@formatter:on
	
	
	public List<Card> getCards() {
		return types;
	}

	/**
	 * Return a card class matching <code>name</code>. Throws an exception if it
	 * doesn't match anything
	 * 
	 * @param name
	 * @return
	 */
	public Card findCard(String name) {
		if (name == null)
			throw new NullPointerException("Invalid name");
		if (name.length() == 0)
			throw new IllegalArgumentException("name cannot be blank");
		name = name.toUpperCase();
		for (Card cc : types)
			if (cc.getName().equalsIgnoreCase(name))
				return cc;
		for (Card cc : types)
			if (cc.getShortcut().equalsIgnoreCase(name))
				return cc;
		throw new DominionError("Unknown card type");
	}

	
	
	
	public void generateCards(CardList tableCards, GameConfig config) {

		List<Card> actions = new ArrayList<>();
		List<Card> selected = new ArrayList<>();
		for (Card cc : getCards()) {
			if (cc.isAlwaysInclude())
				selected.add(cc);
			else
				actions.add(cc);
		}

		//
		// Pick 10 random ones from the actions set
		//
		for (int i = 0; i < config.getNumtypes(); ++i) {
			if (actions.size() == 0)
				break;
			int j = (int) (Math.random() * actions.size());
			selected.add(actions.remove(j));
		}

		for (Card cc : selected) {
			int cnt = 10;

			if (cc.getName().equals("Gold"))
				cnt = 25;
			if (cc.getName().equals("Silver"))
				cnt = 35;
			if (cc.getName().equals("Copper"))
				cnt = 60;
			if (cc.getName().equals("Estate"))
				cnt = 40;
			if (cc.getName().equals("Duchy"))
				cnt = 20;
			if (cc.getName().equals("Province"))
				cnt = 15;
			if (cc.getName().equals("Curse"))
				cnt = 30;

			cnt = (int) (cnt * config.getCardScale());
			
			for (int i = 0; i < cnt; ++i)
				tableCards.add(new CardInstance(cc));
		}
	}

	public void addCardType(Card cardtype) {
		types.add(cardtype);
	}



	@Override
	public Iterator<Card> iterator() {
		return types.iterator();
	}

}
