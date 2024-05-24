package muehle;

import java.awt.Component;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Stack;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class GameController implements Cloneable, Serializable {

	// different game states
	public static final byte PHASE_BEGINNING = 1;
	public static final byte PHASE_MIDGAME = 2;
	public static final byte PHASE_ENDGAME = 3;
	public static final byte PHASE_GAMEOVER = 4;

	private static final byte PIECES_IN_HAND = 9;
	public static final byte SQUARES_ON_BOARD = 24;

	public static final byte EMPTY = 0;
	public static final byte WHITE_PLAYER = BoardModell.WHITE;
	public static final byte BLACK_PLAYER = BoardModell.BLACK;

	// parameters for the game
	public static final byte DIFFICULTY_EASY = 0;
	public static final byte DIFFICULTY_AVG = 1;
	public static final byte DIFFICULTY_HARD = 2;
	public static final byte TIME_EASY = 0;
	public static final byte TIME_AVG = 1;
	public static final byte TIME_HARD = 2;
	public static final byte MODE_MULTI = 0;
	public static final byte MODE_SINGLE = 1;
	public static final byte DEPTH_EASY = 1;
	public static final byte DEPTH_AVG = 2;
	public static final byte DEPTH_HARD = 4;

	public boolean gameIsOver = false;
	public int countNoMills;
	public byte gameState; 
	public byte activePlayer; 
	public byte whitePiecesInHand; 
	public byte blackPiecesInHand; 
	public Stack<GameController> history; // Game history for undo
	public BoardModell modell; // board
	public boolean undoEnabled;
	public String textAusgabe; 
	public MillAI ai;

	public byte difficulty;
	public byte time;
	public byte mode;
	public byte depth;

	
	public GameController(BoardModell m) {
		modell = m;
	}

	// this constructor is only needed for the method clone()
	public GameController() {
		this.newGame();
	}

	public void newGame() {
		if (mode == MODE_MULTI) {
			undoEnabled = false;
		} else if (mode == MODE_SINGLE) {
			undoEnabled = true;

			this.history = new Stack<GameController>();
			ai = new MillAI();

			if (difficulty == DIFFICULTY_EASY) {
				depth = DEPTH_EASY;

			} else if (difficulty == DIFFICULTY_AVG) {
				depth = DEPTH_AVG;

			} else if (difficulty == DIFFICULTY_HARD) {
				depth = DEPTH_HARD;
			}
		}

		this.gameState = PHASE_BEGINNING;
		this.activePlayer = WHITE_PLAYER;
		this.whitePiecesInHand = PIECES_IN_HAND;
		this.blackPiecesInHand = PIECES_IN_HAND;
	}

	public void clearGame() {
		modell.clearBoard();
		this.gameState = PHASE_BEGINNING;
		this.activePlayer = WHITE_PLAYER;
		this.whitePiecesInHand = PIECES_IN_HAND;
		this.blackPiecesInHand = PIECES_IN_HAND;
		modell.messageChanges();
	}


	/**
	 * Create an independent copy of the MillGame object with its own properties
	 * (not just references). The copy should not include previous history
	 * information, but it can be added to it. Return the copied object without any
	 * previous history information.
	 */
	public GameController clone() {
		GameController copy = new GameController();
		copy.gameState = this.gameState;
		copy.activePlayer = this.activePlayer;
		copy.whitePiecesInHand = this.whitePiecesInHand;
		copy.blackPiecesInHand = this.blackPiecesInHand;
		copy.modell = this.modell.clone();
		return copy;
	}

	
	 /** Restore the game to the same position as before the previous move */
	public void undo() throws IllegalStateException {
		if (this.history.empty()) {
			throw new IllegalStateException("Undo cannot be done: first turn");
		} else if (!undoEnabled) {
			setAusgabeText("Rückgängig in Spieler VS. Spieler nicht möglich");
			throw new IllegalStateException("Undo cannot be done: not available");
			
		}
		this.history.pop();
		GameController restored = this.history.pop();
		this.restoreGame(restored);
		System.out.println(modell.toString());
		this.modell.messageChanges();
	}

	/** Returns the game state of another MillGame object to this game object. */
	private void restoreGame(GameController restored) {
		this.gameState = restored.gameState;
		this.activePlayer = restored.activePlayer;
		this.whitePiecesInHand = restored.whitePiecesInHand;
		this.blackPiecesInHand = restored.blackPiecesInHand;
		this.modell.overwriteBoard(restored.modell.getAllIndex());
	}


	/**Returns a copy of the game's used MillBoard object. return A copy of the
	 * MillBoard object used in the game.
	 */
	public BoardModell getMillBoard() {
		return this.modell.clone();
	}

	
	public byte getBlackPiecesInHand() {
		return this.blackPiecesInHand;
	}

	public byte getWhitePiecesInHand() {
		return this.whitePiecesInHand;
	}

	
	public byte getActivePlayer() {
		return this.activePlayer;
	}

	public String getActivePlayerText() {
		if (this.activePlayer == BoardModell.WHITE) {
			return "Weiss";
		} else if (this.activePlayer == BoardModell.BLACK) {
			return "Schwarz";
		} else {
			return "kein aktueller Spieler";
		}
	}

	
	public byte getGameState() {
		return this.gameState;
	}


	public void executeAI() {
		Move aiMove = ai.depthSearch(this, depth);
		execute(aiMove);
	}

	public void execute(Move move) {

		switch (this.gameState) {

		case PHASE_BEGINNING:

			if (activePlayer == WHITE_PLAYER) {
				makeMove(move, undoEnabled, PHASE_BEGINNING, WHITE_PLAYER);
				activePlayer = getOpponent();
			} else if (activePlayer == BLACK_PLAYER) {
				makeMove(move, undoEnabled, PHASE_BEGINNING, BLACK_PLAYER);
				activePlayer = getOpponent();
			}

			// conditions to change the game phase
			if (whitePiecesInHand == 0 && blackPiecesInHand == 0) {
				this.gameState = PHASE_MIDGAME;
				if (requestVictoryBegin()) {
					activePlayer = getOpponent();
					this.gameState = PHASE_GAMEOVER;
					gameIsOver();
					break;
				}
			}

			this.modell.messageChanges();
			break;

		case PHASE_MIDGAME:

			if (activePlayer == WHITE_PLAYER) {
				makeMove(move, undoEnabled, PHASE_MIDGAME, WHITE_PLAYER);
				activePlayer = getOpponent();
			} else if (activePlayer == BLACK_PLAYER) {
				makeMove(move, undoEnabled, PHASE_MIDGAME, BLACK_PLAYER);
				activePlayer = getOpponent();
			}

			// conditions to change the game phase
			if ((modell.getColouredSquares(BoardModell.WHITE).length == 3)
					|| (modell.getColouredSquares(BoardModell.BLACK).length == 3)) {
				this.gameState = PHASE_ENDGAME;
			}
			if (requestVictory()) {
				this.gameState = PHASE_GAMEOVER;
				gameIsOver();
				break;
			}
			if (getCountNoMills() >= 20) {
				this.gameState = PHASE_GAMEOVER;
				gameIsOver();
				break;
			}

			this.modell.messageChanges();
			System.out.println("Anzahl Züge ohne Mühle: " + getCountNoMills());
			break;

		case PHASE_ENDGAME:

			if (activePlayer == WHITE_PLAYER) {
				makeMove(move, undoEnabled, PHASE_MIDGAME, WHITE_PLAYER);
				activePlayer = getOpponent();
			} else if (activePlayer == BLACK_PLAYER) {
				makeMove(move, undoEnabled, PHASE_MIDGAME, BLACK_PLAYER);
				activePlayer = getOpponent();
			}

			// conditions to change the game phase
			if (requestVictory()) {
				this.gameState = PHASE_GAMEOVER;
				gameIsOver();
				break;
			}
			if (getCountNoMills() >= 20) {
				this.gameState = PHASE_GAMEOVER;
				gameIsOver();
				break;
			}
			this.modell.messageChanges();
			System.out.println("Anzahl Züge ohne Mühle: " + getCountNoMills());
			break;

		case PHASE_GAMEOVER:
			System.out.println(modell.toString());

			if (getCountNoMills() >= 20) {
				System.out.println("draw!");
			} else if (getActivePlayer() == BLACK_PLAYER) {
				System.out.println("Black wins!");
			} else {
				System.out.println("White wins!");
			}
			break;
		}
	}

	/** executes a move */
	public void makeMove(Move move, boolean undoEnabled, byte aktuellerZustand, byte player)
			throws IllegalArgumentException, IllegalStateException {
		if (move == null) {
			throw new IllegalArgumentException("makeMove(Move): Parameter 'Move' cannot be null. ");
		}
		if (!LegalMoves.isLegalMove(move, this)) {
			throw new IllegalArgumentException("makeMove(Move): Parameter 'Move' is an illegal move. ");
		}
		if (undoEnabled) {
			// the current situation is saved in the history data
			this.history.push(this.clone());
		}

		byte zustand = aktuellerZustand;
		byte color = player;

		switch (zustand) {

		case PHASE_BEGINNING:
			if (player == WHITE_PLAYER) {
				modell.setPiece(move.TO, color);
				this.whitePiecesInHand--;
			} else if (player == BLACK_PLAYER) {
				modell.setPiece(move.TO, color);
				this.blackPiecesInHand--;
			} else {
				throw new IllegalStateException(
						"makeMove(Move): Internal error --> " + "Illegal activePlayer:" + this.activePlayer);
			}

			if (move.REMOVE >= 0) {
				modell.removePiece(move.REMOVE);
				setCountNoMillsToZero();
			}
			break;

		case PHASE_MIDGAME:
			if (player == WHITE_PLAYER) {
				modell.movePiece(move.FROM, move.TO);
			} else if (player == BLACK_PLAYER) {
				modell.movePiece(move.FROM, move.TO);
			} else {
				throw new IllegalStateException(
						"makeMove(Move): Internal error --> " + "Illegal activePlayer:" + this.activePlayer);
			}

			if (move.REMOVE >= 0) {
				modell.removePiece(move.REMOVE);
				setCountNoMillsToZero();
			} else {
				incCountNoMills();
			}
			break;

		case PHASE_ENDGAME:
			if (player == WHITE_PLAYER) {
				modell.movePiece(move.FROM, move.TO);
			} else if (player == BLACK_PLAYER) {
				modell.movePiece(move.FROM, move.TO);
			} else {
				throw new IllegalStateException(
						"makeMove(Move): Internal error --> " + "Illegal activePlayer:" + this.activePlayer);
			}

			if (move.REMOVE >= 0) {
				modell.removePiece(move.REMOVE);
				setCountNoMillsToZero();
			} else {
				incCountNoMills();
			}
			break;

		}
		System.out.println("GameController Ausgabe:");
		System.out.println(modell);
	}
	
	public boolean makeMoveAI(Move move, boolean undoRedoEnabled) {
		
		return this.moveAI(move, undoRedoEnabled);
		
	}
	
	 /** Transfer checks and the actual implementation */
    private boolean moveAI(Move move, boolean undoRedoEnabled) throws IllegalArgumentException,
                                                                    IllegalStateException {
        if (move == null) {
            throw new IllegalArgumentException("makeMove(Move): Parameter 'Move' cannot be null. ");
        }

        if ( !LegalMoves.isLegalMove(move, this) ) {
        	System.out.println("Illegaler Move: "+ move.toString());
            throw new IllegalArgumentException("makeMove(Move): Parameter 'Move' is an illegal move. ");
        }

        if (undoRedoEnabled) {
            //the current situation is saved in the history data
            this.history.push(this.clone());
        }
        else {
            this.history = new Stack(); // history reset
        }
        // move opens a new branch in the game world and neither
        //previous redo operations no longer make sense.
        if (this.gameState == PHASE_BEGINNING) {
            if (this.activePlayer == WHITE_PLAYER) {
                this.whitePiecesInHand--;
                this.modell.setPiece(move.TO, BoardModell.WHITE);
            }
            else if (this.activePlayer == BLACK_PLAYER) {
                this.blackPiecesInHand--;
                this.modell.setPiece(move.TO, BoardModell.BLACK);
            }
            else {
                throw new IllegalStateException("makeMove(Move): Internal error --> "+
                                                "Illegal activePlayer:"+this.activePlayer);
            }

            if (move.REMOVE != Move.NOWHERE) {
                this.modell.removePiece(move.REMOVE);
            }

            if (this.whitePiecesInHand == 0 && this.blackPiecesInHand == 0) {
                this.gameState = PHASE_MIDGAME;

                   // It may be that after placing all his pieces in the initial game
                   // either player gets stuck in midgame.
                   // Bring down the number of pieces below three
                   // doesn't seem to be even possible, though.
                if ( (this.modell.allPiecesJammed(this.activePlayer) && !this.modell.isAllowedToJump(this.activePlayer) ) ||
                    (this.modell.getColouredSquares(this.activePlayer)).length < 3) {
                        this.gameState = PHASE_GAMEOVER;
                        this.activePlayer = this.getOpponent(); //Opponent won the game!!
                        return true;
                }
                if ( (this.modell.allPiecesJammed(this.getOpponent())  && !this.modell.isAllowedToJump(this.getOpponent())) ||
                    (this.modell.getColouredSquares(this.getOpponent())).length < 3) {
                        this.gameState = PHASE_GAMEOVER;
                        return true;
                }
            }
            this.activePlayer = this.getOpponent();
            return false;
        }
        else if (this.gameState == PHASE_MIDGAME || this.gameState == PHASE_ENDGAME) {
            this.modell.movePiece(move.FROM, move.TO);

            // the piece removed by the mill:
            if (move.REMOVE != Move.NOWHERE) {
                this.modell.removePiece(move.REMOVE);
            }

            if ( (this.modell.getColouredSquares(BoardModell.WHITE)).length == 3  ||
                 (this.modell.getColouredSquares(BoardModell.BLACK)).length == 3)
                    this.gameState = PHASE_ENDGAME;

            // If after the move all of the opponent's pieces are stuck or his
            // the number of his pieces drops below three, now the player on his turn has won!
            // A mistake can also happen to the player himself, in which case the opponent wins.
            if ( (this.gameState == PHASE_MIDGAME && this.modell.allPiecesJammed(this.getOpponent()) && !this.modell.isAllowedToJump(this.getOpponent())) ||
                  (this.modell.getColouredSquares(this.getOpponent())).length < 3) {
                        this.gameState = PHASE_GAMEOVER;
                        return true;
            }
            if ( (this.gameState == PHASE_MIDGAME && this.modell.allPiecesJammed(this.activePlayer) && !this.modell.isAllowedToJump(this.activePlayer) ) ||
                  (this.modell.getColouredSquares(this.activePlayer)).length < 3) {
                        this.gameState = PHASE_GAMEOVER;
                        this.activePlayer = this.getOpponent(); //Opponent won the game!
                        return true;
            }

            this.activePlayer = this.getOpponent();
            return false;
        }
        else {
            throw new IllegalStateException("makeMove(Move): Game is over and no more moves can be played.");
        }
    }


	public void gameIsOver() {
		Component frame = null;
		if (getCountNoMills() >= 20) {
			System.out.println("SPIEL IST BEENDET - draw!");
			JOptionPane.showMessageDialog(frame, "Spiel ist beendet: 20 Züge ohne Mühle - Unentschieden!", "Mühle", JOptionPane.WARNING_MESSAGE);
		} else if (getActivePlayer() == BLACK_PLAYER) {
			System.out.println("SPIEL IST BEENDET - Gewonnen: Black wins!");
			JOptionPane.showMessageDialog(frame, "Spiel ist beendet: Schwarz hat gewonnen!", "Mühle", JOptionPane.WARNING_MESSAGE);
		} else {
			System.out.println("SPIEL IST BEENDET - Gewonnen: White wins!");
			JOptionPane.showMessageDialog(frame, "Spiel ist beendet: Weiss hat gewonnen!", "Mühle", JOptionPane.WARNING_MESSAGE);
		}
		gameIsOver = true;
	}

	public int getCountNoMills() {
		return countNoMills;
	}

	public void incCountNoMills() {
		countNoMills++;
	}

	public void setCountNoMillsToZero() {
		countNoMills = 0;
	}

	/** Returns true if active player won the game in gamestate (MIDGAME - GAME OVER). */ 
	public boolean requestVictory() {
		if ((this.modell.allPiecesJammed(this.activePlayer) && !this.modell.isAllowedToJump(this.activePlayer))
				|| (this.modell.getColouredSquares(this.activePlayer)).length < 3) {
			this.activePlayer = this.getOpponent(); // Opponent won the game
			return true;
		}
		if((this.modell.allPiecesJammed(this.getOpponent()) && !this.modell.isAllowedToJump(this.getOpponent()))
				|| (this.modell.getColouredSquares(this.getOpponent())).length < 3) {
			return true;
		}
		return false;
	}

	/** Returns true if active player won the game in gamestate BEGINNING. */ 
	public boolean requestVictoryBegin() {
		if (this.modell.allPiecesJammed(this.getActivePlayer())) {
			return true;
		}
		return false;
	}

	/** Returns the current player's opponent. */
	byte getOpponent() {
		if (this.activePlayer == WHITE_PLAYER) {
			return BLACK_PLAYER;
			}
		else {
			return WHITE_PLAYER;
		}
	}

	public void setAusgabeText(String s) {
		textAusgabe = s;
		System.out.println("Neuer Ausgabetext: "+ s);
		modell.messageChanges();
	}

	public String getAusgabeText() {
		return textAusgabe;
	}

}
