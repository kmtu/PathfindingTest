import java.awt.Point;


public class MoveOrder extends Order {
	private final Point destination;
	
	public MoveOrder(final Point destination) {
		super(Order.Type.MOVE);
		this.destination = destination;
	}

	public Point getDestination() {
		return destination;
	}
}
