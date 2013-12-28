package uk.co.stikman.dominion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.stikman.dominion.cardsets.DominionCardSet;
import uk.co.stikman.dominion.cardsets.IntrigueCardSet;
import uk.co.stikman.dominion.cardsets.ProsperityCardSet;

public class GameConfig implements Iterable<CardSet> {

	transient private List<CardSet>	availableCardSets	= new ArrayList<>();
	transient private Set<CardSet>	selected			= new HashSet<CardSet>();
	private float					cardscale			= 1.0f;
	private int						numtypes			= 10;
	private CardTypes	selectedCards = null;

	public GameConfig() {
		availableCardSets.add(new DominionCardSet());
		availableCardSets.add(new IntrigueCardSet());
		availableCardSets.add(new ProsperityCardSet());

		selected.add(findSet("Dominion"));
	}

	public CardSet findSet(String name) {
		for (CardSet cs : availableCardSets)
			if (cs.getName().equalsIgnoreCase(name))
				return cs;
		throw new DominionError("Card Set " + name + " does not exist");
	}

	public void addSet(String name) {
		selected.add(findSet(name));
		invalidateBuiltSet();		
	}

	private void invalidateBuiltSet() {
		selectedCards  = null;
	}

	public void removeSet(String name) {
		CardSet s = findSet(name);
		selected.remove(s);
		invalidateBuiltSet();		
	}

	@Override
	public Iterator<CardSet> iterator() {
		return selected.iterator();
	}

	public float getCardScale() {
		return cardscale;
	}

	public void setCardScale(float scale) {
		if (scale < 0.1f)
			throw new DominionError("Card Scale must be greater than 0.1");
		if (scale > 1000.0f)
			throw new DominionError("Card Scale must be less than 1000.0");
		this.cardscale = scale;
	}

	public void addAll() {
		selected.addAll(availableCardSets);
	}

	public Iterable<CardSet> getAvailableSets() {
		return availableCardSets;
	}

	public int getNumtypes() {
		return numtypes;
	}

	public void setNumtypes(int numtypes) {
		if (numtypes < 1)
			throw new DominionError("Number of cards must be greater than 0");
		this.numtypes = numtypes;
	}

	public String save() {
		try {
			JSONObject obj = new JSONObject();
			JSONArray arr = new JSONArray();
			for (CardSet cs : selected)
				arr.put(cs.getName());
			obj.put("sets", arr);
			obj.put("cardscale", cardscale);
			obj.put("numcards", numtypes);
			return obj.toString();

		} catch (JSONException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static GameConfig load(String s) {
		try {
			GameConfig config = new GameConfig();
			config.selected.clear();
			JSONObject obj = new JSONObject(s);
			JSONArray sets = obj.getJSONArray("sets");
			for (int i = 0; i < sets.length(); ++i) 
				config.addSet(sets.getString(i));
			config.setCardScale((float) obj.getDouble("cardscale"));
			config.setNumtypes(obj.getInt("numcards"));
			return config;
		} catch (JSONException ex) {
			throw new RuntimeException(ex);
		}
	}

	public CardTypes getSelectedCards() {
		if (selectedCards == null) 
			selectedCards = CardTypes.createSet(null, this);
		return selectedCards;
	}




}
