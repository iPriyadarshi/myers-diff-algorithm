import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LineReader {
    public static List<byte[]> split(byte[] data) {

        List<byte[]> lines = new ArrayList<>();

        int start = 0;

        // Everything before '\n' is one line.
        // The '\n' itself is not part of the line.
        for (int i = 0; i < data.length; i++) {
            if (data[i] == '\n') {
                lines.add(Arrays.copyOfRange(data, start, i));
                start = i + 1;
            }
        }

        // If there are bytes left after the final '\n',
        // they form the last line.
        if (start < data.length) {
            lines.add(Arrays.copyOfRange(data, start, data.length));
        }

        return lines;
    }
}