import java.util.ArrayList;
import java.awt.Color;
import java.util.NoSuchElementException;

public class HumanPlayer extends Player {
	private ArrayList<Selectable> selectedUnitList;
	private ArrayList<Order> queueOrderList;
	
	public HumanPlayer(World world, Color color) {
		super(world, color);
		selectedUnitList = new ArrayList<Selectable>();
		queueOrderList = new ArrayList<Order>();
	}
	
	/**
     * De-select a unit.
     * Remove a unit from selectedUnitList and change its selected state.
     * 
     * @param   unit to be de-selected
     */
    public void deSelectUnit(Selectable unit) {
        if (selectedUnitList.remove(unit))
            unit.deSelectIt();
        else
            throw new NoSuchElementException("Human has tried to de-select a non-selecting unit!");
    }
    
    /**
     * De-select all units of this player.
     * Remove all unit from selectedUnitList and change their selected state.
     */
    public void deSelectAllUnit() {
        for (int i = 0; i < selectedUnitList.size(); i++) {
        	selectedUnitList.get(i).deSelectIt();
        }
        selectedUnitList.clear();
        queueOrderList.clear();
    }
    
    /**
     * Select a unit.
     * Add a unit to selectedUnitList and change its selected state.
     * 
     * @param   unit to be selected
     */
    public void selectUnit(Selectable unit) {
        if (!selectedUnitList.contains(unit)) {
            selectedUnitList.add(unit);
            unit.selectIt();
        }
    }

	public void queueOrder(Order order) {
		if (order.getType() == Order.Type.STOP) {
			queueOrderList.clear();
		}
		else
			queueOrderList.add(order);
	}

	public void giveOrder() {
		if (!selectedUnitList.isEmpty()) {
	    	ArrayList<Unit> unitList = new ArrayList<Unit>(selectedUnitList.size());
	    	for (int i = 0; i < selectedUnitList.size(); i++) {
	    		unitList.add((Unit)selectedUnitList.get(i));
	    	}
	    	super.formGroup(unitList).cancelAllOrders();
	    	if (queueOrderList.size() > 0) {
	    		super.formGroup(unitList).takeOrder(queueOrderList);
	    	}
    	}
		queueOrderList.clear();
	}
	
//	public void clearQueueOrderList() {
//        queueOrderList.clear();
//	}

}
