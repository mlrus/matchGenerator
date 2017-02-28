// This is unpublished source code. Michah Lerner 2006, 2007, 2008

package util.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Common IO routines, ensuring safe open that does not overwrite an existing file. Will lowercase but not Americanize
 * input.
 * 
 * @author Michah.Lerner
 * 
 */
public class IO {
	@SuppressWarnings("unused")
	private final static Boolean DEV = true;

	public static List<String> readInput(final String fn) {
		return readInput(fn, false);
	}

	public static List<String> readInput(final String fn, final boolean toLowerCase) {
		BufferedReader q = null;
		final List<String> lines = new ArrayList<String>();
		try {
			q = openInput(fn);
			do {
				final String s = q.readLine();
				if (s == null || s.length() == 0) {
					continue;
				}
				lines.add(toLowerCase ? s.toLowerCase(Locale.getDefault()) : s);
			} while (q.ready());
		} catch (final Exception e) {
			e.printStackTrace();
		}
		try {
			if (q != null) {
				q.close();
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return lines;
	}

	public static boolean checkOutputfile(final String name, final boolean deletableOutputfile) {
		final File f = new File(name);
		if (!f.exists() || deletableOutputfile && f.isFile() && f.delete()) {
			return true;
		}
		return false;
	}

	static boolean stdFilename(final String filename) {
		return filename == null || filename.length() == 0 || filename.equalsIgnoreCase("-");
	}

	static PrintStream fileFoundHandler(final String s) throws FileNotFoundException {
		throw new FileNotFoundException("File exists: " + (new File(s)).getAbsolutePath());
	}

	public static BufferedReader openInput(final String filename) throws Exception {
		try {
			return new BufferedReader((stdFilename(filename) ? new InputStreamReader(System.in) : new FileReader(filename)));
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	static public PrintStream safePrintStream() throws FileNotFoundException {
		return safePrintStream(null);
	}

	static public PrintStream safePrintStream(final String filename) throws FileNotFoundException {
		// System.out.println("stdFilename("+filename+")?="+stdFilename(filename));
		if (DEV) {
			checkOutputfile(filename, true);
		}
		if (filename.equals("/dev/null")) {
			return new PrintStream(filename);
		}
		if (filename.equals("")) {
			try {
				return new PrintStream(System.out, true, "UTF-8");
			} catch (final UnsupportedEncodingException e) {
				e.printStackTrace();
				return new PrintStream(System.out, true);
			}
		}
		System.out.println("Checking to be sure there is not already a file named \"" + (new File(filename)).getAbsolutePath()
				+ "\"");
		return stdFilename(filename) ? new PrintStream(System.out) : !new File(filename).exists()
				? new PrintStream(filename)
				: fileFoundHandler(filename);
	}

	static public PrintStream closedSafePrintStream(final String filename) {
		PrintStream res = null;
		try {
			res = safePrintStream(filename);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return res;
	}

}
