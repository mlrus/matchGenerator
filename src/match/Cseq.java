package match;

import java.util.Comparator;

public class Cseq implements CharSequence, Comparator<CharSequence>, Comparable<CharSequence> {
	private final CharSequence charSequence;

	Cseq() {
		charSequence = null;
	}

	Cseq(final Fchar seq) {
		charSequence = seq;
	}

	Cseq(final CharSequence seq) {
		charSequence = seq;
	}

	Cseq(final Cseq seq) {
		charSequence = seq.charSequence;
	}

	public char charAt(final int index) {
		return charSequence.charAt(index);
	}

	public int length() {
		return charSequence.length();
	}

	public CharSequence subSequence(final int start, final int end) {
		return charSequence.subSequence(start, end);
	}

	public int compare(final CharSequence o1, final CharSequence o2) {
		for (int p = 0; p < Math.min(o1.length(), o2.length()); p++) {
			final int cmp = o1.charAt(p) - o2.charAt(p);
			if (cmp != 0) { return cmp; }
		}
		return o1.length() - o2.length();
	}

	public int compareTo(final CharSequence o) {
		for (int p = 0; p < Math.min(length(), o.length()); p++) {
			final int cmp = charAt(p) - o.charAt(p);
			if (cmp != 0) { return cmp; }
		}
		return length() - o.length();
	}

	@Override
	public String toString() {
		return charSequence.length() + "::" + String.valueOf(charSequence);
	}
}