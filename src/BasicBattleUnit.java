import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.util.EnumMap;

public class BasicBattleUnit extends RectangularShapeUnit implements Selectable {
	private Ellipse2D.Double unitShape;
	private boolean selected;
	public static final Dimension DIMENSION = new Dimension(3,3);
	private static final double WHITE_EDGE_WIDTH = 0.1;
	
//	public BasicBattleUnit(Point position, Group group) {
//		super(position, group, DIMENSION, Unit.Type.BASIC_BATTLE_UNIT, makeMovingSpeedFactorMap());
//		unitShape = new Ellipse2D.Double();
//		selected = false;
//	}

	public BasicBattleUnit(Color color, Point position, Group group) {
		super(color, position, group, DIMENSION, Unit.Type.BASIC_BATTLE_UNIT, makeMovingSpeedFactorMap());
		unitShape = new Ellipse2D.Double();
		selected = false;
	}

	@Override
	void draw(Graphics2D g2) {
		g2.setStroke(IOManager.BASIC_STROKE);
//		Player owner = getOwner();
//        Color ownerColor = owner.getColor();
		makeBody();
		g2.setColor(super.getColor());
		g2.fill(unitShape);
		if (isSelected())
            drawSelection(g2);
	}

	private static EnumMap<World.Terrain, Double> makeMovingSpeedFactorMap() {
//		EnumMap<World.Terrain, Double> movingSpeedFactorMap =  new EnumMap<World.Terrain, Double>(World.Terrain.class);
//		movingSpeedFactorMap.put(World.Terrain.FREE_GOUND, 1.);
//		return movingSpeedFactorMap;
		return null; // use default values: FREE_GOUND=1, others=0
	}
	
	/**
     * Make and set the body shape of this unit.
     */
    private void makeBody() {
    	unitShape.setFrame(getDrawPosition().x + WHITE_EDGE_WIDTH, getDrawPosition().y + WHITE_EDGE_WIDTH,
                getDimension().width - 2*WHITE_EDGE_WIDTH, getDimension().height - 2*WHITE_EDGE_WIDTH);
    }
    
    /**
     * Draw the selection glow of this unit.
     * 
     * @param  g2   the context of the graphics
     */
    protected void drawSelection(Graphics2D g2)
    {
        g2.setStroke(IOManager.SELECTION_STROKE);
        g2.setColor(IOManager.SELECTION_GLOW_COLOR);
        g2.draw(unitShape);
    }

	public void deSelectIt() {
		selected = false;
	}

	public void selectIt() {
		selected = true;
	}
	
	public boolean isSelected() {
		return selected;
	}
}
