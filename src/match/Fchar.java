package match;

import java.util.Collection;

public class Fchar extends Cseq {
	final char[] ch;
	int from, to;

	public Fchar(final char[] ch) {
		this(ch, 0, ch.length);
	}

	public Fchar(final String s) {
		this(s.toCharArray(), 0, s.length());
	}

	Fchar(final char[] ch, final int from, final int to) {
		this.ch = ch;
		this.from = from;
		this.to = to - 1;
	}

	@Override
	public char charAt(final int index) {
		return ch[from + index];
	}

	@Override
	public int length() {
		return to - from + 1;
	}

	@Override
	public Fchar subSequence(final int nfrom, final int nto) {
		return new Fchar(ch, this.from + nfrom, this.from + nto);
	}

	@Override
	public String toString() {
		// return to - from + 1 + "::" + String.valueOf(ch, from, (to - from) + 1);
		return String.valueOf(ch, from, (to - from) + 1);
	}

	/*******************************************************************************************************************
	 * Deconstruct a char array into a collection of tokens
	 */

	public static void mkTokens(final Fchar text, final Collection<String> res) {
		int pos = 0, spos;
		final int len = text.length();
		do {
			while (pos < len && text.charAt(pos) == ' ') {
				pos++;
			}
			spos = pos;
			while (pos < len && text.charAt(pos) != ' ') {
				pos++;
			}
			if (pos < len) {
				res.add(text.subSequence(spos, pos).toString());
			}
		} while (pos < len);
	}

	/*******************************************************************************************************************
	 * Construct a char array and index offset vector from the text by blank-padding each element of text
	 * 
	 * @param content
	 *            A collection of strings that should be stored as a blank-padded char array.
	 * @param spos
	 *            A preallocated vector that should contain the start offset for each string in text.
	 * @return Build a char[] from space-padded text, and store the starting position of each text entry.
	 */

	public static char[] mkSdelim(final Collection<String> content, final int[] spos) {
		int len = 1;
		if (spos.length != content.size() + 1) {
			System.err.println("Error on invocation: arg2 should be length text.size()+1");
		}
		for (final String s : content) {
			len += s.length() + 1;
		}
		final char[] ftext = new char[len];
		int pos = 0;
		int sposIndex = 0;
		ftext[pos++] = ' ';
		for (final String s : content) {
			spos[sposIndex++] = pos;
			final int l = s.length();
			s.getChars(0, l, ftext, pos);
			pos += l;
			ftext[pos++] = ' ';
		}
		spos[content.size()] = pos;
		return ftext;
	}

	static void showtextBuf(final Fchar fChar, final int[] spos) {
		for (int i = 0; i < spos.length - 1; i++) {
			System.out.print(spos[i] + "..." + (spos[i + 1] - 2) + " [");
			System.out.print(fChar.subSequence(spos[i], spos[i + 1] - 1));
			System.out.println("]");
		}
	}

}