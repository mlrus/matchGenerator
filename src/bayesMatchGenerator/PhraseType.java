package bayesMatchGenerator;

public enum PhraseType {
	/**
	 * Phrase must precede Set in order to get expected aggregation behavior.
	 */
	Phrase,
	/**
	 * Exact is an ordered collection of items matched in its entirety
	 */
	Set,
	/**
	 * Sequence is an ordered collection of items, and allows any number of in-between gaps.
	 */
	Sequence,
	/**
	 * Phrase is an ordered continuous collection of items without any in-between gaps.
	 */

	Exact,
	/**
	 * False does not match anything
	 */
	False
}