import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

public class Block {
	private Point position;
	private Unit[] units; //one unit on each layer, usage: unit[<World.Layer>.ordinal()]
	private World.Terrain terrain;
	
	//varibles for pathfinding
	private Block parentNode;
	private int realCost = 0;
	private int estimatedCost = 0;
	private int totalCost = 0;
	private long state;
	
///***debug*****/	
private Rectangle2D.Double blockRectangle;
private Color backgroundColor;

	public Block(Point position, World.Terrain terrain) {
		super();
		if (position != null) {
			this.terrain = terrain;
			this.position = position;
			units = new Unit[World.Layer.NUM_LAYERS];
			for (int i = 0; i < World.Layer.NUM_LAYERS; i++) {
				units[i] = null;
			}
			this.state = -1;
		blockRectangle = new Rectangle2D.Double(position.x, position.y, 1, 1);
		}
	}
	
	public Block(Point position) {
		this(position, World.Terrain.FREE_GOUND);
	}
	
//	protected Block(Object NULL)
//    {
//        position = new Point(-1, -1);
//        unit = null;
//        setStepOnAbility(false);
//        if (NULL != null)
//            throw new IllegalArgumentException("Block(Object NULL) can only be called by NullBlock!");
//    }
	
	public void draw(Graphics2D g2, World.Layer layer) {

		if (backgroundColor != null && layer == World.Layer.BACKGROUND_LAYER ) {
			g2.setColor(backgroundColor);
			g2.fill(blockRectangle);
		}
		
/***debug*****/			
//	if (units[i] != null) {
//		
//		g2.setColor(Color.BLACK);
//		Rectangle2D.Double blockRect = new Rectangle2D.Double(position.x, position.y, 1, 1);;
//		g2.fill(blockRect);
//	}
/*************/
		if (units[layer.ordinal()] != null && units[layer.ordinal()].getBlockPosition().equals(this.position))
			units[layer.ordinal()].draw(g2);
	}
	
	/**
     * Add a unit to this block
     * 
     * @param   unit to be added on this block
     */
    public void addUnit(Unit unit)
    {
        if (units[unit.getLayer().ordinal()] != null && !units[unit.getLayer().ordinal()].equals(unit))
        {
            throw new IllegalStateException("A layer of a block can only have one unit at a time.");
        }
        else
        {
            units[unit.getLayer().ordinal()] = unit;
        }
    }
    
    public boolean isUnitOn() {
    	for (int i = 0; i < World.Layer.NUM_LAYERS; i++) {
    		if (units[i] != null )
    			return true;
    	}	
    	return false;
    }
    
    /**
     * isAbleToStepOn
     * 
     * @return   true if this block is able to be stepped on
     */
    public boolean isAbleToStepOn(Unit unit)
    {
    	return unit.isAbleToStepOn(this.terrain);
    }
    
    public boolean isOccupied(Unit unit) {
    	if (units[unit.getLayer().ordinal()] == null || units[unit.getLayer().ordinal()].equals(unit))
    		return false;
    	else
    		return true;
    }
    
    public boolean isStaticUnitOccupied(Unit unit) {
    	if (isOccupied(unit) && !(units[unit.getLayer().ordinal()].getState() == Unit.State.MOVING))
    		return true;
    	else
    		return false;
    }
    
    public Unit getTopUnit() {
    	for (int i = World.Layer.NUM_LAYERS-1; i >= 0; i--) {
    		if (units[i] != null)
    			return units[i];
    	}
    	return null;
    }
    
    public Unit getUnit(World.Layer layer) {
    	return units[layer.ordinal()];
    }
    
    public Point getPosition() {
    	return position;
    }
    
    public int getTotalCost() {
    	return totalCost;
    }
    
    public int getRealCost() {
    	return realCost;
    }
    
    public void calculateEstimatedCost(Point end) {
    	estimatedCost = Math.abs(end.x - this.position.x)*10 + Math.abs(end.y - this.position.y)*10;
    }
    
    public void setRealCost(int cost) {
    	realCost = cost;
    	totalCost = realCost + estimatedCost;
    }
    
    public long getState() {
    	return state;
    }
    
    public void setState(long st) {
    	state = st;
    }
    
    public void setParentNode(Block parent) {
    	parentNode = parent;
    }
    
    public Block getParentNode() {
    	if (parentNode != null)
    		return parentNode;
    	else
    		return null;
    }

	public void removeUnit(final World.Layer layer) {
		 units[layer.ordinal()] = null;
	}

	public void setTerrain(World.Terrain terrain) {
		this.terrain = terrain;
		backgroundColor = terrain.color;
	}
	
	public void setTerrain(final int terrainInt) {
		if (terrainInt > World.Terrain.NUM_TERRAINS - 1)
			throw new IllegalArgumentException("Unable to assign Terrain int="+terrainInt+". Enums: World.Terrain has only "+ World.Terrain.NUM_TERRAINS + " terrains!");
		for (World.Terrain terrain : World.Terrain.values()) {
			if (terrain.ordinal() == terrainInt) {
				this.terrain = terrain;
				break;
			}
		}
		backgroundColor = this.terrain.color;
	}
	
	public World.Terrain getTerrain() {
		return this.terrain;
	}
	
// Debug
public void setBackgroundColor(Color color) {
	backgroundColor = color;
}
	
// Debug
public void resetBackgroundColor() {
backgroundColor = terrain.color;
}
	
// Debug
//public Unit getUnit(World.Layer layer) {
//	return units[layer.ordinal()];
//}

}
