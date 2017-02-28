package match;

import interfaces.EntryData;

import java.util.BitSet;
import java.util.Iterator;

public interface SearchSA<S extends EntryData<S>> {

	BitSet BSgetPhrase(final String s);

	BitSet[] BSgetAnyAllWords(final String s);

	BitSet BSgetAllWords(final String s);

	BitSet BSgetAnyWord(final String s);

	BitSet BSgetEntity(final String s);

	BitSet BSgetEntity(final Fchar p1);

	BitSet[] BSgetAnyAllWords(final Iterator<String> it);

	BitSet BSgetAllWords(final Iterator<String> it);

	BitSet BSgetAnyWord(final Iterator<String> it);

}