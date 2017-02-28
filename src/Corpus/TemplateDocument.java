/**
 * 
 */
package Corpus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

class TemplateDocument {
	protected final Document doc;

	TemplateDocument(final Iterable<String> fieldNames) {
		doc = new TemplateFactory(fieldNames).getDocument();
	}

	int getNumfields() {
		return doc.getFields().size();
	}

	@SuppressWarnings("unchecked")
	List<String> getFieldnames() {
		final List<String> fieldNames = new ArrayList<String>();
		final List<Field> fields = doc.getFields();
		for (int i = 0; i < fields.size(); i++) {
			fieldNames.add(fields.get(i).name());
		}
		return fieldNames;
	}

	@SuppressWarnings("unchecked")
	protected void setValues(final Iterable<String> values) {
		final Iterator<Field> fieldIterator = doc.getFields().iterator();
		final Iterator<String> valueIterator = values.iterator();
		while (fieldIterator.hasNext() && valueIterator.hasNext()) {
			fieldIterator.next().setValue(valueIterator.next());
		}
	}
}