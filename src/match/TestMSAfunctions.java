package match;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;

import util.data.ContentIterator;

public class TestMSAfunctions extends MSAfunctions<StringEntry> {

	final static String ignoreCharPattern = "[\\p{L}&&[^a-zA-Z]]";
	final static String skipCharPattern = "[^0-9a-zA-Z ]";
	final static String compactCharPattern = "  +";

	public String cleanString(final String string) {
		String searchAs = string.replaceAll(ignoreCharPattern, "");
		searchAs = searchAs.replaceAll(skipCharPattern, " ");
		searchAs = searchAs.replaceAll(skipCharPattern, " ");
		return searchAs;
	}

	void answerLoop(final Reader ir) throws IOException {
		String query = null;
		final BufferedReader bir = new BufferedReader(ir);
		while ((query = bir.readLine()) != null && !query.equalsIgnoreCase("//exit")) {
			final String searchAs = cleanString(query);
			if (searchAs.length() > 0) {

				System.out.print("ENTITY:" + searchAs + ".");
				BitSet bs = BSgetEntity(searchAs);
				System.out.println(" count=" + bs.cardinality());
				for (final Iterator<StringEntry> rIt = SAgetDescriptors(bs); rIt.hasNext();) {
					System.out.println("  " + rIt.next());
				}

				System.out.print("PHRASE:" + searchAs);
				bs = BSgetPhrase(searchAs);
				System.out.println(" count=" + bs.cardinality());
				for (final Iterator<StringEntry> rIt = SAgetDescriptors(bs); rIt.hasNext();) {
					System.out.println("  " + rIt.next());
				}

				System.out.print("ANY:" + searchAs);
				final BitSet[] bsAnyAll = BSgetAnyAllWords(searchAs);
				bs = bsAnyAll[0];
				System.out.println(" count=" + bs.cardinality());
				for (final Iterator<StringEntry> rIt = SAgetDescriptors(bs); rIt.hasNext();) {
					System.out.println("  " + rIt.next());
				}

				System.out.print("ALL:" + searchAs);
				bs = bsAnyAll[1];
				System.out.println(" count=" + bs.cardinality());
				for (final Iterator<StringEntry> rIt = SAgetDescriptors(bs); rIt.hasNext();) {
					System.out.println("  " + rIt.next());
				}
			}
		}
	}

	TestMSAfunctions(final Iterator<StringEntry> contentIterator) {
		buildSA(contentIterator);
	}

	static Iterator<StringEntry> sampleContent = StringEntry.getIterator(new ContentIterator().get(new StringReader(
			"book://item/6618,my new discovery\n" + "book://item/705,adventures in happy friendship\n"
					+ "book://item/120,how to be happy\n" + "book://item/17,ski the new happy way")));

	public static void main(final String[] args) throws IOException {

		final Iterator<String> argIterator = Arrays.asList(args).iterator();
		final Iterator<StringEntry> contentIterator = argIterator.hasNext()
				? StringEntry.getIterator(new ContentIterator().get(argIterator.next()))
				: sampleContent;
		final Iterator<String> queryIterator = argIterator.hasNext()
				? argIterator
				: Arrays.asList(new String[] { "new", "happy", "new happy" }).iterator();

		final TestMSAfunctions msaStrings = new TestMSAfunctions(contentIterator);
		msaStrings.answerLoop(new IteratorReader(queryIterator));
		msaStrings.answerLoop(new InputStreamReader(System.in));
	}
}
