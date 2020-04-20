package exception;

// Exception class when playlist parser could not parse a file
public class ParsingException extends RuntimeException {
    // EFFECTS: Constructor for message
    public ParsingException(String msg) {
        super(msg);
    }
    
    // EFFECTS: constructor to create a new ParsingException without a message
    public ParsingException() {
        super();
    }
}
