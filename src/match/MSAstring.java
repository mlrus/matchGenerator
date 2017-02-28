package match;

import java.util.Iterator;

import util.data.ContentIterator;

public class MSAstring extends MSAfunctions<StringEntry> {

	public MSAstring(final String filename) {
		final Iterator<StringEntry> contentIterator = StringEntry.getIterator(new ContentIterator().get(filename));
		buildSA(contentIterator);
	}

	public MSAstring() {
	}

	public void buildMSA(final Iterable<String> inputs) {
		for (final String content : inputs) {
			final String[] cnt = content.split(",", 2);
			if (cnt != null && cnt.length == 2) {
				final StringEntry se = new StringEntry(cnt[0], cnt[1]);
				this.addEntry(se);
			}
		}
		this.makeSuffixArray();
		this.lsa();
	}
}
