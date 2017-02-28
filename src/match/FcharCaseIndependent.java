package match;

public class FcharCaseIndependent extends CseqCaseIndependent {
	char[] ch;
	int from, to;
	FcharCaseIndependent(final char[] ch) {
		this(ch,0,ch.length);
	}
	FcharCaseIndependent(final char[] ch, final int from, final int to) {
		this.ch=ch;
		this.from=from;
		this.to=to;
	}
	@Override
	public char charAt(final int index) {
		return ch[from+index];
	}
	@Override
	public int length() {
		return to-from+1;
	}
	@Override
	public FcharCaseIndependent subSequence(final int nfrom, final int nto) {
		return new FcharCaseIndependent(ch,this.from+nfrom,this.from+nto);
	}
	@Override
	public String toString() {
		return to-from+1+"::"+String.valueOf(ch,from,(to-from+1));
	}
}