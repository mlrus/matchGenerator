// This is unpublished source code. Michah Lerner 2006, 2007, 2008

package util.coll;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cache support overriding the standard super of the BoundedLinkedHashMap
 * 
 * @author Michah.Lerner
 * 
 * @param <K>
 *            Type of key
 * @param <V>
 *            Type of value
 */
public class BoundedLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = 8719353989730527264L;
	private static final int MAX_ENTRIES = 2048;
	private static final int SIZE = 16384;
	private final int maxEntries;

	public BoundedLinkedHashMap(final int size, final float factor, final boolean b) {
		this(size, factor, b, MAX_ENTRIES);
	}

	public BoundedLinkedHashMap(final int size, final float factor, final boolean b, final int maxEntries) {
		super(size, factor, b);
		this.maxEntries = maxEntries;
	}

	public BoundedLinkedHashMap() {
		this(SIZE, 0.75f, true);
	}

	@Override
	protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
		return size() > maxEntries;
	}
}
