import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class HighlightDiffWriter {

    public static void write(List<DiffOperation> operations, OutputStream out) throws IOException {

        int i = 0;

        while (i < operations.size()) {

            DiffOperation operation = operations.get(i);

            // keep lines do not need character highlighting.
            if (operation.getType() == ' ') {
                writeOperation(operation, out);
                i++;
                continue;
            }

            // at a change block, Collect deletions and insertions separately.
            List<DiffOperation> deletions = new ArrayList<>();
            List<DiffOperation> insertions = new ArrayList<>();

            while (i < operations.size() && operations.get(i).getType() != ' ') {

                DiffOperation current = operations.get(i);

                if (current.getType() == '-') {
                    deletions.add(current);
                } else {
                    insertions.add(current);
                }

                i++;
            }

            // print all deletions first.
            for (DiffOperation deletion : deletions) {
                writeOperation(deletion, out);
            }

            // pair the j-th deletion with j-th insertion
            int pairCount = Math.min(deletions.size(), insertions.size());

            for (int j = 0; j < insertions.size(); j++) {

                DiffOperation insertion = insertions.get(j);

                writeOperation(insertion, out);

                if (j < pairCount) {

                    DiffOperation deletion = deletions.get(j);

                    writeHighlight(deletion.getLine(), insertion.getLine(), out);
                }
            }
        }
    }

    private static void writeOperation(DiffOperation operation, OutputStream out) throws IOException {
        out.write(operation.getType());
        out.write(operation.getLine());
        out.write('\n');
    }

    private static void writeHighlight(byte[] oldLine, byte[] newLine, OutputStream out) throws IOException {
        int[] oldCodePoints = Utf8Decoder.decode(oldLine);
        int[] newCodePoints = Utf8Decoder.decode(newLine);

        List<CharacterOperation> operations = CharacterDiff.diff(oldCodePoints, newCodePoints);

        List<HighlightRange> oldRanges = RangeCalculator.calculate(operations, '-');
        List<HighlightRange> newRanges = RangeCalculator.calculate(operations, '+');

        // Write: ? <old ranges> | <new ranges>
        HighlightWriter.writeRange(out, oldRanges, newRanges);
    }
}
