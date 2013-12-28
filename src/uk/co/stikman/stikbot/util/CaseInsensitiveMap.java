package uk.co.stikman.stikbot.util;

import java.util.HashMap;

public class CaseInsensitiveMap<T> extends HashMap<String, T> {

    /**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	@Override
    public T put(String key, T value) {
       return super.put(key.toLowerCase(), value);
    }

    // not @Override because that would require the key parameter to be of type Object
    public T get(String key) {
       return super.get(key.toLowerCase());
    }
}