package muehle;

import java.io.Serializable;
import java.util.Observable;


public class BoardModell extends Observable implements Cloneable, Serializable {

	public static final byte SQUARES_ON_BOARD = 24;
	public static final byte EMPTY = 0;
	public static final byte BLACK = 1;
	public static final byte WHITE = 2;

	// values of squares from 0 to 23 
	private byte[] squares;


	// creates a board with 24 empty squares
	public BoardModell() {
		this.setSquares(new byte[SQUARES_ON_BOARD]);
	}

	
	public BoardModell clone() {
		BoardModell copy = new BoardModell();
		for (int index=0; index < this.squares.length; index++) {
			copy.squares[index] = this.squares[index];
		}
		return copy;
	}

	public void clearBoard() {
		for (int index=0; index < this.squares.length; index++) {
			this.squares[index] = 0;
		}
		messageChanges();
	}


	// neighbours for each squares 
	private static final byte[][] NEIGHBOUR_SQUARES = { { 1, 9 }, { 0, 2, 4 }, { 1, 14 }, { 4, 10 }, { 1, 3, 5, 7 },
			{ 4, 13 }, { 7, 11 }, { 4, 6, 8 }, { 7, 12 }, { 0, 10, 21 }, { 3, 9, 11, 18 }, { 6, 10, 15 }, { 8, 13, 17 },
			{ 5, 12, 14, 20 }, { 2, 13, 23 }, { 11, 16 }, { 15, 17, 19 }, { 12, 16 }, { 10, 19 }, { 16, 18, 20, 22 },
			{ 13, 19 }, { 9, 22 }, { 19, 21, 23 }, { 14, 22 } };

	
	/** calls the update-method in class GUIView, MVC-message */
	public void messageChanges() {
		setChanged();
		notifyObservers();
		System.out.println("Message Changes aufgerufen");
	}

	
	public void setPiece(byte index, byte color) throws IllegalArgumentException, IllegalStateException {
		if ((color != BoardModell.BLACK) && (color != BoardModell.WHITE)) {
			throw new IllegalArgumentException("setPiece(byte,byte): unknown color value:" + color);
		}
		if (this.squares[index] != BoardModell.EMPTY) {
			throw new IllegalStateException("setPiece(byte,byte): square " + index + " is occupied.");
		}
		this.squares[index] = color;
		messageChanges();
	}

	/** stone is removed by the parameter index */
	public void removePiece(byte index) {
		this.squares[index] = BoardModell.EMPTY;
		this.messageChanges();
	}

	/** stone will be moved with the parameter fromIndex */
	public void movePiece(byte fromIndex, byte toIndex) throws IllegalArgumentException, IllegalStateException {
		this.setPiece(toIndex, this.squares[fromIndex]);
		this.removePiece(fromIndex);
	}


	/** returns current board */	
	public byte[] getCurrentBoardCopy() {
		return squares.clone();
	}

	public void overwriteBoard(byte [] array) {
		for (int i = 0; i < 24; i++) {
			this.squares[i] = array[i];
		}
	}

	public byte[] getAllIndex() {
		byte [] tempo = new byte [24];
		for (int i = 0; i < 24; i++) {
			tempo[i] = getByIndex((byte) i);
		}
		return tempo;
	}


	/** returns the color of the requested square */
	public byte getByIndex(byte square) {
		return this.squares[square];
	}

	public byte getPiecesLeft(byte color) {
		byte counter = 0;
		for (int i = 0; i < 24; i++) {
			if(this.squares[i] == color) {
				counter++;
			}
		}
		return counter;
	}
	
	public boolean isAllowedToJump (byte color) {
		if(getPiecesLeft(color) > 3) {
			return false;}
		else {
			return true;
		}
	}

	public boolean isEqual(byte[] array) {
		for (int i = 0; i < 24; i++) {
			if (this.squares [i] != array[i]) {
				return false;
			}
		}
		return true;
	}

	/** returns all neighbours of the requested square */
	public static byte[] getNeighbours(byte square) {
		byte[] neighbours = new byte[NEIGHBOUR_SQUARES[square].length];
		for (byte neighbour = 0; neighbour < neighbours.length; neighbour++) {
			neighbours[neighbour] = NEIGHBOUR_SQUARES[square][neighbour];
		}
		return neighbours;
	}

	// all possible mills
	private static final Object[] MILL_LINES;

	private static Object[] createMillLines() {
		byte[][] square_00 = { { 1, 2 }, { 9, 21 } }, 
				square_01 = { { 0, 2 }, { 4, 7 } },
				square_02 = { { 0, 1 }, { 14, 23 } },
				square_03 = { { 4, 5 }, { 10, 18 } },
				square_04 = { { 1, 7 }, { 3, 5 } }, 
				square_05 = { { 3, 4 }, { 13, 20 } },
				square_06 = { { 7, 8 }, { 11, 15 } }, 
				square_07 = { { 1, 4 }, { 6, 8 } },
				square_08 = { { 6, 7 }, { 12, 17 } }, 
				square_09 = { { 0, 21 }, { 10, 11 } },
				square_10 = { { 3, 18 }, { 9, 11 } }, 
				square_11 = { { 6, 15 }, { 9, 10 } },
				square_12 = { { 8, 17 }, { 13, 14 } }, 
				square_13 = { { 5, 20 }, { 12, 14 } },
				square_14 = { { 2, 23 }, { 12, 13 } }, 
				square_15 = { { 6, 11 }, { 16, 17 } },
				square_16 = { { 15, 17 }, { 19, 22 } }, 
				square_17 = { { 8, 12 }, { 15, 16 } },
				square_18 = { { 3, 10 }, { 19, 20 } }, 
				square_19 = { { 16, 22 }, { 18, 20 } },
				square_20 = { { 5, 13 }, { 18, 19 } }, 
				square_21 = { { 0, 9 }, { 22, 23 } },
				square_22 = { { 16, 19 }, { 21, 23 } }, 
				square_23 = { { 2, 14 }, { 21, 22 } };

		Object[] millLines = new Object[24];
		millLines[0] = square_00;
		millLines[1] = square_01;
		millLines[2] = square_02;
		millLines[3] = square_03;
		millLines[4] = square_04;
		millLines[5] = square_05;
		millLines[6] = square_06;
		millLines[7] = square_07;
		millLines[8] = square_08;
		millLines[9] = square_09;
		millLines[10] = square_10;
		millLines[11] = square_11;
		millLines[12] = square_12;
		millLines[13] = square_13;
		millLines[14] = square_14;
		millLines[15] = square_15;
		millLines[16] = square_16;
		millLines[17] = square_17;
		millLines[18] = square_18;
		millLines[19] = square_19;
		millLines[20] = square_20;
		millLines[21] = square_21;
		millLines[22] = square_22;
		millLines[23] = square_23;

		return millLines;
	}

	static {
		MILL_LINES = createMillLines();
	}

	/** returns all fields that form a mill with the parameter */
	public static byte[][] getMillLines(byte square) {
		byte[][] millLines = { 	{ ((byte[][]) MILL_LINES[square])[0][0], ((byte[][]) MILL_LINES[square])[0][1] },
				{ ((byte[][]) MILL_LINES[square])[1][0], ((byte[][]) MILL_LINES[square])[1][1] } };
		return millLines;
	}


	/** returns all fields of the requested color */
	public byte[] getColouredSquares(byte color) throws IllegalArgumentException {
		if ( (color != BLACK) && (color != WHITE) && (color != EMPTY)) {
			throw new IllegalArgumentException("getSquares(byte): unknown square value:"+color);
		}
		// temporary table 
		byte[] tmp = new byte[SQUARES_ON_BOARD]; // 24 is the max. number of fields 
		int squareCounter = 0;
		for (int index=0; index < this.squares.length; index++) {
			if (this.squares[index] == color) {
				tmp[squareCounter] = (byte) index;
				squareCounter++;
			}
		}
		// Summarising the table 
		byte[] array = new byte[squareCounter];
		for (int index=0; index < squareCounter; index++) {
			array[index] = tmp[index];
		}
		return array;
	}


	/** checks if two fields are neighbours*/
	public boolean areNeighbours(byte square_1, byte square_2) {
		byte[] neighbours = getNeighbours(square_1);
		for (int index=0; index < neighbours.length; index++) {
			if (neighbours[index] == square_2) {
				return true;
			}
		}
		return false;
	}


	/** checks if that move creates a mill */
	public boolean createsNewMill(Move move, byte color) throws IllegalArgumentException {
		if ( (color != BLACK) && (color != WHITE) ) {
			throw new IllegalArgumentException("createsNewMill(Move,byte): unknown color value:"+color);
		}
		
		byte[][] millLines = getMillLines(move.TO);
		byte[] millLine_1 = millLines[0];
		byte[] millLine_2 = millLines[1];

		// In addition, the mill line must have the same color,
		// both of the adjacent boxes must be the one from which the button was not moved
		if ( ((this.squares[millLine_1[0]] == color) && (millLine_1[0] != move.FROM)) &&
				((this.squares[millLine_1[1]] == color) && (millLine_1[1] != move.FROM)) ) {
			return true;
		}

		if ( ((this.squares[millLine_2[0]] == color) && (millLine_2[0] != move.FROM)) &&
				((this.squares[millLine_2[1]] == color) && (millLine_2[1] != move.FROM)) ) {
			return true;
		}
		return false;

	}


	/** checks if the field is part of a mill */
	public boolean isPartOfMill(byte square) {
		if (this.squares[square] == EMPTY) {
			return false;
		}
		byte[][] millLines = getMillLines(square);
		byte[] millLine_1 = millLines[0];
		byte[] millLine_2 = millLines[1];
		byte color = this.squares[square];

		return ( ((this.squares[millLine_1[0]] == color) && (this.squares[millLine_1[1]] == color)) ||
				((this.squares[millLine_2[0]] == color) && (this.squares[millLine_2[1]] == color))   );
	}


	/** checks if all stones of one colour are parts of a mill */
	public boolean allPiecesInMills(byte color) throws IllegalArgumentException {
		if ( (color != BLACK) && (color != WHITE) ) {
			throw new IllegalArgumentException("allPiecesInMills(byte): unknown color value:"+color);
		}
		byte[] colouredSquares = this.getColouredSquares(color);
		for (int index=0; index < colouredSquares.length; index++) {
			if ( !this.isPartOfMill(colouredSquares[index]) ) {
				return false;
			}
		}
		return true;
	}


	/** checks if all stones of one color are jammed */
	public boolean allPiecesJammed(byte color) throws IllegalArgumentException {
		if ( (color != BLACK) && (color != WHITE) ) {
			throw new IllegalArgumentException("allPiecesInMills(byte): unknown color value:"+color);
		}
		byte[] colouredSquares = this.getColouredSquares(color);
		for (int index=0; index < colouredSquares.length; index++) {
			if ( !this.pieceJammed(colouredSquares[index]) ) {
				return false;
			}
		}
		return true;
	}


	/** checks if a stone is jammed */
	private boolean pieceJammed(byte square) {
		byte[] neighbours = getNeighbours(square);
		for (int index=0; index < neighbours.length; index++) {
			if (this.squares[neighbours[index]] == EMPTY) {
				return false;
			}
		}
		return true;
	}

	public String toString() {
		char[] squarechar = new char[24];
		for (int i=0; i < this.squares.length; i++) {
			if (this.squares[i] == BoardModell.EMPTY) {
				squarechar[i] = 'O';
			}
			else if (this.squares[i] == BoardModell.WHITE) {
				squarechar[i] = 'w';
			}
			else if (this.squares[i] == BoardModell.BLACK) {
				squarechar[i] = 'b';
			}
		}

		char[] s = squarechar;
		return "" +
		"  "+s[0]+" --------- "+s[ 1]+" --------- "+s[ 2]                         + "                      0 --------- 1 --------- 2"+ "\n" +
		"  |           |           |"                                              + "                      |           |           |"+ "\n" +
		"  |   "+s[ 3]+" ----- "    +s[ 4]+" ----- "    +s[ 5]+"   |"              + "                      |   3 ----- 4 ----- 5   |"+ "\n" +
		"  |   |       |       |   |"                                              + "                      |   |       |       |   |"+ "\n" +
		"  |   |   "+s[ 6]+" - "+s[ 7]+" - "+s[ 8]+"   |   |"                      + "                      |   |   6 - 7 - 8   |   |"+ "\n" +
		"  |   |   |       |   |   |"                                              + "                      |   |   |       |   |   |"+ "\n" +
		"  "+s[ 9]+" - "+s[10]+" - "+s[11]+"       "+s[12]+" - "+s[13]+" - "+s[14] + "                      9 - 10- 11      12- 13- 14"+ "\n" +
		"  |   |   |       |   |   |"                                              + "                      |   |   |       |   |   |"+ "\n" +
		"  |   |   "+s[15]+" - "+s[16]+" - "+s[17]+"   |   |"                      + "                      |   |   15- 16- 17  |   |"+ "\n" +
		"  |   |       |       |   |"                                              + "                      |   |       |       |   |"+ "\n" +
		"  |   "+s[18]+" ----- "+s[19]+" ----- "+s[20]+"   |"                      + "                      |   18----- 19----- 20  |"+ "\n" +
		"  |           |           |"                                              + "                      |           |           |"+ "\n" +
		"  "+s[21]+" --------- "+s[22]+" --------- "+s[23]                         + "                      21--------- 22--------- 23"       ;

	}

	/** used for JUnit5 test */
	public void setSquares(byte[] squares) {
		this.squares = squares;
	}
}
