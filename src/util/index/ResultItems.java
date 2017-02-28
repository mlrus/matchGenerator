package util.index;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import bayesMatchGenerator.PhraseType;

/***********************************************************************************************************************
 * Generic routines for sorting and comparing lists of lists of comparables
 * 
 * @author mlrus
 * 
 */

public class ResultItems implements Comparable<ResultItems> {
	private PhraseType pt;
	private List<List<String>> data;
	private String description;
	private String url;

	public ResultItems() {
		// TODO Auto-generated constructor stub
	}

	public ResultItems(final PhraseType pt, final List<List<String>> data, final String description, final String url) {
		super();
		this.setPt(pt);
		this.setData(data);
		this.setDescription(description);
		this.setUrl(url);
	}

	void add(final List<String> data) {
		this.getData().add(data);
	}

	public int compareTo(final ResultItems o) {
		int cmp = 0;
		if (cmp == 0) {
			cmp = getUrl().compareTo(o.getUrl());
		}
		if (cmp == 0) {
			cmp = getDescription().compareTo(o.getDescription());
		}
		if (cmp == 0) {
			final Iterator<List<String>> i1 = getData().iterator();
			final Iterator<List<String>> i2 = o.getData().iterator();
			cmp = compare(i1, i2);
		}
		if (cmp == 0) {
			cmp = getPt().compareTo(o.getPt());
		}

		return cmp;
	}

	<T extends Comparable<T>> int compare(final Iterator<List<T>> o1, final Iterator<List<T>> o2) {
		int cmp = 0;
		while (o1.hasNext() && o2.hasNext()) {
			cmp = cmp(o1.next(), o2.next());
			if (cmp != 0) { return cmp; }
		}
		if (!o1.hasNext()) { return -1; }
		return 1;
	}

	<T extends Comparable<T>> int cmp(final Iterable<T> o1, final Iterable<T> o2) {
		final Iterator<T> i1 = o1.iterator();
		final Iterator<T> i2 = o2.iterator();
		while (i1.hasNext() && i2.hasNext()) {
			final int cmp = i1.next().compareTo(i2.next());
			if (cmp != 0) { return cmp; }
		}
		if (!i1.hasNext()) { return -1; }
		return 1;
	}

	class OuterSorter<T extends Comparable<T>> implements Comparator<List<T>> {
		public int compare(final List<T> o1, final List<T> o2) {
			return cmp(o1, o2);
		}
	}

	public <T extends Comparable<T>> void sortOuterLists(final List<List<T>> l) {
		Collections.sort(l, new OuterSorter<T>());
	}

	<T extends Comparable<T>> void sortInnerLists(final List<List<T>> l) {
		for (final List<T> l2 : l) {
			Collections.sort(l2);
		}
	}

	void sortInnerOuterLists(final List<List<String>> l) {
		sortInnerLists(l);
		sortOuterLists(l);
	}

	void sortInner() {
		sortInnerLists(getData());
	}

	public void sortOuter() {
		sortOuterLists(getData());
	}

	public void sortInnerOuter() {
		sortInner();
		sortOuter();
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append(getPt() + " {");
		for (final Iterable<String> is : getData()) {
			sb.append("  [" + is.toString() + "]  ");
		}
		sb.append("} ");
		sb.append(getDescription() + " " + getUrl());
		return sb.toString();
	}

	public void setData(final List<List<String>> data) {
		this.data = data;
	}

	public List<List<String>> getData() {
		return data;
	}

	public void setPt(final PhraseType pt) {
		this.pt = pt;
	}

	public PhraseType getPt() {
		return pt;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}