import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyersDiff {

    public static List<DiffOperation> diff(List<byte[]> a, List<byte[]> b) {
        
        if (a.isEmpty() && b.isEmpty()) {
            return new ArrayList<>();
        }

        int n = a.size();
        int m = b.size();

        int maxD = n + m;

        // diagonal: k = x - y
        // Java arrays cannot use negative indexes, so shift
        // every diagonal by "maxD".
        int offset = maxD;

        int[] v = new int[2 * maxD + 1]; // v[k] = the furthest x position reached on diagonal k.

        List<int[]> trace = new ArrayList<>();

        v[offset + 1] = 0;

        // d = number of edits used so far.
        // For each d, explore every reachable diagonal.
        for (int d = 0; d <= maxD; d++) {
            trace.add(v.clone());   // save current frontier
            for (int k = -d; k <= d; k += 2) {

                int x;

                if (k == -d || (k != d && v[offset + k - 1] < v[offset + k + 1])) {
                    x = v[offset + k + 1];  // insertion
                } else {
                    x = v[offset + k - 1] + 1;  // deletion
                }

                int y = x - k;

                // Follow the diagonal while the lines are equal.
                while (x < n && y < m && same(a.get(x), b.get(y))) {
                    x++;
                    y++;
                }

                // Store the furthest position reached on this diagonal.
                v[offset + k] = x;

                if (x >= n && y >= m) {
                    return reconstruct(a, b, trace, d, offset);
                }
            }
        }

        return new ArrayList<>();
    }

    private static List<DiffOperation> reconstruct(List<byte[]> a, List<byte[]> b, List<int[]> trace, int d, int offset) {
        List<DiffOperation> ops = new ArrayList<>();

        int x = a.size();
        int y = b.size();

        for (int currentD = d; currentD > 0; currentD--) {
            int[] previousV = trace.get(currentD);

            int k = x - y;

            int previousK;

            if (k == -currentD || (k != currentD && previousV[offset + k - 1] < previousV[offset + k + 1])) {
                previousK = k + 1;  // insertion
            } else {
                previousK = k - 1;    // deletion
            }

            int previousX = previousV[offset + previousK];
            int previousY = previousX - previousK;

            // Everything between the previous point and the current point on the same diagonal is unchanged.
            while (x > previousX && y > previousY) {
                ops.add(new DiffOperation(' ', a.get(x - 1)));
                x--;
                y--;
            }

            if (x == previousX) {
                ops.add(new DiffOperation('+', b.get(y - 1)));    // insertion
                y--;
            } else {
                ops.add(new DiffOperation('-', a.get(x - 1)));    // deletion
                x--;
            }
        }

        // matching lines may be present before the first edit
        while (x > 0 && y > 0) {
            ops.add(new DiffOperation(' ', a.get(x - 1)));
            x--;
            y--;
        }

        // remaining lines in 'a' can only be deletions
        while (x > 0) {
            ops.add(new DiffOperation('-', a.get(x - 1)));
            x--;
        }

        // remaining lines in 'b' can only be insertions
        while (y > 0) {
            ops.add(new DiffOperation('+', b.get(y - 1)));
            y--;
        }

        Collections.reverse(ops);

        // normalize consecutive -/+ ops
        // deletion must come before insertion
        return normalize(ops);
    }

    private static List<DiffOperation> normalize(List<DiffOperation> operations) {
        List<DiffOperation> result = new ArrayList<>();

        int i = 0;

        while (i < operations.size()) {
            DiffOperation operation = operations.get(i);

            if (operation.getType() == ' ') {
                result.add(operation);
                i++;
                continue;
            }

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

            result.addAll(deletions);
            result.addAll(insertions);
        }

        return result;
    }

    private static boolean same(byte[] a, byte[] b) {

        if (a.length != b.length) {
            return false;
        }

        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }
}
