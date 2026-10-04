import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CharacterDiff {

    public static List<CharacterOperation> diff(int[] a, int[] b) {

        int n = a.length;
        int m = b.length;

        int maxD = n + m;
        int offset = maxD;

        int[] v = new int[2 * maxD + 1];    // v[k] = the furthest x position reached on diagonal k.

        v[offset + 1] = 0;

        List<int[]> trace = new ArrayList<>();

        for (int d = 0; d <= maxD; d++) {
            trace.add(v.clone());

            for (int k = -d; k <= d; k += 2) {

                int x;

                if (k == -d || (k != d && v[offset + k - 1] < v[offset + k + 1])) {
                    x = v[offset + k + 1];  // Insert a code point from B.
                } else {
                    x = v[offset + k - 1] + 1;  // Delete a code point from A.
                }

                int y = x - k;

                // Follow matching code points.
                while (x < n && y < m && a[x] == b[y]) {
                    x++;
                    y++;
                }

                v[offset + k] = x;

                // reached the end of both strings.
                if (x >= n && y >= m) {
                    return reconstruct(a, b, trace, d, offset);
                }
            }
        }

        return new ArrayList<>();
    }

    private static List<CharacterOperation> reconstruct(int[] a, int[] b, List<int[]> trace, int d, int offset) {

        List<CharacterOperation> operations = new ArrayList<>();

        int x = a.length;
        int y = b.length;

        for (int currentD = d; currentD > 0; currentD--) {

            int[] previousV = trace.get(currentD);

            int k = x - y;

            int previousK;

            if (k == -currentD || (k != currentD && previousV[offset + k - 1] < previousV[offset + k + 1])) {
                previousK = k + 1;  // insertion
            } else {
                previousK = k - 1;  // deletion
            }

            int previousX = previousV[offset + previousK];

            int previousY = previousX - previousK;

            // Walk backwards through matching code points.
            while (x > previousX && y > previousY) {
                operations.add(new CharacterOperation(' ', a[x - 1]));
                x--;
                y--;
            }

            // insertion/deletion
            if (x == previousX) {
                operations.add(new CharacterOperation('+', b[y - 1]));
                y--;
            } else {
                operations.add(new CharacterOperation('-', a[x - 1]));
                x--;
            }
        }

        // Add matching code points before the first edit.
        while (x > 0 && y > 0) {
            operations.add(new CharacterOperation(' ',a[x - 1]));
            x--;
            y--;
        }

        Collections.reverse(operations);

        return operations;
    }
}
