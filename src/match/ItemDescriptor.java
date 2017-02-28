package match;

import java.util.ArrayList;
import java.util.List;

/***********************************************************************************************************************
 * The ItemDescriptor class stores an item, consisting of an identifying segment number, the item's offset from the
 * start of the segment, and the length of the item.
 * 
 * @author mlrus
 * 
 */
public class ItemDescriptor implements Interval<ItemDescriptor>, Comparable<ItemDescriptor> {
	int segment;
	int from;
	int to;
	int fullItemLength;
	List<String> examples = new ArrayList<String>();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + from;
		result = prime * result + fullItemLength;
		result = prime * result + segment;
		result = prime * result + to;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ItemDescriptor other = (ItemDescriptor) obj;
		if (from != other.from) {
			return false;
		}
		if (fullItemLength != other.fullItemLength) {
			return false;
		}
		if (segment != other.segment) {
			return false;
		}
		if (to != other.to) {
			return false;
		}
		return true;
	}

	public float getWeight() {
		return 0f;
	}

	public final int getSegment() {
		return segment;
	}

	public final void setSegment(final int segment) {
		this.segment = segment;
	}

	public final int getFrom() {
		return from;
	}

	public final void setFrom(final int from) {
		this.from = from;
	}

	public final int getTo() {
		return to;
	}

	public final void setTo(final int to) {
		this.to = to;
	}

	public final int getFullItemLength() {
		return fullItemLength;
	}

	public final void setFullItemLength(final int fullItemLenth) {
		this.fullItemLength = fullItemLenth;
	}

	/*******************************************************************************************************************
	 * Simple constructor for an ItemDescriptor
	 * 
	 * @param segment
	 *            identifying segment number
	 * @param from
	 *            offset of this item from the start of the segment
	 * @param fullItemLength
	 *            length of this item
	 */
	public ItemDescriptor(final int segment, final int fullItemLength, final int from, final int to) {
		this.segment = segment;
		this.to = to;
		this.from = from;
		this.fullItemLength = fullItemLength;
	}

	public ItemDescriptor(final ItemDescriptor id) {
		this.segment = id.segment;
		this.to = id.to;
		this.from = id.from;
		this.fullItemLength = id.fullItemLength;
	}

	@Override
	public String toString() {
		return String.format("seg%02d[%04d]:(offset=%04d .. %04d)(seglen=%04d)", segment, fullItemLength, from, to, to - from + 1);
	}

	/*******************************************************************************************************************
	 * The compareTo is defined to simplify checking if an item has a cover in the set, by iterating the first elements
	 * of the tailset.
	 */
	public int compareTo(final ItemDescriptor o) {
		if (segment != o.segment) {
			return segment - o.segment;
		}
		if (this.equals(o)) {
			return 0;
		}
		if (to - from < o.to - o.from) {
			return -1; // shorter segments cannot be covers, so they come first
		}
		if (from < o.from) {
			return -1; // later starts cannot be covers either,
		}
		if (to > o.to) {
			return 1;
		}
		return -1;
	}

	@Deprecated
	public int compareTo(final Interval<ItemDescriptor> o) {
		System.err.println("hit deprecated compareTo(" + o + ")");
		return 0;
	}

	/*******************************************************************************************************************
	 * Returns true if this segment intersects the other segment
	 * 
	 * @param o
	 *            The segment to compare with
	 * @return True if and only if this segment doesn't start after the other, and includes the start of the other
	 */
	public boolean intersects(final ItemDescriptor o) {
		if (segment != o.segment) {
			return false;
		}
		if (from > o.from) {
			return o.intersects(this);
		}
		return to >= o.from;
	}

	/*******************************************************************************************************************
	 * True if and only if this segment starts at-or-before the other and ends at-or-after the other
	 * 
	 * @param o
	 *            The segment to compare with
	 * @return True if and only if this segment contains all of the other segment
	 */
	public boolean covers(final ItemDescriptor o) {
		if (segment != o.segment) {
			return false;
		}
		return from <= o.from && o.to <= to;
	}

	/*******************************************************************************************************************
	 * True if and only if this segment starts at-or-after the other and ends at-or-before the other
	 * 
	 * @param o
	 *            The segment to compare with
	 * @return True if and only if the other segment contains all of this segment
	 */
	public boolean covered(final ItemDescriptor o) {
		if (segment != o.segment) {
			return false;
		}
		return o.from <= from && to <= o.to;
	}

	/*******************************************************************************************************************
	 * True if and only if this segment ends before the other begins
	 * 
	 * @param o
	 *            The segment to compare with
	 * @return True if this segment strictly precedes the other
	 */
	public boolean before(final ItemDescriptor o) {
		if (segment != o.segment) {
			return false;
		}
		return to < o.from;
	}

	/*******************************************************************************************************************
	 * True if and only if this segment starts after the other ends
	 * 
	 * @param o
	 * @return
	 */
	public boolean after(final ItemDescriptor o) {
		if (segment != o.segment) {
			return false;
		}
		return o.before(this);
	}
}
