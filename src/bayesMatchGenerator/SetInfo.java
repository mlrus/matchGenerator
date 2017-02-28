/**
 * 
 */
package bayesMatchGenerator;

import interfaces.Chooser;
import interfaces.PredicateEvaluator;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.coll.PairNC;

public class SetInfo<S> {

	static int depth = 0;

	boolean debug = true;
	boolean useRef = false;
	int DEFAULT_DEPTH = 3;

	final static int NUMENTRIES = 16384;
	final Chooser<S> chooser;
	// BoundedLinked
	HashMap<Collection<S>, SI> setDescriptors;
	final PredicateEvaluator<S> evaluator;

	SetInfo(final PredicateEvaluator<S> evaluator) {
		setDescriptors = new HashMap<Collection<S>, SI>();
		// new BoundedLinkedHashMap<Set<S>, SI>(NUMENTRIES, Constants._documentStore_cacheLoadFactor, true);
		chooser = new ChooseCombIF<S>();
		this.evaluator = evaluator;
	}

	<T> Reference<T> mkReference(final T refable) { // T can be pretty complex, like List<Set<S>>
		return new SoftReference<T>(refable);
	}

	public SI getSI(final List<S> item) {
		return getSI(new HashSet<S>(item));
	}

	public SI getSI(final Collection<S> item) {
		return getSI(new HashSet<S>(item));
	}

	public SI getSI(final Set<S> item) {
		SI si = setDescriptors.get(item);
		if (si == null) {
			si = new SI(item);
			setDescriptors.put(item, si);
		}
		return si;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		for (final Map.Entry<Collection<S>, SI> me : setDescriptors.entrySet()) {
			sb.append(me.getKey() + " :: " + me.getValue() + "\n");
		}
		return sb.toString();
	}

	class LenSort implements Comparator<List<S>> {
		public int compare(final List<S> o1, final List<S> o2) {
			return (-(o1.size() - o2.size()));
		}
	}

	public class SI {
		final Integer count;
		Integer fullPathCount;
		Set<S> item;
		List<Set<S>> items;
		Reference<List<Set<S>>> itemsRef;

		SI(final Set<S> item) {
			this.count = evaluator.getCountOfSet(item);
			this.item = item;
			final List<Set<S>> ref = chooser.leaveOutOne(item);
			if (useRef) {
				itemsRef = mkReference(ref);
			} else {
				items = ref;
			}
			if (false) {
				int sumCnt = 1;
				for (final Set<S> set : items) {
					sumCnt += evaluator.getCountOfSet(set);
				}
				System.out.printf("[ %s ] :: %7.5f \n", item, (1f * sumCnt) / (1f * count + 1));
			}
		}

		@Override
		public String toString() {
			final StringBuffer sb = new StringBuffer();
			sb.append("<<item=" + item + " :: count=" + count + " :: fullPathCount " + fullPathCount + " :: items=");
			int prodOfParts = 1;
			for (final Set<S> set : this.items) {
				final SI si = setDescriptors.get(set);
				final int sumPathCount = si.sumPathCount();
				final int maxFrom = si.maxFrom();
				final int minFrom = si.minFrom();
				final int sumCount = si.sumCount();
				prodOfParts *= sumPathCount;
				sb.append(String.format(" $[ %s : sumPatg=%d max=%d min=%d sum=%d ]$ ", set, sumPathCount, maxFrom, minFrom,
						sumCount));
			}
			sb.append(" :: itemsRef= " + itemsRef + ">>");
			return sb.toString();
		}

		List<PairNC<Float, Set<S>>> collectEst() {
			return collectEst(getLayer());
		}

		List<PairNC<Float, Set<S>>> collectEst(final int n) {
			if (n < this.item.size()) { return collectEst(getLayer(n)); }
			return new ArrayList<PairNC<Float, Set<S>>>();
		}

		List<PairNC<Float, Set<S>>> collectEst(final Collection<Set<S>> level) {
			final List<PairNC<Float, Set<S>>> sresult = new ArrayList<PairNC<Float, Set<S>>>();
			for (final Set<S> itemSet : level) {
				final SI si = setDescriptors.get(itemSet);
				final Float f = si.est();
				sresult.add(new PairNC<Float, Set<S>>(f, itemSet));
			}
			return sresult;
		}

		List<PairNC<Integer, Set<S>>> collectSizes() {
			final List<PairNC<Integer, Set<S>>> sresult = new ArrayList<PairNC<Integer, Set<S>>>();
			final Collection<Set<S>> level = getLayer();
			for (final Set<S> itemFromLevel : level) {
				final SI si = setDescriptors.get(itemFromLevel);
				final Integer cnt = si.getCount();
				sresult.add(new PairNC<Integer, Set<S>>(cnt, itemFromLevel));
			}
			return sresult;
		}

		Object evaluate(final Evaluator<S> ev) {
			return evaluate(ev, DEFAULT_DEPTH);
		}

		Object evaluate(final Evaluator<S> ev, final int evalDepth) {
			final Collection<Set<S>> level = getLayer();
			for (final Set<S> itemFromLevel : level) {
				if (itemFromLevel.size() > 1) {
					final SI si = setDescriptors.get(itemFromLevel);
					if (evalDepth > 0) {
						si.evaluate(ev, evalDepth - 1);
					}
				}
			}
			return null;
		}

		Integer getCount() {
			return count;
		}

		SI mkSI(final Set<S> itemFromLevel) {
			SI si = setDescriptors.get(itemFromLevel);
			if (si == null) {
				si = new SI(itemFromLevel);
			}
			setDescriptors.put(itemFromLevel, si);
			return si;
		}

		/***************************************************************************************************************
		 * Evaluate the top layer (i.e. layer of this.item) by dereferencing and obtaining the est values
		 * 
		 * @return A collection of sets that have been evaluated and stored into the setDescriptors map
		 */
		Collection<Set<S>> getLayer() {
			List<Set<S>> topLevel = useRef
					? itemsRef.get()
					: items;
			if (topLevel == null) {
				topLevel = chooser.leaveOutOne(this.item);
				itemsRef = mkReference(topLevel);
			}
			for (final Set<S> itemFromLevel : topLevel) {
				final SI si = setDescriptors.get(itemFromLevel);
				if (si == null) {
					mkSI(itemFromLevel);
				}
			}
			return topLevel;
		}

		/***************************************************************************************************************
		 * Evaluate the layer of size N layer by dereferencing and obtaining the est values
		 * 
		 * @return A collection of sets that have been evaluated and stored into the setDescriptors map
		 */
		Collection<Set<S>> getLayer(final int n) {
			final List<List<S>> level = chooser.chooseCombIF(new ArrayList<S>(this.item), n);
			final Collection<Set<S>> res = new HashSet<Set<S>>();
			for (final List<S> itemFromLevel : level) {
				final Set<S> set = new HashSet<S>(itemFromLevel);
				res.add(set);
				SI si = setDescriptors.get(set);
				if (si == null) {
					si = mkSI(set);
				}
			}
			return res;
		}

		void expand() {
			expand(DEFAULT_DEPTH);
		}

		void expand(final int currDepth) {
			if (depth > 0) {
				final Collection<Set<S>> itemsFromLayer = getLayer();
				for (final Set<S> setContent : itemsFromLayer) {
					System.out.println(setContent + ".expandBFS() expanding.");
					setDescriptors.get(setContent).expand(depth - 1);
				}
			}
		}

		int sumCount() {
			int cnt = 0;
			final Collection<Set<S>> itemsAtLayer = getLayer();
			for (final Set<S> setContent : itemsAtLayer) {
				final SI s = getSI(setContent);
				cnt += s.count;
			}
			return cnt;
		}

		int sumPathCount() {
			final Collection<Set<S>> itemsAtLayer = getLayer();
			if (itemsAtLayer.size() == 1) { return this.count; }

			int cnt = 0;
			for (final Set<S> itemSets : itemsAtLayer) {
				final SI s = getSI(itemSets);
				final int c = s.sumPathCount();
				cnt += c;
			}
			return cnt;
		}

		int maxFrom() {
			int max = Integer.MIN_VALUE;
			final Collection<Set<S>> itemsAtLayer = getLayer();
			for (final Set<S> itemSets : itemsAtLayer) {
				final SI s = getSI(itemSets);
				max = Math.max(max, s.count);
			}
			return max;
		}

		int minFrom() {
			int min = Integer.MAX_VALUE;
			final Collection<Set<S>> itemsAtLayer = getLayer();
			for (final Set<S> itemSets : itemsAtLayer) {
				final SI s = getSI(itemSets);
				min = Math.min(min, s.count);
			}
			return min;
		}

		float est() {
			return est(10f); // assume equivalent population size is ten
		}

		float est(final float m) { // follows idea of fml 6.22 pg 179 Mitchell
			final float pZero = 1f;// 1f / 1000f;
			final int nc = count;
			final float indep = 1.0f; // 0==>1
			final float pop;
			float est;
			if (item.size() == 1) {
				est = nc * pZero;
			} else {
				final int sumPathCount = sumPathCount();
				final int maxFrom = maxFrom();
				final int minFrom = minFrom();
				final int sumCount = sumCount();
				final int est1 = Math.round(nc * pZero * sumPathCount);
				pop = sumPathCount * indep + maxFrom * (1 - indep);
				final int est2 = Math.round((nc + m * pZero) / (pop + m));
				est = est1;
			}

			return est;
		}
	}

}