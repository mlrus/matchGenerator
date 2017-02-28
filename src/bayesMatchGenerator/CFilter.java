package bayesMatchGenerator;

import interfaces.Chooser;
import interfaces.FilterComb;
import interfaces.PredicateEvaluator;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import match.Fchar;
import util.Constants;
import util.coll.PairNC;
import util.data.StringMethods;

public class CFilter implements FilterComb {
	boolean debug = false;
	boolean subsume = true;
	boolean expandIncrementally = false;
	int maxSetSize = 3;
	final static float subsumptionThresholdSet = (float) (1f - 1e-6);
	final static float subsumptionThresholdPhrase = (float) (1e-6);
	final static float minProbAsSequence = (float) 1e-6;
	final static float minProbAsSet = Math.min(subsumptionThresholdSet, (float) 1e-6);
	final static float goldenRatio = 15f;
	final static float erosionRate = 0.95f;
	final static float cthresh = (float) (1.0f - 1e-3);
	final static int maxSizeToPermute = 4;
	final static int minPhraseLen = 2;

	PhraseTester activePhraseTester = (new PhraseTesters()).minProbPhraseTester;
	private final PredicateEvaluator<String> evaluator;
	final Chooser<String> chooser;
	SetInfo<String> setInfo;

	public CFilter(final PredicateEvaluator<String> evaluator) {
		this.chooser = new ChooseCombIF<String>();
		this.evaluator = evaluator;
		setInfo = new SetInfo<String>(evaluator);
	}

	public List<TextHypothesis> collectResults(final Collection<String> content) {
		return collectResults(content, activePhraseTester);
	}

	public List<TextHypothesis> collectResults(final Collection<String> content, final PhraseTester phraseTester) {

		final List<TextHypothesis> KWResults = new ArrayList<TextHypothesis>();
		final List<PairNC<Float, Set<String>>> sResult = new ArrayList<PairNC<Float, Set<String>>>();

		evaluator.newInput(content);
		final SetInfo<String>.SI si = setInfo.getSI(content);

		for (int i = 1; i <= maxSetSize; i++) {
			sResult.addAll(si.collectEst(i));
		}
		Collections.sort(sResult, new PairNC.SortCar<Float>());

		for (final PairNC<Float, Set<String>> p : sResult) {
			final float prob = evaluator.getFreqOfSet(p.t());
			final TextHypothesis hyp = new TextHypothesis(new MatchDescription(PhraseType.Set, prob, p.s()), p.t());
			KWResults.add(hyp);
		}

		final List<TextHypothesis> PHResults = expandAndTestPhrases(content, phraseTester);

		KWResults.addAll(PHResults);
		Collections.sort(KWResults);
		return KWResults;
	}

	Collection<String> dualContent(final Collection<String> fullContent, final Collection<String> selectedContent) {
		final Collection<String> dualContent = new HashSet<String>(fullContent);
		for (final String s : selectedContent) {
			dualContent.remove(s);
		}
		return dualContent;
	}

	/**
	 * Test each combination using the given PredicateEvaluator, keeping answers according to logic defined and
	 * implemented in the processAnswer routine, and eliminating candidates that contain a "Set" answer since "set" has
	 * the highest threshold. Subsume the candidate set by eliminating supersets of items that are accepted as sets with
	 * very high probability.
	 * 
	 * @param initParams
	 *            A vector of S (typically String) to evaluate
	 * @param tester
	 *            A PredicateEvaluator to tell if a subset is a sufficient representation of a set
	 * @return A list containing the kind of match (set, sequence, exact) and the text of the match
	 */

	public List<TextHypothesis> expandAndTestPhrases(final Collection<String> text) {
		return expandAndTestPhrases(text, activePhraseTester);
	}

	public List<TextHypothesis> expandAndTestPhrases(final Collection<String> content, final PhraseTester phraseTester) {
		final List<TextHypothesis> results = new ArrayList<TextHypothesis>();
		if (content.size() >= minPhraseLen) {
			final int[] spos = new int[content.size() + 1];
			final char[] ftext = mkSdelim(content, spos);
			final Fchar fChar = new Fchar(ftext);
			final List<ProbInfo> info = new ArrayList<ProbInfo>();

			// Generate phrases with size >= minPhraseLen
			for (int s = 0; s < spos.length - minPhraseLen; s++) {
				for (int e = s + minPhraseLen; e < spos.length; e++) {
					final Fchar delimitedWords = fChar.subSequence(spos[s] - 1, spos[e]);
					final BitSet bs = evaluator.getCorpus().msa.BSgetEntity(delimitedWords);
					if (bs.cardinality() == 0) {
						System.out.println(bs.cardinality() + " instances of [" + delimitedWords.toString() + "] in " + content);
					}
					final float prob = 1f / bs.cardinality();
					info.add(new ProbInfo(prob, (e - s), delimitedWords));
					if (debug) {
						System.out.print("add  " + delimitedWords.toString() + " since p = " + prob);
					}
					if (prob > cthresh) {
						if (debug) {
							System.out.println(" (Cease)");
						}
						break; // no need to look further for phrases that start at position s
					}
					if (debug) {
						System.out.println();
					}
				}
			}

			// Keep the items that remain after the phraseTester is done with them.
			if (info.size() > 0) {
				phraseTester.testPhrases(info);
				for (final ProbInfo pi : info) {
					final List<String> tokens = mkTokens(pi.text);
					results.add(new TextHypothesis(new MatchDescription(PhraseType.Phrase, pi.prob), tokens));
				}
			}
		}
		return results;
	}

	/*******************************************************************************************************************
	 * Deconstruct a char array into a collection of tokens
	 */

	List<String> mkTokens(final Fchar text) {
		final List<String> res = new ArrayList<String>();
		mkTokens(text, res);
		return res;
	}

	void mkTokens(final Fchar text, final Collection<String> res) {
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

	char[] mkSdelim(final Collection<String> content, final int[] spos) {
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

	void showtextBuf(final Fchar fChar, final int[] spos) {
		for (int i = 0; i < spos.length - 1; i++) {
			System.out.print(spos[i] + "..." + (spos[i + 1] - 2) + " [");
			System.out.print(fChar.subSequence(spos[i], spos[i + 1] - 1));
			System.out.println("]");
		}
	}

	boolean subsumablePhraseType(final PhraseType phraseType) {
		return subsume && Constants.subsumables.contains(phraseType);
	}

	void subsumeSet(final List<List<String>> candidates, final List<String> currentSet, final int fromPos) {
		for (int pos2 = fromPos; pos2 < candidates.size(); pos2++) {
			while (pos2 < candidates.size() && candidates.get(pos2).containsAll(currentSet)) {
				candidates.remove(pos2);
			}
		}
	}

	void subsumePhrase(final List<List<String>> candidates, final Collection<String> item, final int fromPos) {
		for (int pos2 = fromPos; pos2 < candidates.size(); pos2++) {
			while (pos2 < candidates.size() && StringMethods.containsList(candidates.get(pos2), (List<String>) item)) {
				candidates.remove(pos2);
			}
		}
	}

	/**
	 * Give the PhraseType that is most suitable for the item, and update the search space accordingly when the
	 * PhraseType is very general (i.e., a set).
	 * 
	 * @param answer
	 * @param currItem
	 * @param ansResults
	 * @param minPointItems
	 * @return Add the keymatch to the list of results, and update the list of minimal points if the answer is a set
	 *         lookup.
	 */
	PhraseType filterPhraseTypes(final PhraseType answer, final Set<String> currItem,
			final List<PairNC<PhraseType, Set<String>>> ansResults, final List<Set<String>> minPointItems) {
		if (answer != PhraseType.False) {
			ansResults.add(new PairNC<PhraseType, Set<String>>(answer, currItem));
		}
		if (answer == PhraseType.Set) {
			minPointItems.add(currItem);
		}
		return answer;
	}
}
