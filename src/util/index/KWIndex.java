// This is unpublished source code. Michah Lerner 2007

package util.index;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import util.Constants;
import util.RSP;

/**
 * Class KWIndex builds the definitions of composite query services, based on an xml property file, a map of name/value
 * pairs, or both of these. This allows configuration of multiple query providers. Each query provider is named within
 * the serverType field of the description.
 * 
 * <pre>
 *  &lt;!-- Declare the indexNames to use one index, called &lt;b&gt;bookTitles&lt;/b&gt; &amp;rarr
 *  &lt;entry key=&quot;&lt;u&gt;indexNames&lt;/u&gt;&quot;&gt;&lt;b&gt;bookTitles&lt;/b&gt;&lt;/entry&gt;
 *  &lt;!-- Define the &lt;b&gt;bookTitles&lt;/b&gt; index.  This
 * <em>
 * must
 * </em>
 *  be an NVP.
 *  The &lt;b&gt;serverType&lt;/b&gt; of &lt;b&gt;docStore&lt;/b&gt; defines this as a memory-resident DocumentStore. &amp;rarr
 *   &lt;entry key=&quot;&lt;u&gt;NVP:Index:bookTitles&lt;/u&gt;&quot;&gt;
 *     {shortIndexName,bookTitles},
 *     {serverType,docStore},
 *     {indexName,c:/mlrus/bookTitles.dat}
 *   &lt;/entry&gt;
 * <br><br>
 <br><br>
 *  &lt;!-- Declare the indexNames to use three indices,
 *  &amp;nbsp    called &lt;b&gt;filteredRICs.all&lt;/b&gt;, &lt;b&gt;unfilteredRICs.all&lt;/b&gt;, and &lt;b&gt;news.neighborhood.all&lt;/b&gt; &amp;rarr
 *  &lt;entry key=&quot;&lt;u&gt;indexNames&lt;/u&gt;&quot;&gt;filteredRICs.all,unfilteredRICs.all,news.neighborhood&lt;/entry&gt;
 * 
 *  &lt;!-- Define the &lt;b&gt;filteredRICs.all&lt;/b&gt; index.  This
 * <em>
 * must
 * </em>
 *  be an NVP. &amp;rarr
 *  &lt;entry key=&quot;NVP:Index:filteredRICs.all&quot;&gt;
 *     {shortIndexName,tornado.dat},
 *     {serverType,docStore},
 *     {indexName,c:/temp/content.2col}
 *  &lt;/entry&gt;
 * <br><br>
 *  The &lt;b&gt;serverType&lt;/b&gt; of &lt;b&gt;lucene&lt;/b&gt; defines the next two as pre-indexed in Lucene format. &amp;rarr
 *  &lt;entry key=&quot;NVP:Index:unfilteredRICs.all&quot;&gt;
 *     {serverType,lucene},
 *     {shortIndexName,unfilteredRICs.all},
 *     {analyzerName,SimpleAnalyzer},
 *     {fieldName,company},{indexName,dat/wordFrequencyStudy/RICs.luc},
 *     {queryFormat, ALL}
 *  &lt;/entry&gt;
 * <br><br>
 *  &lt;entry key=&quot;NVP:Index:news.phrase1&quot;&gt;
 *     {serverType,lucene},
 *     {shortIndexName,news.phrase1},
 *     {analyzerName,StandardAnalyzer},
 *     {fieldName,contents},{indexName,dat/wordFrequencyStudy/newsIndex},
 *     {queryFormat,N_PHRASE},{phraseLength,1},{queryModifier,condAll}
 *  &lt;/entry&gt;
 * <br><br>
 *  &lt;entry key=&quot;NVP:Index:news.neighborhood&quot;&gt;
 *     {serverType,lucene},
 *     {shortIndexName,newsIndex.neighborhood},
 *     {analyzerName,&lt;i&gt;StandardAnalyzer&lt;/i&gt;},
 *     {fieldName,contents},{indexName,dat/wordFrequencyStudy/newsIndex},
 *     {&lt;i&gt;queryFormat&lt;/i&gt;,&lt;b&gt;&lt;u&gt;NEIGHBORHOOD&lt;/u&gt;&lt;/b&gt;}
 *  &lt;/entry&gt;
 * </pre>
 * 
 * @author Michah.Lerner
 * 
 */
public class KWIndex extends RSP {

	public String indices;
	List<Map<String, String>> definedIndices = null;

	public KWIndex() {
		System.out.println(this.getClass().getCanonicalName());
	}

	/**
	 * Build an indexing object from the property file only.
	 * 
	 * @param propertyFilename
	 *            the name of the xml property file with values for entities
	 */
	public KWIndex(final String propertyFilename) {
		this(propertyFilename, new HashMap<String, String>());
	}

	/**
	 * Construct an indexing object from properties and a map of additional properies.
	 * 
	 * @param propertyFilename
	 * @param additionalProperties
	 */
	public KWIndex(final String propertyFilename, final Map<String, String> additionalProperties) {
		kwInit(propertyFilename, additionalProperties);
	}

	/**
	 * Get the index definitions in an XML configuration file and any additional properties passed in through the
	 * additionalProperties map. This leverages an extended name/value notation for configuration of lists though the
	 * xml properties file.
	 * 
	 * @param propertyFilename
	 * @param additionalProperties
	 * 
	 */
	public void kwInit(final String propertyFilename, final Map<String, String> additionalProperties) {
		_RSP(propertyFilename, additionalProperties);
		_initControls(this);
		_showRSP(System.out);
		getIndices();
	}

	public List<Map<String, String>> getIndices() {
		if (definedIndices == null) {
			definedIndices = getDefinedIndices(indices != null
					? indices
					: Constants.indexNames);
		}
		return definedIndices;
	}

	/**
	 * Load the indices that are named by the parameter indexNames. The names are passed in as one coma-delimited
	 * string. Each index object is retrieved from the XML configuration data, suitably unmarshalled from its serial
	 * text form into the name value pairs that represent the index configuration. <br>
	 * <br>
	 * The special key (identified symbolically as <code>.indexKeyFormat</code>) will be reformatted to cannonical
	 * form, for identification purposes.
	 * 
	 * @param indices
	 * @return List of the maps that each define a separate index resource
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getDefinedIndices(final String indices) {
		System.out.println(this.getClass().getClass().getCanonicalName() + " :: processing indexNames: " + indices);
		final Map<String, Object> indexDefinitions = _getByType("Index");
		final List<Map<String, String>> defs = new ArrayList<Map<String, String>>();
		for (final String index : indices.split(",")) {
			if (index.length() > 0) {
				System.out.println("loading definition for Index [" + index + "");
				final Object definition = indexDefinitions.get(index);
				if (definition == null) {
					System.out.println("!!!!!\n!!!!!\n!!!!!\n!!!!!\n!!!!!\n");
					System.out.println("  ERROR: No definition retrieved for " + index + "]");
					System.out.println("!!!!!\n!!!!!\n!!!!!\n!!!!!\n!!!!!\n");
				} else {
					if (definition instanceof List) {
						final Map<String, String> map = new HashMap<String, String>();
						final Iterator<String> it = ((List<String>) definition).iterator();
						String name = null;
						String value = null;
						while (it.hasNext()) {
							name = it.next();
							value = it.hasNext()
									? it.next()
									: null;
							if (name.equals(Constants.indexKey)) {
								value = String.format(Constants.indexKeyFormat, value);
							}
							map.put(name, value);
						}
						defs.add(map);
						System.out.println(" (ok)]");
					}
				}
			}
		}
		System.out.println("Made " + defs.size() + " index maps:");
		for (final Map<String, String> m : defs) {
			System.out.println("  map: " + m);
		}
		return defs;
	}

	public void showIndices() {
		showIndices(System.out);
	}

	public void showIndices(final PrintStream out) {
		for (final Map<String, String> map : getIndices()) {
			for (final Map.Entry<String, String> me : map.entrySet()) {
				out.println("  name=" + me.getKey() + " value=" + me.getValue());
			}
			out.println();
		}
	}

}
