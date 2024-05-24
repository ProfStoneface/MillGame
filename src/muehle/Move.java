package muehle;

public class Move{

    public static final byte NOWHERE = -1;		
    public final byte FROM;						// origin stone 
    public final byte TO;						// destination stone 
    public final byte REMOVE;					// stone with this index will be removed 

    /** constructor checks if the Move has valid dates */
    public Move(int from, int to, int remove) throws IllegalArgumentException {
        if (from != NOWHERE && !(0<=from && from<=23) ) 
            throw new IllegalArgumentException("public Move(from,to,remove): bad argument 'from': " + from);
        if ( !(0<=to && to<=23) )
            throw new IllegalArgumentException("public Move(from,to,remove): bad argument 'to': " + to);
        if (remove != NOWHERE && !(0<=remove && remove<=23) ) 
            throw new IllegalArgumentException("public Move(from,to,remove): bad argument 'remove': " + remove);

        this.FROM = (byte) from;
        this.TO = (byte) to;
        this.REMOVE = (byte) remove;
    }
    
    public String toString() {
    	return "from "+ FROM + " to: "+ TO + " remove: "+ REMOVE;    	
    }
}
