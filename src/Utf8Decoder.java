import java.nio.charset.StandardCharsets;

public class Utf8Decoder {
    public static int[] decode(byte[] bytes) {
        String text = new String(bytes, StandardCharsets.UTF_8);
        /*
         * codePoints() works with Unicode code points rather than UTF-16 char units.
         *
         * "😀".length()       = 2
         * "😀".codePointCount = 1
         */
        return text.codePoints().toArray();
    }

    public static String toString(int[] codePoints) {
        return new String(codePoints,0,codePoints.length);
    }
}
