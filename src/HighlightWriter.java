import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class HighlightWriter {

    public static void writeRange(OutputStream out, List<HighlightRange> oldRanges, List<HighlightRange> newRanges) throws IOException {

        // every highlight line starts with "? ".
        out.write('?');
        out.write(' ');

        // print the ranges belonging to the old line.
        writeRanges(out, oldRanges);

        out.write(' ');
        out.write('|');
        out.write(' ');

        // print the ranges belonging to the new line.
        writeRanges(out, newRanges);

        out.write('\n');
    }

    private static void writeRanges(OutputStream out, List<HighlightRange> ranges) throws IOException {

        // no changed characters on this side.
        if (ranges.isEmpty()) {
            out.write('.');
            return;
        }

        for (int i = 0; i < ranges.size(); i++) {

            if (i > 0) {
                out.write(',');
            }

            HighlightRange range = ranges.get(i);

            writeNumber(out, range.getStart());

            out.write('-');

            writeNumber(out, range.getEnd());
        }
    }

    private static void writeNumber(OutputStream out, int number) throws IOException {
        byte[] bytes = Integer.toString(number).getBytes();

        out.write(bytes);
    }
}
