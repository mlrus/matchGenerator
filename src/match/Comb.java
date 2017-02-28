// This is unpublished source code. Michah Lerner 2006, 2007, 2008

package match;

import interfaces.CGen;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Counter-based implementation of the choice generator.
 * 
 * @author Michah.Lerner
 * 
 * @param <T>
 *            Type of items being combined
 */
public class Comb<T> implements CGen<T> {
	Long bits;
	Long maxVal;
	Long maxBit;
	T[] items;

	public Comb(final T[] items) {
		this.items = items;
		maxVal = 1L << items.length;
		maxBit = Long.valueOf(items.length);
		bits = 1L; // Don't need to see the empty set.
	}

	@SuppressWarnings("unchecked")
	public Comb(final Collection<T> itemList) {
		this(itemList.toArray((T[]) (new Object[itemList.size()])));
	}

	public Long nextCombID() {
		if (hasNext()) {
			return bits;
		}
		return -1L;
	}

	public Long maxCombID() {
		return maxVal - 1;
	}

	public boolean hasNext() {
		return bits < maxVal;
	}

	public Collection<T> next() {
		return next(new ArrayList<T>());
	}

	public String nextAsString() {
		return nextAsString(new ArrayList<T>());
	}

	// Padded versions accept an additional placeholder to show the skipped items
	public Collection<T> next(final Collection<T> result, final T pad) {
		for (int bitPos = 0; bitPos < maxBit; bitPos++) {
			if ((bits & (1L << bitPos)) != 0) {
				result.add(this.items[bitPos]);
			} else {
				result.add(pad);
			}
		}
		bits++;
		return result;
	}

	public Collection<T> next(final T pad) {
		return next(new ArrayList<T>(), pad);
	}

	public String nextAsString(final Collection<T> res, final T pad) {
		final Collection<T> result = next(res, pad);
		if (result == null) {
			return null;
		}
		final StringBuffer sb = new StringBuffer();
		for (final T s : result) {
			sb.append(s + " ");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public String nextAsString(final T pad) {
		return nextAsString(new ArrayList<T>(), pad);
	}

	public long count() {
		return maxVal;
	}

	public Collection<T> next(final Collection<T> ans) {
		for (int bitPos = 0; bitPos < maxBit; bitPos++) {
			if ((bits & (1L << bitPos)) != 0) {
				ans.add(this.items[bitPos]);
			}
		}
		bits++;
		return ans;
	}

	public String nextAsString(final Collection<T> resVector) {
		final Collection<T> resultVector = next(resVector);
		if (resultVector == null) {
			return null;
		}
		final StringBuffer sb = new StringBuffer();
		for (final T s : resultVector) {
			sb.append(s + " ");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

}
