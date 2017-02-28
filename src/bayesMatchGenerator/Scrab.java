package bayesMatchGenerator;

import interfaces.Chooser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import util.data.IO;

public class Scrab {
	Chooser<CharSequence> chooser;

	HashMap<Character, Integer> valueMap;

	void mkValueMap() {
		final String valueString = "f=4,b=3,i=1,x=8,n=1,o=1,v=4,l=1,g=2,e=1,c=3,m=3,h=4";
		valueMap = new HashMap<Character, Integer>();
		for (final String s : valueString.split(",")) {
			final String s2[] = s.split("=");
			valueMap.put(s2[0].charAt(0), Integer.parseInt(s2[1]));
		}
	}

	int points(final CharSequence cs) {
		int total = 0;
		for (int i = 0; i < cs.length(); i++) {
			final Character ch = cs.charAt(i);
			final Integer v = valueMap.get(cs.charAt(i));
			if (v != null) {
				total += v;
			} else {
				total += 1;
			}
		}
		return total;
	}

	Set<List<CharSequence>> gen(final List<CharSequence> must, final List<CharSequence> may) {
		final Set<List<CharSequence>> result = new HashSet<List<CharSequence>>();
		final List<List<CharSequence>> basis = chooser.chooseCombIF(may);
		for (final List<CharSequence> cseq : basis) {
			if (must != null && must.size() > 0) {
				cseq.addAll(must);
			}
			final List<List<CharSequence>> re = chooser.permute(cseq);
			result.addAll(re);
		}
		return result;
	}

	Scrab() {
		chooser = new ChooseCombIF<CharSequence>();
		mkValueMap();
	}

	static CharSequence mkSeq(final List<CharSequence> l) {
		final StringBuffer sb = new StringBuffer();
		for (final CharSequence c : l) {
			sb.append(c);
		}
		return sb.toString();
	}

	Set<String> vocab;

	void mkVocab(final String filename) {
		vocab = new HashSet<String>();
		vocab.addAll(IO.readInput(filename, true));
	}

	static List<CharSequence> mkSeq(final CharSequence in) {
		final List<CharSequence> dat = new ArrayList<CharSequence>();
		for (int i = 0; i < in.length(); i++) {
			dat.add(in.subSequence(i, i + 1));
		}
		return dat;
	}

	public static void main(final String[] args) {
		final Scrab scrab = new Scrab();
		scrab.mkVocab("/Users/mlrus/Documents/DictionariesAndData/TWL06.txt");
		final List<List<CharSequence>> plist = new ArrayList<List<CharSequence>>();
		final List<CharSequence> may = mkSeq("DLOWEI".toLowerCase());

		final CharSequence[] pblm = new CharSequence[] {
		// "PONENT", "HEATER", "FIB", "REDRY", "HEM", "SAVE", "SAME", "EN", "NEED",
				// "T", "P", "O", "N",
				//		"EASE", "NEED" };
				"N", "F", "R", "M", "O", "O", "B" };
		for (int i = 0; i < pblm.length; i++) {
			pblm[i] = pblm[i].toString().toLowerCase();
		}
		if (pblm.length == 0) {
			plist.addAll(scrab.gen(null, may));
		} else {
			for (final CharSequence letter : pblm) {
				final List<CharSequence> must = new ArrayList<CharSequence>();
				must.add(letter);
				plist.addAll(scrab.gen(must, may));
			}
		}
		final Set<String> res = new HashSet<String>();
		for (final List<CharSequence> l : plist) {
			final CharSequence seq = mkSeq(l);
			if (scrab.vocab.contains(seq)) {
				res.add(String.format("%03d %s", scrab.points(seq), seq.toString()));
			}
		}
		final List<String> result = new ArrayList<String>(res);
		Collections.sort(result);
		for (final String s : result) {
			System.out.println(s);
		}
	}
}
