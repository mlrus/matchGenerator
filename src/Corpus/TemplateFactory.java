/**
 * 
 */
package Corpus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;

final class TemplateFactory {

	Field.Index fieldIndex = Constants.FIELDINDEXTYPE;
	Field.Store fieldStore = Constants.FIELDSTORETYPE;
	Field.TermVector fieldTermVector = Constants.FIELDTERMVECTORTYPE;

	private final Iterator<String> fieldnames;

	TemplateFactory(final Iterable<String> fieldnames) {
		this.fieldnames = fieldnames.iterator();
	}

	Document getDocument() {
		final Iterable<Fieldable> fields = mkFields(fieldnames);
		final Document document = new Document();
		for (final Fieldable f : fields) {
			document.add(f);
		}
		return document;
	}

	private Iterable<Fieldable> mkFields(final Iterator<String> fieldnameIterator) {
		final List<Fieldable> fieldList = new ArrayList<Fieldable>();
		while (fieldnameIterator.hasNext()) {
			fieldList.add(new Field(fieldnameIterator.next(), "", fieldStore, fieldIndex, fieldTermVector));
		}
		return fieldList;
	}
}