import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 * @author TuTu
 * @date Apr 10, 2008
 */
public class BlockShape implements Cloneable{
	private Board board;
    private ArrayList<Block> blockList;
    private int referenceBlockIndex;
    private static final int INI_NUM_BLOCKS = 10;
    
	public BlockShape(Board board) {
		super();
		this.board = board;
		referenceBlockIndex = -1;
		blockList = new ArrayList<Block>(INI_NUM_BLOCKS);
	}

//	/* (non-Javadoc)
//	 * @see java.lang.Object#clone()
//	 */
//	@Override
//	protected Object clone() throws CloneNotSupportedException {
//		BlockShape cloneShape = new BlockShape(board);
//		for (int i = 0; i < this.blockList.size(); i++) {
//			cloneShape.addBlock(this.getBlock(i));
//		}
//		cloneShape.setReferenceBlock(this.referenceBlock);
//		return cloneShape;
//	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public BlockShape clone() {
		try {
			BlockShape cloneShape = (BlockShape)(super.clone());
			cloneShape.blockList = (ArrayList<Block>) this.blockList.clone();
			return cloneShape;
        } catch (CloneNotSupportedException e) {
            throw new Error("This should not occur since we implement Cloneable");
        }
	}


	// the position of a rectangular unit is the top left corner of that unit
	public static BlockShape makeRectangularBlockShape(Board board, Dimension dimension, Point position) {
		BlockShape blockShape = new BlockShape(board);
        for (int i = 0; i < dimension.width * dimension.height; i++) {
            blockShape.addBlock(new Point((position.x + (i % dimension.width)), (position.y + (i / dimension.width))));
        }
        blockShape.setReferenceBlock(board.getBlock(new Point((position.x + (dimension.width/2)), (position.y + (dimension.height/2)))));
        
        return blockShape;
	}
	
	/**
     * Add a block to this block shape
     * 
     * @param    pos    the block point to be added
     */
    private void addBlock(Point pos) {
        if (!blockList.contains(board.getBlock(pos))) {
            blockList.add(board.getBlock(pos));
        }
    }
    
    /**
     * Add a block to this block shape
     * 
     * @param    block    the block to be added
     */
    private void addBlock(Block block)
    {
        if (!blockList.contains(block))
        {
            blockList.add(block);
        }
    }
    
    private void setReferenceBlock(Block block) {
    	int index = blockList.indexOf(block);
    	if (index != -1) {
    		referenceBlockIndex = index;
    	}
    	else
    		throw new IllegalArgumentException("The reference block must be one of the blocks of the blockshape!");
    }
    
    /**
     * Add a unit to this whole block shape
     * 
     * @param    unit    the unit to be added
     */
    public void addUnit(Unit unit) {
        for (int i = 0; i < blockList.size(); i++) {
        	blockList.get(i).addUnit(unit);
        }
    }
    
    public void setLocation(Block destBlock) {
    	if (referenceBlockIndex != -1) {
    		int dx, dy;
    		Point pos = new Point();
    		dx = destBlock.getPosition().x - getReferenceBlock().getPosition().x;
    		dy = destBlock.getPosition().y - getReferenceBlock().getPosition().y;
    		for (int i = 0; i < blockList.size(); i++) {
    			pos.setLocation(blockList.get(i).getPosition());
    			pos.translate(dx, dy);
    			if (board.isInside(pos))
    				blockList.set(i, board.getBlock(pos));
    			else
    				blockList.set(i, Board.NULL_BLOCK);
    		}		
    	}
    	else
    		throw new IllegalStateException("A blockshape must have a reference block before using setLocation(Block)!");
    		
    }
    
    public boolean isAbleToStepOn(Unit unit) {
    	if (blockList.size() > 0) {
	    	for (int i = 0; i < blockList.size(); i++)
	        {
	            if (!blockList.get(i).isAbleToStepOn(unit))
	                return false;
	        }
	    	return true;
    	}
    	else
    		return false;
    }
    
    public boolean isOccupied(Unit unit) {
    	if (blockList.size() > 0) {
	    	for (int i = 0; i < blockList.size(); i++)
	        {
	            if (blockList.get(i).isOccupied(unit))
	                return true;
	        }
	    	return false;
    	}
    	else
    		return false;
	}
    
    public boolean isStaticUnitOccupied(Unit unit) {
    	if (blockList.size() > 0) {
	    	for (int i = 0; i < blockList.size(); i++)
	        {
	            if (blockList.get(i).isStaticUnitOccupied(unit))
	                return true;
	        }
	    	return false;
    	}
    	else
    		return false;
	}
    
//    private Block getBlock(int index) {
//    	return blockList.get(index);
//    }
    
    /**
     * Get the outer boundary block shape of specified direction of this block shape
     * 
     * @param   dir  the direction to find outer bounary
     * @return     the outer boundary block shape
     */
    public BlockShape getOuterBoundaryBlockShape(Direction dir)
    {
    	if (dir == null)
    		return null;
    	else {
	        BlockShape boundaryBlockShape = new BlockShape(board);
	        Point pos = new Point();
	        for (int i = 0; i < blockList.size(); i++)
	        {        
	            pos.setLocation(blockList.get(i).getPosition());
	            pos.translate(dir.x, dir.y);
	            if (!board.isInside(pos))
	            {
	                boundaryBlockShape.addBlock(Board.NULL_BLOCK);
	            }
	            else if (!blockList.contains(board.getBlock(pos)))
	            {
	                boundaryBlockShape.addBlock(board.getBlock(pos));
	            }
	        }
	        return boundaryBlockShape;
    	}
    }

	public void removeUnit(final World.Layer layer) {
		for (int i = 0; i < blockList.size(); i++) {
            this.blockList.get(i).removeUnit(layer);
        }
	}

	public void translate(int dx, int dy) {
		Point pos = new Point();
		for (int i = 0; i < blockList.size(); i++) {
			pos.setLocation(blockList.get(i).getPosition());
            pos.translate(dx, dy);
            blockList.set(i, board.getBlock(pos));
        }
	}

	public Block getReferenceBlock() {
		if (referenceBlockIndex < 0) {
			throw new IllegalStateException("This blockShape has not been set a referenceBlock!");
		}
		else
			return blockList.get(referenceBlockIndex);
	}
	
	public double getLowestSpeedFactor(EnumMap<World.Terrain, Double> movingSpeedFactorMap) {
		double lowest, test;
		lowest = movingSpeedFactorMap.get(blockList.get(0).getTerrain());
		for (int i = 1; i < blockList.size(); i++) {
			test = movingSpeedFactorMap.get(blockList.get(i).getTerrain());
			if (test < lowest)
				lowest = test;
		}
		return lowest;
	}

	public ArrayList<Unit> getUnitList(World.Layer layer) {
		ArrayList<Unit> unitList = new ArrayList<Unit>(6);
		for (int i = 0; i < blockList.size(); i++) {
			Unit unit = blockList.get(i).getUnit(layer);
			if (unit != null && !unitList.contains(unit))
				unitList.add(blockList.get(i).getUnit(layer));
        }
		return unitList;
	}
}
