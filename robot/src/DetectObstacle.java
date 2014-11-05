package robot2;

import lejos.nxt.*;
import lejos.robotics.subsumption.*;

public class DetectObstacle implements Behavior{
	private boolean suppressed = false;
	private TouchSensor bumper;
	private UltrasonicSensor sonar;
	
	public DetectObstacle(SensorPort bumperport, SensorPort sonarport)
	{
		bumper = new TouchSensor(bumperport);
		sonar = new UltrasonicSensor(sonarport);
	}
	
	public boolean takeControl() {
		sonar.ping();
		return sonar.getDistance() < 20 ||  bumper.isPressed(); 
	}

	public void suppress() {
		suppressed = true;
	}
	   
	public void action() {
		suppressed = false;
		Sound.buzz();
		if (bumper.isPressed())
		{
			//go backwards
			Main.left.rotate(-180, true);
			Main.right.rotate(-360);
		}
		else
		{
			//try to rotate away from the obstacle, arbiter will intervene if something's in the way
			Main.left.rotate(180, true);
		    Main.right.rotate(90, true);
		}	
	    while( Main.right.isMoving() && !suppressed )
	    	Thread.yield();		
	}
}
