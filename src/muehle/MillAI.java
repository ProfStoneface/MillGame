package muehle;

import java.io.Serializable;

public class MillAI implements Serializable{

	private static final short MAX_VALUE = 9999;
	private static final short MIN_VALUE = -9999;
	private int nodesOpened = 0;
	private boolean timeLimited = false;
	private byte depthLimit = -1;
	private long timeLimit = -1;
	private long searchStarted = 0;
	private byte player = -1;
	private byte opponent = -1;

	
	public MillAI() {
	
	}

	public void stopSearch() {
		this.timeLimited = true;
		this.timeLimit = 0;
	}

	/** main-method for AI depthsearch, works with the minimax-algorithm,
	 *  according to the depth given by the parameter. 
	 */
	public Move depthSearch(GameController game, byte depth) {
		if (depth < 0) {
			throw new IllegalArgumentException(
					"depthSearch(MillGame,byte): " + "depth:" + depth + " must be a positive number.");
		}
		this.nodesOpened = 0;
		this.timeLimited = false;
		this.depthLimit = depth;
		this.player = game.getActivePlayer();
		this.opponent = game.getOpponent();
		this.searchStarted = System.currentTimeMillis();

		Node best = maxValue(new Node(game, (byte) 0, null), MIN_VALUE, MAX_VALUE);
		if (best != null && best.PATH != null) {
			System.out.println("move from KI: " + best.PATH.PREVIOUS_MOVE + "\r\n" + "depth: " + depth + " value: "
					+ best.VALUE + " nodes: " + this.nodesOpened + " time: "
					+ (System.currentTimeMillis() - this.searchStarted) + "ms");

		}
		this.printPath(best.PATH);
		if (best == null || best.PATH == null) {
			return null; 
		}
		GUIController.kiIsRunning = false;
		return best.PATH.PREVIOUS_MOVE;
	}

	private boolean timeOut() {
		if (!this.timeLimited) {
			return false;
		}
		return System.currentTimeMillis() - this.searchStarted >= this.timeLimit;
	}

	private Node maxValue(Node currentNode, short alpha, short beta) {
		this.nodesOpened++;

		if (currentNode.DEPTH >= this.depthLimit) {
			currentNode.setValue(this.evaluate(currentNode.GAME));
			currentNode.setPath(null);
			return currentNode;
		}
		short bestValue = Short.MIN_VALUE; 
		Node bestPath = null;

		Move[] allMoves = LegalMoves.getAllLegalMoves(currentNode.GAME);
		this.sortMoves(allMoves);

		GameController copy;
		Move currentMove;
		for (int moveIndex = 0; moveIndex < allMoves.length; moveIndex++) {
			copy = (GameController) currentNode.GAME.clone();
			currentMove = allMoves[moveIndex];

			if (copy.makeMoveAI(currentMove, false)) { 
				if (copy.getActivePlayer() == this.player) {
					currentNode.setValue(MAX_VALUE);
				} else {
					currentNode.setValue(MIN_VALUE);
				}
				currentNode.setPath(new Node(copy, (byte) (currentNode.DEPTH + 1), currentMove));
				return currentNode;
			}


			Node nextNode = this.minValue(new Node(copy, (byte) (currentNode.DEPTH + 1), currentMove), alpha, beta);
			if (this.timeLimited && this.timeOut()) { 
				if (currentNode.DEPTH == 0) {
					if (moveIndex > 0) { 
						currentNode.setValue(bestValue); 
						currentNode.setPath(bestPath); 
					} else {
						currentNode.setValue(MIN_VALUE); 
						currentNode.setPath(null); 
					}
				}
				return currentNode;
			}
			if (nextNode.VALUE == MAX_VALUE) {
				currentNode.setValue(MAX_VALUE);
				currentNode.setPath(nextNode);
				return currentNode;
			}
			if (nextNode.VALUE > bestValue) {
				bestValue = nextNode.VALUE;
				bestPath = nextNode;
			}
			if (bestValue >= beta) { 
				currentNode.setValue(bestValue);
				currentNode.setPath(bestPath);
				return currentNode;
			}
			if (bestValue > alpha) {
				alpha = bestValue;
			}
		}
		currentNode.setValue(bestValue);
		currentNode.setPath(bestPath);
		return currentNode;
	}

	private Node minValue(Node currentNode, short alpha, short beta) {
		this.nodesOpened++;

		if (currentNode.DEPTH >= this.depthLimit) {
			currentNode.setValue(this.evaluate(currentNode.GAME));
			currentNode.setPath(null);
			return currentNode;
		}
		short worstValue = Short.MAX_VALUE; 
		Node worstPath = null;

		Move[] allMoves = LegalMoves.getAllLegalMoves(currentNode.GAME);
		this.sortMoves(allMoves);

		GameController copy;
		Move currentMove;
		for (int moveIndex = 0; moveIndex < allMoves.length; moveIndex++) {
			copy = (GameController) currentNode.GAME.clone();
			currentMove = allMoves[moveIndex];

			if (copy.makeMoveAI(currentMove, false)) { 
				if (copy.getActivePlayer() == this.player) {
					currentNode.setValue(MAX_VALUE);
				} else {
					currentNode.setValue(MIN_VALUE);
				}
				currentNode.setPath(new Node(copy, (byte) (currentNode.DEPTH + 1), currentMove));
				return currentNode;
			}

			Node nextNode = this.maxValue(new Node(copy, (byte) (currentNode.DEPTH + 1), currentMove), alpha, beta);
			if (this.timeLimited && this.timeOut()) { 
				return currentNode;
			}
			if (nextNode.VALUE == MIN_VALUE) { 
				currentNode.setValue(MIN_VALUE);
				currentNode.setPath(nextNode);
				return currentNode;
			}
			if (nextNode.VALUE < worstValue) {
				worstValue = nextNode.VALUE;
				worstPath = nextNode;
			}
			if (worstValue <= alpha) { 
				currentNode.setValue(worstValue);
				currentNode.setPath(worstPath);
				return currentNode;
			}
			if (worstValue < beta) {
				beta = worstValue;
			}
		}
		currentNode.setValue(worstValue);
		currentNode.setPath(worstPath);
		return currentNode;
	}

	private void sortMoves(Move[] moves) { 
		quicksort(moves, 0, moves.length - 1); 
	}

	private void quicksort(Move[] moves, int start, int end) {
		int left = start;
		int right = end;
		Move divider = moves[(start + end) / 2];
		do {
			while (this.compareMoves(moves[left], divider) > 0) { 
				left++;
			}
			while (this.compareMoves(divider, moves[right]) > 0) { 
				right--;
			}
			if (left <= right) {
				Move tmp = moves[left];
				moves[left] = moves[right];
				moves[right] = tmp;
				left++;
				right--;
			}
		} while (left < right);

		if (start < right) {
			quicksort(moves, start, right);
		}
		if (left < end) {
			quicksort(moves, left, end);
		}
	}

	/** returns +1 if move_1 is better then move_2 */
	private int compareMoves(Move move_1, Move move_2) { 
		if (move_1.REMOVE != Move.NOWHERE && move_2.REMOVE == Move.NOWHERE) {
			return +1;
		}
		if (move_1.REMOVE == Move.NOWHERE && move_2.REMOVE != Move.NOWHERE) {
			return -1;
		}

		boolean tmp_1 = this.isJunctionSquare(move_1.TO);
		boolean tmp_2 = this.isJunctionSquare(move_2.TO);
		if (tmp_1 && !tmp_2) {
			return +1;
		}
		if (!tmp_1 && tmp_2) {
			return -1;
		}

		tmp_1 = this.isTJunctionSquare(move_1.TO);
		tmp_2 = this.isTJunctionSquare(move_2.TO);
		if (tmp_1 && !tmp_2) {
			return +1;
		}
		if (!tmp_1 && tmp_2) {
			return -1;
		}

		tmp_1 = this.isMiddleCornerSquare(move_1.TO);
		tmp_2 = this.isMiddleCornerSquare(move_2.TO);
		if (tmp_1 && !tmp_2) {
			return +1;
		}
		if (!tmp_1 && tmp_2) {
			return -1;
		}

		return 0;
	}

	private boolean isJunctionSquare(byte square) {
		return (square == 4 || square == 10 || square == 13 || square == 19);
	}

	private boolean isTJunctionSquare(byte square) {
		return (square == 1 || square == 7 || square == 9 || square == 11 || square == 12 || square == 14
				|| square == 16 || square == 22);
	}

	private boolean isMiddleCornerSquare(byte square) {
		return (square == 3 || square == 5 || square == 18 || square == 20);
	}

	private short evaluate(GameController game) {
		if (game.getGameState() == GameController.PHASE_GAMEOVER) {
			System.out.println("Evaluating winning position!");
			if (game.getActivePlayer() == this.player) {
				return MAX_VALUE;
			} else {
				return MIN_VALUE;
			}
		}

		BoardModell board = game.getMillBoard();
		int value = 0;

		int playerPieces = board.getColouredSquares(this.player).length;
		int opponentPieces = board.getColouredSquares(this.opponent).length;
		byte playerHandPieces;
		byte opponentHandPieces;

		if (game.getGameState() == GameController.PHASE_BEGINNING) {
			if (this.player == GameController.WHITE_PLAYER) {
				playerHandPieces = game.getWhitePiecesInHand();
				opponentHandPieces = game.getBlackPiecesInHand();
			} else {
				playerHandPieces = game.getBlackPiecesInHand();
				opponentHandPieces = game.getWhitePiecesInHand();
			}
			value += 100 * ((playerPieces + playerHandPieces) - (opponentPieces + opponentHandPieces));

			if (playerHandPieces < opponentHandPieces) { 
			} else if (playerHandPieces > opponentHandPieces) {
				value += 40; 						
			}

		} else {
			value += 100 * (playerPieces - opponentPieces);
		}

		byte[] squares = board.getColouredSquares(player);
		for (int squareIndex = 0; squareIndex < squares.length; squareIndex++) {
			value += 20 * this.getEmptyNeighbourCount(squares[squareIndex], board);
		}
		squares = board.getColouredSquares(opponent);
		for (int squareIndex = 0; squareIndex < squares.length; squareIndex++) {
			value -= 20 * this.getEmptyNeighbourCount(squares[squareIndex], board);
		}
		return (short) value;
	}

	private int getEmptyNeighbourCount(byte square, BoardModell board) {
		byte[] neighbours = BoardModell.getNeighbours(square);
		int counter = 0;
		for (int neighbourIndex = 0; neighbourIndex < neighbours.length; neighbourIndex++) {
			if (board.getByIndex(neighbours[neighbourIndex]) == BoardModell.EMPTY) {
				counter++;
			}
		}
		return counter;
	}

	private void printPath(Node node) {
		Node tmp = node;
		while (tmp != null) {
			tmp = tmp.PATH;
		}
	}

}
