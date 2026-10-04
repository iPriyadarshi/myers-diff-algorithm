import java.util.ArrayList;
import java.util.List;

public class RangeCalculator {
    public static List<HighlightRange> calculate(List<CharacterOperation> operations, char target) {

        List<HighlightRange> ranges = new ArrayList<>();

        // target: '-' -> positions in the old string
        // target: '+' -> positions in the new string

        int position = 0;

        int rangeStart = -1;

        for (CharacterOperation operation : operations) {

            char type = operation.getType();

            // does this operation belong to the string whose range we are calculating?
            boolean changed = (type == target);

            if (changed) {
                // start a new range if this is the first changed character in a consecutive group.
                if (rangeStart == -1) {
                    rangeStart = position;
                }
            } else if (rangeStart != -1){
                // The changed section has ended. End is exclusive
                ranges.add(new HighlightRange(rangeStart, position));
                rangeStart = -1;
            }

            if (type == ' ' || type == target) {
                position++;
            }
        }

        // Close a range that reaches the end of the string.
        if (rangeStart != -1) {
            ranges.add(new HighlightRange(rangeStart, position));
        }

        return mergeTouching(ranges);
    }

    private static List<HighlightRange> mergeTouching(List<HighlightRange> ranges) {

        if (ranges.isEmpty()) {
            return ranges;
        }

        List<HighlightRange> merged = new ArrayList<>();

        HighlightRange current = ranges.get(0);

        for (int i = 1; i < ranges.size(); i++) {

            HighlightRange next = ranges.get(i);

            // 1-3 and 3-4 become:1-4
            if (next.getStart() <= current.getEnd()) {
                current = new HighlightRange(current.getStart(), Math.max(current.getEnd(), next.getEnd()));
            } else {
                merged.add(current);
                current = next;
            }
        }

        merged.add(current);

        return merged;
    }
}