package muehle_Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import muehle.*;

public class MillAI_Test {
	private MillAI millAI;
	private GameController game;
	private BoardModell modell;

    @BeforeEach
    public void setUp() {
        millAI = new MillAI();
		modell = new BoardModell();
        game = new GameController(modell);
		game.activePlayer = GameController.BLACK_PLAYER;	
    }

    
    @Test
    public void testDepth2Search_ReturnsNotNull() {

        byte depth = 2;
        game.gameState = GameController.PHASE_MIDGAME;
		byte [] array = {0, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2, 0, 2, 2, 2, 1, 0, 2, 0, 0, 0, 2, 0, 0};
		modell.setSquares(array);
		
        Move result = millAI.depthSearch(game, depth);

        assertNotNull(result);
    }
    
    @Test
    public void testDepthSearch_AIWins_ReturnsNotNull() {

        byte depth = 2;
        game.gameState = GameController.PHASE_MIDGAME;
		byte [] array = {0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 0, 2, 0, 0, 1, 0, 2, 0, 0, 0, 2, 0, 0};
		modell.setSquares(array);
		
        Move result = millAI.depthSearch(game, depth);
        
        assertTrue(game.makeMoveAI(result, false),() -> "there's a problem ");
        assertNotNull(result);
    }
    
    @Test
    public void testDepthSearch_IlegalDepth_ReturnsNull() {

        byte depth = -1;
        game.gameState = GameController.PHASE_MIDGAME;
		byte [] array = {0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 0, 2, 0, 0, 1, 0, 2, 0, 0, 0, 2, 0, 0};
		modell.setSquares(array);
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			millAI.depthSearch(game, depth);
        });

    }

}

