// This is unpublished source code. Michah Lerner 2006, 2007, 2008

package interfaces;

import java.util.Collection;
import java.util.List;

import Corpus.Corpus;

public interface PredicateEvaluator<S> {

	float getCProbOfSet(final Collection<S> arg);

	float getCProbOfSet(List<S> words, final Collection<S> arg);

	float getFreqOfSet(final Collection<S> content);

	int getCountOfSet(final Collection<S> content);

	void newInput(final Collection<S> content);

	List<S> getInput();

	int getNumDocs();

	Corpus getCorpus();
}
