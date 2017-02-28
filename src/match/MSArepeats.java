package match;

import interfaces.EntryData;

import java.util.Iterator;
import java.util.List;

public class MSArepeats<S extends EntryData<S>> extends MSA<S> {
	public static final Integer MAXITEMSFORREPEAT = 128;
	public static final Integer MINCHARSFORREPEAT = 32;
	public static final Float THRESHOLDASREPEAT = 0.25f; // within the query results

	Integer maxItemsForRepeat;
	Integer minCharsForRepeat;
	Float thresholdAsRepeat;

	public MSArepeats(final Integer maxItemsForRepeat, final Integer minCharsForRepeat, final Float thresholdAsRepeat) {
		super();
		this.maxItemsForRepeat = maxItemsForRepeat;
		this.minCharsForRepeat = minCharsForRepeat;
		this.thresholdAsRepeat = thresholdAsRepeat;
	}

	public List<List<S>> collectRepeats(final List<S> entries) {
		final Iterator<S> entryIterator = entries.iterator();
		int limit = 2 * maxItemsForRepeat;
		while (entryIterator.hasNext() && limit-- > 0) {
			final S entry = entryIterator.next();
			addEntry(entry);
		}
		makeSuffixArray();
		lsa();
		linkRepeats();
		final List<List<S>> subT = segmentLinks.makeHitList();
		if (showDebug) {
			final StringBuffer sb = segmentLinks.showSubtrees(subT);
			System.out.println("MATCHES\n" + sb.toString());
		}
		return subT;
	}

	/*******************************************************************************************************************
	 * Obtain the repeated matched content by processing the suffix array backwards in reverse order, so the longer
	 * common prefix appear before the shorter ones. This gives the lexically larger repetition, all other things being
	 * the same.
	 */
	void linkRepeats() {
		final ItemCovers<ItemDescriptor> ic = new ItemCovers<ItemDescriptor>();
		if (showDebug) {
			System.out.println("\nmatchRepeatedContent START\n");
		}
		for (int i = suffixArray.length - 1; i > 0; i--) {
			final int suffixLoc = suffixArray[i];
			final int priorLoc = suffixArray[i - 1];
			final int suffixLen = lsa[i];

			final int segmentId = getSegmentForPos(suffixLoc);
			final int offset = suffixLoc - segmentStartindIndices.get(segmentId);
			final int eOffset = offset + suffixLen;
			final int textLength = contentBuffer.get(segmentId).length();
			final ItemDescriptor ido1 = new ItemDescriptor(segmentId, textLength, offset, eOffset);

			final int priorSegId = getSegmentForPos(priorLoc);
			final int priorOffset = priorLoc - segmentStartindIndices.get(priorSegId);
			final int priorEoffset = priorOffset + suffixLen;
			final ItemDescriptor ido2 = new ItemDescriptor(priorSegId, textLength, priorOffset, priorEoffset);

			if (suffixLen < minCharsForRepeat) {
				continue;
			}
			if (suffixLen < textLength * thresholdAsRepeat) {
				continue; // this is actually the final postcondition which we are testing early
			}
			final boolean c1 = ic.covers(ido1);
			final boolean c2 = ic.covers(ido2);
			if (!c1) {
				ic.itemSet.add(ido1);
				final int parent = segmentLinks.link(segmentId, priorSegId);
				if (showDebug) {
					System.out.println("Parent[" + segmentId + "," + priorSegId + "]==>" + parent + "; Adding cover ido1 " + ido1
							+ " : " + String.valueOf(textArray, suffixLoc, suffixLen));
					System.out.println("LINK: " + segmentId + " : " + String.valueOf(textArray, suffixLoc, suffixLen));
					System.out.println("    : " + priorSegId + " : " + String.valueOf(textArray, priorLoc, suffixLen));
				}
			}
			if (!c2) {
				ic.itemSet.add(ido2);
				final int parent = segmentLinks.link(segmentId, priorSegId);
				if (showDebug) {
					System.out.println("Parent[" + segmentId + "," + priorSegId + "]==>" + parent + "; Adding cover ido2 " + ido2
							+ " : " + String.valueOf(textArray, priorLoc, suffixLen));
					System.out.println("LINKB: " + segmentId + " : " + String.valueOf(textArray, suffixLoc, suffixLen));
					System.out.println("    !: " + priorSegId + " : " + String.valueOf(textArray, priorLoc, suffixLen));
				}
			}
		}
	}
}
