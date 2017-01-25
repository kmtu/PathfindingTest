import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public abstract class Player {	
//	private ArrayList<Unit> unitList;
	private ArrayList<Group> groupList;
	private static final int INI_NUM_GROUPS = 50;
	private World world;
	private Color color;
	
	public Player(World world, Color color) {
		super();
		this.world = world;
		this.color = color;
		groupList = new ArrayList<Group>(INI_NUM_GROUPS);
	}

	public void excuteOrder(final double timeStep) {
		for (int i = 0; i < groupList.size(); i++) {
			groupList.get(i).excuteOrder(timeStep);
		}
	}
	
//	public void addGroup(Group group) {
//		groupList.add(group);
//	}
	
	private Group createGroup() {
		Group newGroup = new Group(this);
		groupList.add(newGroup);
		return newGroup;
	}
	
//	public void removeGroup(Group group) {
//		if (!groupList.remove(group))
//			throw new IllegalArgumentException("The group you tried to remove does not belong to the player!");
//	}
	
	public Color getColor() {
		return color;
	}
	
	public World getWorld() {
		return world;
	}
	
	public void createUnit(Unit.Type unitType, Point pos) {
		createUnit(unitType, pos, this.color);
	}
	
	public void createUnit(Unit.Type unitType, Point pos, Color color) {
		Group newGroup = this.createGroup();
		switch (unitType) {
			case BASIC_BATTLE_UNIT:
				newGroup.addUnit(new BasicBattleUnit(color, pos, newGroup));
				break;
		}
	}
	
	protected Group formGroup(ArrayList<Unit> unitList) {
		boolean inSameGroup = true;
		if (unitList.size() > 1) {
			for (int i = 0; i < unitList.size()-1; i++) {
	        	if ( !unitList.get(i).getGroup().equals(unitList.get(i+1).getGroup()))
	        		inSameGroup = false;
	    	}
		}
		if (inSameGroup && unitList.get(0).getGroup().getNumOfUnits() == unitList.size()) {
    		return unitList.get(0).getGroup();
    	}
		else {
			Group newGroup = this.createGroup();
			this.changeGroup(unitList, newGroup);
			return newGroup;
		}
	}

	private void changeGroup(ArrayList<Unit> unitList, Group newGroup) {
		Group group;
		for (int i = 0; i < unitList.size(); i++) {
			group = unitList.get(i).getGroup();
			group.removeUnit(unitList.get(i), newGroup);
			if (group.getNumOfUnits() == 0)
				groupList.remove(group);
			newGroup.addUnit(unitList.get(i));
		}		
	}
	
// Debug
//public void removeAllUnit(World.Layer layer) {
//	for (int i = 0; i < groupList.size(); i++) {
//		groupList.get(i).removeAllUnit(layer);
//		if (groupList.get(i).getNumOfUnits() == 0)
//			groupList.remove(i);
//	}
//}

//Debug
//public void removeUnit(Unit unit) {
//System.out.println(unit.toString());
//	Group group = unit.getGroup();
//	if (group.getNumOfUnits() == 0)
//		groupList.remove(group);
//	unit.getBlockShape().removeUnit(unit.getLayer());
//}

}
