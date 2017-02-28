package Corpus;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.SetBasedFieldSelector;

public class Constants {
	public static final String CONTENT = "content";
	public static final String URL = "url";
	public static final String DESCRIPTION = "description";
	public static final String KEYWORDS = "keywords";

	private static String[] DEFAULTFIELDSNAMES = new String[] { URL, DESCRIPTION, KEYWORDS };
	private static String[] DEFAULTDISPLAYFIELDS = new String[] { URL, DESCRIPTION };

	static final String DEFAULTSEARCHFIELD = DESCRIPTION;
	static Field.Index FIELDINDEXTYPE = org.apache.lucene.document.Field.Index.TOKENIZED;
	static Field.Store FIELDSTORETYPE = org.apache.lucene.document.Field.Store.YES;
	static Field.TermVector FIELDTERMVECTORTYPE = org.apache.lucene.document.Field.TermVector.WITH_POSITIONS_OFFSETS;
	static List<String> fieldNames = Arrays.asList(DEFAULTFIELDSNAMES);
	static Set<String> displayFields = new LinkedHashSet<String>(Arrays.asList(DEFAULTDISPLAYFIELDS));
	static FieldSelector DEFAULTFIELDSELECTOR = new SetBasedFieldSelector(Constants.displayFields, Collections.emptySet());
	static Class<? extends Analyzer> DEFAULTANALYZER = StandardAnalyzer.class;
	static final boolean CREATE = true;

	static final int HITSPERPAGE = 10;

	public static final double DEFAULTRAMBUFFERSIZE = 512D;
	public static final int ALLDOCUMENTS = 32768;
}
