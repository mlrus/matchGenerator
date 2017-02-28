package match;

public class CseqCaseIndependent extends Cseq {

	@Override
	public int compare(final CharSequence o1, final CharSequence o2) {
		for(int p = 0; p<Math.min(o1.length(),o2.length()); p++) {
			final int cmp = Character.toLowerCase(o1.charAt(p))-Character.toLowerCase(o2.charAt(p));
			if(cmp!=0)return cmp;
		}
		return o1.length()-o2.length();
	}

	@Override
	public int compareTo(final CharSequence o) {
		for(int p=0; p<Math.min(length(),o.length()); p++) {
			final int cmp = Character.toLowerCase(this.charAt(p))-Character.toLowerCase(o.charAt(p));
			if(cmp!=0)return cmp;
		}
		return length()-o.length();
	}
}