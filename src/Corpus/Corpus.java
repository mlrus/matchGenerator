package Corpus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import match.MSAstring;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;

import util.data.IO;

public class Corpus {

	final IndexWriter writer;
	final Analyzer analyzer;
	final QueryParser queryParser;
	final TemplateWriter templateWriter;
	Class<? extends Analyzer> analyzerClass = Constants.DEFAULTANALYZER;

	public MSAstring msa;

	Corpus() throws CorruptIndexException, LockObtainFailedException, InstantiationException, IllegalAccessException, IOException {
		this(new RAMDirectory(), Constants.fieldNames, Constants.DEFAULTSEARCHFIELD);
	}

	public Corpus(final String path) throws CorruptIndexException, LockObtainFailedException, InstantiationException,
			IllegalAccessException, IOException {
		this(path, Constants.fieldNames, Constants.DEFAULTSEARCHFIELD);
	}

	Corpus(final String path, final List<String> fieldNames, final String defaultSearchfield) throws CorruptIndexException,
			LockObtainFailedException, InstantiationException, IllegalAccessException, IOException {
		this(FSDirectory.getDirectory(path), fieldNames, defaultSearchfield);
	}

	Corpus(final Directory indexDir, final List<String> fieldNames, final String defaultSearchfield) throws InstantiationException,
			IllegalAccessException, CorruptIndexException, LockObtainFailedException, IOException {
		BooleanQuery.setAllowDocsOutOfOrder(true);
		analyzer = analyzerClass.newInstance();
		queryParser = new QueryParser(Constants.DEFAULTSEARCHFIELD, analyzer);
		writer = new IndexWriter(indexDir, analyzer, MaxFieldLength.UNLIMITED);
		writer.setRAMBufferSizeMB(Constants.DEFAULTRAMBUFFERSIZE);
		templateWriter = new TemplateWriter(writer, queryParser, fieldNames);
		templateWriter.setInfoStream(System.out);
		msa = new MSAstring();
	}

	public final void fillFromFile(final String filename) {
		final List<String> content = IO.readInput(filename, true);
		System.out.println("start processing content.");
		for (final String input : content) {
			this.addContent(input.split(",", 2));
		}
		System.out.println("start building suffix array : " + this.msa.hashCode());
		this.msa.buildMSA(content);
		System.out.println("ReadFromFile:: #records=" + this.writer.maxDoc());
	}

	boolean addContent(final Iterable<String> values) {
		final boolean result = templateWriter.addContent(values);
		return result;
	}

	boolean addContent(final String[] values) {
		return addContent(Arrays.asList(values));
	}

	TopDocCollector search(final String query) throws ParseException, CorruptIndexException, IOException {
		final Query q = queryParser.parse(query);
		return templateWriter.search(q);
	}

	String mkString(final Collection<String> arg) {
		final StringBuffer sb = new StringBuffer();
		final Iterator<String> it = arg.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext()) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	public int countMatches(final Query query) throws CorruptIndexException, IOException {
		return templateWriter.search(query).getTotalHits();
	}

	// public long countDocsWithSet(final Collection<String> words) {
	// if (words == null || words.size() == 0) { return 0; }
	// try {
	// return templateWriter.countDocsWithSet(words);
	// } catch (final CorruptIndexException e) {
	// e.printStackTrace();
	// } catch (final ParseException e) {
	// e.printStackTrace();
	// } catch (final IOException e) {
	// e.printStackTrace();
	// }
	// return 0;
	// }

	public Query termsQuery(final Collection<String> arg) throws ParseException {
		final BooleanQuery bq = new BooleanQuery(true);
		for (final String term : arg) {
			final String t = term.replaceAll("[^a-zA-Z0-9 ]", "");
			if (t.length() == 0) {
				continue;
			}
			final Query q = queryParser.parse(t);
			final Set<Term> qterms = new HashSet<Term>();
			q.extractTerms(qterms);
			if (qterms.size() == 0) {
				continue;
			}
			bq.add(q, BooleanClause.Occur.MUST);
		}

		return bq;
	}

	Query phraseQuery(final Collection<String> arg) throws ParseException, CorruptIndexException, IOException {
		final PhraseQuery pq = new PhraseQuery();
		pq.setSlop(0);
		final Set<Term> terms = templateWriter.getTerms(queryParser.parse(mkString(arg)));
		for (final Term term : terms) {
			pq.add(term);
		}
		return pq;
	}

	@SuppressWarnings("unchecked")
	List<String> getDocFields(final int docn) throws CorruptIndexException, IOException {
		final List<String> res = new ArrayList<String>(Constants.displayFields.size());
		final Document doc = templateWriter.getDoc(docn);
		for (final Fieldable fi : (Iterable<Fieldable>) doc.getFields()) {
			res.add(fi.stringValue());
		}
		return res;
	}

	public int numDocs() {
		try {
			return writer.numDocs();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public Iterator<Document> documentIterator() {
		return templateWriter.docIterator(Constants.DEFAULTFIELDSELECTOR);
	}
}
