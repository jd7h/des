package example1;

import lejos.robotics.subsumption.*;

public class S1 implements Behavior{
	private boolean suppressed = false;
	private Robot robot;

	public S1(Robot robot){ 
		this.robot = robot;
	}

	public boolean takeControl(){
		if( (!robot.bumper.isPressed())  && robot.getCurrentState == 1)
			return true;
		return false;
	}

	public void suppress(){
		suppressed = true;
		//standaard gewoon de motoren stoppen?
		robot.right.stop(true);
		robot.left.stop();
		Sound.beep() //robot beeps on suppress
	}

	public void action(){
		robot.setCurrentState(0);
		suppressed = false;
		//convert all actions of this state to javacode
		right.forward();
		left.forward();
		Delay.msDelay(1000);
	}