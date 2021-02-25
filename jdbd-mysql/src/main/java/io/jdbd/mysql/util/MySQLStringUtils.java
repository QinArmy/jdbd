package io.jdbd.mysql.util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class MySQLStringUtils extends org.qinarmy.util.StringUtils {

    protected MySQLStringUtils() {
        throw new UnsupportedOperationException();
    }


    public static byte[] getBytesNullTerminated(String text, Charset charset) {
        byte[] textBytes = text.getBytes(charset);
        byte[] bytes = new byte[textBytes.length + 1];

        System.arraycopy(textBytes, 0, bytes, 0, textBytes.length);
        bytes[textBytes.length] = 0;
        return bytes;
    }

    public static boolean isBinaryString(String text) {
        final int length = text.length();
        char ch;
        for (int i = 0; i < length; i++) {
            ch = text.charAt(i);
            if (ch != '0' && ch != '1') {
                return false;
            }
        }
        return true;
    }

    /**
     * Splits input into a list, using the given delimiter and skipping all between the given markers.
     * <p>
     * note:openMarker[i] and openMarker[i] is match.
     * </p>
     *
     * @param input       the string to split
     * @param openMarker  the string to split on
     * @param closeMarker characters which delimit the beginning of a text block to skip
     * @return the  list of strings, split by delimiter, maybe empty.
     * @throws IllegalArgumentException if an error occurs
     */
    public static List<String> split(String input, String delimiter, String openMarker, String closeMarker) {

        final char[] delimiterArray = MySQLObjects.requireNonNull(delimiter, "delimiter").toCharArray();
        final char[] openMarkerArray = MySQLObjects.requireNonNull(openMarker, "openMarker").toCharArray();
        final char[] closeMarkerArray = MySQLObjects.requireNonNull(closeMarker, "closeMarker").toCharArray();

        if (openMarkerArray.length != closeMarkerArray.length) {
            throw new IllegalArgumentException(String.format
                    ("openMarker[%s] and closeMarker[%s] not match.", openMarker, closeMarker));
        }

        final Stack<Character> openMarkerStack = new Stack<>();
        List<String> list = new ArrayList<>();

        final int size = MySQLObjects.requireNonNull(input, "input").length();
        int start = 0;
        char current, lastOpenMarker;
        for (int i = 0, openMarkerIndex, closeMarkerIndex, charCount = 0; i < size; i++) {
            current = input.charAt(i);
            if (Character.isWhitespace(current)) {
                continue;
            }
            charCount++;

            openMarkerIndex = indexMarker(current, openMarkerArray);
            closeMarkerIndex = indexMarker(current, closeMarkerArray);

            if (openMarkerIndex > -1 && (openMarkerStack.isEmpty()) | closeMarkerIndex < 0) {
                // current is open marker
                openMarkerStack.push(current);
                continue;
            } else if (openMarkerIndex > -1) {
                // current is both open marker and close marker . and openMarkerStack not empty
                lastOpenMarker = openMarkerStack.peek();
                if (lastOpenMarker == current) {
                    openMarkerStack.pop();
                } else {
                    openMarkerStack.push(current);
                }
                continue;
            } else if (closeMarkerIndex > -1) {
                // current is just close marker
                if (openMarkerStack.isEmpty()) {
                    throw createFormatException(input, i);
                }
                lastOpenMarker = openMarkerStack.peek();
                openMarkerIndex = indexMarker(lastOpenMarker, openMarkerArray);
                if (openMarkerIndex != closeMarkerIndex) {
                    throw createFormatException(input, i);
                }
                // marker match
                openMarkerStack.pop();
                continue;
            }
            // current is neither open marker nor close marker.
            if (!openMarkerStack.isEmpty()) {
                continue;
            }
            if (isDelimiter(current, delimiterArray)) {
                charCount--; //skip delimiter
                if (charCount == 0) {
                    throw createFormatException(input, i);
                }
                list.add(input.substring(start, i).trim());
                start = i + 1;
                charCount = 0;
            }

        }
        if (!openMarkerStack.isEmpty()) {
            throw new IllegalArgumentException(String.format("[%s] not close marker", input));
        }
        if (start < size) {
            list.add(input.substring(start));
        }
        return list;
    }

    public static boolean isMultiStatement(String sql) {
        return false;
    }

    public static boolean isSingleStatement(String sql) {
        return false;
    }

    /*################################## blow private static method ##################################*/


    /**
     * @see #split(String, String, String, String)
     */
    private static boolean isDelimiter(char current, char[] delimiterArray) {
        for (char c : delimiterArray) {
            if (c == current) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see #split(String, String, String, String)
     */
    private static int indexMarker(char current, final char[] markerArray) {

        for (int i = 0; i < markerArray.length; i++) {
            if (current == markerArray[i]) {
                return i;
            }
        }
        return -1;
    }

    private static IllegalArgumentException createFormatException(String input, int currentIndex) {
        final int len = input.length();
        if (currentIndex < 0 || currentIndex >= len) {
            throw new IllegalArgumentException("currentIndex error");
        }
        int start = currentIndex - 10;
        int end = currentIndex + 10;
        if (start < 0) {
            start = 0;
        }
        if (end >= len) {
            end = len;
        }
        throw new IllegalArgumentException(
                String.format("Index[%s] Char[%s] nearby[%s] format error."
                        , currentIndex, input.charAt(currentIndex), input.substring(start, end)));
    }

}
