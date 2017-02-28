package util.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ResultItemsList implements Comparator<ResultItems>, List<ResultItems> {

	List<ResultItems> resultItemsList;

	ResultItemsList() {
		resultItemsList = new ArrayList<ResultItems>();
	}

	public void add(final int index, final ResultItems element) {
		resultItemsList.add(index, element);
	}

	public boolean add(final ResultItems o) {
		return resultItemsList.add(o);
	}

	public boolean addAll(final Collection<? extends ResultItems> c) {
		return resultItemsList.addAll(c);
	}

	public boolean addAll(final int index, final Collection<? extends ResultItems> c) {
		return resultItemsList.addAll(index, c);
	}

	public void clear() {
		resultItemsList.clear();
	}

	public boolean contains(final Object o) {
		return resultItemsList.contains(o);
	}

	public boolean containsAll(final Collection<?> c) {
		return resultItemsList.containsAll(c);
	}

	@Override
	public boolean equals(final Object o) {
		return resultItemsList.equals(o);
	}

	public ResultItems get(final int index) {
		return resultItemsList.get(index);
	}

	@Override
	public int hashCode() {
		return resultItemsList.hashCode();
	}

	public int indexOf(final Object o) {
		return resultItemsList.indexOf(o);
	}

	public boolean isEmpty() {
		return resultItemsList.isEmpty();
	}

	public Iterator<ResultItems> iterator() {
		return resultItemsList.iterator();
	}

	public int lastIndexOf(final Object o) {
		return resultItemsList.lastIndexOf(o);
	}

	public ListIterator<ResultItems> listIterator() {
		return resultItemsList.listIterator();
	}

	public ListIterator<ResultItems> listIterator(final int index) {
		return resultItemsList.listIterator(index);
	}

	public ResultItems remove(final int index) {
		return resultItemsList.remove(index);
	}

	public boolean remove(final Object o) {
		return resultItemsList.remove(o);
	}

	public boolean removeAll(final Collection<?> c) {
		return resultItemsList.removeAll(c);
	}

	public boolean retainAll(final Collection<?> c) {
		return resultItemsList.retainAll(c);
	}

	public ResultItems set(final int index, final ResultItems element) {
		return resultItemsList.set(index, element);
	}

	public int size() {
		return resultItemsList.size();
	}

	public List<ResultItems> subList(final int fromIndex, final int toIndex) {
		return resultItemsList.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return resultItemsList.toArray();
	}

	public <T> T[] toArray(final T[] a) {
		return resultItemsList.toArray(a);
	}

	public int compare(final ResultItems o1, final ResultItems o2) {
		return o1.compareTo(o2);
	}
}
