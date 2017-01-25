import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.EnumMap;

public abstract class Unit {
	public static enum Type {
		BASIC_BATTLE_UNIT (World.Layer.GROUND_UPPER_UNIT_LAYER, 10.);
		
		private final double basicSpeed;
		private final World.Layer layer;
//		public EnumMap<World.Terrain, Double> movingSpeedFactorMap;
		public static final int NUM_UNIT_TYPES = 2;
		Type(World.Layer layer, double basicSpeed) {
			this.layer = layer;
			this.basicSpeed = basicSpeed;
		}
	}
	
	public static enum State {
		STANDBY,
		MOVING,
		WAITING_TO_MOVE;
	}
	
	private BlockShape blockShape;
	private Group group;
	private Point2D.Double referencePosition; // reference position (the exact top-left point of the reference block)
	private Point2D.Double drawPosition; // exact top-left (for drawing) position
	private double speed;
	private Type type;
	private Color color;
	private boolean fitInBlock;
	private EnumMap<World.Terrain, Double> movingSpeedFactorMap;
	private Direction movingDirection;
	private State state;
	private ArrayList<Unit> collisionUnitList;
	
	public Unit(Color color, BlockShape blockShape, Point position, Group group, Type type, EnumMap<World.Terrain, Double> movingSpeedFactorMap) {
		this.blockShape = blockShape;
		this.referencePosition = new Point2D.Double(blockShape.getReferenceBlock().getPosition().x, blockShape.getReferenceBlock().getPosition().y);
		this.drawPosition = new Point2D.Double(position.x, position.y);
		this.type = type;
		this.group = group;
		this.speed = type.basicSpeed;
		blockShape.addUnit(this);
		this.color = color;
		fitInBlock = true;
		this.state = State.STANDBY;
		this.collisionUnitList = new ArrayList<Unit>(0);
		
		if (movingSpeedFactorMap == null) {
			movingSpeedFactorMap = new EnumMap<World.Terrain, Double>(World.Terrain.class);
			this.movingSpeedFactorMap = movingSpeedFactorMap;
			for (World.Terrain terrain : World.Terrain.values()) {
				if (!this.movingSpeedFactorMap.containsKey(terrain)) {
					if (terrain == World.Terrain.FREE_GOUND)
						this.movingSpeedFactorMap.put(terrain, 1.);
					else
						this.movingSpeedFactorMap.put(terrain, 0.);
				}
			}
		}
		else {
			this.movingSpeedFactorMap = movingSpeedFactorMap;
		}
		if (!blockShape.isAbleToStepOn(this) || blockShape.isOccupied(this)) {
			throw new IllegalArgumentException("Initialize unit failed! Unit:"+ this.toString() + " is unable to present at Position:" + blockShape.getReferenceBlock().getPosition().toString());
		}
	}
	
//	public Unit(BlockShape blockShape, Point position, Group group, Type type, EnumMap<World.Terrain, Double> movingSpeedFactorMap) {
//		this(group.getOwner().getColor(), blockShape, position, group, type, movingSpeedFactorMap);
//	}
	
	abstract void draw(Graphics2D g2);
	
	public World.Layer getLayer() {
		return type.layer;
	}
	
	public Player getOwner() {
        return group.getOwner();
    }
	
	public Point getBlockPosition() {
//		return new Point((int)exactPosition.x, (int)exactPosition.y);
		return blockShape.getReferenceBlock().getPosition();
	}
	
	public Point2D.Double getReferencePosition() {
		return referencePosition;
	}
	
	public Point2D.Double getDrawPosition() {
		return drawPosition;
	}

//	/**
//	 * This method cannot be called freely.
//	 * It can only be called by Group.addUnit(Unit)
//	 * 
//	 * @param newGroup
//	 */
//	public void changeToGroup(Group newGroup) {
//		group.removeUnit(this);
//		group = newGroup;
//		// no need to call "group.addUnit(this)" because this method is just called by it!
//	}
	
	public Group getGroup() {
		return group;
	}
	
	/**
     * Get the BlockShape of this unit
     * 
     * @return BlockShape
     */
    public BlockShape getBlockShape()
    {
        return blockShape;
    }

    /* A unit can only be set direction while it is fit in block
    *  return true if it is fit in block, false if it is not
    */
    public boolean setDirection(Direction dir) {
    	if (fitInBlock) {
    		this.movingDirection = dir;
    		return true;
    	}
    	else
    		return false;
    }
    
	public boolean move(double timeStep) {
		collisionUnitList.clear();
		if (movingDirection != null) {
			double newX, newY;
			Point destination = new Point(this.getBlockPosition());
			destination.translate(movingDirection.x, movingDirection.y);
			BlockShape destShape = this.getBlockShape().getOuterBoundaryBlockShape(movingDirection);
			if (destShape.isAbleToStepOn(this) && !destShape.isOccupied(this)) {
				destShape.addUnit(this);
			}
			else { // unable to move to that direction
				this.setCollisionUnitList();
				return false;
			}
	// Debug
	//System.out.println("dx = " + dx + " dy = " + dy);
	//System.out.println(dir.toString());
	//System.out.println(this.exactPosition.toString());
	//System.out.println(this.speed);
			newX = this.referencePosition.x + this.getSpeed() * movingDirection.unitX * timeStep;
			newY = this.referencePosition.y + this.getSpeed() * movingDirection.unitY * timeStep;
			// return true if moving too far or is reached the dest, ie. exceed or at the destination position
			if (((newX >= destination.x && destination.x >= this.referencePosition.x) || (newX <= destination.x && destination.x <= this.referencePosition.x))
		            && ((newY >= destination.y && destination.y >= this.referencePosition.y) || (newY <= destination.y && destination.y <= this.referencePosition.y))) {
	// Debug
	//System.out.println("Fit in Block!");
				fitInBlock = true;
//				this.referencePosition.setLocation(destination);
				this.setReferencePosition(destination.x, destination.y);
				this.calculateDrawPositionFromReference();
				this.blockShape.removeUnit(type.layer);
				this.blockShape.translate(movingDirection.x, movingDirection.y);
				this.blockShape.addUnit(this);

	// Debug
	//System.out.println("Current Exact Position = " + this.exactPosition.toString());
	//System.out.println("Current Block Position = " + this.getBlockPosition().toString());
			}
			else {
				this.referencePosition.setLocation(newX, newY);
				this.calculateDrawPositionFromReference();
				fitInBlock = false;
			}
			return true;
		}
		else {// no direction to move
			throw new IllegalStateException("A unit can only be asked to move after giving it a direction!");
		}
	}

	private void setReferencePosition(double x, double y) {
		this.referencePosition.x = x;
		this.referencePosition.y = y;
	}
	
	protected void setDrawPosition(double x, double y) {
		this.drawPosition.x = x;
		this.drawPosition.y = y;
	}

	protected abstract void calculateDrawPositionFromReference();
	
	public double getSpeed() {
		return this.speed * this.blockShape.getLowestSpeedFactor(this.movingSpeedFactorMap);
	}
	
//	private double getSpeed(World.Terrain terrain) {
//		return speed * movingSpeedFactorMap.get(terrain);
////		Double speedFactor = movingSpeedFactorMap.get(terrain);
////		if (speedFactor != null)
////			return speed*speedFactor;
////		else // this should not happen since we have set all the terrains in the constructors
////			throw new IllegalStateException("Terrain:" + terrain.toString() + " is not set for Unit:"+ this.toString());
//	}
	
	public Color getColor() {
		return color;
	}
	
	public void setGroup(Group group) {
		this.group = group;
	}

	public boolean isFitInBlock() {
		return fitInBlock;
	}

	public boolean isAbleToStepOn(World.Terrain terrain) {
		Double speedFactor = movingSpeedFactorMap.get(terrain);
		if (speedFactor != null)
			return speedFactor > 0;
		else // this should not happen since we have set all the terrains in the constructors
			throw new IllegalStateException("Terrain:" + terrain.toString() + " is not set for Unit:"+ this.toString());
	}

//	public boolean isMoving() {
//		return state == State.MOVING;
//	}
	
	public State getState() {
		return this.state;
	}
	
	public void setState(State state) {
		if (state == Unit.State.STANDBY) {
			movingDirection = null;
			collisionUnitList.clear();
		}
		this.state = state;
	}

	private void setCollisionUnitList() {
//		if (movingDirection != null) {
//		}
//		else {// no direction to move
//			throw new IllegalStateException("A unit can only set collisionUnitList after giving it a moving direction!");
//		}
		this.collisionUnitList = this.getBlockShape().getOuterBoundaryBlockShape(this.movingDirection).getUnitList(this.getLayer());
	}
	
//	public void clearCollisionUnitList() {
//		collisionUnitList.clear();
//	}

	public ArrayList<Unit> getCollisionUnitList() {
		return this.collisionUnitList;
	}
}

