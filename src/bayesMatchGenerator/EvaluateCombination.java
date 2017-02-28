// This is unpublished source code. Michah Lerner 2006, 2007, 2008
// This is unpublished source code. Michah Lerner 2007

package bayesMatchGenerator;

import interfaces.PredicateEvaluator;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;

import util.coll.BoundedLinkedHashMap;
import Corpus.Corpus;
import Corpus.StatCorpus;

/**
 * This provides the inner function for valuation of the phrases. The critical function is <code>foundP</code> which
 * evaluates the current <code>groupOfWords</code> relative to the terms it is invoked with.
 * 
 * @author Michah.Lerner
 * 
 */
public class EvaluateCombination implements PredicateEvaluator<String> {

	/**
	 * The groupOfWords is the item that we need to find keyword matches for
	 */
	protected List<String> fullWordSequence;

	/**
	 * The docStore is the document store we are iterating over
	 */
	protected StatCorpus corpus;

	/**
	 * Initialize fields, and invoke the rsp.initControls function to store values from the properties file.
	 * 
	 * @param corpus
	 */

	public EvaluateCombination(final StatCorpus corpus) {
		this.corpus = corpus;
	}

	public Corpus getCorpus() {
		return corpus;
	}

	/**
	 * Set the base case group of words. Multiple hypotheses will be evaluated to see if they match.
	 */
	public void newInput(final Collection<String> words) {
		this.fullWordSequence = (List<String>) words;
	}

	public List<String> getInput() {
		return this.fullWordSequence;
	}

	public float getCProbOfSet(final Collection<String> arg) {
		float prob;
		try {
			prob = corpus.getCProbOfSet(fullWordSequence, arg);
		} catch (final CorruptIndexException e) {
			e.printStackTrace();
			return 0F;
		} catch (final ParseException e) {
			e.printStackTrace();
			return 0F;
		} catch (final IOException e) {
			e.printStackTrace();
			return 0F;
		}
		return prob;
	}

	public float getCProbOfSet(final List<String> wordSequence, final Collection<String> arg) {
		float prob;
		try {
			prob = corpus.getCProbOfSet(wordSequence, arg);
		} catch (final CorruptIndexException e) {
			e.printStackTrace();
			return 0F;
		} catch (final ParseException e) {
			e.printStackTrace();
			return 0F;
		} catch (final IOException e) {
			e.printStackTrace();
			return 0F;
		}
		return prob;
	}

	public float getFreqOfSet(final Collection<String> words) {
		float prob = 0F;
		try {
			prob = 1f / corpus.countMatches(corpus.termsQuery(words));
		} catch (final CorruptIndexException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		return prob;
	}

	LinkedHashMap<Collection<String>, Integer> docMap = new BoundedLinkedHashMap<Collection<String>, Integer>();

	public int getCountOfSet(final Collection<String> words) {
		final Integer cnt = docMap.get(words);
		if (cnt != null) { return cnt; }
		Integer count = 0;
		try {
			count = corpus.countMatches(corpus.termsQuery(words));
		} catch (final CorruptIndexException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		docMap.put(words, count);
		return count;
	}

	public int getNumDocs() {
		return corpus.numDocs();
	}
}
