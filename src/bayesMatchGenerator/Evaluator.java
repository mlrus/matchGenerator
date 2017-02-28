package bayesMatchGenerator;

import java.util.Collection;
import java.util.Set;

public interface Evaluator<S> {
	Object evaluate(Collection<Set<S>> c);

	Object evalItem(Set<S> c);

	boolean more(Set<S> node);
}
