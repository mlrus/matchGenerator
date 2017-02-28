/**
 * 
 */
package bayesMatchGenerator;

import match.Fchar;

final class ProbInfo implements Comparable<ProbInfo> {
	final float prob; // empirical frequency of the phrase within the corpus
	final int len; // number of tokens in the phrase
	final Fchar text; // storage of the character data of the phrase

	ProbInfo(final float prob, final int len, final Fchar text) {
		this.prob = prob;
		this.len = len;
		this.text = text;
	}

	@Override
	public String toString() {
		return String.format("%05.3f %04d %s", prob, len, text);
	}

	public int compareTo(final ProbInfo o) {
		int cmp;
		cmp = this.len < o.len
				? -1
				: this.len > o.len
						? 1
						: 0;
		if (cmp == 0) {
			cmp = this.prob < o.prob
					? 1
					: this.prob > o.prob
							? -1
							: 0;
		}

		if (cmp == 0) {
			cmp = this.text.compareTo(o.text);
		}
		return cmp;
	}
}