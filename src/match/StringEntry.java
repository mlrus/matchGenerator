package match;

import interfaces.EntryData;

import java.util.Iterator;

public class StringEntry implements EntryData<StringEntry> {
	private String string;
	private String ident;

	StringEntry(final String string) {
		this(string, null);
	}

	public StringEntry(final String ident, final String string) {
		this.string = string;
		this.ident = ident;
	}

	public String getIdent() {
		return ident;
	}

	public String getText() {
		return string;
	}

	public int compare(final String o1, final String o2) {
		return o1.compareTo(o2);
	}

	public int compareTo(final String o) {
		return compareTo(o);
	}

	public int compare(final StringEntry o1, final StringEntry o2) {
		return o1.ident.compareTo(o2.ident);
	}

	public int compareTo(final StringEntry o) {
		return this.compareTo(o);
	}

	public StringEntry getData() {
		return this;
	}

	public void setData(final StringEntry data) {
		this.ident = data.ident;
		this.string = data.string;

	}

	public void setIdent(final String ident) {
		this.ident = ident;
	}

	public void setText(final String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return getIdent() + " " + getText();
	}

	public static Iterator<StringEntry> getIterator(final Iterator<String> cit) {
		class StringEntryIterator implements Iterator<StringEntry> {
			Iterator<String> it;

			StringEntryIterator(final Iterator<String> it) {
				this.it = it;
			}

			public boolean hasNext() {
				return it.hasNext();
			}

			public StringEntry next() {
				final String s = it.next();
				final String[] line = s.split(",", 2);
				return (new StringEntry(line[0], line[1]));
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		}
		return new StringEntryIterator(cit);
	}
}
