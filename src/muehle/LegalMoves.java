package muehle;


public final class LegalMoves{


	private LegalMoves() {
		
	} 


	public static boolean isLegalMove(Move move, GameController game) {
		return legal(move, game, game.getMillBoard());
	}


	private static boolean legal(Move move, GameController game, BoardModell board) {
		byte activePlayer = game.getActivePlayer();
		byte opponent = game.getOpponent();
		byte gameState = game.getGameState();
		

		if (board.getByIndex(move.TO) != BoardModell.EMPTY) { // try on not empty square 
			return false;
		}

		if (gameState == GameController.PHASE_GAMEOVER) { /* Game already over */
			return false;
		} else if (gameState == GameController.PHASE_BEGINNING) {
			if (move.FROM != Move.NOWHERE) {      // In the initial game, no pieces are moved 
				return false;
			}
		} else if (gameState == GameController.PHASE_MIDGAME) {
			if (move.FROM == Move.NOWHERE) {     // In the middle game, you have to move a piece 
				return false;
			}
			if (board.getByIndex(move.FROM) != activePlayer) {     // The moveable piece is not yours 
				return false;
			}
			if ( !board.areNeighbours(move.FROM, move.TO) ) { // You only move to adjacent squares 
				return false;
			}
		} else if (gameState == GameController.PHASE_ENDGAME) {
			if (move.FROM == Move.NOWHERE) {       // In the endgame, you have to move a piece 
				return false;
			}
			if (board.getByIndex(move.FROM) != activePlayer) {     // The moveable piece is not yours 
				return false;
			}
		}


		if (move.REMOVE == Move.NOWHERE) {                  //No attempt is made to remove pieces
			if ( !board.createsNewMill(move, activePlayer) ) {  //A new mill is not formed, double check
				return true;
			}
		}
		else if (board.getByIndex(move.REMOVE) == opponent) {       //The opponent's piece must be removed
			if (board.createsNewMill(move, activePlayer)) { 		//A new mill is formed
				if ( !board.isPartOfMill(move.REMOVE) ) {   		//The remover does not belong to the mill
					return true;
				}
				else if (board.allPiecesInMills(opponent)) {		//All of the opponent's pieces belong to the mills
					return true;
				}
			}
		}
		
		return false;
	}


	public static Move[] getAllLegalMoves(GameController game) {
		byte gameState = game.getGameState();

		if (gameState == GameController.PHASE_BEGINNING) {
			return getAllLegalMoves_beginning(game);
		}
		else if (gameState == GameController.PHASE_MIDGAME) {
			return getAllLegalMoves_middlegame(game);
		}
		else if (gameState == GameController.PHASE_ENDGAME) {
			return getAllLegalMoves_endgame(game);
		}
		else {
			return new Move[0];
		}
	}


	private static Move[] getAllLegalMoves_beginning(GameController game) {
		byte activePlayer = game.getActivePlayer();
		byte opponent = game.getOpponent();
		BoardModell board = game.getMillBoard();

		Move[] allMoves = new Move[1000];  //the maximum value thrown from the head
		int counter = 0;
		byte from = Move.NOWHERE;
		for (byte to = 0; to < BoardModell.SQUARES_ON_BOARD; to++) {
			if (board.getByIndex(to) != BoardModell.EMPTY) {
				continue;
			}
			if (board.createsNewMill( new Move(from, to, (byte)0), activePlayer) ) {
				for (byte remove = 0; remove < BoardModell.SQUARES_ON_BOARD; remove++) {
					if (board.getByIndex(remove) != opponent) {
						continue;
					}
					//Does the piece belong in the mill? If so, we'll check
					//whether all the opponent's pieces belong to the mill.
					if (board.isPartOfMill(remove)) {
						if ( board.allPiecesInMills(opponent) ) {
							allMoves[counter] = new Move(from, to, remove);
							counter++;
						}
					}
					else {
						allMoves[counter] = new Move(from, to, remove);
						counter++;
					}
				}
			}
			else {
				allMoves[counter] = new Move(from, to, Move.NOWHERE);
				counter++;
			}
		}
		return compressArray(allMoves, counter);
	}



	private static Move[] getAllLegalMoves_middlegame(GameController game) {
		byte activePlayer = game.getActivePlayer();
		byte opponent = game.getOpponent();
		BoardModell board = game.getMillBoard();

		Move[] allMoves = new Move[1000];  //the maximum value thrown from the head
		int counter = 0;
		for (byte from = 0; from < BoardModell.SQUARES_ON_BOARD; from++) {
			if (board.getByIndex(from) != activePlayer) {
				continue;
			}
			byte[] neighbours = BoardModell.getNeighbours(from);
			for (byte neighbourIndex = 0; neighbourIndex < neighbours.length; neighbourIndex++) {
				byte to = neighbours[neighbourIndex];
				if (board.getByIndex(to) != BoardModell.EMPTY) {
					continue;
				}
				if (board.createsNewMill( new Move (from, to, (byte)0), activePlayer)) {
					for (byte remove = 0; remove < BoardModell.SQUARES_ON_BOARD; remove++) {
						if (board.getByIndex(remove) != opponent) {
							continue;
						}
						//Does the piece belong in the mill? If so, we'll check
						// whether all the opponent's pieces belong to the mill.
						if (board.isPartOfMill(remove)) {
							if ( board.allPiecesInMills(opponent) ) {
								allMoves[counter] = new Move(from, to, remove);
								counter++;
							}

						}
						else {
							allMoves[counter] = new Move(from, to, remove);
							counter++;
						}
					}
				}
				else {
					allMoves[counter] = new Move(from, to, Move.NOWHERE);
					counter++;
				}
			}
		}
		return compressArray(allMoves, counter);
	}


	private static Move[] getAllLegalMoves_endgame(GameController game) {
		byte activePlayer = game.getActivePlayer();
		byte opponent = game.getOpponent();
		BoardModell board = game.getMillBoard();

		if ( (board.getColouredSquares(activePlayer)).length > 3) {
			return getAllLegalMoves_middlegame(game);
		}

		Move[] allMoves = new Move[1000];  //the maximum value thrown from the head
		int counter = 0;
		for (byte from = 0; from < BoardModell.SQUARES_ON_BOARD; from++) {
			if (board.getByIndex(from) != activePlayer) {
				continue;
			}
			for (byte to = 0; to < BoardModell.SQUARES_ON_BOARD; to++) {
				if (board.getByIndex(to) != BoardModell.EMPTY) {
					continue;
				}
				if ( board.createsNewMill( new Move(from, to, (byte)0), activePlayer) ) {
					for (byte remove = 0; remove < BoardModell.SQUARES_ON_BOARD; remove++) {
						if (board.getByIndex(remove) != opponent) {
							continue;
						}
						//Does the piece belong in the mill? if true check if the opponent pieces are all in mill
						if (board.isPartOfMill(remove)) {
							if ( board.allPiecesInMills(opponent) ) {
								allMoves[counter] = new Move(from, to, remove);
								counter++;
							}
						}
						else {
							allMoves[counter] = new Move(from, to, remove);
							counter++;
						}
					}
				}
				else {
					allMoves[counter] = new Move(from, to, Move.NOWHERE);
					counter++;
				}
			}
		}
		return compressArray(allMoves, counter);
	}


	private static boolean containsValue(byte[] array, byte value) {
		for (int index = 0; index < array.length; index++) {
			if (array[index] == value) {
				return true;
			}
		}
		return false;
	}

	private static Move[] compressArray(Move[] array, int count) {
		Move[] compressed = new Move[count];
		for (int moveIndex = 0; moveIndex < count; moveIndex++)
			compressed[moveIndex] = array[moveIndex];
		return compressed;
	}

	private static byte[] compressArray(byte[] array, int count) {
		byte[] compressed = new byte[count];
		for (int moveIndex = 0; moveIndex < count; moveIndex++)
			compressed[moveIndex] = array[moveIndex];
		return compressed;
	}

}
