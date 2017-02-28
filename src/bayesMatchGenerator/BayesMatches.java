// This is unpublished source code. Michah Lerner 2006, 2007, 2008

package bayesMatchGenerator;

// run with args: -prop C:\mlrus\config.001.xml -DshowAsGened=false -DshowZeros=false -Dexplain=false -DshowAsGened=true
// -DqueryFile=c:\temp\fourInputLines.txt -DtraceLevel=0 C:/TEMP/content.2col -

import interfaces.FilterComb;
import interfaces.PredicateEvaluator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import util.coll.PairNC;
import util.coll.Sortable;
import util.data.IO;
import Corpus.StatCorpus;

/**
 * @author Michah.Lerner
 * 
 */

public class BayesMatches extends KWIndexProcessor {

	protected StatCorpus corpus;
	private FilterComb filterComb;
	PredicateEvaluator<String> evaluator;
	public Boolean debug = false;

	float MAXBIAS = 0.5f;

	/**
	 * Configuration variables showStats sets whether to show statistics.
	 */
	public static Boolean showStats = false;

	/**
	 * Configuration variables suppressMatchGeneration suppresses output to allow just statistical computing.
	 */
	public static Boolean suppressMatchGeneration = false;

	/**
	 * Configuration variables allowIterative enables the iterative processing of choices, which saves considerable
	 * space for large word groups, but runs slower for smaller groups. A good tradeoff happens at about seven or eight
	 * elements.
	 */
	public static Boolean allowIterative = true;

	public static float minProb = 0.125f;

	BayesMatches() {
		// empty
	}

	BayesMatches(final String[] args) {
		processArgs(args);
	}

	class RecentlyUsed {
		Set<Set<String>> storedMatches = new HashSet<Set<String>>();

		void add(final Set<String> s) {
			storedMatches.add(s);
		}

		boolean isRedundant(final Set<String> s) {
			for (final Set<String> alreadyMatched : storedMatches) {
				if (s.containsAll(alreadyMatched)) { return true; }
			}
			return false;
		}
	}

	public static final String allwordFormat = "%-60s";
	public static final String lineFormat = "%s ! %-50s ! %5.3f ! %5.3f ! %5.3f\n";
	public static final String docFormat = String.format("%-80s ! %-70s ! %5s | %5s | %s", "fullText", "subText", "raw", "bias",
			"mixed");

	interface Doc {
		List<String> getWordlist();
	}

	/*******************************************************************************************************************
	 * Main routine.
	 * 
	 * @param docIterator
	 * @return
	 */
	public List<Sortable> computeProbabilities(final Iterator<Document> docIterator) {
		final List<Sortable> resultsList = new ArrayList<Sortable>();
		if (debug) {
			System.out.println(docFormat);
			System.out.println(docFormat.replaceAll(".", "="));
		}
		while (docIterator.hasNext()) {
			final Document doc = docIterator.next();
			final List<String> dl = Arrays.asList(doc.get(Corpus.Constants.DESCRIPTION).split(" "));
			final String s1 = String.format(allwordFormat, dl);

			final Collection<TextHypothesis> res = filterComb.collectResults(dl);
			final InvertingMap<TextHypothesis, Collection<String>> invm = new InvertingMap<TextHypothesis, Collection<String>>(res,
					TextHypothesis.accessor());

			for (final TextHypothesis hyp : res) {
				final Collection<String> asaSet = hyp.getAsaSet();
				final Collection<String> cmp = complement(dl, asaSet);
				final TextHypothesis th = invm.get(cmp);

				final float bias = th == null
						? 0
						: th.getProb();
				final float biasedProb = hyp.getProb() + MAXBIAS * bias * (1 - hyp.getProb());

				if (debug) {
					System.out.printf(lineFormat, s1, hyp.getAsaList(), hyp.getProb(), bias, biasedProb);
				}
				hyp.setProb(biasedProb);

			}
			if (res.size() == 0) {
				res.add(new TextHypothesis(new MatchDescription(PhraseType.Sequence, 0.5f), Arrays.asList(doc.get(
						Corpus.Constants.DESCRIPTION).split(" "))));
			}

			final RecentlyUsed checkIfUsed = new RecentlyUsed();
			for (final TextHypothesis hypothesis : res) {
				final Set<String> currSet = hypothesis.getAsaSet();
				if (!checkIfUsed.isRedundant(currSet)) {
					if (hypothesis.getProb() >= minProb) {
						resultsList.add(new Sortable(doc, hypothesis));
						checkIfUsed.add(currSet);
						if (debug) {
							System.out.println("USE:  " + hypothesis.toString());
						}
					}
				} else {
					if (debug) {
						System.out.println("SKIP: " + hypothesis.toString());
					}
				}
			}
		}
		Collections.sort(resultsList);
		return resultsList;
	}

	/*******************************************************************************************************************
	 * Method dualContent gives the dual of the selected content within the full content
	 * 
	 * @param fullContent
	 *            An instance of content
	 * @param selectedContent
	 *            A selection from the instance
	 * @return The complement of selectedContent in the domain fullContent
	 */
	Collection<String> complement(final Collection<String> fullContent, final Collection<String> selectedContent) {
		final Collection<String> dualContent = new HashSet<String>(fullContent);
		for (final String s : selectedContent) {
			dualContent.remove(s);
		}
		return dualContent;
	}

	/**
	 * Make a keymatch of the default type, which is defined here as a sequence of items.
	 * 
	 * @param words
	 * @return a PairNC consisting of a label (the phrase type) and the words (as set of strings)
	 */
	PairNC<MatchDescription, Collection<String>> mkDefaultKM(final Collection<String> words) {
		return new PairNC<MatchDescription, Collection<String>>(new MatchDescription(PhraseType.Sequence, 0.5f),
				!(words instanceof Set)
						? new LinkedHashSet<String>(words)
						: (Set<String>) words);
	}

	public void summarizeConfiguration() {
		System.out.println("============ configuration =================");
		System.out.println("infile :" + new File(getInfile()).getAbsolutePath());
		if (outFilename != null) {
			System.out.println("outfile:" + new File(outFilename).getAbsolutePath());
		}
		System.out.println("traceLevel:" + traceLevel);
		System.out.println("stats  :" + showStats);
		System.out.println("threshold_isaSet = " + threshold_isaSet);
		System.out.println("threshold_isaSequence = " + threshold_isaSequence);
		System.out.println("threshold_isaExactmatch =  " + threshold_isaExactmatch);
		System.out.println("subsume=" + subsume);
		System.out.println("adjustifShortWords=" + adjustifShortWords);
		System.out.println("explain=" + explain);
		System.out.println("-------------------");
	}

	public void summarizeResults() {
		System.out.println("\n\nProcessing Summary");
		summarizeConfiguration();
		KeymatchFormatter.summarize(corpus);
	}

	public static void usage() {
		System.out.println("Usage:  java -jar bayesGen [options] inputName outputName");
		System.out
				.println("  inputName with prefix \"_doc\" is special -- says use first expander of <entry key=\"indexNames\"> for input document.");
		System.out.println("  options are  [-prop [propertyFile]] -v[erbose] [-explain] [-st[atistics]]");
		System.out.println("               [-Dproperty=value]");
		throw new RuntimeException("Check usage and retry.");
	}

	/*
	 * The finishInitialization function is a convenience to allocate and prepare the standard items needed for
	 * processing.
	 */
	void finishInitialization() throws CorruptIndexException, LockObtainFailedException, InstantiationException,
			IllegalAccessException, IOException {
		configureFromPropertyfile(propertyFilename);
		out = IO.safePrintStream(outFilename);
		corpus = new StatCorpus();
		corpus.fillFromFile(getInfile());
		evaluator = new EvaluateCombination(corpus);
		filterComb = new CFilter(evaluator);
	}

	/**
	 * Standard placeholder for processing.
	 * 
	 * The doOptionalProcessing will produce, in this case, a list of the words that occur once in the 'small' content
	 * and how many times the word occurs in the the 'large' file. This is usefl because, for example, Campbell occurs
	 * only once in the top N but many times in the full list. The computeProbasbilities iterates over the content to
	 * obtain the accepted results for each document. These get sorted by symbol and then emitted in the format give by
	 * the KeymatchFormatter.
	 */
	public List<Sortable> process() {

		final Iterator<Document> docIterator = corpus.documentIterator();
		final List<Sortable> allProbResults = computeProbabilities(docIterator);
		return allProbResults;
	}

	public void emit(final List<Sortable> results) {
		KeymatchFormatter.emit(corpus, results, out);
	}

	/**
	 * Wrapper routine, configures, gets input and does processing.
	 * 
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws LockObtainFailedException
	 * @throws CorruptIndexException
	 */
	public void processKwIndices() throws CorruptIndexException, LockObtainFailedException, InstantiationException,
			IllegalAccessException, IOException {
		finishInitialization();
		summarizeConfiguration();
		final List<Sortable> results = process();
		emit(results);
		summarizeResults();
		out.close();
	}

	public static void main(final String args[]) throws CorruptIndexException, LockObtainFailedException, InstantiationException,
			IllegalAccessException, IOException {
		final BayesMatches processor = new BayesMatches();
		processor.processArgs(args);
		long time = System.currentTimeMillis();
		processor.processKwIndices();
		time = System.currentTimeMillis() - time;
		System.out.println("tElapsed=" + time + " ms.");
		processor.corpus.report();

		System.out.println();
	}
}
