package util.data;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class DropWordsIf {
	final private static List<Integer> dropSchedule = mkDropschedule();

	static List<Integer> mkDropschedule() {
		final List<Integer> sched = new LinkedList<Integer>();

		for (final int i : new Integer[] { 15, 14, 13, 12, 11, 10, 9, 8 }) {
			sched.add(i);
			sched.add(1);
		}
		for (final int i : new Integer[] { 15, 14, 13, 12, 11, 10, 9, 8 }) {
			sched.add(i);
			sched.add(2);
		}
		for (final int i : new Integer[] { 15, 14, }) {
			sched.add(i);
			sched.add(3);
		}

		return sched;
	}

	public static void dropWordsIf(final List<String> words) {
		final ListIterator<Integer> it = dropSchedule.listIterator();
		final int targetLength = it.next();
		it.previous();
		while (it.hasNext() && words.size() > targetLength) {
			if (delmin(words, it.next(), it.next())) {
				break;
			}
		}
	}

	static boolean delmin(final List<String> words, final int ifLen, final int minLen) {
		if (words.size() > ifLen) {
			final ListIterator<String> li = words.listIterator(words.size() - 1);
			while (li.hasPrevious()) {
				if (li.previous().length() <= minLen) {
					li.remove();
					if (words.size() <= ifLen) {
						return true;
					}
				}
			}
			return false;
		}
		return true;
	}
}
