// This is unpublished source code. Michah Lerner 2006, 2007, 2008

package util.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import util.data.StringMethods;

/**
 * Build a list of unique lines, split into a maximum of <code>colIndex</code> columns.
 */
public class ContentIterator {

	public Iterator<String> get(final String filename, final int... colIndex) {
		try {
			return get(new FileReader(filename), colIndex);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			return (new ArrayList<String>()).iterator();
		}
	}

	public Iterator<String> get(final File file, final int... colIndex) {
		try {
			return get(new FileReader(file), colIndex);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			return (new ArrayList<String>()).iterator();
		}
	}

	public Iterator<String> get(final Reader input, final int... colIndex) {
		return get(new BufferedReader(input), colIndex);
	}

	public Iterator<String> get(final BufferedReader input, final int... colIndex) {
		final int[] colInfo = colIndex.length != 0
				? colIndex
				: new int[] { 0, 1 };
		final Set<String> content = new HashSet<String>();
		int lineNo = 0;
		int maxCol = 0;
		for (final int c : colInfo) {
			maxCol = Math.max(maxCol, c);
		}
		try {
			while (input.ready()) {
				String line = input.readLine();
				lineNo++;
				if (line == null) {
					break;
				}
				if (line.length() == 0) {
					continue;
				}
				line = StringMethods.fixCodePoint(line);
				final String l[] = line.split(",", colInfo.length);
				if (maxCol >= l.length) {
					System.out.println("ERROR: Ignoring line with too few columns, lineno=" + lineNo + " : " + line);
					continue;
				}
				final StringBuffer contentLine = new StringBuffer();
				for (final int i : colInfo) {
					if (l.length >= i) {
						contentLine.append(l[colInfo[i]] + ",");
					}
				}
				contentLine.deleteCharAt(contentLine.length() - 1);
				content.add(contentLine.toString());
			}
			input.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return content.iterator();
	}
}