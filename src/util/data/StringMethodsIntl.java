package util.data;

public class StringMethodsIntl extends StringMethods {

	final static String skipCharPattern = "[^0-9\\p{L}a-zA-Z ]";
	final static String compactCharPattern = "  +";

	public static final String cleanString(final String s) {
		if (true) { return s.replaceAll(compactCharPattern, " "); }
		final String res = s.replaceAll(skipCharPattern, " ").replaceAll(compactCharPattern, " ");
		if (!s.equals(res)) {
			System.err.println("Changed \"" + s + "\" to \"" + res + "\"");
		}
		return res;
	}

}
