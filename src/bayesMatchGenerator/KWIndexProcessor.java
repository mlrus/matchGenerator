// This is unpublished source code. Michah Lerner 2006, 2007, 2008

package bayesMatchGenerator;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import util.index.KWIndex;

/**
 * Provides a simple and common wrapper for the processing of multiple indices. This extends the KWIndex and "wraps"
 * together a unified configuration, command-line processing and with index setup.
 * 
 * @author Michah.Lerner
 * 
 */

public class KWIndexProcessor extends KWIndex {

	public Boolean showExpansions = true;
	public Boolean showInterimLines = false;
	public String propertyFilename;
	static Map<String, String> additionalProperties = new HashMap<String, String>();
	public String outFilename = null;
	private String inFilename = null;
	public PrintStream out = null;

	public static String noteSettables = "infile,outfile,showDefinitions,showIf[InvalidInput,NoDataForQuery,Identity,OK,Neighborhood,ZeroOne,Other,ValueONE,ValueZERO,ValueNaN]";

	static void usage() {
		System.out.println("usage:  java prog [-Dfeature=value] (inputFile|-) (outputFile|-)");
		System.out.println("       local features are: " + noteSettables);
		throw new RuntimeException("Check usage and retry.");
	}

	/**
	 * Parse standard options into the additionalProperties list.
	 * 
	 * @param args
	 *            the input of option and value
	 * @return the name of the property filename, if one is provided
	 */
	public String processArgs(final String args[]) {
		int argc = 0;
		if (args.length == 0) {
			usage();
		}
		while (argc < args.length) {
			if (args[argc].startsWith("-h")) {
				usage();
			}
			if (args[argc].toLowerCase().startsWith("-prop")) {
				propertyFilename = args[++argc];
				System.out.println("PROPERTY FILE: " + propertyFilename);
				argc++;
				continue;
			}
			if (args[argc].startsWith("-D")) {
				String arg = args[argc++];
				if (arg.startsWith("\"") && arg.endsWith("\"") && arg.length() > 1) {
					arg = arg.substring(1, arg.length());
					arg = arg.substring(0, arg.length() - 2);
				}
				if (arg.length() == 2) {
					continue;
				}
				arg = arg.substring(2);
				if (arg.indexOf('=') < 0) {
					additionalProperties.put(arg, "true");
					continue;
				}
				final String[] nvp = arg.split("=", 2);
				System.out.println("ARG name=" + nvp[0].trim() + "  value=" + nvp[1].trim() + ".");
				additionalProperties.put(nvp[0].trim(), nvp[1].trim());
				continue;
			}
			if (getInfile() == null) {
				setInfile(args[argc++]);
				continue;
			}
			if (outFilename == null) {
				outFilename = args[argc++];
				continue;
			}
			break;
		}
		if (argc < args.length) {
			System.out.println("Extra args ignored: ");
			while (argc < args.length) {
				System.out.print(args[argc++] + " ");
			}
		}
		return propertyFilename;
	}

	/**
	 * Configure from an xml property file.
	 * 
	 * @param propertyFilename
	 */
	public void configureFromPropertyfile(final String propertyFilename) {
		configureFromPropertyfile(propertyFilename, additionalProperties);
	}

	/**
	 * Configure from an xml property file and an additional hashlist of name/value pairs. The actual configuration will
	 * be done by an "RSP" object utilizing reflection.
	 * 
	 * @param propertyFilename
	 * @param additionalProperties
	 */
	@SuppressWarnings("unchecked")
	public void configureFromPropertyfile(final String propertyFilename, final Map<String, String> additionalProperties) {
		kwInit(propertyFilename, additionalProperties);
	}

	public void setInfile(final String infile) {
		this.inFilename = infile;
	}

	public String getInfile() {
		return inFilename;
	}

}
