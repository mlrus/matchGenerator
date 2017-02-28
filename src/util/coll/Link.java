package util.coll;

import interfaces.EntryData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/***********************************************************************************************************************
 * Forest of trees. Trees will share a common parent if and only if the trees are linked. The common parent the lower
 * numbered of their parents. Findroot does path compression.
 * 
 * @author mlrus
 * 
 * @param <S>
 */

public class Link<S extends EntryData<S>> {

	protected final int[] parent;
	List<S> value;

	public Link(final int n) {
		parent = new int[n];
		value = new ArrayList<S>(n);
		init();
	}

	S get(final int i) {
		return value.get(i);
	}

	public S set(final int i, final S s) {
		final S o = get(i);
		value.set(i, s);
		return o;
	}

	boolean isRoot(final int i) {
		return parent[i] == i;
	}

	void init() {
		for (int i = 0; i < parent.length; i++) {
			parent[i] = i;
			value.add(i, null);
		}
	}

	public int link(final int i, final int j) {
		parent[i] = findRoot(i);
		parent[j] = findRoot(j);
		if (parent[i] == parent[j]) {
			return Math.min(i, j); // Return if not disjoint.
		}
		if (parent[i] < parent[j]) {
			parent[j] = parent[i];
			return i;
		}
		parent[i] = parent[j];
		return j;
	}

	int findRoot0(final int i) {
		int n = i;
		while (!isRoot(n)) {
			n = findRoot(parent[i]);
		}
		return n;
	}

	int findRoot(final int i) {
		int n = i;
		while (!isRoot(n)) {
			n = findRoot(parent[i]);
			parent[i] = n;
		}
		return parent[i];
	}

	int getNumitems() {
		int lastIndex;
		for (lastIndex = value.size() - 1; lastIndex > 0; lastIndex--) {
			if (value.get(lastIndex).getText() != null) {
				break;
			}
		}
		return lastIndex + 1;
	}

	/*******************************************************************************************************************
	 * Roll up repeated segments into the parent name (subtreeNames) and elements (subtreeContents)
	 * 
	 * @param subtreeNames
	 *            names, will be cleared before use
	 * @param subtreeContents
	 *            contents, will be cleared before use
	 * @return number of trees
	 */
	int mergeRepeats(final List<Integer> subtreeNames, final List<List<S>> subtreeContents) {
		class SortBy implements Comparator<Integer[]> {
			int fieldNumber = 0;

			SortBy(final int fieldNumber) {
				this.fieldNumber = fieldNumber;
			}

			public int compare(final Integer[] o1, final Integer[] o2) {
				return o1[fieldNumber].compareTo(o2[fieldNumber]);
			}
		}
		final Integer TREENAME = 0; // Sorting by index into Integer[]
		subtreeNames.clear();
		subtreeContents.clear();
		if (parent.length == 0) {
			return 0;
		}
		final List<Integer[]> ilist = new ArrayList<Integer[]>();
		for (int i = 0; i < parent.length; i++) {
			ilist.add(new Integer[] { parent[i], i });
		}
		Collections.sort(ilist, new SortBy(TREENAME));

		final int lastIndex = getNumitems();

		List<S> values = new ArrayList<S>();
		Integer[] priorItem = ilist.get(0);
		for (int i = 0; i < lastIndex; i++) {
			final Integer[] currItem = ilist.get(i);
			if (currItem[0].compareTo(priorItem[0]) != 0) {
				subtreeNames.add(priorItem[0]);
				subtreeContents.add(values);
				priorItem = currItem;
				values = new ArrayList<S>();
			}
			values.add(value.get(currItem[1]));
		}
		subtreeNames.add(priorItem[0]);
		subtreeContents.add(values);
		return subtreeNames.size();
	}

	/*******************************************************************************************************************
	 * Re-express the two-list form of mergeRepeats (which maintained the input's sequencing) into a one-list form. The
	 * makeHitList method adds a List<S> to the List<List<S>> for each repeat
	 * 
	 * @return List of repeats for each hit
	 */
	public List<List<S>> makeHitList() {
		final List<List<S>> result = new ArrayList<List<S>>();
		;
		final List<Integer> nameList = new ArrayList<Integer>();
		final List<List<S>> valueList = new ArrayList<List<S>>();
		mergeRepeats(nameList, valueList);
		final Iterator<Integer> nameIter = nameList.iterator();
		final Iterator<List<S>> valueIter = valueList.iterator();
		while (nameIter.hasNext() && valueIter.hasNext()) {
			final Integer loc = nameIter.next();
			final List<S> val = valueIter.next();
			while (loc >= result.size()) {
				result.add(null);
			}
			result.set(loc, val);
		}
		return result;
	}

	public StringBuffer showSubtrees(final List<List<S>> subTrees) {
		final StringBuffer sb = new StringBuffer("Got " + subTrees.size() + " subtrees.\n");
		int hitNo = 0;
		for (final List<S> subTree : subTrees) {
			if (subTree == null) {
				sb.append(String.format("Result %04d (repeated content)\n", hitNo));
			} else {
				sb.append(String.format("Result %04d (%d items)\n", hitNo, subTree.size()));
				int repNo = 0;
				for (final S item : subTree) {
					sb.append(String.format("     %04d:%04d : getIdent=%s : getText=<<%s>>\n", hitNo, repNo++, item.getIdent(),
							item.getText()));
				}
			}
			hitNo++;
		}
		return sb;
	}
}
