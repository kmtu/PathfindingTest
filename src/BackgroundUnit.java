import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;


public class BackgroundUnit extends RectangularShapeUnit {
	private static final Dimension DIMENSION = new Dimension(1,1);
	
//	public BackgroundUnit(Point position, Group group) {
//		super(position, group, DIMENSION, null, null);
//	}
	
	public BackgroundUnit(Color color, Point position, Group group) {
		super(color, position, group, DIMENSION, null, null);
	}

	@Override
	void draw(Graphics2D g2) {
		Point pos = super.getBlockPosition();
		g2.setColor(super.getColor());
		Rectangle2D.Double blockRect = new Rectangle2D.Double(pos.x, pos.y, 1, 1);;
		g2.fill(blockRect);
	}
}
