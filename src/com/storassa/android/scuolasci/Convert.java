package com.storassa.android.scuolasci;


import java.io.IOException;
import java.io.InputStream;

public class Convert {
   
   /**
    * Convert InputStream to String
    * @throws IOException 
    */
    public static String inputStreamToString(InputStream in) throws IOException {
       String result;
       java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
       result = s.hasNext() ? s.next() : "";
       s.close();
       in.close();
       
       return result;
    }
	/**
	 * Converts char array into byte array by stripping high byte.
	 */
	public static byte[] charToByteArray(char[] carr) {
		if (carr == null) {
			return null;
		}
		byte[] barr = new byte[carr.length];
		for (int i = 0; i < carr.length; i++) {
			barr[i] = (byte) carr[i];
		}
		return barr;
	}

	/**
	 * Converts char array to byte array using provided encoding.
	 */
	public static byte[] charToByteArray(char[] carr, String charset)
			throws Exception {
		return new String(carr).getBytes(charset);
	}

	/**
	 * Converts char array into ASCII array.
	 * 
	 * @see #toAscii(char)
	 */
	public static byte[] charToAsciiArray(char[] carr) {
		if (carr == null) {
			return null;
		}
		byte[] barr = new byte[carr.length];
		for (int i = 0; i < carr.length; i++) {
			barr[i] = (byte) charToAscii(carr[i]);
		}
		return barr;
	}

	/**
	 * Converts char sequence into byte array. Chars are truncated to byte size.
	 */
	public static byte[] charToByteArray(CharSequence charSequence) {
		if (charSequence == null) {
			return null;
		}
		byte[] barr = new byte[charSequence.length()];
		for (int i = 0; i < barr.length; i++) {
			barr[i] = (byte) charSequence.charAt(i);
		}
		return barr;
	}

	/**
	 * Converts char sequence into ASCII array.
	 */
	public static byte[] charToAsciiArray(CharSequence charSequence) {
		if (charSequence == null) {
			return null;
		}
		byte[] barr = new byte[charSequence.length()];
		for (int i = 0; i < barr.length; i++) {
			barr[i] = (byte) charToAscii(charSequence.charAt(i));
		}
		return barr;
	}

	// ---------------------------------------------------------------- to char
	// array

	/**
	 * Converts byte array to char array by simply extending.
	 */
	public static char[] charToCharArray(byte[] barr) {
		if (barr == null) {
			return null;
		}
		char[] carr = new char[barr.length];
		for (int i = 0; i < barr.length; i++) {
			carr[i] = (char) barr[i];
		}
		return carr;
	}

	/**
	 * Converts byte array of specific encoding to char array.
	 */
	public static char[] charToCharArray(byte[] barr, String charset)
			throws Exception {
		return new String(barr, charset).toCharArray();
	}

	/**
	 * Returns ASCII value of a char. In case of overload, 0x3F is returned.
	 */
	public static int charToAscii(char c) {
		if (c <= 0xFF) {
			return c;
		} else {
			return 0x3F;
		}
	}

	// ---------------------------------------------------------------- find

	/**
	 * Match if one character equals to any of the given character.
	 * 
	 * @return <code>true</code> if characters match any character from given
	 *         array, otherwise <code>false</code>
	 */
	public static boolean charEqualsOne(char c, char[] match) {
		for (char aMatch : match) {
			if (c == aMatch) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds index of the first character in given array the matches any from
	 * the given set of characters.
	 * 
	 * @return index of matched character or -1
	 */
	public static int charFindFirstEqual(char[] source, int index, char[] match) {
		for (int i = index; i < source.length; i++) {
			if (charEqualsOne(source[i], match) == true) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds index of the first character in given array the matches any from
	 * the given set of characters.
	 * 
	 * @return index of matched character or -1
	 */
	public static int charFindFirstEqual(char[] source, int index, char match) {
		for (int i = index; i < source.length; i++) {
			if (source[i] == match) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds index of the first character in given array the differs from the
	 * given set of characters.
	 * 
	 * @return index of matched character or -1
	 */
	public static int charFindFirstDiff(char[] source, int index, char[] match) {
		for (int i = index; i < source.length; i++) {
			if (charEqualsOne(source[i], match) == false) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds index of the first character in given array the differs from the
	 * given set of characters.
	 * 
	 * @return index of matched character or -1
	 */
	public static int charFindFirstDiff(char[] source, int index, char match) {
		for (int i = index; i < source.length; i++) {
			if (source[i] != match) {
				return i;
			}
		}
		return -1;
	}

	// ---------------------------------------------------------------- is char
	// at

	public static boolean isCharAtEqual(char[] source, int index, char match) {
		if ((index < 0) || (index >= source.length)) {
			return false;
		}
		return source[index] == match;
	}

	public static boolean isCharAtEqual(CharSequence source, int index,
			char match) {
		if ((index < 0) || (index >= source.length())) {
			return false;
		}
		return source.charAt(index) == match;
	}

	// ---------------------------------------------------------------- is

	/**
	 * Returns <code>true</code> if character is a white space. White space
	 * definition is taken from String class (see: <code>trim()</code>
	 */
	public static boolean charIsWhitespace(char c) {
		return c <= ' ';
	}

	/**
	 * Returns <code>true</code> if specified character is lowercase ASCII. If
	 * user uses only ASCIIs, it is much much faster.
	 */
	public static boolean charIsLowercaseLetter(char c) {
		return (c >= 'a') && (c <= 'z');
	}

	/**
	 * Returns <code>true</code> if specified character is uppercase ASCII. If
	 * user uses only ASCIIs, it is much much faster.
	 */
	public static boolean charIsUppercaseLetter(char c) {
		return (c >= 'A') && (c <= 'Z');
	}

	public static boolean charIsLetter(char c) {
		return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'));
	}

	public static boolean charIsDigit(char c) {
		return (c >= '0') && (c <= '9');
	}

	public static boolean charIsLetterOrDigit(char c) {
		return charIsDigit(c) || charIsLetter(c);
	}

	public static boolean charIsWordChar(char c) {
		return charIsDigit(c) || charIsLetter(c) || (c == '_');
	}

	public static boolean charIsPropertyNameChar(char c) {
		return charIsDigit(c) || charIsLetter(c) || (c == '_') || (c == '.')
				|| (c == '[') || (c == ']');
	}

	// ----------------------------------------------------------------
	// conversions

	/**
	 * Uppers lowercase ASCII char.
	 */
	public static char charToUpperAscii(char c) {
		if (charIsLowercaseLetter(c)) {
			c -= (char) 0x20;
		}
		return c;
	}

	/**
	 * Lowers uppercase ASCII char.
	 */
	public static char charToLowerAscii(char c) {
		if (charIsLowercaseLetter(c)) {
			c += (char) 0x20;
		}
		return c;
	}

	public static String exceptionToString(Throwable e) {
		// TODO Auto-generated method stub
		String error = e.toString();
		if (e.getMessage() != null)
			error += "Message: " + e.getMessage();
		if (e.getLocalizedMessage() != null)
			error += "\nLocalized message: " + e.getLocalizedMessage();
		if (e.getCause() != null)
			error += "; Cause: " + e.getCause().toString();
		error += "\n";
		for (StackTraceElement ste : e.getStackTrace())
			error += ste.toString() + "\n";
		return error;
	}
}
