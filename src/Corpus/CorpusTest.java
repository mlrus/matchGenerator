package Corpus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.BitSet;
import java.util.Iterator;

import match.StringEntry;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocCollector;
import org.junit.Before;
import org.junit.Test;

public class CorpusTest {

	String[][] testData = new String[][] {
			new String[] { "1", "Merrill Lynch First Franklin Mortgage Loan Trust, Series 200", "" },
			new String[] { "2", "J.P. Morgan Chase Commercial Mortgage Securities Trust 2007-", "" },
			new String[] { "3", "BLACKROCK PREFERRED & CORPORATE INCOME STRATEGIES FUND, INC.", "" },
			new String[] { "4", "Eaton Vance Tax-Managed Global Diversified Equity Income Fun", "" },
			new String[] { "5", "VALLEY FORGE LIFE INSURANCE CO VARIABLE LIFE SEPARATE ACCOUN", "" },
			new String[] { "6", "Bear Stearns Commercial Mortgage Securities Trust 2007-TOP28", "" },
			new String[] { "7", "Greenwich Capital Commercial Funding Corp. Commercial Mortga", "" },
			new String[] { "8", "Deutsche Alt-A Securities Mortgage Loan Trust, Series 2007-O", "" },
			new String[] { "9", "SAP AKTIENGESELLSCHAFT SYSTEMS APPLICATIONS PRODUCTS IN DATA", "" },
			new String[] { "10", "Structured Adjustable Rate Mortgage Loan Trust Series 2007-1", "" },
			new String[] { "11", "Eaton Vance Tax-Advantaged Global Dividend Opportunities Fun", "" },
			new String[] { "12", "PACIFIC SELECT EXEC SEPARATE ACCOUNT OF PACIFIC LIFE & ANNUI", "" },
			new String[] { "13", "VARIABLE ANNUITY ACCOUNT B OF ING LIFE INSURANCE & ANNUITY C", "" },
			new String[] { "14", "PRUCO LIFE INURANCE CO OF NEW JERSEY FLXBL PRMIUM VAR ANN AC", "" },
			new String[] { "15", "William C. & Betty Jane France Alaska Community Property Tru", "" },
			new String[] { "16", "Deutsche Alt-A Securities Mortgage Loan Trust, Series 2007-3", "" },
			new String[] { "17", "Deutsche Alt-A Securities Mortgage Loan Trust, Series 2007-A", "" },
			new String[] { "18", "Merrill Lynch Alternative Note Asset Trust, Series 2007-OAR5", "" },
			new String[] { "19", "Merrill Lynch Mortgage Backed Securities Trust, Series 2007-", "" },
			new String[] { "20", "MortgageIT Securities Corp. Mortgage Loan Trust, Series 2007", "" } };

	Corpus corpus;

	@Before
	public final void fileTest() {
		try {
			corpus = new Corpus();
			corpus.fillFromFile("/Users/mlrus/Workspace/KgenII/INPUTS/master.2col");
			for (final String q : "media*,sha*,comm*,east AND coast,+east +coast,+growth +fund,+structured +asset,solo*,ler*,lern*"
					.split(",")) {
				doSearch(q);
			}
			for (final String q : "hoover,media,communicate,management,mortgage,growth,fund,structured,asset,solo,ler,asset management"
					.split(",")) {
				doSearch(q);
				testMSA2(q);
			}
		} catch (final Exception e) {
			final String error = "Error: " + e.getMessage();
			org.junit.Assert.fail(error);
		}
		assertNotNull(corpus);
	}

	@Test
	public final void testNumFields() {
		assertEquals("Numfields is wrong.", corpus.templateWriter.getNumfields(), Constants.fieldNames.size());
	}

	@Test
	public final void testFieldnames() {
		final Iterator<String> fieldIt = corpus.templateWriter.getFieldnames().iterator();
		for (final String field : Constants.fieldNames) {
			assertEquals(field, fieldIt.next());
		}
	}

	// public final void readFromFile(final String filename) {
	// final List<String> content = IO.readInput(filename, true);
	// System.out.println("start processing content.");
	// for (final String input : content) {
	// corpus.addContent(input.split(",", 2));
	// }
	// System.out.println("start building suffix array : " + corpus.msa.hashCode());
	// corpus.msa.buildMSA(content);
	// System.out.println("ReadFromFile:: #records=" + corpus.writer.maxDoc());
	// }

	public String cleanString(final String string) {
		final String ignoreCharPattern = "[\\p{L}&&[^a-zA-Z]]";
		final String skipCharPattern = "[^0-9a-zA-Z ]";
		String searchAs = string.replaceAll(ignoreCharPattern, "");
		searchAs = searchAs.replaceAll(skipCharPattern, " ");
		searchAs = searchAs.replaceAll(skipCharPattern, " ");
		return searchAs;
	}

	public void testMSA2(final String queryText) {
		System.out.println("\ntestMSA2(" + queryText + ")");
		final String searchAs = cleanString(queryText);
		if (searchAs.length() > 0) {

			long t0, t1;
			double time0;
			BitSet bs;

			System.out.print("ENTITY::");
			t0 = System.nanoTime();
			bs = corpus.msa.BSgetEntity(searchAs);
			t1 = System.nanoTime();
			time0 = (t1 - t0) / 1E6;
			System.out.printf("::MSA::%s : %d hits.  t0=%5.7f msec\n", queryText, bs.cardinality(), time0);
			for (final Iterator<StringEntry> rIt = corpus.msa.SAgetDescriptors(bs); rIt.hasNext();) {
				System.out.println("  entity(" + searchAs + ")::" + rIt.next());
			}

			System.out.print("PHRASE::");
			t0 = System.nanoTime();
			bs = corpus.msa.BSgetPhrase(searchAs);
			t1 = System.nanoTime();
			time0 = (t1 - t0) / 1E6;
			System.out.printf("::MSA::%s : %d hits.  t0=%5.7f msec\n", queryText, bs.cardinality(), time0);
			for (final Iterator<StringEntry> rIt = corpus.msa.SAgetDescriptors(bs); rIt.hasNext();) {
				System.out.println("  phrase::" + searchAs + ")::" + rIt.next());
			}

			System.out.print("ANY::");
			final BitSet[] bsAnyAll = corpus.msa.BSgetAnyAllWords(searchAs);
			t1 = System.nanoTime();
			time0 = (t1 - t0) / 1E6;
			System.out.printf("::MSA::%s : %d hits.  t0=%5.7f msec\n", queryText, bs.cardinality(), time0);
			bs = bsAnyAll[0];
			for (final Iterator<StringEntry> rIt = corpus.msa.SAgetDescriptors(bs); rIt.hasNext();) {
				System.out.println("  any::" + searchAs + ")::" + rIt.next());
			}
			System.out.print("ALL:" + searchAs);
			bs = bsAnyAll[1];
			System.out.printf("::MSA::%s : %d hits.  t0=%5.7f msec\n", queryText, bs.cardinality(), 0D);
			for (final Iterator<StringEntry> rIt = corpus.msa.SAgetDescriptors(bs); rIt.hasNext();) {
				System.out.println("  all::" + searchAs + ")::" + rIt.next());
			}
		}
	}

	public final void doSearch(final String queryText) {
		System.out.println("\ndoSearch(" + queryText + ")");
		try {
			final long t0 = System.nanoTime();
			final TopDocCollector collector = corpus.search(queryText);
			final long t1 = System.nanoTime();
			final ScoreDoc[] hits = collector.topDocs().scoreDocs;
			final double time0 = (t1 - t0) / 1E6;
			System.out.printf("SEARCH::%s : %d hits.  t0=%5.7f msec\n", queryText, collector.getTotalHits(), time0);
			for (int i = 0; i < hits.length; i++) {
				System.out.println("Hit" + i + " : " + hits[i].score + " : " + hits[i].doc + ": "
						+ corpus.getDocFields(hits[i].doc));
			}
		} catch (final CorruptIndexException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
			final String error = "Error: " + e.getMessage();
			org.junit.Assert.fail(error);
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		org.junit.Assert.assertTrue(true);
	}
}
