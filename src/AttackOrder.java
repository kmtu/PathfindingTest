
public class AttackOrder extends Order {
	Unit targetUnit;
	
	public AttackOrder(Unit targetUnit) {
		super(Order.Type.ATTACK);
	}
}
