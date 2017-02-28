// This is unpublished source code. Michah Lerner 2006, 2007, 2008

package util.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Constants;

/**
 * <b>Class Tkn</b> provides several features. These are (1) simple US domain tokenization, (2) prefix/suffix removal,
 * (3) escaping of characters that will break many query parsers, (4) conditional replacement of non USAscii with the
 * closest graphically similar US character (controlled by , <code>Constants.AmericanizeDyacritics</code>) (5) remove
 * repetitions of a token.
 * 
 * <br>
 * <br>
 * <em>For example,</em> "see spot's spots run on spot when he runs" becomes [see, spot, spots, run, on, when, he,
 * runs]<br/> The ampersand and hyphen are limited word characters as they may occur after any word character. This is
 * important because the ampersand and hyphen may appear embedded within a compant name.
 * 
 * <br>
 * <br>
 * <B>Tokens</B> match the regular expression <code>_patternString</code>, which consists of one or more word
 * characters (other than the underscore) and it also allows a hyphens or ampersand after any of the word characters.
 * That is, it does not split on embedded hyphen or ampersand.
 * 
 * <br>
 * <br>
 * <B>Prefix/Suffix</B> removal gets rid of trailing apostrophe-s or s-apostrophe, as well as leading d-apostrope,
 * D-apostrophe, l-apostrophe or L-apostrophe. <br>
 * <br>
 * <em>Important note </em>Prefix removal of L-apostrophe modifies the company name <u>L'Oreal</u>; it becomes simply
 * <u>Oreal</u>.
 * 
 * @author Michah.Lerner
 * 
 */
public class Tkn {

	final static char[] _escapables = new char[] {};
	// { '\\', '+', '-', '&', '|', '!', '(', ')', '{', '}', '[', ']', '^', '"', '~', '*','?', ':' };
	final static char[] _dropables = new char[] { '(', ')', '"', '\\' };
	// final static String _patternString = "(:?[1-9]([0-9]*,?)([0-9]+,?)*)|(:?[\\w&&[^_]]+[-&']?)+";
	final static String _patternString = "(:?[1-9]([0-9]*,?)([0-9]+,?)*)|(:?[\\p{L}0-9]+[-&']?)+";

	static char[] escapables;
	static char[] dropables;
	static String patternString;
	static String escStr;
	static String dropStr;

	static final String dropPatternString = "\\(|\\)|'|\"|\\\\";
	static final int phraseLength = 5; // span phraseLength + numArgs
	static final int minTokenLen = 1; // tokens must at least this long

	Pattern tokenPattern;

	public Tkn(final char[] escapables, final char[] dropables, final String patternString) {
		Tkn.escapables = escapables;
		Tkn.dropables = dropables;
		Tkn.patternString = patternString;
		Tkn.escStr = String.copyValueOf(escapables);
		Tkn.dropStr = String.copyValueOf(dropables);
		tokenPattern = Pattern.compile(patternString);
	}

	public Tkn() {
		this(_escapables, _dropables, _patternString);
	}

	public Tkn(final char[] escapables) {
		this(escapables, _dropables, _patternString);
	}

	public Tkn(final char[] escapables, final char[] dropables) {
		this(escapables, dropables, _patternString);
	}

	public Tkn(final String patternString) {
		this(_escapables, _dropables, patternString);
	}

	/**
	 * This <em>b</em> function does five things (1) Americanized accented characters, (2) removes four special cases
	 * of prefix (L' l' D' d'), (3) removes two special cases of suffix ('s s'), (4) tokens around non-word characters,
	 * and (5) removes repetitions of any token
	 * 
	 * @param s
	 *            the text to clean and tokens
	 * @return List of non-prefixed/suffixed tokens with substitutions made for accented characters
	 */
	public ArrayList<String> z(final String in) {
		String s = in.replaceAll("-", " ");
		// final String input = (Constants.AmericanizeDyacritics)
		// ? CopyClean.stringCleaner(s)
		// : s;
		final List<String> ans = new ArrayList<String>();
		// TOTO: move these definitions to the Constants.java
		final String[] suffixes = new String[] { "'s", "s'" };
		final String[] prefixes = new String[] { "l'", "L'", // NOTE: This affect company name "L'oreal" as well
				"d'", "D'" };

		if (Constants.removeCommonPunctuation) {
			for (final String sf : suffixes) {
				final String pat = sf + " ";
				while (s.contains(pat)) {
					s = s.replace(pat, " ");
				}
				if (s.endsWith(sf)) {
					s = s.substring(0, s.length() - sf.length());
				}
			}
			for (final String sf : prefixes) {
				final String pat = " " + sf;
				while (s.contains(pat)) {
					s = s.replace(pat, " ");
				}
				if (s.startsWith(sf)) {
					s = s.substring(sf.length());
				}
			}
		}

		final Matcher m = tokenPattern.matcher(s);
		while (m.find()) {
			final String st = s.substring(m.start(), m.end());
			if (st.length() >= minTokenLen) {
				ans.add(st);
			}
		}
		return Constants.processPhraseAsSet
				? uniqueAndClean(ans)
				: clean(ans);
	}

	/**
	 * Returns the tokens as an array instead of a list, see function <code>z</code> in this class.
	 * 
	 * @param s
	 *            Text to tokenize
	 * @return Array of strings
	 */
	public String[] za(final String s) {
		final List<String> ans = z(s);
		return ans.toArray(new String[ans.size()]);
	}

	/**
	 * Drop forbidden characters, and escape escapable characters.
	 * 
	 * @param s
	 * @return cleaned up string
	 */
	public String clean(final String s) {
		final char[] ch = s.toCharArray();
		final StringBuffer sb = new StringBuffer();
		for (final char c : ch) {
			if (dropStr.indexOf(c) >= 0) {
				continue;
			}
			if (escStr.indexOf(c) >= 0) {
				sb.append("\\");
			}
			sb.append(c);
		}
		final String ans = sb.toString().replaceAll("(^,)|(,$)", "").trim();
		return ans;
	}

	public ArrayList<String> clean(final List<String> arg) {
		final ArrayList<String> cleanArgs = new ArrayList<String>();
		for (final String s : arg) {
			final String st = clean(s);
			if (st.length() > 0) {
				cleanArgs.add(st);
			}
		}
		return cleanArgs;
	}

	/**
	 * Drop forbidden characters, escape escapable characters, and also drop repetitions of words.
	 * 
	 * @param arg
	 *            a list of strings to process
	 * @return a new list of strings, with the same sequence but without repetitions of words
	 */
	public ArrayList<String> uniqueAndClean(final List<String> arg) {
		final Set<String> cleanArgs = new HashSet<String>();
		for (final String s : arg) {
			final String st = clean(s);
			if (st.length() > 0) {
				cleanArgs.add(st);
			}
		}
		return new ArrayList<String>(cleanArgs);
	}

	public static void main(final String[] args) {
		final Tkn tkn = new Tkn();
		final boolean toLowerCase = true;
		for (final String fn : args) {
			final List<String> input = IO.readInput(fn, toLowerCase);
			for (final String query : input) {
				for (final String tk : tkn.z(query)) {
					System.out.println(tk);
				}
			}
		}
	}
	// query = "see spot's spots run on spot when he runs";
	// System.out.println(tkn.z(query));
	// query = "123,456";
	// System.out.println(tkn.z(query));
	// query = "first 1,000,000,000,010,4 second 02,000,000,000,010,7 third";
	// System.out.println(tkn.z(query));

}
