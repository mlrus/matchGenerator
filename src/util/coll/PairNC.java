package util.coll;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PairNC<S, T> implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7347885059418034978L;
	S s;
	T t;

	@SuppressWarnings("unchecked")
	public PairNC() {
		s = (S) "";
		t = (T) Integer.valueOf(-1);
	}

	public PairNC(final S s, final T t) {
		this.s = s;
		this.t = t;
	}

	public S S() {
		return s;
	}

	public T T() {
		return t;
	}

	public PairNC<S, T> set(final S s, final T t) {
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

	public S s() {
		return s;
	}

	public T t() {
		return t;
	}

	@SuppressWarnings("unchecked")
	public List<S> asList() {
		final List<S> res = new ArrayList<S>();
		res.add(s);
		res.add((S) t);
		return res;
	}

	@Override
	public String toString() {
		return "{" + s().toString() + ", " + t().toString() + "}";
	}

	public static class SortCar<S extends Comparable<S>> implements Comparator<PairNC<S, ?>> {
		public int compare(final PairNC<S, ?> o1, final PairNC<S, ?> o2) {
			return o1.s().compareTo(o2.s());
		}
	}

}
