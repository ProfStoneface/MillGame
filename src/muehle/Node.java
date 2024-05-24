package muehle;

	/** Each node in the game tree represents the state of the game after a move.
	 * The children of a node represent the possible moves,
	 * that can be executed in the next step.
	 */
	 
public class Node {
    public final GameController GAME;
    public final byte DEPTH;
    public final Move PREVIOUS_MOVE;
    public Node PATH;
    public short VALUE;

    /** Creates new node:
     * game state is saved, level is saved
     * previous move is saved
     * Path is only occupied by the following node
     */
    public Node(GameController game, byte depth, Move previousMove) {
        this.GAME = game;
        this.DEPTH = depth;
        this.PREVIOUS_MOVE = previousMove;
        this.PATH = null;
    }

    
    /** The following node describes this variable */
    public void setPath(Node next) {
        this.PATH = next;
    }
    
    /** The variable Value represents how favourable or unfavourable a move is.
     * Is needed for the evaluation of the AI. By setting the node value
     * the algorithm can
     * determine the best path through the game tree and thus select the optimal 
     * move.
     */
    public void setValue(short value) {
        this.VALUE = value;
    }
    
}