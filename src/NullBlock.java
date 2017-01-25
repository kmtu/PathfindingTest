
/**
 * class NullBlock represents a block which is not on the board,
 * such as a block outside the board.
 * 
 * @author TuTu
 * @version 2007.11.14
 */
public class NullBlock extends Block
{
    /**
     * Constructor for objects of class NullBlock
     */
    public NullBlock()
    {
        super(null, null);
    }
    
    /**
     * isAbleToStepOn
     * 
     * @return   true if this block is able to be stepped on
     */
    @Override
    public final boolean isAbleToStepOn(Unit unit)
    {
		return false;
    }
}
