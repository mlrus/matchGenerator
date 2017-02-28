/**
 * 
 */
package util.data;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.Numerics;
import bayesMatchGenerator.ChooseCombIF;
import docStore.DocumentStore;

/**
 * @author Michah.Lerner
 * 
 */
public class SummaryStats {

	List<String> argList;
	Set<Collection<String>> llsCollection;

	class Methods {
		DocumentStore ds;
		ChooseCombIF<String> chooser = new ChooseCombIF<String>();

		Methods(final DocumentStore ds) {
			this.ds = ds;
		}

		List<String> sortedCounts(final Collection<Collection<String>> lls) {
			final List<String> res = new ArrayList<String>();
			for (final Collection<String> ls : lls) {
				final int docsHaving = ds.countDocsHaving(ls);
				// final Double entropy = ds.getEntropy(ls);
				res.add(String.format("%06d %12.10f  %s", docsHaving, entropy, ls));
			}
			Collections.sort(res);
			return res;
		}

		List<String> scl2(final String[] ls) {
			return scl2(Arrays.asList(ls));
		}

		Boolean disjoint(final Collection<String> s1, final Collection<String> s2) {
			Collection<String> c1, c2;
			if (s1.size() < s2.size()) {
				c1 = s1;
				c2 = s2;
			} else {
				c1 = s2;
				c2 = s1;
			}
			for (final String s : c1) {
				if (c2.contains(s)) { return false; }
			}
			return true;
		}

		Collection intersect(final Collection<String> s1, final Collection<String> s2) {
			Collection<String> c1, c2;
			final Set<String> result = new HashSet<String>();
			if (s1.size() < s2.size()) {
				c1 = s1;
				c2 = s2;
			} else {
				c1 = s2;
				c2 = s1;
			}
			for (final String s : c1) {
				if (c2.contains(s)) {
					result.add(s);
				}
			}
			return result;
		}

		List<String> scl2(final List<String> ls) {
			final List<String> res = new ArrayList<String>();
			chooser.chooseCombIF(ls.toArray(new String[ls.size()]), true);
			while (chooser.hasNext()) {
				final Collection<String> subset = chooser.next();
				System.out.printf("  entropy=%12f  relativeEntropy=%12f  nTerms=%2d  %s\n", ds.getEntropy(subset), ds
						.getGroupEntropy(subset), subset.size(), subset);
			}
			Collections.sort(res);
			return res;
		}
	}

	public void describe(final DocumentStore ds1, final Collection<Integer> docIDs, final PrintStream out) {
		out.printf("%6s %30s \t %12s \t %s\n", "docID", "|FischerLogProb|", "entropy", "content");
		final List<String> l = new ArrayList<String>();
		for (final int docID : docIDs) {
			final ArrayList<String> docContent = ds1.getContent(docID);
			final ArrayList<String> wl2 = (ArrayList<String>) docContent.clone();
			Collections.sort(wl2);
			l.add(String.format("%12.10f \t %4d \t %s", ds1.getEntropy(docContent), docID, wl2));
		}
		Collections.sort(l);
		for (final String s : l) {
			out.println(s);
		}
	}

	public void exec(final String args[]) throws Exception {
		String inputCorpus = "-";
		String testInputs = "-";
		if (args.length > 0) {
			inputCorpus = args[0];
		}
		if (args.length > 1) {
			testInputs = args[1];
		}
		final DocumentStore ds1 = new DocumentStore(inputCorpus);
		e2(ds1, testInputs);
	}

	public void e2(final DocumentStore ds1, final String testInputs) throws Exception {
		final ChooseCombIF<String> choose = new ChooseCombIF<String>();
		describe(ds1, Numerics.range(0, 100), System.out);
		final Methods methods = new Methods(ds1);
		final BufferedReader r = IO.openInput(testInputs);
		llsCollection = new HashSet<Collection<String>>();
		do {
			if (testInputs.equals("-")) {
				System.out.print("?");
			}
			String line = r.readLine();
			if (line == null) {
				break;
			}
			line = line.trim();
			if (line.equals(".")) {
				break;
			}
			argList = Arrays.asList(line.split("\\s+"));
			choose.chooseCombIF(argList, true);
			while (choose.hasNext()) {
				final Collection<String> se = choose.next();
				final List<String> cl = new ArrayList<String>(ds1.getCoOccurringWords(se));
				Collections.sort(cl);
				System.out.println("H(" + se + ") \t= " + String.format("%12.10f", ds1.getEntropy(se)));
				final List<String> outputLines = new ArrayList<String>();
				for (final String co : cl) {
					final HashSet<String> mset = new HashSet<String>();
					mset.addAll(se);
					mset.add(co);
					final Collection<Integer> ans = ds1.getDocidsHavingAll(mset);
					if (ans != null && ans.size() > 0) {
						outputLines.add(String.format("%6d occurrences of %s", ans.size(), mset));
					}
				}
				Collections.sort(outputLines);
				for (final String s : outputLines) {
					System.out.println(s);
				}
			}

			final Collection<Collection<String>> lls = ds1.getWordsetsWithAny(argList);
			// ds1.getWordsetsWith(argList);
			llsCollection.addAll(lls);
			final List<String> ans = methods.sortedCounts(lls);
			for (final String s : ans) {
				System.out.println(s);
			}
		} while (true);
		if (ds1.expensiveTallies) {
			show(ds1.getWordsAsSetCount(), ds1);
			// DocDescriptiveStats.genFreqWord(ds1, System.out);
		}
	}

	public void show(final Map<Set<String>, Integer> map, final DocumentStore ds1) {
		System.out.printf("QUOTIENT_SCR    CNT   PHRASETROPY  LEN WORD_SET  {TERM_PROBS } [PHRASE_PROB  PROB_PRODUCT]\n");

		final List<String> res = new ArrayList<String>();

		final Double d = 1D / (1D * ds1.getCorpusWordcount());
		for (final Map.Entry<Set<String>, Integer> me : map.entrySet()) {
			final StringBuffer sb = new StringBuffer();
			sb.append(String.format("%6d (%12.10f) %3d %s  {", me.getValue(), ds1.getEntropy(me.getKey()), me.getKey().size(), me
					.getKey()));
			final Set<String> key = me.getKey();
			final Double docProb = d * ds1.countDocsHaving(key);
			Double probIfIndependent = 1D;
			for (final String s : key) {
				final Double pr = d * ds1.countDocsHaving(Collections.singletonList(s));
				sb.append(String.format("%35.2f ", pr));
				probIfIndependent *= pr;
			}
			sb.append(String.format("} [%12.10f %12.10f]", docProb, probIfIndependent));
			sb.insert(0, String.format("%16f ", docProb / probIfIndependent));
			// chooser.chooseCombIF(me.getKey().toArray(new String[me.getKey().size()]), true);
			// while(chooser.hasNext()) {
			// Set<String> next = chooser.next();
			// sb.append(String.format(" [%s %12.10f]",next,ds1.getEntropy(next)));
			// }
			res.add(sb.toString());
		}
		Collections.sort(res);
		for (final String s : res) {
			System.out.println(s);
		}
	}

	public static void main(final String[] args) throws Exception {
		final SummaryStats summaryStats = new SummaryStats();
		summaryStats.exec(args);
	}
}
