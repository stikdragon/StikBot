package uk.co.stikman.stikbot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Channels implements Iterable<Channel> {
	private List<Channel>	channels	= new ArrayList<>();

	public Channels(StikBot stikBot) {
	}

	public Channel get(String name) {
		for (Channel c : channels)
			if (c.getName().equals(name))
				return c;
		return null;
	}

	public void add(Channel ch) {
		if (get(ch.getName()) != null)
			throw new RuntimeException("Already added");
		this.channels.add(ch);
	}

	public void remove(Channel ch) {
		if (ch == null)
			return;
		if (get(ch.getName()) == null)
			throw new RuntimeException("Not in channel");
		this.channels.remove(ch);
	}

	public void remove(String name) {
		remove(get(name));
	}

	@Override
	public Iterator<Channel> iterator() {
		return channels.iterator();
	}

	public boolean contains(String name) {
		for (Channel c : channels)
			if (c.getName().equalsIgnoreCase(name))
				return true;
		return false;
	}

}
