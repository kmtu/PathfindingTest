import java.awt.Point;


/**
 * @author TuTu
 * @date Sep 29, 2008
 */
public enum Direction {
	UP (0, -1, 10),
	RIGHT (1, 0, 10),
	DOWN (0, 1, 10),
	LEFT (-1, 0, 10),
	UP_RIGHT (1, -1, 14),
	DOWN_RIGHT (1, 1, 14),
	DOWN_LEFT (-1, 1, 14),
	UP_LEFT (-1, -1, 14);
	
	public int x, y, cost;
	public double unitX, unitY;
	Direction (int x, int y, int cost) {
		this.x = x;
		this.y = y;
		this.cost = cost;
		double length = Math.sqrt(x*x + y*y);
		this.unitX = x / length;
		this.unitY = y / length;
	}
	
	public boolean isHorizontal() {
		if (this == RIGHT || this == LEFT)
			return true;
		else
			return false;
	}
	
	public boolean isVertical() {
		if (this == UP || this == DOWN)
			return true;
		else
			return false;
	}

	public boolean isDiagonal() {
//		if (!this.isHorizontal() && !this.isVertical())
//			return true;
//		else
//			return false;
		return this.ordinal() > 3;
	}
	
	public static Direction getDirection(final double dx, final double dy) {
		if (dx > 0) {
			if (dy > 0)
				return DOWN_RIGHT;
			else if (dy == 0)
				return RIGHT;
			else // if (dy < 0)
				return UP_RIGHT;
		}
		else if (dx == 0) {
			if (dy > 0)
				return DOWN;
			else if (dy == 0)
				return null;
			else // if (dy < 0)
				return UP;
		}
		else { // if (dx < 0)
			if (dy > 0)
				return DOWN_LEFT;
			else if (dy == 0)
				return LEFT;
			else // if (dy < 0)
				return UP_LEFT;
		}
	}
	
	public static Direction getDirection(Point start, Point end) {
		int dx = end.x - start.x;
		int dy = end.y - start.y;
		return getDirection(dx, dy);
	}
}
