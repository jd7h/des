package Robot;

import lejos.robotics.Color;

public class Lake {

	public boolean found;
	public int color; // which color
	public float celsius; //which temp 
	//public int number; //at which place the lake is found
	
	public Lake(int color) {
		this.color = color;
		celsius = 0;
		found = false;
		//number = 0;
	}

}
