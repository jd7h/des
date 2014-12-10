package example1;

/* 
 * Automatically generated code
 * eindopdracht DES
 * Judith van Stegeren and Mirjam van Nahmen
 * 
 */

/* 
 * assumptions:
 * this week only one brick, no bluetooth
     */

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.TouchSensor;
import lejos.nxt.Sound;
import lejos.nxt.ColorSensor;
import java.util.Random;
import java.util.Arrays;

public class Main{

//Constance for the Lightsensorvalues
public static int BRIGHT = 20;
public static int DARK = 80;

//public variables 
public State current;

//maak een enum van de beginstates
	public enum State {
				//added extra state for when everything is finished
		START,
			//added extra state for when everything is finished
		PROBLEMLEFT,
			//added extra state for when everything is finished
		PROBLEMRIGHT
,FINISHED	}
	
//definieer lijst van endstates
State[] endStates = {State.PROBLEMRIGHT};

//definieer standaard equipment op Robot
//maak de robot
//things on brick1 (Master)
NXTRegulatedMotor left = Motor.A;
NXTRegulatedMotor right = Motor.B;
LightSensor lightL = new LightSensor(SensorPort.S1);
LightSensor lightR = new LightSensor(SensorPort.S2);
TouchSensor bumperL = new TouchSensor(SensorPort.S3);
TouchSensor bumperR = new TouchSensor(SensorPort.S4);
NXTRegulatedMotor lamp = Motor.C;

//todo: zet een BT-kanaal op tussen de master en de slave
	
public Main(){

	//todo: zet de robot in de beginstate
	State current = State.START;
	
	//startconfiguratie met feedback
	LCD.drawString("EndGameRobot",0,1);
	LCD.drawString("Judith & Mirjam",0,2);
	Button.waitForAnyPress();

	//start de loop of doom
	while(!inEndState())
	{
		execute(current);
	}
}


//make methods for every state seperately
public void Start()
{
if(	(BRIGHT == lightL.readValue() 
	)||(bumperL.isPressed()
	)){
		current = State.PROBLEMLEFT;
		return; //later aanpassen: switch state
	}else if(
	(BRIGHT == lightR.readValue()
	)||(bumperR.isPressed()
	)){
		current = State.PROBLEMRIGHT;
		return; //later aanpassen: switch state
	}
	else{
		right.setSpeed(200);
		left.setSpeed(200);
		right.forward();
		left.forward();
if((BRIGHT == lightL.readValue() 
)||(bumperL.isPressed()
)){
		current = State.PROBLEMLEFT;
		return; //later aanpassen: switch state
	}else if(
(BRIGHT == lightR.readValue()
)||(bumperR.isPressed()
)){
		current = State.PROBLEMRIGHT;
		return; //later aanpassen: switch state
	}
	}
}

public void ProblemLeft()
{
if(	(DARK == lightL.readValue()&&
	!bumperL.isPressed()
	)){
		current = State.START;
		return; //later aanpassen: switch state
	}
	else{
		left.stop(true);
		right.stop();
if((DARK == lightL.readValue()&&
!bumperL.isPressed()
)){
		current = State.START;
		return; //later aanpassen: switch state
	}
	}
if(	(DARK == lightL.readValue()&&
	!bumperL.isPressed()
	)){
		current = State.START;
		return; //later aanpassen: switch state
	}
	else{
		int degree =  randInt(15, 90);
		right.rotate(degree);
if((DARK == lightL.readValue()&&
!bumperL.isPressed()
)){
		current = State.START;
		return; //later aanpassen: switch state
	}
	}
}

public void ProblemRight()
{
if(	(DARK == lightR.readValue()&&
	!bumperR.isPressed()
	)){
		current = State.START;
		return; //later aanpassen: switch state
	}
	else{
		left.stop(true);
		right.stop();
if((DARK == lightR.readValue()&&
!bumperR.isPressed()
)){
		current = State.START;
		return; //later aanpassen: switch state
	}
	}
if(	(DARK == lightR.readValue()&&
	!bumperR.isPressed()
	)){
		current = State.START;
		return; //later aanpassen: switch state
	}
	else{
		int degree =  randInt(15, 90);
		left.rotate(degree);
if((DARK == lightR.readValue()&&
!bumperR.isPressed()
)){
		current = State.START;
		return; //later aanpassen: switch state
	}
	}
}

public void execute(State s)
{
	switch(s){
	case START:
		Start();
		break;
	case PROBLEMLEFT:
		ProblemLeft();
		break;
	case PROBLEMRIGHT:
		ProblemRight();
		break;
		default:
			break;
	}
}	

public boolean inEndState()
{
	return Arrays.asList(endStates).contains(current);
}


	
	
	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {
	
	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();
	
	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	
	    return randomNum;
	}
}	