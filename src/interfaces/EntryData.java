package interfaces;

import java.util.Comparator;

public interface EntryData<S> extends Comparator<S>, Comparable<S> {
	String getText(); // Content piece

	S getData(); // Data

	String getIdent(); // (identifier)

	void setText(String text);

	void setData(S data);

	void setIdent(String ident);
}
