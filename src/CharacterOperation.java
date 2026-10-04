public class CharacterOperation {
    private final char type;
    private final int codePoint;

    public CharacterOperation(char type, int codePoint) {
        this.type = type;
        this.codePoint = codePoint;
    }

    public char getType() {
        return type;
    }

    public int getCodePoint() {
        return codePoint;
    }
}