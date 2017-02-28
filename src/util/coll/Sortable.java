// This is unpublished source code. Michah Lerner 2006, 2007, 2008

package util.coll;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This provides a flexible generic sort for comparables, as well as collections, maps and Pairs. It takes vararg input,
 * which may include collections, comparables and non-comparables. Comparisons use the natural iteration order of the
 * underlying collections. This is a convenience function but specific cases may require special handling to be
 * certifiable, in order to eliminate possible dependence on the iterator order.
 */
public class Sortable implements Comparable<Sortable>, Comparator<Object> {
	Integer len;
	public Object[] o;

	public Sortable(final Object... o) {
		this.len = o.length;
		this.o = o;
	}

	public String asString() {
		final StringBuffer sb = new StringBuffer();
		for (final Object o1 : this.o) {
			sb.append(o1.toString() + ", ");
		}
		sb.delete(sb.length() - 2, sb.length() - 1);
		return sb.toString();
	}

	/**
	 * Sort collection of objects into an array of sorted objects
	 * 
	 * @param c
	 *            collection of objects to sort by placing them into an array and sorting the array
	 * @return The sorted array of objects.
	 */
	<S extends Comparable<S>> Object[] sort(final Collection<S> c) {
		final Object[] o1 = new Object[c.size()];
		final Iterator<?> it = c.iterator();
		for (int pos = 0; pos < o1.length; pos++) {
			o1[pos] = it.next();
		}
		Arrays.sort(o1);
		return o1;
	}

	/**
	 * Sort map of objects into an array of sorted objects using key, value
	 * 
	 * @param m
	 *            collection of objects to sort by placing them into an array and sorting the array
	 * @return The sorted array of objects.
	 */

	<S extends Comparable<? super T>, T extends Comparable<? super S>> Sortable[] sort(final Map<S, T> m) {
		final Sortable[] o1 = new Sortable[m.size()];
		final Iterator<Entry<S, T>> it = m.entrySet().iterator();
		for (int pos = 0; pos < o1.length; pos++) {
			final Map.Entry<S, T> n = it.next();
			o1[pos] = new Sortable(n.getKey(), n.getValue());
		}
		Arrays.sort(o1);
		return o1;
	}

	/**
	 * Sort map of objects into an array of sorted objects using value, key
	 * 
	 * @param m
	 *            collection of objects to sort by placing them into an array and sorting the array
	 * @return The sorted array of objects.
	 */
	<S extends Comparable<? super T>, T extends Comparable<? super S>> Sortable[] sort2(final Map<S, T> m) {
		final Sortable[] o1 = new Sortable[m.size()];
		final Iterator<Entry<S, T>> it = m.entrySet().iterator();
		for (int pos = 0; pos < o1.length; pos++) {
			final Map.Entry<S, T> n = it.next();
			o1[pos] = new Sortable(n.getValue(), n.getKey());
		}
		Arrays.sort(o1);
		return o1;
	}

	/**
	 * CompareTo function over sortables. It loops over a pair of Sortables, returning the comparison for the first
	 * non-zero comparison.
	 */
	@SuppressWarnings("unchecked")
	public int compareTo(final Sortable o1) {
		int diff = 0;
		for (int i = 0; i < Math.min(this.len, o1.len); i++) {
			if (this.o[i] instanceof Comparable && o1.o[i] instanceof Comparable) {
				diff = ((Comparable<Object>) this.o[i]).compareTo(o1.o[i]);
				if (diff != 0) { return diff; }
			} else {
				if (this.o[i] instanceof Collection && o1.o[i] instanceof Collection) {
					diff = compare((Collection<Collection>) this.o[i], (Collection<Collection>) o1.o[i]);
					if (diff != 0) { return diff; }
				}
			}
		}
		return this.o.length - o1.o.length;
	}

	@SuppressWarnings("unchecked")
	public int compare(final Collection<Collection> o1, final Collection<Collection> o2) {
		final Iterator<Collection> i1 = o1.iterator();
		final Iterator<Collection> i2 = o2.iterator();
		int diff;
		while (i1.hasNext() && i2.hasNext()) {
			final Object ob1 = i1.next();
			final Object ob2 = i2.next();
			if (ob1 instanceof Comparable && ob2 instanceof Comparable) {
				diff = ((Comparable<Object>) ob1).compareTo(ob2);
				if (diff != 0) { return diff; }
			}
		}
		if (i1.hasNext()) { return 1; }
		if (i2.hasNext()) { return -1; }
		return 0;
	}

	<S extends Comparable<T>, T> int compare(final S o1, final T o2) {
		return o1.compareTo(o2);
	}

	public int compare(final Object o1, final Object o2) {
		final int h1 = o1.hashCode();
		final int h2 = o2.hashCode();
		if (h1 < h2) { return -1; }
		if (h1 > h2) { return 1; }
		return 0;
	}

	<S extends Comparable<S>, T extends Comparable<T>> int compare(final Pair<S, T> o1, final Pair<S, T> o2) {
		int cmp = o1.s().compareTo(o2.s());
		if (cmp == 0) {
			cmp = o1.t().compareTo(o2.t());
		}
		return cmp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((len == null)
				? 0
				: len.hashCode());
		result = prime * result + Arrays.hashCode(o);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final Sortable other = (Sortable) obj;
		if (len == null) {
			if (other.len != null) { return false; }
		} else if (!len.equals(other.len)) { return false; }
		if (!Arrays.equals(o, other.o)) { return false; }
		return true;
	}
}
