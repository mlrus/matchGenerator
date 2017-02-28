package interfaces;

public interface Pairs<S extends Comparable<? super S>, T extends Comparable<? super T>> {
	S S();

	S s();

	T T();

	T t();

	S setS(S s);

	T setT(T t);

	String toString();
}
