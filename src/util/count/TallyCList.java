// This is unpublished source code. Michah Lerner 2006, 2007, 2008

package util.count;

import interfaces.CGen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import match.Comb;

/**
 * Tally the frequences of word <em>sequences</em>, which are generated terms of the input collections.<br>
 * <br>
 * This is very useful when we need to get the frequencies of word sequences. These routines provide the precomputation
 * to achieve very very fast computation of Bayesian statistics from content sets. These routines are used in the
 * DocumentStore class. The drawback of the method is the large size required to generate the combinations when the
 * vocabulary is big. <br>
 * <Br>
 * Note the resulting tally gives a complete picture of the probability distribution for the terms given. This lets one
 * go through the high-frequency subgroups at a very fast clip.
 * 
 * @author Michah.Lerner
 * @see DocumentStore
 * @see TallyCSet which computes the same thing using the set (HashSet) structure instead of the list structure
 * @param <S>
 *            type of the objects from which the frequencies of combinations are computed.
 */

public class TallyCList<S extends Comparable<? super S>> extends HashMap<Collection<S>, Integer> {
	private static final long serialVersionUID = -2981686934594170276L;

	/**
	 * Update the frequency count for each of the combinations generated from the input terms.
	 * 
	 * @param s
	 *            a collection of terms
	 */
	public void insert(final Collection<S> s) {
		ArrayList<S> si;
		if (s instanceof ArrayList) {
			si = (ArrayList<S>) s;
		} else {
			si = new ArrayList<S>(s);
		}
		final CGen<S> combiner = new Comb<S>(si);
		while (combiner.hasNext()) {
			final Collection<S> nextItem = combiner.next();
			this.put(nextItem, (this.containsKey(nextItem)
					? this.get(nextItem)
					: 0) + 1);
		}
	}

	/**
	 * Update the statistics of one tally, by adding in the statistics of the other tally.
	 * 
	 * @param other
	 *            the tally that should be added to the current tally
	 * @return ths current tally updated withby adding the statistics of the other tally
	 */
	public TallyCList<S> add(final TallyCList<S> other) {
		for (final Map.Entry<Collection<S>, Integer> me : other.entrySet()) {
			final Collection<S> s = me.getKey();
			final Integer i = me.getValue();
			if (!this.containsKey(s)) {
				this.put(s, i);
			} else {
				this.put(s, this.get(s) + i);
			}
		}
		return this;
	}

	/**
	 * Get the number of occurrences of the given collection.
	 * 
	 * @param s
	 * @return the count computed for compbinations of the input texts
	 */
	Integer get(final Collection<S> s) {
		return super.get(s);
	}

	public Map<Collection<S>, Integer> get() {
		return this;
	}
}