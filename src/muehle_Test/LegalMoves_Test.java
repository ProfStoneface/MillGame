package muehle_Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import muehle.*;


public class LegalMoves_Test {
	private GameController game;
	private BoardModell modell;
	
	@BeforeEach                                         
	void setUp() {
		modell = new BoardModell();
		game = new GameController(modell);
		game.activePlayer = GameController.BLACK_PLAYER;	}

	@Test
	public void testIsLegalMove_InitialGame_MoveNowhere_ReturnsTrue() {
		game.gameState = GameController.PHASE_BEGINNING;
		Move move = new Move(Move.NOWHERE, 0, -1);
		assertTrue(LegalMoves.isLegalMove(move, game));
	}

	@Test
	public void testIsLegalMove_GameOver_ReturnsFalse() {
		game.gameState = GameController.PHASE_GAMEOVER;
		Move move = new Move(0, 0, 0);
		assertFalse(LegalMoves.isLegalMove(move, game));
	}

	@Test
	public void testIsLegalMove_Midgame_InvalidMove_ReturnsFalse() {
		game.gameState = GameController.PHASE_MIDGAME;
		Move move = new Move(0, 1, 0);
		assertFalse(LegalMoves.isLegalMove(move, game));
	}

	@Test
	public void testGetAllLegalMoves_BeginningGame_ReturnsValidMoves() {
		game.gameState = GameController.PHASE_BEGINNING;
		Move[] moves = LegalMoves.getAllLegalMoves(game);
		assertNotNull(moves);
		assertTrue(moves.length > 0);
	}

	@Test
	public void testGetAllLegalMoves_Midgame_ReturnsValidMoves() {
		game.gameState = GameController.PHASE_MIDGAME;
		byte [] array = {0, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2, 0, 2, 2, 2, 1, 0, 2, 0, 0, 0, 2, 0, 0};
		modell.setSquares(array);
		Move[] moves = LegalMoves.getAllLegalMoves(game);
		assertNotNull(moves);
		assertTrue(moves.length > 0);
	}

	@Test
	public void testGetAllLegalMoves_Endgame_ReturnsValidMoves() {
		game.gameState = GameController.PHASE_ENDGAME;
		byte [] array = {0, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2, 0, 2, 2, 2, 1, 0, 2, 0, 0, 0, 2, 0, 0};
		modell.setSquares(array);
		Move[] moves = LegalMoves.getAllLegalMoves(game);
		assertNotNull(moves);
		assertTrue(moves.length > 0);
	}

	@Test
	void testLegalMove() {
		Move move = new Move(-1,3,-1);
		game.gameState = GameController.PHASE_BEGINNING;
		boolean result = LegalMoves.isLegalMove(move, game);
		assertTrue(result);
	}
}


