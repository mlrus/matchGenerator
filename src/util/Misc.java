package util;

public class Misc {

	String spa = (new Object() {
		String mkspa(int n) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < n; i++) {
				sb.append(" ");
			}
			return sb.toString();
		}
	}).mkspa(100);
	String pad = spa;

}
