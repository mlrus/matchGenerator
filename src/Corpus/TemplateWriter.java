/**
 * 
 */
package Corpus;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.SetBasedFieldSelector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocCollector;
import org.apache.lucene.util.OpenBitSet;

import util.TimeLogger;

class TemplateWriter extends TemplateDocument {

	private final IndexWriter indexWriter;
	private IndexSearcher indexSearcher;
	private final TimeLogger timeLogger;
	private final QueryParser queryParser;

	public void report() {
		timeLogger.report();
	}

	TemplateWriter(final IndexWriter indexWriter, final QueryParser queryParser, final Iterable<String> fieldNames) {
		super(fieldNames);
		this.indexWriter = indexWriter;
		this.queryParser = queryParser;
		timeLogger = new TimeLogger();
	}

	void setInfoStream(final PrintStream printStream) {
		indexWriter.setInfoStream(printStream);
	}

	boolean addContent(final Iterable<String> values) {
		timeLogger.start("addContent");
		setValues(values);
		try {
			indexWriter.addDocument(doc);
			return true;
		} catch (final CorruptIndexException e) {
			e.printStackTrace();
			return false;
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			timeLogger.stop();
		}
	}

	TopDocCollector search(final Query query) throws CorruptIndexException, IOException {
		try {
			timeLogger.start("search(query)");
			final TopDocCollector collector = new TopDocCollector(Constants.ALLDOCUMENTS);
			getIndexSearcher().search(query, collector);
			return collector;
		} finally {
			timeLogger.stop();
		}
	}

	TopDocCollector search(final Query query, final Filter filter) throws CorruptIndexException, IOException {
		try {
			timeLogger.start("search(query,filter)");
			final TopDocCollector collector = new TopDocCollector(Constants.ALLDOCUMENTS);
			getIndexSearcher().search(query, filter, collector);
			return collector;
		} finally {
			timeLogger.stop();
		}
	}

	Set<Term> getTerms(final Query query) throws CorruptIndexException, IOException {
		try {
			timeLogger.start("getTerms(query)");
			final Query rewritten = query.rewrite(getIndexReader());
			final Set<Term> terms = new HashSet<Term>();
			rewritten.extractTerms(terms);
			return terms;
		} finally {
			timeLogger.stop();
		}
	}

	Filter mkFilter(final Query query) {
		try {
			timeLogger.start("mkFilter(query)");
			final Filter filter = new CachingWrapperFilter(new QueryWrapperFilter(query));
			return filter;
		} finally {
			timeLogger.stop();
		}
	}

	int countElements(final Filter filter) throws CorruptIndexException, IOException {
		try {
			timeLogger.start("countElements(filter)");
			final DocIdSet docIdSet = filter.getDocIdSet(getIndexReader());
			int cnt = 0;
			final DocIdSetIterator it = docIdSet.iterator();
			while (it.next()) {
				cnt++;
			}
			return cnt;
		} finally {
			timeLogger.stop();
		}
	}

	OpenBitSet mkDocIdSet(final TopDocCollector docs) {
		try {
			timeLogger.start("mkDocIdSet(docs)");
			final OpenBitSet docIdSet = new OpenBitSet();
			final ScoreDoc[] scoreDocs = docs.topDocs().scoreDocs;
			for (final ScoreDoc scoreDoc : scoreDocs) {
				docIdSet.set(scoreDoc.doc);
			}
			return docIdSet;
		} finally {
			timeLogger.stop();
		}
	}

	Document getDoc(final int docn, final Set<String> displayFields) throws CorruptIndexException, IOException {
		final FieldSelector fieldSelector = new SetBasedFieldSelector(displayFields, Collections.emptySet());
		return getIndexSearcher().doc(docn, fieldSelector);
	}

	Document getDoc(final int docn) throws CorruptIndexException, IOException {
		return getIndexSearcher().doc(docn, Constants.DEFAULTFIELDSELECTOR);
	}

	Iterator<Document> docIterator(final FieldSelector fieldSelector) {
		class DocIterator implements Iterator<Document> {
			int currDoc;
			IndexReader reader;
			FieldSelector fieldSelector1;

			DocIterator() {
				this(Constants.DEFAULTFIELDSELECTOR);
			}

			DocIterator(final FieldSelector fs) {
				currDoc = 0;
				this.fieldSelector1 = fs;
				try {
					reader = getIndexReader();
				} catch (final CorruptIndexException e) {
					e.printStackTrace();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}

			public boolean hasNext() {
				return currDoc < reader.maxDoc();
			}

			public Document next() {
				try {

					return reader.document(currDoc++, fieldSelector1);
				} catch (final CorruptIndexException e) {
					e.printStackTrace();
				} catch (final IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			public void remove() {
				// TODO Auto-generated method stub
			}
		}

		return new DocIterator();
	}

	private IndexSearcher getIndexSearcher() throws CorruptIndexException, IOException {
		try {
			timeLogger.start("getIndexSearcher()");
			if (indexSearcher == null) {
				indexWriter.commit();
				indexSearcher = new IndexSearcher(indexWriter.getDirectory());
				System.err.println("#doc=" + indexSearcher.getIndexReader().maxDoc());
			}
			return indexSearcher;
		} finally {
			timeLogger.stop();
		}
	}

	IndexReader getIndexReader() throws CorruptIndexException, IOException {
		try {
			timeLogger.start("getIndexReader()");
			return getIndexSearcher().getIndexReader();
		} finally {
			timeLogger.stop();
		}
	}

	Collection<String> mkCollection(final String string) {
		final List<String> res = new ArrayList<String>(1);
		res.add(string);
		return res;
	}

	// LinkedHashMap<Collection<String>, OpenBitSet> docMap = new BoundedLinkedHashMap<Collection<String>,
	// OpenBitSet>();
	//
	// public long countDocsWithSet(final Collection<String> words) throws ParseException, CorruptIndexException,
	// IOException {
	// timeLogger.start("countDocsWithSet()");
	// OpenBitSet docIdSet = docMap.get(words);
	// if (docIdSet == null) {
	// final List<OpenBitSet> wordsIn = new ArrayList<OpenBitSet>();
	// for (final String word : words) {
	// OpenBitSet dis = docMap.get(word);
	// if (dis == null) {
	// final BooleanQuery bq = new BooleanQuery(true);
	// final String t = word.replaceAll("[^a-zA-Z0-9 ]", "");
	// if (t.length() == 0) {
	// continue;
	// }
	// final Query q = queryParser.parse(t);
	// final Set<Term> qterms = new HashSet<Term>();
	// q.extractTerms(qterms);
	// if (qterms.size() == 0) {
	// continue;
	// }
	// bq.add(q, BooleanClause.Occur.MUST);
	// final TopDocCollector topDocCollector = search(bq);
	// dis = mkDocIdSet(topDocCollector);
	// wordsIn.add(dis);
	// docMap.put(mkCollection(word), dis);
	// }
	// }
	// final Iterator<OpenBitSet> setIterator = wordsIn.iterator();
	// if (setIterator.hasNext()) {
	// docIdSet = setIterator.next();
	// while (setIterator.hasNext()) {
	// docIdSet.and(setIterator.next());
	// }
	// docMap.put(words, docIdSet);
	// }
	// }
	// final long ans = docIdSet == null
	// ? 0
	// : docIdSet.cardinality();
	// timeLogger.stop();
	// return ans;
	// }
}