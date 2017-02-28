// This is unpublished source code. Michah Lerner 2006, 2007, 2008

package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * <strong>Autoconfiguration</strong> from property file and/or additional map of name/value pairs. The definitions
 * acquired from the property file and name/value map are used to set the values of like-named variables that are found
 * during a reflective "crawl" through the name space in the order of variable inheritance.
 * 
 * <br>
 * <br>
 * Three kinds of entry keys are: (a) plain strings, (b) typed names consisting of a
 * &lt;type&gt;&lt;dot&gt;&lt;name&gt;, and (c) structured instances consisting of
 * <code> NVP&lt;colon&gt;&lt;type&gt;&lt;dot&gt;&lt;name&gt;</code> <br>
 * <br>
 * Example of each form: <br>
 * &lt;entry key="maxInternalResults"&gt;4096&lt;/entry&gt; <br>
 * &lt;entry key="cache.DS"&lt;documentStore,1024,0.75&lt;/entry&gt; <br>
 * &lt;entry key="NVP:Index.newsIndex"&gt;{-i,c:/tmp/wordFrequencyStudy/newsIndex},
 * {-a,_analyzerName},{-f,contents}&lt;/entry&gt;
 * 
 * value of such names is inferred to be a map of name/value pairs.
 * 
 * @author Michah.Lerner
 * 
 */
public class RSP extends Constants {
	static String _propFilename = "RSP.properties";
	Properties _RSprop;
	Set<String> _rspTypes;
	Boolean _RSPshowSets;

	protected RSP() {
		_RSprop = new Properties();
		_RSPshowSets = false;
	}

	protected RSP(final Boolean showSets) {
		this();
		this._RSPshowSets = showSets;
	}

	/**
	 * Read a structured property file. Extend this to avoid needing public sharing. Three kinds of entry keys are: (a)
	 * plain strings, (b) typed names consisting of a <type><dot><name>, and (c) structured instances consisting of
	 * "NVP"<colon><type><dot><name> Example of each form: <entry key="maxInternalResults">4096</entry> <entry
	 * key="cache.DS">documentStore,1024,0.75</entry> <entry
	 * key="NVP:Index.newsIndex">{-i,c:/tmp/wordFrequencyStudy/newsIndex}, {-a,_analyzerName},{-f,contents}</entry>
	 * 
	 * @param propertyFilename
	 */

	protected RSP(final String propertyFilename, final Map<String, String> additionalProperties) {
		this();
		_RSP(propertyFilename, additionalProperties);
	}

	protected RSP(final String propertyFilename) {
		this(propertyFilename, (Map<String, String>) null);
	}

	public RSP(final Map<String, String> propertyList) {
		this(null, propertyList);
	}

	protected void _RSP(final String propertyFilename, final Map<String, String> additionalProperties) {
		_RSprop = new Properties();
		if (propertyFilename != null) {
			_RSP(propertyFilename);
		}
		if (additionalProperties != null) {
			_RSP(additionalProperties);
		}
		_updateMap();
	}

	private void _RSP(final String propertyFilename) {
		try {
			final InputStream in = new FileInputStream(propertyFilename);
			System.out.println("Configuring from " + new File(propertyFilename).getAbsolutePath());
			_RSprop.loadFromXML(in);
			System.out.println("RSPROP:" + _RSprop);
			in.close();
		} catch (final Exception e) {
			System.out.println("Cannot open " + new File(propertyFilename).getAbsolutePath());
			e.printStackTrace();
		}
	}

	protected void _RSP(final Map<String, String> propertyList) {
		_rspTypes = new HashSet<String>();
		for (final Map.Entry<String, String> me : propertyList.entrySet()) {
			_RSprop.setProperty(me.getKey(), me.getValue());
		}
	}

	private void _updateMap() {
		_rspTypes = new HashSet<String>();
		_rspTypes.add("anon");
		for (final Object key : new HashSet<Object>(_RSprop.keySet())) {
			if (!(key instanceof String)) {
				continue;
			}
			final String name = (String) key;
			System.out.println("name=" + name);
			if (name.startsWith("NVP:")) {
				final String typeBaseName = name.substring(name.indexOf(':') + 1);
				final String typeDetailName = typeBaseName.substring(0, typeBaseName.indexOf(':'));
				_rspTypes.add(typeDetailName);
				_RSprop.put(typeBaseName, dPair(_RSprop.getProperty(name)));
				System.out.println("typeBaseName:" + typeBaseName + "  == " + dPair(_RSprop.getProperty(name)));
				_RSprop.remove(key);
			} else if (name.indexOf(':') >= 0) {
				_rspTypes.add(name.substring(0, name.indexOf(':')));
				_RSprop.put(name, Arrays.asList(_RSprop.getProperty(name).split("\\s*,\\s*")));
			}
		}
	}

	public void _showRSP() {
		_showRSP(System.out);
	}

	public void _showRSP(final PrintStream out) {
		out.println("SHOW: ");
		out.println("TYPES: " + _rspTypes);
		for (final String type : this._rspTypes) {
			for (final Map.Entry<String, Object> o : this._getByType(type).entrySet()) {
				out.println(type + " : " + o.getKey() + " : " + o.getValue());
			}
		}
	}

	public void _initControls(final Object obj) {
		Class klaz = obj.getClass();
		// is this the right list order for unwinding names?
		final Set<Field> fields = new LinkedHashSet<Field>();
		while (klaz != null) {
			fields.addAll(Arrays.asList(klaz.getDeclaredFields()));
			fields.addAll(Arrays.asList(klaz.getFields()));
			klaz = klaz.getSuperclass();
		}
		klaz = null;
		for (final Field f : fields) {
			for (final Map.Entry<Object, Object> me : this._RSprop.entrySet()) {
				final String fieName = "." + f.getName();
				final Object keyObj = me.getKey();
				final Object valObj = me.getValue();
				final String objName = "." + keyObj.toString();
				boolean didAssign = false;
				if (fieName.endsWith(objName) || objName.endsWith(fieName)) {
					// System.out.println(objName+ " " + fieName);
					final Class classOfValue = valObj.getClass();
					final Object[] val = new Object[] { valObj };
					final Class typeClass = f.getType();
					for (final Constructor c : typeClass.getConstructors()) {
						for (final Class cl : c.getParameterTypes()) {
							if (cl.equals(classOfValue)) {
								try { // lousy to use exceptions for this ..
									// but 1X not too bad .
									didAssign = true;
									f.set(obj, c.newInstance(val));
								} catch (final IllegalArgumentException e) {
									didAssign = false;
								} catch (final IllegalAccessException e) {
									didAssign = false;
								} catch (final InstantiationException e) {
									didAssign = false;
								} catch (final InvocationTargetException e) {
									didAssign = false;
								}
								if (didAssign) {
									break;
								}
							}
						}
						if (didAssign) {
							if (_RSPshowSets) {
								System.out.println("RSP::propertyConfiguration::" + f + ".set(" + valObj.toString() + ")");
								System.out.flush();
							}
							break;
						}
					}
					if (!didAssign) {
						System.out.println("Did not do assign for " + f);
					}
				}
			}
		}
	}

	/**
	 * Get hashmap according to the type of the object.
	 * 
	 * @return Hashmap of names and objects, organized by the type prefixes given in the configuration file.
	 */
	public Map<String, Object> _getByType() {
		return _getAnon();
	}

	public Map<String, Object> _getByType(final String type) {
		final Map<String, Object> res = new HashMap<String, Object>();
		if (type == null || type.length() == 0 || type.equals("anon")) {
			return _getByType();
		}
		for (final String s : _getIfPrefix(type + ":")) {
			res.put(s.substring(s.indexOf(':') + 1), _RSprop.get(s));
		}
		return res;
	}

	private List<String> _getIfPrefix(final String prefix) {
		final List<String> res = new ArrayList<String>();
		for (final Map.Entry me : _RSprop.entrySet()) {
			final String name = (String) me.getKey();
			if (name.startsWith(prefix)) {
				res.add(name);
			}
		}
		return res;
	}

	private Map<String, Object> _getAnon() {
		final Map<String, Object> res = new HashMap<String, Object>();
		for (final Map.Entry me : _RSprop.entrySet()) {
			if (((String) me.getKey()).indexOf(':') < 0) {
				res.put((String) me.getKey(), me.getValue());
			}
		}
		return res;
	}

	/**
	 * Split list of pairs from form {head,tail}, ... {head,tail} into list of length 2N
	 * 
	 * @param value
	 *            a comma-separated list of name-value pairs (also comma-delimited) contained in braces
	 * @return a list of strings without extraneous spaces or delimeters
	 */
	private List<String> dPair(final String value) {
		class PairIterator {
			Iterator<String> pi;

			PairIterator(final String s) {
				final String pairs[] = s.split("^\\s*\\{\\s*|\\s*\\}\\s*,\\s*\\{\\s*|\\s*\\}\\s*$");
				pi = Arrays.asList(pairs).iterator();
			}

			boolean hasNext() {
				return pi.hasNext();
			}

			String[] next() {
				return pi.next().split("\\s*,\\s*", 2);
			}
		}

		final List<String> pairs = new ArrayList<String>();
		final PairIterator pi = new PairIterator(value);
		while (pi.hasNext()) {
			final String[] p = pi.next();
			if (p.length != 2) {
				continue;
			}
			pairs.add(p[0].trim());
			pairs.add(p[1].trim());
		}
		return pairs;
	}
}
