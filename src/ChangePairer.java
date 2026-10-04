import java.util.ArrayList;
import java.util.List;

public class ChangePairer {
    public static List<LinePair> pair(List<DiffOperation> operations) {
        List<LinePair> pairs = new ArrayList<>();

        int i = 0;

        while (i < operations.size()) {

            // keep lines are not part of a change block.
            if (operations.get(i).getType() == ' ') {
                i++;
                continue;
            }

            List<byte[]> deletions = new ArrayList<>();
            List<byte[]> insertions = new ArrayList<>();

            while (i < operations.size() && operations.get(i).getType() != ' ') {
                DiffOperation operation = operations.get(i);

                if (operation.getType() == '-') {
                    deletions.add(operation.getLine());
                } else {
                    insertions.add(operation.getLine());
                }

                i++;
            }

            // pair the j-th deletion with the j-th insertion,
            int count = Math.min(deletions.size(), insertions.size());

            for (int j = 0; j < count; j++) {
                pairs.add(new LinePair(deletions.get(j), insertions.get(j)));
            }
        }

        return pairs;
    }
}