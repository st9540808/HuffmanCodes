import java.lang.reflect.*;

public class StringTraverse {
	public static void main(String[] args) {
		fieldTraverse(args[0]);
	}

	public static void fieldTraverse(String str) {
		Field field = null;
		try {
			field = String.class.getDeclaredField("value");
		} catch(NoSuchFieldException ex) {
			System.exit(1);
		}
		field.setAccessible(true);

		char ch;
		try {
			final char[] chars = (char[]) field.get(str);
			final int length = chars.length;
			for (int i = 0; i < length; ++i) {
				ch = chars[i];
			}
		} catch (Exception ex) {
			System.exit(1);
		}
	}

	public static void charAtTraverse(String str) {
		char ch;
		final int length = str.length();
		for (int i = 0; i < length; ++i) {
			ch = str.charAt(i);
		}
	}
}
