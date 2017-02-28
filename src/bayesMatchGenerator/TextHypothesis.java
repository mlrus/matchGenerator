package bayesMatchGenerator;

import interfaces.MatchInfoIF;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import util.data.StringMethods;

public class TextHypothesis implements Comparable<TextHypothesis>, Accessor<TextHypothesis, Collection<String>>,
		Accessible<TextHypothesis, Collection<String>> {
	private MatchDescription matchDescription;
	private Set<String> set;
	private List<String> list;

	private final static TextHypothesis singleton = new TextHypothesis();

	static public Accessor<TextHypothesis, Collection<String>> accessor() {
		return singleton;
	}

	public Accessor<TextHypothesis, Collection<String>> getAccessor() {
		return singleton;
	}

	public Collection<String> getKey(final TextHypothesis base) {
		return base.getAsaSet();
	}

	public TextHypothesis() {
		super();
	}

	public TextHypothesis(final TextHypothesis th) {
		this.matchDescription = th.matchDescription;
		this.set = th.set;
		this.list = th.list;
	}

	public TextHypothesis(final MatchDescription matchDescription, final List<String> list) {
		super();
		this.matchDescription = matchDescription;
		this.set = null;
		this.list = list;
	}

	public TextHypothesis(final MatchDescription matchDescription, final Set<String> set) {
		super();
		this.matchDescription = matchDescription;
		this.set = set;
		this.list = new ArrayList<String>(set);
	}

	@Override
	public String toString() {
		return String.format(" %s %s ::  %s", matchDescription.toString(), set == null
				? ""
				: set.toString(), list == null
				? list
				: list.toString());
	}

	// Compare two collections: Compares size, then words as they appear under the iterator
	int ccompareTo(final Collection<String> l1, final Collection<String> l2) {
		if (l1.size() != l2.size()) { return (l1.size() - l2.size()); }
		final Iterator<String> i1 = l1.iterator();
		final Iterator<String> i2 = l2.iterator();
		int cmp = 0;
		while (cmp == 0 && i1.hasNext()) {
			cmp = i1.next().compareTo(i2.next());
		}
		return cmp;
	}

	int compare(final float a, final float b) {
		return a < b
				? 1
				: a > b
						? -1
						: 0;

	}

	// Put same words in adjacent positions, with the PhraseMatch before the KeyMatch
	public int compareTo(final TextHypothesis o) {
		int cmp = 0;

		if (cmp == 0) {
			cmp = compare(this.getProb(), o.getProb());
		}

		if (cmp == 0) {
			cmp = (this.list.size() - o.list.size());
		}

		if (cmp == 0) {
			cmp = -compare(this.getEstimate(), o.getEstimate());
		}

		if (cmp == 0) {
			cmp = ccompareTo(list, o.list);
		}

		if (cmp == 0) {
			cmp = matchDescription.compareTo(o.matchDescription);
		}

		return cmp;
	}

	public MatchDescription getMatchDescription() {
		return matchDescription;
	}

	public void setMatchDescription(final MatchDescription matchDescription) {
		this.matchDescription = matchDescription;
	}

	public int compareTo(final MatchInfoIF o) {
		return matchDescription.compareTo(o);
	}

	public PhraseType getPhraseType() {
		return matchDescription.getPhraseType();
	}

	public float getProb() {
		return matchDescription.getProb();
	}

	public float getEstimate() {
		return matchDescription.getEstimate();
	}

	public Set<String> getAsaSet() {
		if (set == null) {
			set = new HashSet<String>(list);
		}
		return set;
	}

	public List<String> getAsaList() {
		return list;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null)
				? 0
				: list.hashCode());
		result = prime * result + ((matchDescription == null)
				? 0
				: matchDescription.hashCode());
		result = prime * result + ((set == null)
				? 0
				: set.hashCode());
		return result;
	}

	public boolean equals(final TextHypothesis obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final TextHypothesis other = obj;
		if (list == null) {
			if (other.list != null) { return false; }
		} else if (!list.equals(other.list)) { return false; }
		if (matchDescription == null) {
			if (other.matchDescription != null) { return false; }
		} else if (!matchDescription.equals(other.matchDescription)) { return false; }
		if (set == null) {
			if (other.set != null) { return false; }
		} else if (!set.equals(other.set)) { return false; }
		return true;
	}

	/*******************************************************************************************************************
	 * Returns true if and only if this.set contains all the elements of the input
	 * 
	 * @param set
	 * @return
	 */
	boolean containsSet(final Collection<String> set) {
		if (this.set == null) {
			this.set = new HashSet<String>(list);
		}
		return (this.set.containsAll(set));
	}

	/*******************************************************************************************************************
	 * Returns true if and only if the input contains all the elements of this.set
	 * 
	 * @param set
	 * @return
	 */
	boolean setContains(final Set<String> set) {
		return this.set != null
				? set.containsAll(this.set)
				: set.containsAll(this.list);
	}

	/*******************************************************************************************************************
	 * Returns true if and only if the phrase is included by this.list as a phrase
	 * 
	 * @param phrase
	 * @return
	 */
	public boolean containsPhrase(final List<String> phrase) {
		return StringMethods.containsList(this.list, phrase);
	}

	/*******************************************************************************************************************
	 * Returns true if and only if the container includes this.list as a phrase
	 * 
	 * @param container
	 * @return
	 */
	public boolean phraseContains(final List<String> container) {
		return StringMethods.containsList(container, this.list);
	}

	public void setEstimate(final float biasedEstimate) {
		this.getMatchDescription().setEstimate(biasedEstimate);
	}

	public void setProb(final float biasedProb) {
		this.getMatchDescription().setProb(biasedProb);

	}

}
