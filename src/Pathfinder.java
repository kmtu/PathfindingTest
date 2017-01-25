import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;


public class Pathfinder {
	private Board board;
	private Block[] openList;
	private Block[] closeList;
	private int lastIndexOfOpenList;
	private int lastIndexOfCloseList;
	private static long openState = 0;
	private static long closeState = 1;

// Debug
//	private MotherPlayer motherPlayer;
	
	public Pathfinder(Board board) {
		this.board = board;
		this.openList = new Block[board.getDimension().width * board.getDimension().height +1];
		this.closeList = new Block[board.getDimension().width * board.getDimension().height +1];
//		this.lastIndexOfOpenList = 0;
//		this.lastIndexOfCloseList = 0;
	}

	public ArrayList<Point> findPath(Unit unit, Point start, Point end) {
		Block currentNode;
		Block evaluateNode;
		Point evaluatePos = new Point();
		BlockShape evaluateBlockShape;
		int newRealCost;
		boolean[] passibility = new boolean[4]; //as Direction ordinal
// Debug
//motherPlayer = unit.getOwner().getWorld().getPlayers().getMotherPlayer();		

		openState += 2;
		closeState += 2;
		
		openList[1] = board.getBlock(start);
		this.lastIndexOfOpenList = 1;
		this.lastIndexOfCloseList = 0;
		openList[1].setRealCost(0);
		openList[1].setParentNode(null);
		
		while (true) {
// Debug
//System.out.println("Enter findPath while loop");
			if (openList[1].getPosition().equals(end)) {
			// found the path!!
// Debug
//System.out.println("Found the path!");
				ArrayList<Point> path = new ArrayList<Point>(board.getDimension().width * board.getDimension().height);
				currentNode = openList[1];
				do {
// Debug
currentNode.setBackgroundColor(Color.RED);
//motherPlayer.removeUnit(currentNode.getUnit(World.Layer.BACKGROUND_LAYER));
//motherPlayer.createUnit(Unit.Type.BACKGROUND_UNIT, currentNode.getPosition(), Color.BLACK);
					path.add(currentNode.getPosition());
					currentNode = currentNode.getParentNode();
				} while (currentNode != null);
// Debug
//System.out.println(path.size());
//System.out.println(path.toString());
				return path;
				// return a path from the end block to the 2nd start block
			}
			else if (lastIndexOfOpenList == 0) {
			// unable to find the path
// Debug
//System.out.println("Found no path!");
				return null;
			}
			currentNode = openList[1];
			closeLowestNode();
			
// Debug
//System.out.println("enter direction_loop");
			direction_loop:
				for (Direction dir : Direction.values()) {
// Debug
//System.out.println(dir.toString());
					evaluatePos.setLocation(currentNode.getPosition().x + dir.x, currentNode.getPosition().y + dir.y);
					if (board.isInside(evaluatePos)) {
// Debug
//System.out.println("board.isInside(evaluatePos)");
						evaluateNode = board.getBlock(evaluatePos);
						if (isOnCloseList(evaluateNode)) {
							if (!dir.isDiagonal())
								passibility[dir.ordinal()] = true;
							continue direction_loop;
						}
					}
					else {
						if (!dir.isDiagonal())
							passibility[dir.ordinal()] = false;
						continue direction_loop;
					}
					
					
					if (dir.isDiagonal()) {
// Debug
//System.out.println("dir.isDiagonal()");
						switch (dir) {
							case UP_RIGHT:
								if (!passibility[Direction.UP.ordinal()] || !passibility[Direction.RIGHT.ordinal()])
									continue direction_loop;
							case DOWN_RIGHT:
								if (!passibility[Direction.DOWN.ordinal()] || !passibility[Direction.RIGHT.ordinal()])
									continue direction_loop;
							case DOWN_LEFT:
								if (!passibility[Direction.DOWN.ordinal()] || !passibility[Direction.LEFT.ordinal()])
									continue direction_loop;
							case UP_LEFT:
								if (!passibility[Direction.UP.ordinal()] || !passibility[Direction.LEFT.ordinal()])
									continue direction_loop;
						}
					}
						
					
					if (isOnOpenList(evaluateNode)) {
						if (!dir.isDiagonal())
							passibility[dir.ordinal()] = true;
						newRealCost = currentNode.getRealCost() + dir.cost;
						if (newRealCost < evaluateNode.getRealCost()) {
							evaluateNode.setRealCost(newRealCost);
							evaluateNode.setParentNode(currentNode);
							for (int i = 1; i <= lastIndexOfOpenList; i++){
								if (evaluateNode.getPosition().equals(openList[i].getPosition())) {
									resortOpenList(i);
									break;
								}
							}
						}
					}
					else { // evaluateNode is not on openList
// Debug
//System.out.println("Not on openList");
//System.out.println(unit.getBlockShape().getBlock(0).toString());
						evaluateBlockShape = unit.getBlockShape().clone();
						evaluateBlockShape.setLocation(evaluateNode);
						
						if (evaluateBlockShape.isAbleToStepOn(unit)) {
// Debug
//System.out.println("evaluateBlockShape.isAbleToStepOn(unit)");
							
							if (currentNode.getPosition().distance(start) < 10 && evaluateBlockShape.isStaticUnitOccupied(unit)) {
							// avoid neighbor static unit, where 10 is the avoiding radius
								if (!dir.isDiagonal()) {
									passibility[dir.ordinal()] = false;
								}
							}
							else {
								evaluateNode.setParentNode(currentNode);
								evaluateNode.calculateEstimatedCost(end);
								evaluateNode.setRealCost(currentNode.getRealCost() + dir.cost);
								addToOpenList(evaluateNode);
								if (!dir.isDiagonal()) {
									passibility[dir.ordinal()] = true;
								}
							}
						}
						else {
							// evalutaeNode is unable to step on for the unit
							if (!dir.isDiagonal()) {
								passibility[dir.ordinal()] = false;
							}
						}	
					}
				}
			// direction_loop end
		}
	}
	
	private void closeLowestNode() {
// Debug
//System.out.println("closeLowestNode:" + openList[1].getPosition().toString() 
//		+ " realCost:" + openList[1].getRealCost() + " totCost:" + openList[1].getTotalCost());
openList[1].setBackgroundColor(Color.GREEN);
		openList[1].setState(closeState);
		lastIndexOfCloseList++;
		closeList[lastIndexOfCloseList] = openList[1];
		openList[1] = openList[lastIndexOfOpenList];
		lastIndexOfOpenList--;
		int currentIndex = 1;
// Debug
//System.out.println("Enter closeLowestNode while");
		while (currentIndex*2 <= lastIndexOfOpenList) {
			if (openList[currentIndex*2].getTotalCost() < openList[currentIndex].getTotalCost()) {
				if (currentIndex*2+1 <= lastIndexOfOpenList) {
					if (openList[currentIndex*2+1].getTotalCost() < openList[currentIndex*2].getTotalCost()) {
						swapArrayElement(openList, currentIndex, currentIndex*2+1);
						currentIndex = currentIndex*2+1;
					}
					else {
						swapArrayElement(openList, currentIndex, currentIndex*2);
						currentIndex = currentIndex*2;
					}
				}
				else {
					swapArrayElement(openList, currentIndex, currentIndex*2);
					currentIndex = currentIndex*2;
				}
			}
			else if (currentIndex*2+1 <= lastIndexOfOpenList) {
				if (openList[currentIndex*2+1].getTotalCost() < openList[currentIndex].getTotalCost()) {
					swapArrayElement(openList, currentIndex, currentIndex*2+1);
					currentIndex = currentIndex*2+1;
				}
				else
					break;
			}
			else
				break;
		}
	}
	
	private void addToOpenList(Block node) {
// Debug
node.setBackgroundColor(Color.GRAY);
//System.out.println("addToOpenList:" + node.getPosition().toString() 
//		+ "realCost:" + node.getRealCost() + " totCost:" + node.getTotalCost());
//motherPlayer.createUnit(Unit.Type.BACKGROUND_UNIT, node.getPosition(), Color.GRAY);
		node.setState(openState);
		lastIndexOfOpenList++;
		openList[lastIndexOfOpenList] = node;
		resortOpenList(lastIndexOfOpenList);
	}
	
	private void resortOpenList(int resortIndex) {
		while (resortIndex > 1) {
			if (openList[resortIndex].getTotalCost() < openList[resortIndex/2].getTotalCost()) {
				swapArrayElement(openList, resortIndex, resortIndex/2);
				resortIndex = resortIndex/2;
			}
			else
				break;
		}
	}
	
	private static void swapArrayElement(Object[] array, final int index1, final int index2) {
		Object swap;
		swap = array[index1];
		array[index1] = array[index2];
		array[index2] = swap;
	}
	
//	private void calculateCosts(Block from, Block end) {
//		
//	}
	
	private boolean isOnCloseList(Block node) {
		return node.getState() == closeState;
	}
	
	private boolean isOnOpenList(Block node) {
		return node.getState() == openState;
	}

}
