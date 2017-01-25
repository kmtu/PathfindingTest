import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.EnumMap;

public abstract class RectangularShapeUnit extends Unit {
	private Dimension dimension;
	
//	public RectangularShapeUnit(Point position, Group group, Dimension dimension, Type type, EnumMap<World.Terrain, Double> movingSpeedFactorMap) {
//		super(BlockShape.makeRectangularBlockShape(group.getOwner().getWorld().getBoard(), dimension, position), position, group, type, movingSpeedFactorMap);
//		this.dimension = dimension; 
//	}
	
	public RectangularShapeUnit(Color color, Point position, Group group, Dimension dimension, Type type, EnumMap<World.Terrain, Double> movingSpeedFactorMap) {
		super(color, BlockShape.makeRectangularBlockShape(group.getOwner().getWorld().getBoard(), dimension, position), position, group, type, movingSpeedFactorMap);
		this.dimension = dimension; 
	}
	
	/**
     * Get the dimension of this rect-shape unit
     * 
     * @return dimension
     */
    public final Dimension getDimension() {
        return dimension;
    }

	/* (non-Javadoc)
	 * @see Unit#calculateDrawPositionFromReference()
	 */
	@Override
	protected void calculateDrawPositionFromReference() {
		super.setDrawPosition(super.getReferencePosition().x - (dimension.width/2), super.getReferencePosition().y - (dimension.height/2));
	}
}
