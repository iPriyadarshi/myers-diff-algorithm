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
                // The changed section has ended.
                ranges.add(new HighlightRange(rangeStart, position - 1));
                rangeStart = -1;
            }

            if (type == ' ' || type == target) {
                position++;
            }
        }

        // Close a range that reaches the end of the string.
        if (rangeStart != -1) {
            ranges.add(new HighlightRange(rangeStart, position - 1));
        }

        return ranges;
    }
}