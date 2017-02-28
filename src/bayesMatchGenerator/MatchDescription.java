package bayesMatchGenerator;

import interfaces.MatchInfoIF;

public class MatchDescription implements MatchInfoIF, Comparable<MatchInfoIF> {

	PhraseType phraseType;
	float prob;
	float estimate;

	public PhraseType getPhraseType() {
		return phraseType;
	}

	public float getProb() {
		return prob;
	}

	public float getEstimate() {
		return estimate;
	}

	public void setEstimate(final float estimate) {
		this.estimate = estimate;
	}

	public void setProb(final float prob) {
		this.prob = prob;
	}

	@Override
	public String toString() {
		return String.format("%6s@%5.3f [?%6.4f]", phraseType.toString(), prob, estimate);
	}

	MatchDescription(final PhraseType phraseType, final Number prob) {
		this.phraseType = phraseType;
		this.prob = prob.floatValue();
		this.estimate = this.prob;
	}

	MatchDescription(final PhraseType phraseType, final float prob, final float estimate) {
		this.phraseType = phraseType;
		this.prob = prob;
		this.estimate = estimate;
	}

	/*******************************************************************************************************************
	 * This assumes the phraseType enum is developed with "preferred" values earlier. Specifically, we require that the
	 * Phrase precede the Set in order to remove the set when the phrase has already appeared.
	 */

	public int compareTo(final MatchInfoIF o) {
		int cmp = phraseType.compareTo(o.getPhraseType());
		if (cmp == 0) {
			cmp = prob < o.getProb()
					? -1
					: prob > o.getProb()
							? 1
							: 0;
		}
		return cmp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((phraseType == null)
				? 0
				: phraseType.hashCode());
		result = prime * result + Float.floatToIntBits(prob);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final MatchDescription other = (MatchDescription) obj;
		if (phraseType == null) {
			if (other.phraseType != null) { return false; }
		} else if (!phraseType.equals(other.phraseType)) { return false; }
		if (Float.floatToIntBits(prob) != Float.floatToIntBits(other.prob)) { return false; }
		return true;
	}
}
