// This is unpublished source code. Michah Lerner 2006, 2007, 2008

package util.coll;

import interfaces.Pairs;

import java.util.ArrayList;
import java.util.List;

/**
 * Typesafe pairs which implements a comparator over pairs. Also works when the component types are collections of
 * comparable objects, using the natural iteration order of the collections.
 * 
 * @author Michah.Lerner
 * 
 * @param <S>
 *            type "car" "head" or "s" of the pair
 * @param <T>
 *            type "cdr" "tail" or "t" of the pair
 */
@SuppressWarnings("serial")
public class Pair<S extends Comparable<S>, T extends Comparable<T>> implements Pairs<S, T>, Comparable<Pair<S, T>>,
		java.io.Serializable {
	S s;
	T t;

	@SuppressWarnings("unchecked")
	public Pair() {
		//
	}

	public Pair(final S s, final T t) {
		this.s = s;
		this.t = t;
	}

	public S S() {
		return s;
	}

	public S s() {
		return s;
	}

	public T T() {
		return t;
	}

	public T t() {
		return t;
	}

	public Pair<S, T> set(final S s, final T t) {
		this.s = s;
		this.t = t;
		return this;
	}

	public S setS(final S s) {
		this.s = s;
		return s;
	}

	public T setT(final T t) {
		this.t = t;
		return t;
	}

	public List<?> asList() {
		final List<Object> res = new ArrayList<Object>();
		res.add(s);
		res.add(t);
		return res;
	}

	@Override
	public String toString() {
		return "{" + s().toString() + ", " + t().toString() + "}";
	}

	public int compare(final Pair<S, T> o1, final Pair<S, T> o2) {
		int cmp = o1.s().compareTo(o2.s());
		if (cmp == 0) {
			cmp = o1.t().compareTo(o2.t());
		}
		return cmp;
	}

	public int compareTo(final Pair<S, T> o) {
		return compare(this, o);
	}

	public boolean equals(final Pair<S, T> o) {
		final S s1 = s();
		final S s2 = o.s();
		if (!s1.equals(s2)) { return false; }
		final T t1 = t();
		final T t2 = o.t();
		return t1.equals(t2);
	}

	@Override
	public int hashCode() {
		final int hc = 47 * t.hashCode() + s.hashCode();
		return hc;
	}

}