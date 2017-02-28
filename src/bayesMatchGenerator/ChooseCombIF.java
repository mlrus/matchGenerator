// This is unpublished source code. Michah Lerner 2006, 2007, 2008

package bayesMatchGenerator;

import interfaces.Chooser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This class provides efficient combinatorial expansion through goal subsumption and bottom up expansion. This gives an
 * iterator to go through the non-subsumed items. For small searches the results are generated at the outset, whereas
 * for small searches the items are generated with a lazy-evaluation that expands the "next" layer only as needed. Both
 * mechanisms are the same in other respects, and in particular they use the same iterator and subsumption (as will be
 * described). <br>
 * <br>
 * The class constructor is called once. It can then process any number of problem instances (i.e., phrases to make
 * keywords for). <br>
 * <br>
 * The method <code>collectMinPoints( phrase, predicate)</code> generates the <em>correct</em> and
 * <em>reasonable</em> constraints and keywords that represent the phrase within the context of the query context. The
 * method may be called repeatedly with different <code>phrase</code>s, in order to obtain results for each phrase.
 * The <code>predicate</code> evaluates whether an item is a good representative of the phrase. The predicate returns
 * a constraint, stating the kind of match (if any) finds the phrase from the item. For example, a "set" constraint
 * works if the item is a subset of the user's query. An "exact" constraint works if the item is exactly the same as the
 * user's query.
 * 
 * <br>
 * <br>
 * To clarify the terms used, <br>
 * <br>
 * <em>Correct</em> keymatches designate non-ambiguous entity identifiers, to within the currently configured
 * threshold of certainty <br>
 * <em>Reasonable</em> keymatches are neither trivial, nor rare. They are what users might reasonably type as a query.
 * <br>
 * The code sets the <em>query context</em> by configuration of two or more information sources. Information sources
 * implement a interface supporting caching and multiplexing of specialized queries to multiple search providers. <br>
 * <em>Keywords</em> are the actual words that the users' queries get matched to, in order to return a result <br>
 * <em>Constraints</em> define the kind of matching that is performed for the given keywords. Different keywords can
 * have different constraints. These correspond the capabilities of the engine that will be used to answer the user
 * queries, such as a GB-series device. <br>
 * <em>Subsumption</em> eliminates the possibilities that do not need to be considered, based on the partial results.
 * This can save substantial space and time. It essentially prunes the search space and often reduces a search space
 * dramatically. Subsumption occurs automatically for items that occur that are probably correct, as defined by the
 * threshold of certainty. Results can be generated even when subsumption does not occur; the results in that case have
 * more restrictive contraints than the more general subsuming results. <br>
 * <em>Lazy evaluation</em> allows searching of fairly large spaces by only generating items when they are needed.
 * This avoids the potentially huge space requirements that can occur when searching for large phrases
 * 
 * @author Michah.Lerner
 * 
 * @param <S>
 */
public class ChooseCombIF<S> implements Chooser<S> {
	S[] items; // current combination being constructed
	S[] data; // input info
	int M, N, P;
	LinkedList<List<S>> results;
	Iterator<List<S>> instanceIterator = null;

	// List<Set<S>> minPoints; // subsume items with any subset of these horizon points

	public ChooseCombIF() {
		results = new LinkedList<List<S>>();
		M = N = P = 0;
	}

	@SuppressWarnings("unchecked")
	public List<List<S>> chooseCombIF(final List<S> c) {
		return chooseCombIF((S[]) c.toArray());
	}

	@SuppressWarnings("unchecked")
	public List<List<S>> chooseCombIF(final List<S> c, final int siz) {
		return chooseCombIF((S[]) c.toArray(), siz);
	}

	@SuppressWarnings("unchecked")
	public List<List<S>> chooseCombIF(final List<S> c, final int from, final int to) {
		return chooseCombIF((S[]) c.toArray(), from, to);
	}

	public List<List<S>> chooseCombIF(final S[] l) {
		return chooseCombIF(l, 1, l.length);
	}

	public List<List<S>> chooseCombIF(final S[] l, final int siz) {
		return chooseCombIF(l, siz, siz);
	}

	@SuppressWarnings("unchecked")
	public List<List<S>> chooseCombIF(final List<S> c, final boolean iterable) {
		return chooseCombIF((S[]) c.toArray(), iterable);
	}

	@SuppressWarnings("unchecked")
	public List<List<S>> chooseCombIF(final S[] l, final int from, final int to) {
		results = new LinkedList<List<S>>();
		M = N = P = 0;
		M = l.length;
		data = l;
		for (int n = from; n <= to; n++) {
			items = (S[]) (new Object[n]);
			choose(M, n);
		}
		return results;
	}

	public List<List<S>> chooseCombIF(final S[] l, boolean iterable) {
		if (!iterable) {
			chooseCombIF(l);
		} else {
			results = new LinkedList<List<S>>();
			M = N = P = 0;
			M = l.length;
			data = l;
			// minPoints = new ArrayList<Set<S>>();
		}
		return results;
	}

	public boolean hasNext() {
		if (instanceIterator == null) { return _hasNext(); }
		return instanceIterator.hasNext() || _hasNext();
	}

	public boolean hasMore() {
		return instanceIterator != null && instanceIterator.hasNext();
	}

	public List<S> next() {
		if (instanceIterator == null || !instanceIterator.hasNext()) {
			if (!_hasNext()) { return null; }
			_next();
		}
		return instanceIterator.next();
	}

	public void choose(final int m, final int n) {
		if (n == 0) {
			final List<S> res = new LinkedList<S>();
			for (final S item : items) {
				res.add(item);
			}
			results.add(res);
			return;
		}
		if (m < n) { return; }
		items[n - 1] = data[m - 1];
		choose(m - 1, n - 1); // with item in position n
		choose(m - 1, n); // without item in position n (overwrite)
		return;
	}

	public List<List<S>> permute(final List<S> l) {
		final List<List<S>> rl = new ArrayList<List<S>>();
		if (l.size() == 0) { return null; }
		if (l.size() == 1) {
			rl.add(l);
			return rl;
		}
		if (l.size() > 1) {
			for (int i = 0; i < l.size(); i++) {
				final S item = l.get(0);
				l.subList(0, 1).clear();
				final List<List<S>> p2 = permute(l);
				for (final List<S> scol : p2) {
					final List<S> res = new ArrayList<S>(scol);
					res.add(item);
					rl.add(res);
				}
				l.add(item);
			}
		}
		return rl;
	}

	public boolean _hasNext() {
		return P < M;
	}

	@SuppressWarnings("unchecked")
	public List<List<S>> _next() {
		results = new LinkedList<List<S>>();
		items = (S[]) (new Object[++P]);
		chooseIncrementally(M, P);
		instanceIterator = results.iterator();
		return results;
	}

	public void chooseIncrementally(final int m, final int n) {
		if (n == 0) {
			final List<S> res = new LinkedList<S>();
			for (final S item : items) {
				res.add(item);
			}
			// if (!subsumed(res)) {
			results.add(res);
			// }
			return;
		}
		if (m < n) { return; }
		items[n - 1] = data[m - 1];
		chooseIncrementally(m - 1, n - 1); // with item in position n
		chooseIncrementally(m - 1, n); // without item in position n (overwrite)
		return;
	}

	public Collection<List<S>> leaveOutOne(final List<S> cs) {
		final List<List<S>> res = new ArrayList<List<S>>(cs.size());
		for (int i = 0; i < cs.size(); i++) {
			final List<S> item = new ArrayList<S>(cs);
			item.remove(i);
			res.add(item);
		}
		return res;
	}

	public List<Set<S>> leaveOutOne(final Set<S> cs) {
		final List<Set<S>> res = new ArrayList<Set<S>>(cs.size());
		for (final S s : cs) {
			final Set<S> item = new HashSet<S>(cs);
			item.remove(s);
			res.add(item);
		}
		return res;
	}
}
