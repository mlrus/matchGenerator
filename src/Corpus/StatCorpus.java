package Corpus;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocCollector;
import org.apache.lucene.store.LockObtainFailedException;

public class StatCorpus extends Corpus {

	public StatCorpus() throws CorruptIndexException, LockObtainFailedException, InstantiationException, IllegalAccessException,
			IOException {
		super();
	}

	public StatCorpus(final String path) throws CorruptIndexException, LockObtainFailedException, InstantiationException,
			IllegalAccessException, IOException {
		this(path, Constants.fieldNames, Constants.DEFAULTSEARCHFIELD);
	}

	StatCorpus(final String path, final List<String> fieldNames, final String defaultSearchfield) throws CorruptIndexException,
			LockObtainFailedException, InstantiationException, IllegalAccessException, IOException {
		super(path, fieldNames, defaultSearchfield);
	}

	public void report() {
		templateWriter.report();
	}

	public float getCProbOfSet(final Collection<String> groupOfWords, final Collection<String> arg) throws ParseException,
			CorruptIndexException, IOException {
		float ans;
		final Query termQuery = termsQuery(arg);
		final Filter termFilter = templateWriter.mkFilter(termQuery);
		final Query phraseQuery = phraseQuery(groupOfWords);
		final TopDocCollector filteredResult = templateWriter.search(phraseQuery, termFilter);
		final int numInSet = templateWriter.countElements(termFilter);
		final int numInGroup = filteredResult.getTotalHits();

		final float prob = numInSet == 0
				? 0
				: (float) numInGroup / numInSet;
		ans = prob;
		// if (biasWeights) {
		// final float gfactor = WordStats.probA(groupOfWords.size(), arg.size());
		// ans *= gfactor;
		// }
		return ans;
	}

}
