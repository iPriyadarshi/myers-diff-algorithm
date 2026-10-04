import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MyersDiff {

    public static List<DiffOperation> diff(List<byte[]> a, List<byte[]> b) {

        if (a.isEmpty() && b.isEmpty()) {
            return new ArrayList<>();
        }

        int n = a.size();
        int m = b.size();

        // common prefix that does not need to be diffed.
        int prefix = 0;

        while (prefix < n && prefix < m && same(a.get(prefix), b.get(prefix))) {
            prefix++;
        }

        // common suffix that does not need to be diffed.
        int suffix = 0;

        while (suffix < n - prefix && suffix < m - prefix && same(a.get(n - 1 - suffix), b.get(m - 1 - suffix))) {
            suffix++;
        }

        List<DiffOperation> result = new ArrayList<>();

        // Add the common prefix.
        for (int i = 0; i < prefix; i++) {
            result.add(new DiffOperation(' ', a.get(i)));
        }

        // Run Myers only on the part that actually differs.
        List<byte[]> middleA = a.subList(prefix, n - suffix);

        List<byte[]> middleB = b.subList(prefix, m - suffix);

        result.addAll(diffMiddle(middleA, middleB));

        // Add the common suffix.
        for (int i = n - suffix; i < n; i++) {
            result.add(new DiffOperation(' ', a.get(i)));
        }

        return result;
    }

    private static List<DiffOperation> diffMiddle(List<byte[]> a, List<byte[]> b) {

        if (a.isEmpty() && b.isEmpty()) {
            return new ArrayList<>();
        }

        // If one side is empty, all lines are inserts or deletes.
        if (a.isEmpty()) {

            List<DiffOperation> operations = new ArrayList<>(b.size());

            for (byte[] line : b) {
                operations.add(new DiffOperation('+', line));
            }
            return operations;
        }

        if (b.isEmpty()) {

            List<DiffOperation> operations = new ArrayList<>(a.size());

            for (byte[] line : a) {
                operations.add(new DiffOperation('-', line));
            }

            return operations;
        }

        int n = a.size();
        int m = b.size();

        int maxD = n + m;

        // diagonal: k = x - y
        // Java arrays cannot use negative indexes, so shift
        // every diagonal by "maxD".
        int offset = maxD;

        int[] v = new int[2 * maxD + 1]; // v[offset+k] = the furthest x position reached on diagonal k.

        List<int[]> trace = new ArrayList<>();

        v[offset + 1] = 0;

        // d = number of edits used so far.
        // For each d, explore every reachable diagonal.
        for (int d = 0; d <= maxD; d++) {
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
                    return reconstruct(a, b, trace, d);
                }
            }

            // Save the frontier after processing this edit distance.
            int[] snapshot = new int[d + 1];

            for (int k = -d; k <= d; k += 2) {
                snapshot[(k + d) / 2] = v[offset + k];
            }

            trace.add(snapshot);
        }

        return new ArrayList<>();
    }

    private static List<DiffOperation> reconstruct(List<byte[]> a, List<byte[]> b, List<int[]> trace, int d) {
        List<DiffOperation> ops = new ArrayList<>();

        int x = a.size();
        int y = b.size();

        for (int currentD = d; currentD > 0; currentD--) {
            // trace[currentD - 1] contains the frontier before the current edit.
            int previousD = currentD - 1;
            int[] previousV = trace.get(previousD);

            int k = x - y;

            int previousK;

            if (k == -currentD || (k != currentD && get(previousV, previousKIndex(k - 1, previousD)) < get(previousV, previousKIndex(k + 1, previousD)))) {
                previousK = k + 1;  // insertion
            } else {
                previousK = k - 1;    // deletion
            }

            int previousX = get(previousV, previousKIndex(previousK, previousD));
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

    private static int previousKIndex(int k, int d) {
        // At edit distance d, only diagonals -d, -d+2, ..., d exist.
        return (k + d) / 2;
    }

    private static int get(int[] values, int index) {
        // A diagonal outside the previous frontier is treated as unreachable.
        if (index < 0 || index >= values.length) {
            return Integer.MIN_VALUE / 2;
        }

        return values[index];
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

        return Arrays.equals(a, b);
    }
}
