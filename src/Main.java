import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        boolean known = args.length == 3 && (args[0].equals("lines") || args[0].equals("highlight"));
        if (!known) {
            System.err.println("usage: Main lines|highlight A_PATH B_PATH");
            System.exit(2);
        }
        String command = args[0];
        String aPath = args[1];
        String bPath = args[2];

        try {
            byte[] fileA = Files.readAllBytes(Path.of(aPath));
            byte[] fileB = Files.readAllBytes(Path.of(bPath));

            List<byte[]> linesA = LineReader.split(fileA);
            List<byte[]> linesB = LineReader.split(fileB);

            // keep operations for the common prefix
            int common = Math.min(linesA.size(), linesB.size());
            for (int i = 0; i < common; i++) {
                if (same(linesA.get(i), linesB.get(i))) {
                    DiffOperation op = new DiffOperation(' ', linesA.get(i));
                }
            }

        } catch (IOException e) {
            System.err.println("error: " + e.getMessage());
            System.exit(2);
        }
    }

    // Compare two lines byte-for-byte.
    // We cannot use String.equals() because the input may not contain valid UTF-8.
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
