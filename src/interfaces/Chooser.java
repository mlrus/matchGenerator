// This is unpublished source code. Michah Lerner 2006, 2007, 2008

package interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * This interface supports the "choosing" of items from an array of items.
 * 
 * @author Michah.Lerner
 */
public interface Chooser<S> {

	/**
	 * Set the vector of items to make choices from
	 * 
	 * @param l
	 *            an array of items to make choicese from
	 */
	public List<List<S>> chooseCombIF(S[] l);

	public List<List<S>> chooseCombIF(final S[] l, final boolean iterable);

	public List<List<S>> chooseCombIF(final List<S> c);

	public List<List<S>> chooseCombIF(final List<S> c, int siz);

	public List<List<S>> chooseCombIF(final List<S> c, int from, int to);

	public List<List<S>> chooseCombIF(final List<S> c, final boolean iterable);

	public boolean hasNext();

	public boolean hasMore();

	public List<S> next();

	/**
	 * Generate the combinations of m items taken n at a time, storing the result locally.
	 * 
	 * @param m
	 *            the number of items to choose from
	 * @param n
	 *            the number of items to choose
	 */
	public void choose(int m, int n);

	public List<List<S>> permute(final List<S> l);

	/**
	 * Internal iterator support, supports incremental generation.
	 * 
	 * @return Items that will be iterated over by calling <code>next()<code>.
	 */
	public List<List<S>> _next();

	public Collection<List<S>> leaveOutOne(final List<S> cs);

	public List<Set<S>> leaveOutOne(final Set<S> cs);

	/**
	 * Generic generate, test and prune. Generate candidate sets in order of increasing length. Test candidate sets with
	 * the PredicateEvaluator passed into this method. The <code>PredicateEvaluator.foundP</code> member defines the
	 * property being collected, and it returns <code>true</code> when its first arg satisfies the property. These
	 * satisfying sets are stored into 'minpoints', and all larger sets that contain a midpoint are immediately removed
	 * from the list of candidate sets. TODO: Currently does not use anticipatory order or caching.
	 * 
	 * @param args
	 *            the items that should be formed into combinations
	 * @param tester
	 *            a class with a foundP vararg tester
	 * @return list items that pass but don't contain another passing item.
	 */
	// public List<PairNC<PhraseType,Set<S>>> collectMinPoints(S[] args, PredicateEvaluator<S> tester);
}