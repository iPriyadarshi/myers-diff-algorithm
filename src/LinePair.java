public class LinePair {
    private final byte[] oldLine;
    private final byte[] newLine;

    public LinePair(byte[] oldLine, byte[] newLine) {
        this.oldLine = oldLine;
        this.newLine = newLine;
    }

    public byte[] getOldLine() {
        return oldLine;
    }

    public byte[] getNewLine() {
        return newLine;
    }
}