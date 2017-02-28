package match;

// interface Interval<S> extends Comparable<Interval<S>> {
interface Interval<S> extends Comparable<S> {

	public String toString();

	/*******************************************************************************************************************
	 * Negative if and only if this segment either starts earlier, or starts the same but is shorter.
	 */
	public int compareTo(final S o);

	/*******************************************************************************************************************
	 * Returns true if this segment intersects the other segment
	 * 
	 * @param o
	 *            The segment to compare with
	 * @return True if and only if this segment doesn't start after the other, and includes the start of the other
	 */
	public boolean intersects(final S o);

	/*******************************************************************************************************************
	 * True if and only if this segment starts at-or-before the other and ends at-or-after the other
	 * 
	 * @param o
	 *            The segment to compare with
	 * @return True if and only if this segment contains all of the other segment
	 */
	public boolean covers(final S o);

	/*******************************************************************************************************************
	 * True if and only if this segment ends before the other begins
	 * 
	 * @param o
	 *            The segment to compare with
	 * @return True if this segment strictly precedes the other
	 */
	public boolean before(final S o);

	/*******************************************************************************************************************
	 * True if and only if this segment starts after the other ends
	 * 
	 * @param o
	 * @return
	 */
	public boolean after(final S o);

}