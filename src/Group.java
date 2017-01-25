import java.util.ArrayList;
import java.awt.Point;
import java.lang.IllegalArgumentException;

public class Group {
//	private World world;
	private ArrayList<Unit> unitList;
	private ArrayList<Order> orderList;
	private Order processingOrder;
	
	// below index stands for each unit in group
	private ArrayList<Order.Status> processingOrderStatusList;
	private ArrayList<Boolean> orderCancelingList;
	private ArrayList<ArrayList<Point>> pathList;
	
	private Player owner;
//	private boolean halt;
	private static final int INI_NUM_UNITS = 50;
	private static final int INI_NUM_QUEUEING_ORDERS = 10;
//	private static final int INI_NUM_POINTS = 500;

	protected Group(Player owner) {
		super();
		this.owner = owner;
		unitList = new ArrayList<Unit>(INI_NUM_UNITS);
		orderList = new ArrayList<Order>(INI_NUM_QUEUEING_ORDERS);
		pathList = new ArrayList<ArrayList<Point>>(INI_NUM_UNITS);
		processingOrderStatusList = new ArrayList<Order.Status>(INI_NUM_UNITS);
		orderCancelingList = new ArrayList<Boolean>(INI_NUM_UNITS);
	}

//	public Group(Unit unit) {
//		this(unit.getOwner());
//		unitList.add(unit);
//	}

//	public Group(ArrayList<Unit> units) {
//		for (int i = 1; i < units.size(); i++) {
//			if (!units.get(i).getOwner().equals(units.get(i).getOwner()))
//				throw new IllegalArgumentException("Units in a group must have the same owner!");
//		}
//		unitList = new ArrayList<Unit>();
//		orderList = new ArrayList<Order>();
//		for (int i = 0; i < units.size(); i++) {
//			units.get(i).changeToGroup(this);
//		}
//	}

	public void excuteOrder(final double timeStep) {
// Debug
//System.out.println("excutingOrder");
//System.out.println(orderList.toString());
//////
		if (processingOrder == null) {
			if (!orderList.isEmpty()) {
// Debug
owner.getWorld().getBoard().resetBackgroundColor();
System.out.println("queOrder size:" + this.orderList.size());
				processingOrder = orderList.remove(0);
				resetProcessingOrderStatusList();
			}
		}
		
		else {
//			if (isOneProcessingOrder(Order.Status.FAILED) && isNoProcessingOrder(Order.Status.PROCESSING)) {
//				processingOrder = null;
//				orderList.clear();
//				resetProcessingOrderStatusList();
//			}
//			else if (isAllProcessingOrder(Order.Status.DONE)) {
//				processingOrder = null;
//			}
			if (isNoProcessingOrder(Order.Status.PROCESSING)) {
				processingOrder = null;
			}
			// there is an unfinished order
			else {
				switch (processingOrder.getType()) {
					case MOVE:
						MoveOrder moveOrder = (MoveOrder)processingOrder;
						for (int i = 0; i < unitList.size() ; i++) {
							if (this.processingOrderStatusList.get(i) == Order.Status.PROCESSING) {
								Unit movingUnit = unitList.get(i);
								if (movingUnit.getSpeed() > 0) {
									movingUnit.setState(Unit.State.MOVING);
									if (pathList.get(i) == null) {
										pathList.set(i, owner.getWorld().getBoard().findPath(movingUnit, movingUnit.getBlockPosition(), moveOrder.getDestination()));
										if (pathList.get(i) == null) {
				// Debug
				//System.out.println("deleteCurrentOrder: No path!");
				//System.exit(1);
											// No path
		//									processingOrder.setStatus(Order.Status.FAILED);
											processingOrderStatusList.set(i, Order.Status.FAILED);
											orderCancelingList.set(i, false);
											movingUnit.setState(Unit.State.STANDBY);
										}
									}
//									else {
										if (movingUnit.isFitInBlock()) {
											if (orderCancelingList.get(i)) { // terminating, cancel order
												pathList.set(i, null);
		//											processingOrder.setStatus(Order.Status.DONE);
												processingOrderStatusList.set(i, Order.Status.DONE);
												orderCancelingList.set(i, false);
												movingUnit.setState(Unit.State.STANDBY);
												continue;
											}
											else if (movingUnit.getCollisionUnitList().isEmpty()) { // no collision, no waiting
												pathList.get(i).remove(pathList.get(i).size()-1);
											}
											
											if (!pathList.get(i).isEmpty()) {
												Direction dir = Direction.getDirection(movingUnit.getBlockPosition(), pathList.get(i).get(pathList.get(i).size()-1));
												if (dir != null) {
													movingUnit.setDirection(dir);
													MOVE_LOOP:
													while (!movingUnit.move(timeStep)) {
													// unit is unable to move to that direction; path needs to be changed!
														ArrayList<Unit> collisionUnitList = movingUnit.getCollisionUnitList();
														for (int j = 0; j < collisionUnitList.size(); j++) {
															if ((collisionUnitList.get(j).getState() != Unit.State.MOVING && collisionUnitList.get(j).getState() != Unit.State.WAITING_TO_MOVE)
																|| ( collisionUnitList.get(j).getCollisionUnitList().contains(movingUnit) && collisionUnitList.get(j).getState() == Unit.State.WAITING_TO_MOVE) ) {
																// blocked by static unit(s) || head collision with other units: find a new path || 
																pathList.set(i, owner.getWorld().getBoard().findPath(movingUnit, movingUnit.getBlockPosition(), moveOrder.getDestination()));
																if (pathList.get(i) == null) {
																	// No path, stop and failed
																	processingOrderStatusList.set(i, Order.Status.FAILED);
																	orderCancelingList.set(i, false);
																	movingUnit.setState(Unit.State.STANDBY);
																	break MOVE_LOOP;
																}
																else {
																	pathList.get(i).remove(pathList.get(i).size()-1);
																	if (!pathList.get(i).isEmpty()) {
																		dir = Direction.getDirection(movingUnit.getBlockPosition(), pathList.get(i).get(pathList.get(i).size()-1));
																		if (dir != null) {
																			movingUnit.setDirection(dir);
																			continue;
//																			if (!movingUnit.move(timeStep))		
//																				throw new RuntimeException("A second pathfinding should have avoided neighbor collision!");
																		}
																	}
																	else
																		throw new RuntimeException("A second pathfinding occured, path should not be empty, i.e. impossible reach destination now!");
																}
															}
															else {
																// wait for other units move out!
																movingUnit.setState(Unit.State.WAITING_TO_MOVE);
																break MOVE_LOOP;
															}
														}
//														pathList.set(i, null);
//														processingOrderStatusList.set(i, Order.Status.FAILED);
//														movingUnit.setState(Unit.State.STANDBY);
													}
													// able to move
//													movingUnit.clearCollisionUnitList();
												}
//												else { // same as destination reached!
//													// direction == null, the unit has no need to move
//													pathList.set(i, null);
//													processingOrderStatusList.set(i, Order.Status.DONE);
//													movingUnit.setState(Unit.State.STANDBY);
//												}
											}
											else {// destination reached!
												//TODO
												//Refine the definition of reaching the destination to avoid swing back and forth!
												pathList.set(i, null);
		//										processingOrder.setStatus(Order.Status.DONE);
												processingOrderStatusList.set(i, Order.Status.DONE);
												movingUnit.setState(Unit.State.STANDBY);
											}
										}
										else {// if unit is not fit in block, let it keep moving
											movingUnit.move(timeStep);
										}
//									}
								}
								else {
									// the unit is not movable, ignore it
								}
							}
							else {
								// the unit has finished or failed the order, ignore it
							}
						}
						break;
					case ATTACK:
	//					if (attackPower != -1) 
	//						AttackOrder attackOrder = (AttackOrder)orderList.get(0);
						break;
				}
			}
		}
	}

//	private boolean isAllProcessingOrder(Order.Status status) {
//		for (int i = 0; i < this.processingOrderStatusList.size(); i++) {
//			if (processingOrderStatusList.get(i) != status)
//				return false;
//		}
//		return true;
//	}
	
	private boolean isOneProcessingOrder(Order.Status status) {
		return this.processingOrderStatusList.contains(status);
	}

	private boolean isNoProcessingOrder(Order.Status status) {
		return !this.processingOrderStatusList.contains(status);
	}
	
	private void resetProcessingOrderStatusList() {
		for (int i = 0; i < this.processingOrderStatusList.size(); i++) {
			processingOrderStatusList.set(i, Order.Status.PROCESSING);
		}
	}

	public void removeUnit(Unit unit, Group newGroup) {
		int index = unitList.indexOf(unit);
		if (unitList.remove(unit)) {
			if (!pathList.isEmpty()) {
				pathList.remove(index);
				processingOrderStatusList.remove(index);
				orderCancelingList.remove(index);
			}
			unit.setGroup(newGroup);
		}
		else {
			throw new IllegalArgumentException("The unit you tried to remove does not belong to the group!");
		}
	}
	
	public void addUnit(Unit unit) {
		if (!unitList.contains(unit)) {
			unitList.add(unit);
			pathList.add(null);
			processingOrderStatusList.add(Order.Status.PROCESSING);
			orderCancelingList.add(false);
		}
		else
			throw new IllegalArgumentException("The unit you tried to add is already in the group!");
	}

	public void takeOrder(ArrayList<Order> queueOrderList) {
//		if (queueOrderList.size() == 1) {
////Debug
//owner.getWorld().getBoard().resetBackgroundColor();
//			this.cancelAllOrders();
//		}
//		if (queueOrderList.get(0).getType() != Order.Type.STOP) {
			// No need to add the StopOrder into orderList, just skip it
			orderList.addAll(queueOrderList);
//		}
	}
	
	public void cancelAllOrders() {
		if (!orderList.isEmpty())
			orderList.clear();
		cancelProcessingOrder();
	}
	
	private void cancelProcessingOrder() {
		if (processingOrder != null && isOneProcessingOrder(Order.Status.PROCESSING)) {
			for (int i = 0; i < this.unitList.size(); i++) {
				if (this.processingOrderStatusList.get(i) == Order.Status.PROCESSING)
					orderCancelingList.set(i, true);
			}
		}
	}

	public Player getOwner() {
        return owner;
    }
	
	public int getNumOfUnits() {
		return unitList.size();
	}

// Debug
//public void removeAllUnit(World.Layer layer) {
//	for (int i = 0; i < unitList.size(); i++) {
//		if (unitList.get(i).getLayer().name().equals(layer.name())) {
//			unitList.get(i).getBlockShape().removeUnit(layer);
//			unitList.remove(i);
//		}
//	}
//}
//Debug
//public boolean isContain(Unit unit) {
//	return unitList.contains(unit);
//}

}
