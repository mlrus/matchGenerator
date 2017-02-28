// This is unpublished source code. Michah Lerner 2006, 2007, 2008

package util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Numeric processing of vectors of floats, doubles or integers. Provides entropy computation and information score,
 * which the commons-math does not provide. Also provides several other functions (min, max, sum, variance) although
 * those are better provided by commons-math.
 * 
 * @author Michah.Lerner
 * 
 */
public final class Numerics {

	public static class Factorial {
		public final static int baseFactorialCeiling = 40;
		private final static List<BigInteger> factorials;
		private static BigInteger multiplier = BigInteger.ONE;

		static {
			factorials = new ArrayList<BigInteger>();
			factorials.add(BigInteger.ZERO);
			factorials.add(BigInteger.ONE);
			for (int i = 2; i < baseFactorialCeiling; i++) {
				multiplier = multiplier.add(BigInteger.ONE);
				factorials.add(multiplier.multiply(factorials.get(factorials.size() - 1)));
			}
		}

		public static BigInteger factValue(final int num) {
			while (num >= factorials.size()) {
				multiplier = multiplier.add(BigInteger.ONE);
				factorials.add(multiplier.multiply(factorials.get(factorials.size() - 1)));
			}
			return factorials.get(num);
		}

		public static BigInteger bincoef(final int m, final int n) {
			if (m == n) {
				return BigInteger.ONE;
			}
			final BigInteger numerator = factValue(m);
			final BigInteger denom = factValue(m - n).multiply(factValue(n));
			return numerator.divide(denom);
		}

		public static double itemProb(final int numItems, final int numPicked) {
			return 1D / bincoef(numItems, numPicked).doubleValue();
		}

		public static void testFactorials(String[] args) {
			if (args.length == 0) {
				args = new String[] { "2:2", "2:1", "3:3", "3:2", "3:1", "4:4", "4:3", "4:2", "4:1", "5:5", "5:4", "5:3", "5:2",
						"5:1" };
			}
			for (final String s : args) {
				final String[] p = s.split(":");
				final Integer[] v = new Integer[p.length];
				for (int i = 0; i < v.length; i++) {
					v[i] = Integer.parseInt(p[i]);
				}
				System.out.println(s + " " + bincoef(v[0], v[1]) + " " + itemProb(v[0], v[1]));
			}
		}
	}

	public final static Double closeEnough = 5E-5;
	double max = Float.NEGATIVE_INFINITY;
	double min = Float.POSITIVE_INFINITY;
	double sum = (float) 0.0;
	double mean = Float.NaN;
	double sd = Double.NaN;

	Numerics(final float[] v) {
		for (final float element : v) {
			min = Math.min(min, element);
			max = Math.max(max, element);
			sum += element;
		}
		mean = sum / v.length;
		float ssd = (float) 0.0;
		for (final float element : v) {
			ssd += (square(element - mean));
		}
		if (v.length > 1) {
			sd = Math.sqrt(ssd / (v.length - 1.0D));
		}
	}

	Numerics(final double[] v) {
		for (final double element : v) {
			min = Math.min(min, element);
			max = Math.max(max, element);
			sum += element;
		}
		mean = sum / v.length;
		double ssd = 0D;
		for (final double element : v) {
			ssd += (square(element - mean));
		}
		if (v.length > 1) {
			sd = Math.sqrt(ssd / (v.length - 1.0D));
		}
	}

	class NumericVector<T extends Number> {
		double sum(final List<T> in) {
			double s = 0.0;
			for (final Number n : in) {
				s += n.doubleValue();
			}
			return s;
		}
	}

	static double H(final double[] v) {
		double s = 0.0;
		final double prob = 1.0 / v.length;
		for (final double element : v) {
			s -= (prob) * Math.log(element);
		}
		return s;
	}

	static double log2(final double v) {
		return Math.log(v) / Math.log(2.0D);
	}

	static double entropy(final double[] v) { // Entropy computation
		double ntrop = 0.0;
		double t = 0.0;
		for (final double d : v) {
			t += d;
		}
		for (final double d : v) {
			final double prob = d / t;
			ntrop -= prob * Math.log(prob);
		}
		return ntrop;
	}

	public static double entropy(final float[] v) { // Entropy computation
		double ntrop = 0.0;
		double t = 0.0;
		for (final float d : v) {
			t += d;
		}
		for (final float d : v) {
			final double prob = d / t;
			ntrop -= prob * Math.log(prob);
		}
		return ntrop;
	}

	public static Double entropy(final Collection<Double> v) { // Entropy computation
		Double ntrop = 0.0;
		Double t = 0.0;
		for (final Double d : v) {
			t += d;
		}
		for (final Double d : v) {
			final Double prob = d.doubleValue() / t;
			ntrop -= prob * Math.log(prob);
		}
		return ntrop;
	}

	static double entropy(final int[] v) {
		int tot = 0;
		for (final int element : v) {
			tot += element;
		}
		final double[] fv = new double[v.length];
		for (int i = 0; i < v.length; i++) {
			fv[i] = (double) v[i] / tot;
		}
		System.out.println("intEnt: ");
		for (int i = 0; i < v.length; i++) {
			System.out.print("p(n=" + i + ")=" + fv[i] + "; ");
		}
		System.out.println();
		return entropy(fv);
	}

	static double square(final double v) {
		return v * v;
	}

	static int sumInt(final int[] iv) {
		int s = 0;
		for (final int element : iv) {
			s += element;
		}
		return s;
	}

	static float sumFloat(final float[] v) {
		// TODO:numerically stable sum
		float s = (float) 0.0;
		for (final float element : v) {
			s += element;
		}
		return s;
	}

	static double sumDouble(final double[] v) {
		// TODO:numerically stable sum
		double s = 0.0D;
		for (final double element : v) {
			s += element;
		}
		return s;
	}

	static String printVec(final int[] ivec) {
		return printVec(ivec, "%-3d ");
	}

	static String printVec(final int[] ivec, final String fmt) {
		final StringBuffer sb = new StringBuffer();
		for (final int element : ivec) {
			sb.append(String.format(fmt, element));
		}
		return sb.toString();
	}

	static String printVec(final double[] fvec) {
		return printVec(fvec, "%-3.1f ");
	}

	static String printVec(final double[] fvec, final String fmt) {
		final StringBuffer sb = new StringBuffer();
		for (final double element : fvec) {
			sb.append(String.format(fmt, element));
		}
		return sb.toString();
	}

	/**
	 * @param v1
	 * @param v2
	 * @return relative error
	 */
	public static float relativeError(final Float v1, final Float v2) {
		final float d1 = Math.abs(v1);
		final float d2 = Math.abs(v2);
		return (float) (Math.abs(d1 - d2) / (d1 + d2) / 2D);
	}

	/**
	 * @param v1
	 * @param v2
	 * @return relative error
	 */
	public static Double relativeError(final Double v1, final Double v2) {
		if (v1 == null && v2 == null) {
			return null;
		}
		final Double d1 = Math.abs(v1);
		final Double d2 = Math.abs(v2);
		return (Math.abs(d1 - d2) / (d1 + d2) / 2D);
	}

	/**
	 * Get min of the elements that are neither NaN nor Null..
	 * 
	 * @param vlist
	 * @return min of the items that are neither NaN nor Null.
	 */
	public static Number nmin(final Number... vlist) {
		return nmin(Arrays.asList(vlist));
	}

	public static Number nmin(final List<Number> vlist) {
		Double result = Double.NaN;
		for (final Number d : vlist) {
			final Double dd = d.doubleValue();
			if (dd == null || dd.isNaN()) {
				continue;
			}
			if (result == null || result.isNaN()) {
				result = dd;
			} else {
				result = Math.min(result, dd);
			}
		}
		return result;
	}

	/**
	 * Get max of the elements that are neither NaN nor Null..
	 * 
	 * @param vlist
	 * @return max of the items that are neither NaN nor Null.
	 */
	public Number nmax(final Number... vlist) {
		return nmax(Arrays.asList(vlist));
	}

	public Number nmax(final List<Number> vlist) {
		Double result = Double.NaN;
		for (final Number d : vlist) {
			final Double dd = d.doubleValue();
			if (dd == null || dd.isNaN()) {
				continue;
			}
			if (result == null || result.isNaN()) {
				result = dd;
			} else {
				result = Math.max(result, dd);
			}
		}
		return result;
	}

	public static double euclidNorm(final Double[] terms) {
		double total = 0D;
		for (final Double d : terms) {
			if (!d.isNaN()) {
				total += d * d;
			}
		}
		return Math.sqrt(total) / Math.sqrt(terms.length * 1D);
	}

	public static Double euclidNorm(final Collection<Double> terms) {
		double total = 0D;
		for (final Double d : terms) {
			if (!d.isNaN()) {
				total += d * d;
			}
		}
		return Math.sqrt(total) / Math.sqrt(terms.size() * 1D);
	}

	public static Double product(final Collection<Double> terms) {
		Double val = 1D;
		for (final Double term : terms) {
			val *= term;
		}
		return terms.size() == 0
				? Double.NaN
				: val;
	}

	public static Double minMaxProd(final Collection<Double> terms) {
		return min(terms) * max(terms);
	}

	public static Double min(final Collection<Double> terms) {
		Double val = 1D;
		for (final Double term : terms) {
			val = Math.min(val, term);
		}
		return terms.size() == 0
				? Double.NaN
				: val;
	}

	public static Double max(final Collection<Double> terms) {
		Double val = 1D;
		for (final Double term : terms) {
			val = Math.max(val, term);
		}
		return terms.size() == 0
				? Double.NaN
				: val;
	}

	/**
	 * Frobenius norm
	 * 
	 * @return sqrt of sum of squares of all elements.
	 */

	public static double normF(final double[][] A) {
		double f = 0;
		final int m = A.length;
		final int n = A[0].length;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				f = hypot(f, A[i][j]);
			}
		}
		return f;
	}

	/**
	 * sqrt(a^2 + b^2) without under/overflow.
	 */
	public static double hypot(final double a, final double b) {
		double r;
		if (Math.abs(a) > Math.abs(b)) {
			r = b / a;
			r = Math.abs(a) * Math.sqrt(1 + r * r);
		} else if (b != 0) {
			r = a / b;
			r = Math.abs(b) * Math.sqrt(1 + r * r);
		} else {
			r = 0.0;
		}
		return r;
	}

	/**
	 * Penalties for short words; approximate back-off for short words.
	 * 
	 * @param args
	 *            the collection of words to obtain an aggregated score for
	 */
	public static Double lengthFactor(final Collection<String> args) {
		final int len = args.size();
		final Double baseFactor[] = { 0D, 1.5, 1.6 };// , 1.7 };
		final int numLens = baseFactor.length - 1;
		final Integer[] wlen = new Integer[numLens + 1];
		for (int i = 0; i <= numLens; i++) {
			wlen[i] = 0;
		}
		int tdel = 0;
		for (final String s : args) {
			final int l = s.length();
			if (l <= numLens) {
				wlen[l]++;
				tdel++;
			}
		}
		Double penalty = 1D;
		Double sumTerms = 0D;
		for (int i = 1; i <= numLens; i++) {
			final Double term = wlen[i] * Math.pow(baseFactor[i], len / (double) (1 + wlen[i]));
			penalty += term;
			sumTerms += term;
		}
		if (sumTerms == 0) {
			return 1D;
		}
		final Double r = sumTerms / penalty;
		if (r.isNaN() || r.isInfinite() || r < 0 || r > 1) {
			return 1D;
		}
		return r;
	}

	public static int minlength(final Collection<String> arg) {
		if (arg == null || arg.size() < 1) {
			return 0;
		}
		int minlength = Integer.MAX_VALUE;
		for (final String word : arg) {
			minlength = Math.min(minlength, word.length());
		}
		return minlength;
	}

	public static Collection<Number> toNumberList(final String[] args) {
		final List<Number> vals = new ArrayList<Number>();
		for (final String s : args) {
			if (!s.matches("([0-9]+(\\.[0-9]*))|(\\.[0-9]+)")) {
				vals.add(Double.NaN);
			} else {
				vals.add(Double.parseDouble(s));
			}
		}
		return vals;
	}

	public static Collection<Double> toDoubleList(final String[] args) {
		final List<Double> vals = new ArrayList<Double>();
		for (final String s : args) {
			if (!s.matches("([0-9]+(\\.[0-9]*))|(\\.[0-9]+)")) {
				vals.add(Double.NaN);
			} else {
				vals.add(Double.parseDouble(s));
			}
		}
		return vals;
	}

	public static Collection<Double> toDoubleList(final double[] args) {
		final List<Double> vals = new ArrayList<Double>();
		for (final double s : args) {
			vals.add(s);
		}
		return vals;
	}

	public static Collection<Integer> range(final int from, final int to) {
		final List<Integer> result = new ArrayList<Integer>();
		for (Integer i = from; i < to; i++) {
			result.add(i);
		}
		return result;
	}

}
