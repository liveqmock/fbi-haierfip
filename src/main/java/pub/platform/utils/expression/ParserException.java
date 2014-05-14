package pub.platform.utils.expression;

public class ParserException extends Exception {
    String description;
    int start;
    int end;

    public ParserException(String description, int start, int end) {
        super(description + " : at " + start + "-" + end);
        this.description = description;
        this.start = start;
        this.end = end;
    }
}