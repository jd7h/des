package robot2;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.robotics.subsumption.Behavior;


public class DetectBlackLine implements Behavior{
	private boolean suppressed = false;
	private ColorSensor color;
	
	public DetectBlackLine(SensorPort port)
	{
		color = new ColorSensor(port);
	}
	
	@Override
	public boolean takeControl() {
		return color.getColorID() == 7;
	}

	/* probleem: 
	 * robot rijdt nog een stukje door 
	 * voordat hij de actie doet, en daarna rijdt hij vaak over de zwarte lijn heen, 
	 * waardoor de action niet correct wordt afgehandeld.
	 * Fix:
	 * voeg true toe aan de driveForward behaviour. :)
	 */
	@Override
	public void action() {
		suppressed = false;
		Main.left.rotate(-180, true);// start Motor.A rotating backward
	    Main.right.rotate(-360);  // rotate C farther to make the turn
	}

	@Override
	public void suppress() {
		suppressed = true;
	}

}
