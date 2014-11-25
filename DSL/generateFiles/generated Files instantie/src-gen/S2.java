package example1;

import lejos.robotics.subsumption.*;

public class S2 implements Behavior{
	private boolean suppressed = false;
	private Robot robot;

	public S2(Robot robot){ 
		this.robot = robot;
	}

	public boolean takeControl(){
		if( (robot.bumper.isPressed())  && robot.getCurrentState == 0)
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
		robot.setCurrentState(1);
		suppressed = false;
		//convert all actions of this state to javacode
		right.backward();
		left.backward();
		Delay.msDelay(2000);
		right.stop(true);
		left.stop(true);
		Delay.msDelay(500);
		right.forward();
		left.backward();
		Delay.msDelay(1000);
	}