// This is unpublished source code. Michah Lerner 2006, 2007, 2008

package bayesMatchGenerator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.document.Document;

import util.coll.PairNC;
import util.coll.Sortable;
import util.data.StringMethodsIntl;
import Corpus.StatCorpus;

/**
 * Format keymatch results and send the output to a print stream.
 * 
 * @author Michah.Lerner
 * 
 */
public class KeymatchFormatter {
	static int numoutSymbols = 0, numoutNames = 0, numoutMatches = 0;

	/**
	 * Emit the results of generating keymatch
	 * 
	 * @param corpus
	 *            The document store from which the keymatches were generated
	 * @param resultByCompany
	 *            The results of the keymatch generation.
	 * @param out
	 *            The stream to get the keymatches
	 */
	@SuppressWarnings("unchecked")
	static public void emit(final StatCorpus docStats, final List<Sortable> resultByCompany, final PrintStream out) {
		if (out == null || resultByCompany == null) { return; }
		final List<String> resultList = new ArrayList<String>(); // to allow resorting so we drop duplicates
		// (specifically for the exactmatch heuristic)
		for (final Sortable sortedItem : resultByCompany) {

			final Document doc = (Document) sortedItem.o[0];
			final TextHypothesis hypothesis = (TextHypothesis) sortedItem.o[1];

			final String matchType = mkMatchtype(hypothesis);
			final String matchtext = StringMethodsIntl.mkString(hypothesis.getAsaList());
			Float prob = hypothesis.getProb();

			if (prob.isInfinite()) {
				prob = 1.1f;
			}
			if (prob.isNaN()) {
				prob = -0.0f;
			}

			if (prob.isInfinite() || prob.isNaN()) {
				System.err.println("number error on " + doc.toString());
				continue;
			}
			final String descCol = doc.get(Corpus.Constants.DESCRIPTION);
			final String symbol = doc.get(Corpus.Constants.URL);
			String symbolTail = symbol;
			if (symbolTail.matches("[a-zA-Z]+=[a-zA-Z]+[0-9]+_")) {
				final int truncAt = symbolTail.indexOf('_');
				symbolTail = symbolTail.substring(truncAt);
			}
			if (!util.Constants._dropIfEqualDescription || (!matchtext.equals(descCol) && !matchtext.equals(symbolTail))) {
				final String URLfrag = mkUrlFrag(symbol);
				final String r = lineFormatter(prob, matchtext, matchType, URLfrag, descCol);
				if (r != null) {
					resultList.add(r);
				}
				numoutMatches++;
			}
		}
		// resultList = cleanupDuplicates(resultList);
		for (final String res : resultList) {
			out.println(res);
		}
	}

	static List<String> cleanupDuplicates(final List<String> results) {
		final List<String> finalResults = new ArrayList<String>();
		String priorPrefix = "";
		Collections.sort(results);
		for (final String line : results) {
			final int pos = line.indexOf(',');
			final String currentPrefix = line.substring(pos + 1, line.indexOf(',', pos + 1));
			if (!currentPrefix.equalsIgnoreCase(priorPrefix)) {
				final String lineWithoutRankingCode = line.replaceAll("rank[0-9]+_", "");
				finalResults.add(lineWithoutRankingCode);
			} else if (util.Constants._showDroppedDuplicates) {
				System.out.println("drop dup: " + line);
			}
			priorPrefix = currentPrefix;
		}
		System.out.println("Cleanup: " + results.size() + " records in, " + finalResults.size() + " records out.");
		return finalResults;
	}

	/**
	 * Format a single line of key match, given the precise String texts to format
	 * 
	 * @param matchtext
	 *            the terms to match
	 * @param matchtype
	 *            the kind of match
	 * @param urlCol
	 *            the unique entity specification
	 * @param descCol
	 *            the general description of name of the item
	 * @return a formatted string giving the full keymatch entry
	 */
	static public String lineFormatter(final float prob, String matchtext, final String matchtype, final String urlCol,
			final String descCol) {
		if (matchtext == null || matchtype == null || urlCol == null || descCol == null) { return null; }
		if (!matchtext.contains("\\\\")) {
			matchtext = matchtext.replaceAll("\\\\", ""); // yes, second occurrence is in different context.
		}
		final String mtext = matchtext.trim();
		if (mtext.length() == 0) { return null; }
		return String.format("#%5.3f,%s,%s,%s,%s", prob, mtext, matchtype, urlCol, descCol);
	}

	/**
	 * Format a single line of key match, given the collections of words and the result PairNC
	 * 
	 * @param corpus
	 *            The document corpus from which this is drawn
	 * @param documentWordList
	 *            The words that a keymatch has been generated for
	 * @param ss
	 *            The keymatch specified as a phrase type and a (LinkedList) set of words to format
	 * @return a formatted string giving the full keymatch entry
	 */

	static public String mkLine(final StatCorpus docStats, final Document doc, final PairNC<MatchDescription, Collection<String>> ss) {
		if (doc == null || ss == null) { return null; }

		final String matchType = mkMatchtype(ss.S().getPhraseType());
		final float probOfMatch = ss.s().getProb();
		String matchtext = collect(ss.T());
		final String desc = doc.get(Corpus.Constants.DESCRIPTION);
		final String symbol = doc.get(Corpus.Constants.URL);
		final String URLfrag = mkUrlFrag(symbol);
		if (!matchtext.contains("\\\\")) {
			matchtext = matchtext.replaceAll("\\\\", "");
		}
		final String mtext = matchtext.trim();
		if (mtext.length() == 0) { return null; }
		return String.format("#%5.3f,%s,%s,%s,%s", probOfMatch, mtext, matchType, URLfrag, desc);
	}

	/**
	 * Obtain the vendor-specific match type that this result is valid for
	 * 
	 * @param match
	 *            a PairNC with a type and content
	 * @return the specific vendor specific content to emit
	 */
	static String mkMatchtype(final TextHypothesis hypothesis) {
		return mkMatchtype(hypothesis.getPhraseType());
	}

	/**
	 * Obtain the vendor specific match as a string, given the generic collection type of the phrase
	 * 
	 * @param p
	 *            the kind of generic result to emit
	 * @return the vendor specific result
	 */
	static String mkMatchtype(final PhraseType p) {
		switch (p) {
			case Sequence:
				return "InorderMatch";
			case Phrase:
				return "PhraseMatch";
			case Exact:
				return "ExactMatch";
			case Set:
			default:
				return "KeywordMatch";
		}
	}

	/**
	 * Give summary information
	 * 
	 * @param corpus
	 */
	public static void summarize(final StatCorpus corpus) {
		System.out.printf("%6d entities aggregated\n", corpus.numDocs());
		System.out.printf("%6d symbols emitted\n", numoutSymbols);
		System.out.printf("%6d names emitted\n", numoutNames);
		System.out.printf("%6d matches emitted\n", numoutMatches);
		System.out.printf("%6d total items emited\n", numoutSymbols + numoutNames + numoutMatches);
	}

	/**
	 * Format an URL fragment into a lookup string
	 * 
	 * @param s
	 *            a fragment
	 * @return a lookup string
	 */
	static String mkUrlFrag(final String s) {
		return s;
	}

	static String cleanupString(final String in) {
		return in.replaceAll(",", " ").replaceAll(" +", " ");
	}

	static String collect(final Collection<String> sc) {
		final StringBuffer sb = new StringBuffer();
		for (final String s : sc) {
			sb.append(s.trim() + " ");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	static public void emitLine(String matchtext, final String matchtype, final String urlCol, final String descCol,
			final PrintStream out) {
		if (matchtext == null || matchtype == null || urlCol == null || descCol == null) { return; }
		if (!matchtext.contains("\\\\")) {
			matchtext = matchtext.replaceAll("\\\\", "");
		}
		final String mtext = matchtext.trim();
		if (mtext.length() == 0) { return; }
		out.println(mtext + "," + matchtype.trim() + "," + urlCol.trim() + "," + descCol.trim());
	}
}
