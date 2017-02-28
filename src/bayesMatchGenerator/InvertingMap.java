package bayesMatchGenerator;

import java.util.Collection;
import java.util.HashMap;

/***********************************************************************************************************************
 * The class InvertMap generates an inverted map based on an {@link Accessor}
 * 
 * @param <S>
 *            The type of the input collection's members
 * @param <T>
 *            The type of the element that will index the map
 */
@SuppressWarnings("serial")
public final class InvertingMap<S, T> extends HashMap<T, S> {
	InvertingMap(final Collection<S> coll, final Accessor<S, T> accessor) {
		super();
		T t = null;
		for (final S s : coll) {
			t = accessor.getKey(s);
			this.put(t, s);
		}
	}
}
