//This is unpublished source code. Michah Lerner 2006, 2007, 2008

package interfaces;

import java.util.Collection;

/**
 * Generalized combination generation, used only for high-level statistical summary
 * @author Michah.Lerner
 *
 */
public interface CGen<T> {
	/**
	 * Are there more possibilities to iterator over
	 * @return true if there are more possibilities
	 */
	boolean hasNext();
	/**
	 * Get the next item, storing into the collection provided
	 * @param resultVector the collection to receive the result
	 * @return the resultVector with items added to it
	 */
	Collection<T>next(Collection<T>resultVector);
	Collection<T>next();	
	Long nextCombID();
	Long maxCombID();
	String nextAsString(Collection<T>resultVector);
	String nextAsString();

	// padded versions (should be optional)
	Collection<T>next(Collection<T>resultVector, T pad);
	Collection<T>next(T pad);	
	String nextAsString(Collection<T>resultVector, T pad);
	String nextAsString(T pad);
	long count();
}