package uk.co.stikman.dominion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * List of {@link CardInstance}. Does not allow duplicates (silently ignores
 * them)
 * 
 * @author Stik
 * 
 */
public class CardList implements Collection<CardInstance>, Iterable<CardInstance> {

	private List<CardInstance>	list	= new ArrayList<CardInstance>();
	private Set<CardInstance>	listSet	= new HashSet<CardInstance>();

	public CardList(CardList hand) {
		for (CardInstance ci : hand)
			add(ci);
	}

	public CardList() {
	}

	@Override
	public Iterator<CardInstance> iterator() {
		return list.iterator();
	}

	public CardInstance get(int idx) {
		return list.get(idx);
	}

	public CardInstance remove(int idx) {
		CardInstance ci = list.remove(idx);
		listSet.remove(ci);
		return ci;
	}

	public void add(int position, CardInstance c) {
		if (!listSet.contains(c)) {
			listSet.add(c);
			list.add(position, c);
		}
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return listSet.contains(o);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		return (T[]) list.toArray();
	}

	@Override
	public boolean add(CardInstance card) {
		if (listSet.contains(card))
			return false;
		listSet.add(card);
		return list.add(card);
	}

	@Override
	public boolean remove(Object o) {
		if (listSet.remove(o))
			return list.remove(o);
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return listSet.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends CardInstance> c) {
		boolean b = false;
		for (CardInstance ci : c)
			b |= add(ci);
		return b;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean b = false;
		for (Object ci : c)
			b |= remove(ci);
		return b;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		list.clear();
		listSet.clear();
	}

	public CardInstance findByType(String name, boolean allownull) {
		for (CardInstance c : list)
			if (c.getCard().getName().equalsIgnoreCase(name))
				return c;
		if (allownull)
			return null;
		throw new DominionError("Cannot find card of type " + name);
	}

	/**
	 * Returns <code>null</code> if not found
	 * 
	 * @param type
	 * @param allownull
	 * @return
	 */
	public CardInstance findByType(Card type, boolean allownull) {
		for (CardInstance c : list)
			if (c.getCard().equals(type))
				return c;
		if (allownull)
			return null;
		throw new DominionError("Cannot find card of type " + type.getName());
	}

	/**
	 * Returns <code>null</code> if not found. Will not return any instances
	 * present in <code>exclude</code>
	 * 
	 * @param findCard
	 * @param allownull
	 * @param exclude
	 * @return
	 */
	public CardInstance findByTypeExclude(Card type, boolean allownull, CardList exclude) {
		for (CardInstance c : list)
			if (c.getCard().equals(type) && !exclude.contains(c))
				return c;
		if (allownull)
			return null;
		throw new DominionError("Cannot find card of type " + type.getName());
	}

	/**
	 * Returns <code>null</code> if not found. Will not return any instances
	 * present in <code>exclude</code>
	 * 
	 * @param findCard
	 * @param allownull
	 * @param exclude
	 * @return
	 */
	public CardInstance findByTypeExclude(String type, boolean allownull, CardList exclude) {
		for (CardInstance c : list)
			if (c.getCard().getName().equalsIgnoreCase(type) && !exclude.contains(c))
				return c;
		if (allownull)
			return null;
		throw new DominionError("Cannot find card of type " + type);
	}

	/**
	 * Return a list of {@link Card}s that feature in this list of
	 * {@link CardInstance}
	 * 
	 * @return
	 */
	public List<Card> getCards() {
		Set<Card> tmp = new HashSet<>();
		for (CardInstance ci : this)
			tmp.add(ci.getCard());
		return new ArrayList<Card>(tmp);
	}

}
