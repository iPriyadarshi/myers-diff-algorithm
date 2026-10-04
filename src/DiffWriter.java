import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class DiffWriter {

    public static void write(List<DiffOperation> operations, OutputStream out) throws IOException {
        for (DiffOperation operation : operations) {
            out.write(operation.getType());     // operation prefix
            out.write(operation.getLine());
            out.write('\n');
        }
    }
}
