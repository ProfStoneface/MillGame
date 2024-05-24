package muehle_Test;

import org.junit.jupiter.api.Test;
import muehle.GUIController;
import muehle.BoardModell;
import muehle.GameController;

import static org.junit.jupiter.api.Assertions.*;

public class GUIController_Test {

	@Test
	void clickOnBoard_ShouldUpdateSelectedSquares() {
		BoardModell mockModell = new BoardModell(); 
		GameController mockController = new GameController(); 
		GUIController guiController = new GUIController(mockModell, mockController);

		guiController.clickOnBoard(128, 118);
		
		assertEquals(1, guiController.getSquaresSelected());
//		assertEquals(0, guiController.getFrom());
//		assertEquals(1, guiController.getSelectedSquare1());
		assertEquals(-2, guiController.getSelectedSquare2());
		assertEquals(-2, guiController.getSelectedSquare3());
	}
	
}
