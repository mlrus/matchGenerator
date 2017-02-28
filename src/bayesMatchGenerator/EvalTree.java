package bayesMatchGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.coll.PairNC;
import bayesMatchGenerator.MatchDescription;
import bayesMatchGenerator.PhraseType;
import bayesMatchGenerator.SetInfo;
import bayesMatchGenerator.TextHypothesis;
import bayesMatchGenerator.SetInfo.SI;

public class EvalTree<S> {
	// final Chooser<String> chooser;
	// final PredicateEvaluator evaluator;
	SetInfo<S> setInfo;
	boolean debug = true;

	EvalTree(final SetInfo<S> setInfo) {
		super();
		this.setInfo = setInfo;
	}

	void rtn(final Collection<String> content) {

		final int contentCnt = evaluator.getCountOfSet(content);

		final SI ci = siFactory.mkSetInfo((List<String>) content);

		final List<TextHypothesis> KWResults = new ArrayList<TextHypothesis>();

		final Map<List<String>, Float> valmap = ci.mkValmap(content.size());

		final List<PairNC<Float, List<String>>> sresult = new ArrayList<PairNC<Float, List<String>>>();
		for (final Map.Entry<List<String>, Float> me : valmap.entrySet()) {
			System.out.println(me.getKey());
			sresult.add(new PairNC<Float, List<String>>(me.getValue(), me.getKey()));
		}
		Collections.sort(sresult, new PairNC.SortCar<Float>());
		int maxNum = 10;
		final Set<Set<String>> covers = new HashSet<Set<String>>();
		for (final PairNC<Float, List<String>> p : sresult) {
			for (final List<String> cover : chooser.leaveOutOne(p.T())) {
				if (cover.size() > 3) {
					continue;
				}

				final Set<String> hs = new HashSet<String>(cover);
				if (!covers.contains(hs)) {
					covers.add(new HashSet<String>(p.T()));
					final TextHypothesis hyp = new TextHypothesis(new MatchDescription(PhraseType.Set, evaluator
							.getFreqOfSet(p.t())), p.t());
					KWResults.add(hyp);
					if (true || debug) {
						System.out.printf("ANS: %12.7e %s\n", p.S(), p.t().toString() + " :: " + hyp.toString());
					}
					break;
				}
			}

			if (maxNum-- <= 0) {
				break;
			}
		}
	}

	SI counter(final List<String> l, final String p) {
		final List<Integer> lin = new LinkedList<Integer>();
		final int c = evaluator.getCountOfSet(l);
		final SI thisSetExpansion = siFactory.mkSetInfo(c, l);
		if (l.size() == 1) {
			lin.add(c);
		} else {
			for (final List<String> l2 : chooser.leaveOutOne(l)) {
				final SI lin2 = counter(l2, p + "  ");
				thisSetExpansion.add(lin2);
				lin.add(0, lin2.count);
			}
			// final float est = thisSetExpansion.est();
			// System.out.printf(p + "%d [est %6.4f] [%s] %s [counter]\n", c, est, lin.toString(), l);

		}
		return thisSetExpansion;
	}

	// String expandPiece(final TextHypothesis th) {
	// class Piece extends TextHypothesis {
	// float sum;
	// float prod;
	// List<Float> freq;
	// List<String> s;
	//
	// public Piece(final TextHypothesis th) {
	// super(th);
	// sum = 0;
	// prod = 1;
	// freq = new ArrayList<Float>();
	// s = new ArrayList<String>();
	// }
	//
	// String toString(final List<Float> flist, final String format) {
	// final StringBuffer sb = new StringBuffer("[");
	// for (final float f : flist) {
	// sb.append(String.format(format, f) + ", ");
	// }
	// if (sb.length() > 1) {
	// sb.deleteCharAt(sb.length() - 2);
	// }
	// sb.setCharAt(sb.length() - 1, ']');
	// return sb.toString();
	// }
	//
	// public void add(final float freq, final String s) {
	// this.sum += freq;
	// this.prod *= freq;
	// this.freq.add(freq);
	// this.s.add(s);
	// }
	//
	// @Override
	// public String toString() {
	// return super.toString() + "==>"
	// + String.format("[%5.3f : %5.3f : %s : %s]", sum, prod, toString(freq, "%5.3f"), s.toString());
	// }
	// }
	// if (th.getList().size() < 2) { return ""; }
	// final StringBuffer sb = new StringBuffer();
	// final Piece piece = new Piece(th);
	// System.out.println("PIECE:" + th.getList());
	// final List<List<String>> c = chooser.leaveOutOne(th.getList());
	// for (final List<String> s : c) {
	// final float freq = evaluator.getFreqOfSet(s);
	// System.out.println("[ :: " + freq + " " + s + "]");
	// piece.add(freq, s.toString());
	// }
	// final Iterator<List<String>> it = c.iterator();
	//
	// while (it.hasNext()) {
	// final List<String> l = it.next();
	// final float freq = evaluator.getFreqOfSet(l);
	// sb.append(String.format("[%s/%5.3f]", l, freq));
	// if (it.hasNext()) {
	// sb.append(" ");
	// }
	// }
	// return piece.toString();
	// }

}
