package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import util.coll.Pair;

public class TimeLogger {

	String label = null;

	public TimeLogger(final String label) {
		this.label = label;
	}

	public TimeLogger() {
		this("TIMEREPORT -- Reporting");
	}

	Stack<Object> info = new Stack<Object>();
	Stack<Object> stk = new Stack<Object>();

	synchronized public void start(final String task) {
		info.push(task);
		info.push(System.nanoTime());
	}

	synchronized public void stop() {
		info.push(System.nanoTime());
	}

	synchronized public boolean report() {
		final List<String> reportList = new ArrayList<String>();
		final Map<String, Pair<Integer, Long>> summaryTimes = new HashMap<String, Pair<Integer, Long>>();
		Object item;
		while (!info.empty()) {
			item = info.pop();
			if (item instanceof String) {
				if (stk.size() < 2) {
					reportList.add("?? possible ERROR computing report (improper call sequence)");
					for (final Object o : stk) {
						reportList.add("REPORT: unmatched" + o.toString());
					}
					stk.push(item);
					return false;
				}
				final Object o1 = stk.pop();
				final Object o2 = stk.pop();
				final Long nsec = -(Long) o1 + (Long) o2;
				final Double nsecUsed = nsec / 1E9;
				Pair<Integer, Long> mresult = summaryTimes.get(item);
				if (mresult == null) {
					mresult = new Pair<Integer, Long>(0, 0L);
				}
				mresult.setS(mresult.s() + 1);
				mresult.setT(mresult.t() + nsec);
				summaryTimes.put((String) item, mresult);
				reportList.add(String.format("TimeLogger %s::T[%s]=%11.8f sec", label, String.format("%40s", (String) item),
						nsecUsed));
			} else {
				stk.push(item);
			}
		}
		clear();
		for (int i = reportList.size() - 1; i >= 0; i--) {
			System.out.println(reportList.get(i));
		}
		for (final Map.Entry<String, Pair<Integer, Long>> me : summaryTimes.entrySet()) {
			final Pair<Integer, Long> v = me.getValue();
			System.out.printf("%30s : %10d total; %8d count; %12.8f avg\n", me.getKey(), v.t(), v.s(), v.t() / (1D * v.s()));
		}
		return true;
	}

	synchronized void clear() {
		info.clear();
		stk.clear();
	}

	synchronized void stackCheck(final String where) {
		stop();
		start("stackcheck" + where);
		stop();
		if (!report()) {
			System.out.println("ERROR " + where);
			System.exit(-1);
		}
		start("contine");
	}
}