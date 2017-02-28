package interfaces;

import bayesMatchGenerator.PhraseType;

public interface MatchInfoIF {

	public abstract PhraseType getPhraseType();

	public abstract float getProb();

}