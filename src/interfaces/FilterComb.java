package interfaces;

import java.util.Collection;
import java.util.List;

import bayesMatchGenerator.TextHypothesis;

public interface FilterComb {

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
	public List<TextHypothesis> collectResults(Collection<String> content);

}
