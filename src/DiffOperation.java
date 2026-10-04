public class DiffOperation {

    // ' ' = line exists in both files
    // '-' = line exists only in A
    // '+' = line exists only in B
    private final char type;

    // We keep bytes rather than String because Part A must work
    // even when the input is not valid UTF-8.
    private final byte[] line;

    public DiffOperation(char type, byte[] line) {
        this.type = type;
        this.line = line;
    }

    public char getType() {
        return type;
    }

    public byte[] getLine() {
        return line;
    }
}