package inventory;

import items.Jetpack;
import items.SpeedBox;

public class DummyItem {

	private int count;
	private Item i;

	public DummyItem(String type) {
		count = 0;
		if (type.equals("Speed"))
			i = new SpeedBox();
		else if(type.equals("Jetpack"))
			i = new Jetpack();
		else
			i = null;
	}

	public void incrementCount() {
		count++;
	}

	public void decrementCount() {
		count--;
	}

	public int getCount() {
		return count;
	}

	public String getType() {
		return i.getType();
	}

	public void use() {
		i.use();
	}

}