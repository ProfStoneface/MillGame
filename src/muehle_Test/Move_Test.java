package muehle_Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import muehle.Move;

public class Move_Test {

    @Test
    public void testValidMove() {
        int from = 1;
        int to = 2;
        int remove = 3;

        Move move = new Move(from, to, remove);

        assertEquals(from, move.FROM);
        assertEquals(to, move.TO);
        assertEquals(remove, move.REMOVE);
    }

    @Test
    public void testValidMoveWithNowhere() {
        int from = Move.NOWHERE;
        int to = 2;
        int remove = Move.NOWHERE;

        Move move = new Move(from, to, remove);

        assertEquals(from, move.FROM);
        assertEquals(to, move.TO);
        assertEquals(remove, move.REMOVE);
    }

    @Test
    public void testInvalidFromArgument() {
        int from = -3;
        int to = 2;
        int remove = 3;

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Move(from, to, remove);
        });
    }

    @Test
    public void testInvalidToArgument() {
        int from = 1;
        int to = 24;
        int remove = 3;

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Move(from, to, remove);
        });
    }

    @Test
    public void testInvalidRemoveArgument() {
        int from = 1;
        int to = 2;
        int remove = 24;

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Move(from, to, remove);
        });
    }


}


