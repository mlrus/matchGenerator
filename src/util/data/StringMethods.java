package util.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class StringMethods {

	/*******************************************************************************************************************
	 * What looks like (in some editors): "book://guten/item13600,trait� �de la v�rit� de la religion chr�tienne" is
	 * actually "book://guten/item13600,trait� �de la v�rit� de la religion chr�tienne" and needs to get changed to
	 * "book://guten/item13600,trait� �de la v�rit� de la religion chr�tienne". This occurs for example with codepoints
	 * \217 and \351 [decimal pairs Changing 200 to 232, Changing \351 : 200 to 232 Changing \350 : 203 to 235 Changing
	 * \357 : 212 to 244
	 * 
	 * Changing � : 194 to 226; Changing � : 202 to 234; Changing � : 193 to 225; Changing � : 203 to 235; Changing � :
	 * 200 to 232; Changing � : 205 to 237; Changing � : 206 to 238; Changing � : 207 to 239; Changing � : 204 to 236;
	 * Changing � : 211 to 243; Changing � : 212 to 244; Changing � : 210 to 242; Changing � : 218 to 250; Changing � :
	 * 219 to 251; Changing � : 217 to 249
	 * 
	 * @param line
	 * @return
	 */
	public static String fixCodePoint(final String line) {
		final char[] chars = line.toCharArray();
		final char space = ' ';
		boolean changed = false;
		for (int i = 1; i < chars.length - 2; i++) {
			if (chars[i - 1] == space) {
				continue;
			}
			final boolean lc1 = Character.isLowerCase(Character.codePointAt(chars, i));
			final boolean lc2 = Character.isUpperCase(Character.codePointAt(chars, i + 1));
			if (lc1 && lc2) {
				chars[i + 1] = (char) Character.toLowerCase(Character.codePointAt(chars, i + 1));
				// Character.toLowerCase(chars[i + 1]);
				changed = true;
			}
		}
		return changed
				? new String(chars)
				: line;
	}

	public static boolean containsList(final String inContainer, final String inPhrase) {
		return inContainer.contains(inPhrase);
	}

	public static boolean containsList(final List<String> inContainer, final String inPhrase) {
		return inContainer.contains(inPhrase);
	}

	public static boolean containsList(final List<String> inContainer, final List<String> inPhrase) {
		if (inPhrase.size() == 0) { return true; }
		final String headWord = inPhrase.get(0);
		final List<String> phrase = inPhrase.subList(1, inPhrase.size());
		List<String> content = inContainer;
		int headPos;
		while ((headPos = content.indexOf(headWord)) >= 0) {
			content = content.subList(headPos + 1, content.size());
			final Iterator<String> phraseIterator = phrase.iterator();
			final Iterator<String> contentIterator = content.iterator();
			do {
				if (!phraseIterator.hasNext()) { return true; }
				if (!contentIterator.hasNext()) { return false; }
			} while (contentIterator.next().equals(phraseIterator.next()));
		}
		return false;
	}

	public static final String mkString(final Collection<String> col) {
		final StringBuffer sb = new StringBuffer();
		final Iterator<String> it = col.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext()) {
				sb.append(" ");
			}
		}
		final String str = sb.toString();
		final String form = StringMethodsIntl.cleanString(str);
		return form;
	}
}
