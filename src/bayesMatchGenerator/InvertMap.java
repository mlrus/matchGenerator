package bayesMatchGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/***********************************************************************************************************************
 * The class InvertMap generates an inverted map for the input collection, based on an {@link Accessor} that is passed
 * to the constructor.
 * 
 * @author mlrus
 * 
 * @param <S>
 *            The type of the input collection's members
 * @param <T>
 *            The type of the element that will index the map
 */
public final class InvertMap<S, T> {
	private final Map<T, S> invMap;

	InvertMap(final Collection<S> coll, final Accessor<S, T> accessor) {
		this.invMap = new HashMap<T, S>();
		T t = null;
		for (final S s : coll) {
			t = accessor.getKey(s);
			invMap.put(t, s);
		}
	}

	public boolean containsKey(final T key) {
		return invMap.containsKey(key);
	}

	public boolean containsValue(final S value) {
		return invMap.containsValue(value);
	}

	public Set<Entry<T, S>> entrySet() {
		return invMap.entrySet();
	}

	@Override
	public boolean equals(final Object o) {
		return invMap.equals(o);
	}

	public S get(final Object key) {
		return invMap.get(key);
	}

	@Override
	public int hashCode() {
		return invMap.hashCode();
	}

	public boolean isEmpty() {
		return invMap.isEmpty();
	}

	public Set<T> keySet() {
		return invMap.keySet();
	}

	public int size() {
		return invMap.size();
	}

	public Collection<S> values() {
		return invMap.values();
	}
}
