package match;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/***********************************************************************************************************************
 * The ItemCovers class stores sequences that are not contained in any sequence seen thusfar.
 * 
 * @author mlrus
 * 
 */
class ItemCovers<S extends Interval<S>> {
	SortedSet<S> itemSet;
	boolean debug = true;

	ItemCovers() {
		itemSet = new TreeSet<S>();
	}

	ItemCovers(final Comparator<S> comparator) {
		itemSet = new TreeSet<S>(comparator);
	}

	/*******************************************************************************************************************
	 * Predicate, returns true if and only if the set contains an item that covers "o". This does not use your garden
	 * variety of comparator. Here is such a comparator: public int compareTo(final ItemDescriptor o) {
	 * if(segment!=o.segment)return segment-o.segment; if(this.equals(o))return 0; if(to-from<o.to-o.from)return -1; //
	 * shorter segments cannot be covers, so they come first if(from<o.from)return -1; // later starts cannot be covers
	 * either, if(to>o.to)return 1; return -1; }
	 * 
	 * @param o
	 * @return
	 */
	boolean covers(final S o) {
		for (final S coveringItem : itemSet.tailSet(o)) {
			if (coveringItem.covers(o)) {
				return true;
			}
			if (coveringItem.after(o)) {
				break;
			}
		}
		return false;
	}

	S findMinlenthCover(final S o) {
		for (final S coveringItem : itemSet.tailSet(o)) {
			if (coveringItem.covers(o)) {
				return coveringItem;
			}
			if (coveringItem.after(o)) {
				break;
			}
		}
		return null;
	}

	boolean isaCover(final S o) {
		for (final S s : itemSet) {
			if (o.covers(s)) {
				return true;
			}
		}
		return false;
	}

	boolean intersects(final S o) {
		for (final S s : itemSet) {
			if (o.intersects(s)) {
				return true;
			}
		}
		return false;
	}

	boolean before(final S o) {
		for (final S s : itemSet) {
			if (o.before(s)) {
				return true;
			}
		}
		return false;
	}

	boolean after(final S o) {
		for (final S s : itemSet) {
			if (o.after(s)) {
				return true;
			}
		}
		return false;
	}

	String getKey() {
		return "B=before A=after I=intersect E=equal C=covers";
	}
}