package exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParsingExceptionTest {
    @Test
    void testEmpty() {
        try {
            throw new ParsingException();
        } catch (ParsingException e) {
            assertEquals(e.getMessage(), null);
        }
    }

    @Test
    void testMessage() {
        String msg = "somebody once told me the world is gonna roll me. I ain't the sharpest tool in the shed.";
        try {
            throw new ParsingException(msg);
        } catch (ParsingException e) {
            assertEquals(e.getMessage(), msg);
        }
    }
}
