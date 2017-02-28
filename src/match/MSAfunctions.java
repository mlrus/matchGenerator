package match;

import interfaces.EntryData;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MSAfunctions<S extends EntryData<S>> extends MSA<S> implements SearchSA<S> {

	/*******************************************************************************************************************
	 * Class Cell is a general pair container
	 * 
	 * @author mlrus
	 * 
	 * @param <T>
	 *            The type of the head and tail of the pair
	 */
	protected class Cell<T> {
		T car, cdr;

		Cell(final T car, final T cdr) {
			this.car = car;
			this.cdr = cdr;
		}

		T car() {
			return car;
		}

		T cdr() {
			return cdr;
		}

		@Override
		public String toString() {
			return "(" + car.toString() + " " + cdr.toString() + ")";
		}
	}

	int iceilDiv(final int num, final int den) {
		int q = num / den;
		final int r = num - den * q;
		if (r != 0) {
			q++;
		}
		return q;
	}

	// See fig. 4 [Makinen and Navarro 06]
	// For case-insensitive, pass in CseqCaseIndependent or wrap P with new CseqCaseIndependent(P)
	public Cell<Integer> SASearch(final Cseq P, final Integer[] A, final Cseq T) {
		final int m = P.length();
		final int n = T.length();
		int sp = 0;
		int st = n;
		while (sp < st) {
			final int s = (sp + st) / 2;
			// if (P.compareTo(T.subSequence(A[s], A[s] + m - 1)) > 0) {
			if (P.compareTo(T.subSequence(A[s], A[s] + m)) > 0) {
				sp = s + 1;
			} else {
				st = s;
			}
		}
		int ep = sp - 1;
		int et = n;
		while (ep < et) {
			final int e = iceilDiv(ep + et, 2);
			// if (P.compareTo(T.subSequence(A[e], A[e] + m - 1)) == 0) {
			if (P.compareTo(T.subSequence(A[e], A[e] + m)) == 0) {
				ep = e;
			} else {
				et = e - 1;
			}
		}
		return new Cell<Integer>(sp, ep);
	}

	public int SAcount(final String s) {
		final Cseq p1 = new Fchar(s);// Cseq(s);
		final Cell<Integer> c1 = SASearch(p1, suffixArray, text);
		return (c1.cdr() - c1.car() + 1);
	}

	public int SAcount(final Cseq p1) {
		final Cell<Integer> c1 = SASearch(p1, suffixArray, text);
		return (c1.cdr() - c1.car() + 1);
	}

	public int SAcount(final Fchar p1) {
		final Cell<Integer> c1 = SASearch(p1, suffixArray, text);
		return (c1.cdr() - c1.car() + 1);
	}

	/*******************************************************************************************************************
	 * Method SAget constructs an iterator over the set of descriptions that contain the content.
	 * 
	 * @param s
	 *            Content requested
	 * @return An iterator over the Entry descriptions
	 */

	public Iterator<S> SAget(final String s) {
		final Cseq p1 = new Fchar(s);// Cseq(s);
		final Cell<Integer> c1 = SASearch(p1, suffixArray, text);
		final Set<S> resSet = new HashSet<S>();
		for (int i = c1.car(); i <= c1.cdr(); i++) {
			resSet.add(segDescriptions.get(getSegmentForPos(suffixArray[i])));
		}
		return resSet.iterator();
	}

	public Iterator<S> SAgetPhrase(final String s) {
		final Cseq p1 = new Cseq(' ' + s.trim() + ' ');
		final Cell<Integer> c1 = SASearch(p1, suffixArray, text);
		final Set<S> resSet = new HashSet<S>();
		for (int i = c1.car(); i <= c1.cdr(); i++) {
			resSet.add(segDescriptions.get(getSegmentForPos(suffixArray[i])));
		}
		return resSet.iterator();
	}

	public BitSet BSgetPhrase(final String s) {
		return BSgetEntity(' ' + s.trim() + ' ');
	}

	public BitSet[] BSgetAnyAllWords(final String s) {
		return BSgetAnyAllWords(Arrays.asList(s.split(" +")).iterator());
	}

	public BitSet BSgetAllWords(final String s) {
		return BSgetAllWords(Arrays.asList(s.split(" +")).iterator());
	}

	public BitSet BSgetAnyWord(final String s) {
		return BSgetAnyWord(Arrays.asList(s.split(" +")).iterator());
	}

	public BitSet BSgetEntity(final String s) {
		return BSgetEntity(new Fchar(s));// Cseq(s);)
	}

	public BitSet BSgetEntity(final Fchar p1) {
		final Cell<Integer> c1 = SASearch(p1, suffixArray, text);
		final BitSet bs = new BitSet();
		for (int i = c1.car(); i <= c1.cdr(); i++) {
			bs.set(getSegmentForPos(suffixArray[i]));
		}
		return bs;
	}

	public BitSet[] mkArray(final BitSet... bs) {
		return bs;
	}

	public BitSet[] BSgetAnyAllWords(final Iterator<String> it) {
		final BitSet bOr = new BitSet();
		final BitSet bAnd = new BitSet();
		if (it.hasNext()) {
			BitSet bs = BSgetPhrase(it.next());
			bOr.or(bs);
			bAnd.or(bs);
			while (it.hasNext()) {
				bs = BSgetPhrase(it.next());
				bOr.or(bs);
				bAnd.and(bs);
			}
		}
		return mkArray(bOr, bAnd);
	}

	public BitSet BSgetAllWords(final Iterator<String> it) {
		if (it.hasNext()) {
			final BitSet bs = BSgetPhrase(it.next());
			while (bs.cardinality() > 0 && it.hasNext()) {
				bs.and(BSgetPhrase(it.next()));
			}
			return bs;
		}
		return new BitSet();
	}

	public BitSet BSgetAnyWord(final Iterator<String> it) {
		if (it.hasNext()) {
			final BitSet bs = BSgetPhrase(it.next());
			while (it.hasNext()) {
				bs.or(BSgetPhrase(it.next()));
			}
			return bs;
		}
		return new BitSet();
	}

	public Iterator<S> SAgetAllWords(final String s) {
		return SAgetDescriptors(BSgetAllWords(s));
	}

	public Iterator<S> SAgetAllWords(final Iterator<String> it) {
		return SAgetDescriptors(BSgetAllWords(it));
	}

	public Iterator<S> SAgetAnyWord(final String s) {
		return SAgetDescriptors(BSgetAnyWord(s));
	}

	public Iterator<S> SAgetAnyWord(final Iterator<String> it) {
		return SAgetDescriptors(BSgetAnyWord(it));
	}

	public Iterator<S> SAgetDescriptors(final BitSet bs) {
		final Set<S> resSet = new HashSet<S>();
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
			resSet.add(segDescriptions.get(i));
		}
		return resSet.iterator();
	}

	/*******************************************************************************************************************
	 * Phi maps suffix T[A[i]..n] to suffix T[A[i]+1..n]
	 */
	public void makePhi() {
		final int rA[] = new int[suffixArray.length];
		for (int i = 1; i < suffixArray.length; i++) {
			rA[suffixArray[i]] = i;
		}
		phi = new int[suffixArray.length];
		for (int i = 1; i < suffixArray.length; i++) {
			if (suffixArray[i] == 0) {
				phi[1] = rA[0];
				continue;
			}
			phi[rA[suffixArray[i] - 1]] = i;
		}
	}

	public Integer[][] findRuns() {
		int i = 1;
		final List<Integer[]> runList = new ArrayList<Integer[]>();
		while (i < suffixArray.length) {
			int rLen = 0;
			while (i + rLen + 1 < suffixArray.length && phi[i + rLen + 1] - phi[i + rLen] == 1) {
				rLen++; // TODO: caseInsensitive
			}
			if (rLen > 0) {
				runList.add(new Integer[] { i, i + rLen });
			}
			i = i + rLen + 1;
		}
		return runList.toArray(new Integer[runList.size()][]);
	}

	PrintStream open(final String filename) {
		if (filename == null || filename.length() == 0) { return System.out; }
		try {
			final FileOutputStream fileOutputStream = new FileOutputStream(filename, true);
			return new PrintStream(fileOutputStream);
		} catch (final Exception e) {
			e.printStackTrace();
			return System.out;
		}
	}

	void shut(final OutputStream stream) {
		if (stream == null || stream.equals(System.out) || stream.equals(System.err)) { return; }
		try {
			stream.flush();
		} catch (final Exception e1) {
			e1.printStackTrace();
		}
		try {
			stream.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	// void logInput(final List<String> texts, final List<String> names) {
	// if (CommandInvoker.log.getLevel() != null && CommandInvoker.log.getLevel().equals(org.apache.log4j.Level.DEBUG))
	// {
	// CommandInvoker.log.debug("Enter MSA.pp at " + new Date() + " with #texts=" + texts.size() + "; #names" +
	// names.size());
	// for (int i = 0; i < Math.min(texts.size(), names.size()); i++) {
	// CommandInvoker.log.debug("Item " + i + " : " + names.get(i) + " :: " + texts.get(i));
	// // System.out.println("Input " + i + " : " + names.get(i)+ " :: " + texts.get(i));
	// }
	// }
	// }

}
