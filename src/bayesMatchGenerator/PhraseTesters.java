package bayesMatchGenerator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

public class PhraseTesters {

	boolean debug = false;

	class ProbInfo_len_revProb implements Comparator<ProbInfo> {
		public int compare(final ProbInfo o1, final ProbInfo o2) {
			int cmp = 0;
			if (cmp == 0) {
				cmp = o1.len < o2.len
						? -1
						: o1.len > o2.len
								? 1
								: 0;
			}
			if (cmp == 0) {
				cmp = o1.prob < o2.prob
						? 1
						: o1.prob > o2.prob
								? -1
								: 0;
			}

			if (cmp == 0) {
				cmp = o1.text.compareTo(o2.text);
			}
			return cmp;
		}
	}

	// defines
	// final PhraseTester minProbPhraseTester
	// final PhraseTester dummyPhraseTester

	final PhraseTester minProbPhraseTester = (new Object() {
		Comparator<ProbInfo> comparator = new ProbInfo_len_revProb();

		class PT implements PhraseTester {
			public boolean testPhrases(final List<ProbInfo> info) {
				Collections.sort(info, comparator);
				if (debug) {
					for (ProbInfo pi : info) {
						System.out.println("PI: " + pi);
					}
				}
				final int threshLen = info.get(0).len;
				final float lowThresh = info.get(0).prob / 2f;
				ProbInfo item;
				for (final ListIterator<ProbInfo> li = info.listIterator(); li.hasNext();) {
					item = li.next();
					boolean remove = item.len > threshLen || item.prob < lowThresh;
					if (debug) {
						System.out.println((remove
								? "drop"
								: "keep") + " " + item);
					}
					if (remove) {
						li.remove();
					}
				}
				return true;
			}
		}

		PhraseTester gPT() {
			return new PT();
		}
	}).gPT();

	final PhraseTester dummyPhraseTester = (new Object() {
		class PT implements PhraseTester {
			public boolean testPhrases(final List<ProbInfo> info) {
				return true;
			}
		}

		PhraseTester gPT() {
			return new PT();
		}
	}).gPT();

}
