package match;

import interfaces.EntryData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import util.coll.Link;
import util.data.StringMethodsIntl;

/***********************************************************************************************************************
 * Class MSA does repeats finding by a simple Java language implementation of the suffix array. This implementation uses
 * O(n * log(n) ) time. There are much better native implementations. Given a String input of length N, sort the vector
 * [0..N-1] using a comparator that compares input[a..N-1] with input[b..N-1]. Of course, we don't actually make the
 * substrings, but use pointers to char[] instead. There are substantially better implementations. It is well- known to
 * the art that methods based on radix sort with expanding codeword size can perform in O(n) time, and there are
 * self-indexes which use less space than the source text itself.
 * 
 * NOTE: This effectiveness of this implementation may depend greatly on the duplication level of the content. When
 * there are many large segments it is much more effective to compute individually and then merge.
 * 
 * 
 * @author mlrus
 * 
 */

/*
 * USAGE:
 * 
 * final MSA<ContentResult> msa = new MSA<ContentResult>(); final List<List<Result>> repeatsInfo =
 * msa.collectRepeats(tsResultCollector.getResult()); List<Result> captionResultsList =
 * tsResultCollector.aggregateRepeatedItems(repeatsInfo);
 */

public class MSA<S extends EntryData<S>> {

	/*******************************************************************************************************************
	 * Class sString is simple charSequence processor that makes a native char[] from a string. Field []p stores
	 * positions for sorting Field []sa stores the suffix array Field [][]saPos stores the sorted suffix array (i.e.,
	 * sorted by length of the matching string)
	 * 
	 * @author mlrus
	 * 
	 */

	public static final boolean CASEINDEPENDENTMATCHING = true;

	char[] textArray; // the array of text
	Fchar text;
	Integer[] suffixArray; // the suffix array
	int[] lsa; // the suffix match lengths
	int[] phi; // inverse :: suffixArray[phi[i]] = suffixArray[i] + 1 (See defn 11 of Navarro and Makinen 2006])

	List<String> contentBuffer;
	List<Integer> segmentStartindIndices;
	List<S> segDescriptions;
	Link<S> segmentLinks;
	String bufferAsString;
	// long elap;
	final int indexOffset = 1;
	int cntr = 0;
	Integer nPos;
	Comparator<Integer> derefStrComparator;
	Comparator<Integer[]> derefCharComparator;

	boolean showDebug;

	public MSA() {
		final class CompareStringsAt implements Comparator<Integer> {
			public int compare(Integer o1, Integer o2) {
				if (o1 == textArray.length - 1) { return -1; }
				if (o2 == textArray.length - 1) { return 1; }
				while (textArray[o1] == textArray[o2]) {
					o1++;
					o2++;
				}
				return textArray[o1] - textArray[o2];
			}
		}
		final class CompareStringsAtIgnoreCase implements Comparator<Integer> {
			public int compare(Integer o1, Integer o2) {
				if (o1 == textArray.length - 1) { return -1; }
				if (o2 == textArray.length - 1) { return 1; }
				while (Character.toLowerCase(textArray[o1]) == Character.toLowerCase(textArray[o2])) {
					o1++;
					o2++;
				}
				return Character.toLowerCase(textArray[o1]) - Character.toLowerCase(textArray[o2]);
			}
		}
		class SAPairSort implements Comparator<Integer[]> {
			public int compare(final Integer[] o1, final Integer[] o2) {
				return -o1[0].compareTo(o2[0]);
			}
		}
		class SAPairSortCaseIndependent implements Comparator<Integer[]> {
			public int compare(final Integer[] o1, final Integer[] o2) {
				return -o1[0].compareTo(o2[0]);
			}
		}

		showDebug = false;
		derefCharComparator = CASEINDEPENDENTMATCHING
				? new SAPairSortCaseIndependent()
				: new SAPairSort();
		derefStrComparator = CASEINDEPENDENTMATCHING
				? new CompareStringsAtIgnoreCase()
				: new CompareStringsAt();
		contentBuffer = new ArrayList<String>();
		segmentStartindIndices = new ArrayList<Integer>();
		segDescriptions = new ArrayList<S>();// Cannot set nodeList until we know how many hits there are.
	}

	public void addEntry(final S entry) {
		final String searchableForm = StringMethodsIntl.cleanString(entry.getText());
		contentBuffer.add(searchableForm);
		segDescriptions.add(entry);
		if (showDebug) {
			System.out.println(String.format("addEntry  %30s %6d \"%s\" stored as \"%s\"", entry.getIdent(), cntr++, entry
					.getText(), searchableForm));
		}
	}

	public void buildSA(final Iterator<S> it) {
		while (it.hasNext()) {
			addEntry(it.next());
		}
		makeSuffixArray();
		lsa();
	}

	public static final char[] prefixSeparator = "::XX:: ".toCharArray();
	public static final char[] suffixSeparator = " ".toCharArray();

	private void initializeStructures() {
		nPos = contentBuffer.size();
		segmentLinks = new Link<S>(nPos);
		for (int hitNo = 0; hitNo < nPos; hitNo++) {
			segmentLinks.set(hitNo, segDescriptions.get(hitNo));
		}

		int len = 0;
		for (final String st : contentBuffer) {
			len += st.length();
		}
		len += contentBuffer.size() * (prefixSeparator.length + suffixSeparator.length);
		textArray = new char[len + 1];
		textArray[len] = (char) 0;

		len = 0;
		for (final String st : contentBuffer) {
			segmentStartindIndices.add(len);
			System.arraycopy(prefixSeparator, 0, textArray, len, prefixSeparator.length);
			len += prefixSeparator.length;
			st.getChars(0, st.length(), textArray, len);
			len += st.length();
			System.arraycopy(suffixSeparator, 0, textArray, len, suffixSeparator.length);
			len += suffixSeparator.length;
		}
		text = new Fchar(textArray);
		suffixArray = new Integer[textArray.length];
		for (int i = 0; i < textArray.length; i++) {
			suffixArray[i] = i;
		}

		bufferAsString = String.valueOf(textArray);
	}

	/*******************************************************************************************************************
	 * Method makeSuffixArray is a simple but slow way to build the suffix array. It just sorts the numbers 1..N
	 * according to the strings that start at positions 1..N in the input.
	 */
	public void makeSuffixArray() {
		initializeStructures();
		Arrays.sort(suffixArray, derefStrComparator);
		suffixArray[0] = 0;
	}

	/*******************************************************************************************************************
	 * Method lsa finds length of repeat starting at s[i-1] and s[i]. This simple implementation can be made
	 * considerably faster.
	 */
	public void lsa() {
		lsa = new int[suffixArray.length];
		for (int i = 1; i < suffixArray.length; i++) {
			int l0 = suffixArray[i - 1], l1 = suffixArray[i];
			while (textArray[l0] == textArray[l1]) {
				l0++;
				l1++;
			}
			lsa[i] = l1 - suffixArray[i];
		}
	}

	int getNumentries() {
		return segDescriptions.size();
	}

	int getNumberofSegments() {
		return segmentStartindIndices.size();
	}

	int getSegmentForPos(final int pos) {
		final Integer segmentIs = Collections.binarySearch(segmentStartindIndices, pos);
		if (segmentIs >= 0) { return segmentIs; }
		return -2 - segmentIs;
	}
}
