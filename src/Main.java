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

            // lines
            if(command.equals("lines")){
                List<DiffOperation> ops = MyersDiff.diff(linesA, linesB);
                DiffWriter.write(ops, System.out);
                return;
            }

            // highlight
            List<DiffOperation> ops = MyersDiff.diff(linesA, linesB);
            HighlightDiffWriter.write(ops, System.out);


        } catch (IOException e) {
            System.err.println("error: " + e.getMessage());
            System.exit(2);
        }
    }
}
