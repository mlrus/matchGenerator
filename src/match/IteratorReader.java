/**
 * 
 */
package match;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Iterator;

/***********************************************************************************************************************
 * This class builds a Reader from a string iterator. This allows Reader to be used ubiquitously
 * 
 * @author mlrus
 * 
 */

class IteratorReader extends Reader {
	Iterator<String> it;

	IteratorReader(final Iterator<String> it) {
		this.it = it;
	}

	@Override
	public int read(final CharBuffer cb) throws IOException {
		if (it.hasNext()) {
			final String s = it.next();
			final int n = s.length();
			cb.put(s, 0, n);
			return n;
		}
		return -1;
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public int read(final char[] cbuf, final int off, final int len) throws IOException {
		if (it.hasNext()) {
			final String s = it.next();
			final int l = Math.min(len, s.length());
			for (int i = off; i < l; i++) {
				cbuf[i] = s.charAt(i - off);
			}
			cbuf[l] = '\n';
			return l + 1;
		}
		return -1;
	}
}