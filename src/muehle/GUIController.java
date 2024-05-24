package muehle;

import javax.swing.Timer;

// Interface between GUIView and GameController
public class GUIController {
	public static BoardModell modell;
	private GameController controller;
	private Move move;
	private int from = -2; // with values, otherwise Code Wont Work
	private int to = -2; // with values, otherwise Code Wont Work
	private int remove = -1; // This Value if no Stone is Removed
	private int selectedSquare1 = -2; // with values, otherwise Code Wont Work
	private int selectedSquare2 = -2; // with values, otherwise Code Wont Work
	private int selectedSquare3 = -2; // with values, otherwise Code Wont Work
	private byte squaresSelected = 0; // Counter
	public byte[] temporaer = null;
	public boolean makesMill = false;
	public static boolean approved = true;
	public Timer timer;
	public boolean executed;
	public static boolean kiIsRunning = false;
	public static boolean moveDone = false;

	public final int[][] POSITION = { { 36, 36 }, { 315, 36 }, { 595, 36 }, { 128, 128 }, { 315, 128 }, { 503, 128 },
			{ 220, 220 }, { 315, 220 }, { 410, 220 }, { 36, 317 }, { 128, 317 }, { 220, 317 }, { 410, 317 },
			{ 505, 317 }, { 595, 317 }, { 220, 413 }, { 315, 413 }, { 410, 413 }, { 128, 505 }, { 315, 505 },
			{ 505, 505 }, { 36, 595 }, { 315, 595 }, { 595, 595 } };

	
	public GUIController(BoardModell modell, GameController controller) {
		this.modell = modell;
		this.controller = controller;
		temporaer = modell.getCurrentBoardCopy();

	}

	/**
	 * creates moves that match the phase of the game, calculates which square has been pressed.
	 * was pressed.
	 */
	public void clickOnBoard(int coordX, int coordY) {
		if (coordX > 697 || coordY > 697 || coordX <0 || coordY <0) {
			throw new IllegalArgumentException(
					"public clickOnBoard bad argument: Illegal Values for coordX and coordY");
		}
		int deltaX;
		int deltaY;
		temporaer = modell.getCurrentBoardCopy();
		executed = false;
		if (kiIsRunning) {
			coordX = 0;
			coordY = 0;
		}

		for (int square = 0; square < POSITION.length; square++) {
			deltaY = coordY - POSITION[square][1];
			deltaX = coordX - POSITION[square][0];
			//numbers represent size of sensitive fields
			if (deltaX <= 60 && deltaY <= 60 && deltaX >= -10 && deltaY >= -10) { 
				squaresSelected++;
				System.out.println("");
				System.out.println("Square " + square + " selected, #Selected: " + squaresSelected);

				// Check if inputs make sense in general, only control, none yet variables described
				if (squaresSelected == 2 && modell.getByIndex((byte) square) == controller.getOpponent()) {
					controller.setAusgabeText("Dieses Feld ist bereits belegt.");
					squaresSelected = 1; //Reset counter to last selection
					approved = false;
					throw new IllegalArgumentException(
							"public clickOnBoard bad argument: illegalField Selected: selectedSquare2 is not empty: "
									+ square);
				} else if (squaresSelected == 2 && modell.getByIndex((byte) square) == GameController.EMPTY) {
					approved = true;
					System.out.println("Approved wieder auf true gesetzt");
				}

				if (squaresSelected == 3) {
					// If the stone to be removed is part of a mill and not all opposing stones are in mills
					if (modell.isPartOfMill((byte) square) && !modell.allPiecesInMills(controller.getOpponent())) {
						squaresSelected = 2; // Reset counter to last selection
						approved = false;
						controller.setAusgabeText("Dieser Stein befindet sich in einer Mühle.");
						throw new IllegalArgumentException(
								"public clickOnBoard bad argument: illegalField Selected: Stone is part of mill. Square: "
										+ square);
					} // Field must contain opponent
					if (modell.getByIndex((byte) square) != controller.getOpponent()) {
						approved = false;
						controller.setAusgabeText("Dieses Feld enthält keinen Gegner.");
						squaresSelected = 2; //Reset counter to last selection
						selectedSquare3 = -2;
						throw new IllegalArgumentException(
								"public clickOnBoard bad argument: illegalField Selected: Field does not contain opponent. Square:"
										+ square);
					} // When last set error was fixed again by the player.
					if (!modell.isPartOfMill((byte) square) && modell.allPiecesInMills(controller.getOpponent())
							|| modell.getByIndex((byte) square) == controller.getOpponent()) {
						approved = true;

					}

				}

				// If entries were allowed during verification above.
				if (squaresSelected == 1) {
					selectedSquare1 = square;
				}
				//  Always used for the target position when moving
				if (squaresSelected == 2) {
					selectedSquare2 = square;
				}
				// Used only for selection stone to be removed
				if (squaresSelected == 3) {
					selectedSquare3 = square;
				}

			}
		}

		//Now the individual inputs are still controlled for the respective play phases
		//and the variables from, to , remove are initialize
		try

		{
			// Control and initialize variables for moves in game PHASE_BEGINNING
			if (controller.getGameState() == GameController.PHASE_BEGINNING) {

				// Press first time
				if (squaresSelected == 1) {
					if (modell.getByIndex((byte) selectedSquare1) == GameController.EMPTY) {  // Field must be empty
						from = -1;
						to = selectedSquare1;
						controller.setAusgabeText("Stein am gewünschten Ort platzieren.");
						approved = true;
					}
					if (modell.getByIndex((byte) selectedSquare1) != GameController.EMPTY) { // If occupied
						approved = false;
						controller.setAusgabeText("Dieses Feld ist bereits belegt.");
						squaresSelected = 0; // reset counter
						selectedSquare1 = -2;
						throw new IllegalArgumentException(
								"public clickOnBoard bad argument: selectedSquare1 is not empty");
					}
				}
			}

			// Initialize control and variables for moves in PHASE_MIDGAME or
			//PHASE_ENDGAME
			if ((controller.getGameState() == GameController.PHASE_MIDGAME)
					|| (controller.getGameState() == GameController.PHASE_ENDGAME)) {

				// Press first time, select stone to be moved
				if (from == -2 && squaresSelected == 1) {
					if (modell.getByIndex((byte) selectedSquare1) == controller.activePlayer) {
						approved = false;
						controller.setAusgabeText("Zielort für Stein auswählen.");
						from = selectedSquare1;
						System.out.println("Zu verschiebenden Stein ausgewählt");
					}
					if (modell.getByIndex((byte) selectedSquare1) == GameController.EMPTY) { // If field unoccupied
						approved = false;
						controller.setAusgabeText("Dieses Feld enthält keinen eigenen Stein.");
						squaresSelected = 0; // reset counter
						from = -2;
						throw new IllegalArgumentException(
								"public clickOnBoard bad argument: selectedSquare1 is empty: " + selectedSquare1);
					}
					if (modell.getByIndex((byte) selectedSquare1) != controller.activePlayer) {
						approved = false;
						controller.setAusgabeText("Eigenen Stein auswählen, nicht gegnerischen.");
						squaresSelected = 0; // reset counter
						from = -2;
						throw new IllegalArgumentException(
								"public clickOnBoard bad argument: Opponent instead of own selected "
										+ selectedSquare1);
					}

				}
				// Press second time, move stone
				if (to == -2 && squaresSelected == 2) {

					// When field is neighbor
					if (modell.areNeighbours((byte) from, (byte) selectedSquare2)
							&& modell.getByIndex((byte) selectedSquare2) == GameController.EMPTY) {
						approved = true;
						System.out.println("Zweites mal gedrückt");
						to = selectedSquare2;
					}

					if (modell.getByIndex((byte) selectedSquare2) == controller.getActivePlayer()) {
						approved = false;
						controller.setAusgabeText("Zielort für Stein auswählen.");
						from = selectedSquare2;
						squaresSelected = 1;
						System.out.println("Anderen zu verschiebenden Stein ausgewählt");

					}
					// If field is not neighbor
					if (modell.getByIndex((byte) selectedSquare2) == GameController.EMPTY
							&& !modell.areNeighbours((byte) from, (byte) selectedSquare2)) {
						if (modell.isAllowedToJump(controller.getActivePlayer())) {
							approved = true;
							to = selectedSquare2;
							System.out.println("Zweites mal gedrückt, Stein ist gesprungen");
						} else {
							controller.setAusgabeText("Dieses Feld liegt nicht in Reichweite.");
							squaresSelected = 1;
							to = -2;
							throw new IllegalArgumentException("public clickOnBoard bad argument: Square "
									+ selectedSquare2 + " is not neighbour from Square " + selectedSquare1);
						}
						//Field already in use by opponent
					} else if (modell.getByIndex((byte) selectedSquare2) == controller.getOpponent()) { 
						approved = false;
						controller.setAusgabeText("Dieses Feld ist vom Gegner bereits belegt.");
						squaresSelected = 1; // reset counter
						to = -2;
						throw new IllegalArgumentException(
								"public clickOnBoard bad argument: selectedSquare2 is used by opponent: "
										+ selectedSquare2);

					}

				}
				// Press third time, remove stone
				if (remove == -1 && squaresSelected == 3
						&& modell.getByIndex((byte) selectedSquare3) == controller.getOpponent()) {
					remove = selectedSquare3;
					approved = true;
					System.out.println("Drittes mal gedrückt");
				}

			}

			// If the above checks are passed, is divided into moves, also whether mills are formed is checked 
			if (to != -2) { // If at least one destination is defined for a stone
				move = new Move(from, to, remove);

				//If previously created Move creates a mill
				//Move will be assigned with "remove-infos".
				if (modell.createsNewMill(move, controller.activePlayer) && squaresSelected == 3) {
					Move moveWithRemove = new Move(from, to, selectedSquare3);
					System.out.println("Move der Mühle bildet erstellt: (" + from + "," + to + "," + remove + ")");
					executed = true;
					controller.execute(moveWithRemove);
					resetGUIController();
					controller.setAusgabeText("Stein wurde entfernt.");

				}

				// execute Move that does not form a mill
				else if (from != -2 && to != -2 && !modell.createsNewMill(move, controller.activePlayer)
						&& remove == -1) {
					if (LegalMoves.isLegalMove(move, controller)) { // act if legal
						System.out.println(
								"Neuer Move der keine Mühle bildet erstellt: (" + from + "," + to + "," + remove + ")");
						executed = true;
						controller.execute(move);
						modell.messageChanges();
						resetGUIController();
					}
				}

				// If move in PHASE_BEGINNING forms mill, update GUI and query again
				else if (squaresSelected == 1 && controller.getGameState() == GameController.PHASE_BEGINNING
						&& (modell.createsNewMill(move, controller.activePlayer))) {
					controller.setAusgabeText("Zu entfernenden Stein auswählen.");
					makesMill = true;
					System.out.println("Move bildet in Phase 1 Mühle. Nun zu entfernender Stein auswählen");
					temporaer = modell.getCurrentBoardCopy();
					temporaer[to] = controller.getActivePlayer();
					modell.messageChanges();
					makesMill = false;
					squaresSelected = 2; // Skip selection 2;
				}

				// If move in phase 2/3 forms mill, update gui and query again
				else if (squaresSelected == 2 && controller.getGameState() == GameController.PHASE_MIDGAME
						|| controller.getGameState() == GameController.PHASE_ENDGAME) {
					controller.setAusgabeText("Zu entfernenden Stein auswählen.");
					makesMill = true;
					System.out.println("Move bildet Mühle in Phase 2/3. Nun zu entfernender Stein auswählen");
					temporaer = modell.getCurrentBoardCopy();
					byte tmp = temporaer[from];
					temporaer[from] = temporaer[to];
					temporaer[to] = tmp;
					modell.messageChanges();
					makesMill = false;
					squaresSelected = 2;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// If AI mode is enabled, the next step is to execute an AI move.
		if (controller.mode == GameController.MODE_SINGLE && executed
				&& controller.getActivePlayer() == GameController.BLACK_PLAYER) {
			kiIsRunning = true;
			controller.setAusgabeText("Computer ist am überlegen...");
			moveAI(1000);

		}
	}

	private void moveAI(int pause) {

		timer = new Timer(1000, e -> {
			controller.executeAI();
			timer.stop();
			executed = false;
			resetGUIController();
		});
		timer.setInitialDelay(pause);
		timer.start();

	}

	public void startGame(byte difficulty, byte time, byte player) {
		controller.difficulty = difficulty;
		controller.time = time;
		controller.mode = player;
		controller.newGame();
		resetGUIController();
		System.out.println("Spiel gestartet");
	}

	public void resetGUIController() {
		from = -2;
		to = -2;
		remove = -1;
		selectedSquare1 = -2;
		selectedSquare2 = -2;
		selectedSquare3 = -2;
		squaresSelected = 0;
		//temporaer = null;
		approved = true;
		if (controller.getGameState() == GameController.PHASE_BEGINNING) {
			controller.setAusgabeText("Stein am gewünschten Ort platzieren.");
		}
		if (controller.getGameState() == GameController.PHASE_MIDGAME
				|| controller.getGameState() == GameController.PHASE_ENDGAME) {
			controller.setAusgabeText("Zu verschiebenden Stein auswählen");
		}

	}

	public void loadGame() {
		controller.clearGame();
		resetGUIController();
		modell.messageChanges();
	}


	public void newGame() {
		controller.clearGame();
		resetGUIController();
	}

	public void undo() {
		resetGUIController();
		controller.setAusgabeText("Rückgängig gemacht.");
		controller.undo();
	}

	// for JUnit5 test 
	public byte getSquaresSelected() {
		return squaresSelected;
	}

	public int getFrom() {
		return from;
	}

	public int getSelectedSquare1() {
		return selectedSquare1;
	}

	public int getSelectedSquare2() {
		return selectedSquare2;
	}

	public int getSelectedSquare3() {
		return selectedSquare3;
	}

}
