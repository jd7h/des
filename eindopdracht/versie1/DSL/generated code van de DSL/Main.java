

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
import lejos.util.Delay;

import java.util.Random;

public class Main{

//Constants for the Lightsensorvalues
public static int BRIGHT = 40;
public static int DARK = 30;

//public variables 
public static State current;

//maak een enum van de beginstates
	public enum State {
				//added extra state for when everything is finished
		START,
			//added extra state for when everything is finished
		PROBLEMLEFT,
			//added extra state for when everything is finished
		PROBLEMRIGHT,
			//added extra state for when everything is finished
		FINISHED
	}
	
//definieer lijst van endstates
static State[] endStates = {State.FINISHED};

//standaard equipment op Robot
private static NXTRegulatedMotor left;
private static NXTRegulatedMotor right;
private static LightSensor lightL;
private static LightSensor lightR;
private static TouchSensor bumperL;
private static TouchSensor bumperR;
private static NXTRegulatedMotor lamp;

//todo: zet een BT-kanaal op tussen de master en de slave
	
public static void main(String[] args){
	//initialiseer alle equipment
	left = Motor.A;
	right = Motor.B;
	lightL = new LightSensor(SensorPort.S1);
	lightR = new LightSensor(SensorPort.S2);
	bumperL = new TouchSensor(SensorPort.S3);
	bumperR = new TouchSensor(SensorPort.S4);
	lamp = Motor.C;

	//todo: zet de robot in de beginstate
	current = State.START;
	
	//opstart-info
	LCD.drawString("EndGameRobot",0,1);
	LCD.drawString("Judith & Mirjam",0,2);
	Button.waitForAnyPress();
	
	LCD.drawString("Left white",0,1);
	Button.waitForAnyPress();
	BRIGHT = lightL.readValue();
	LCD.drawString("Left black",0,1);
	Button.waitForAnyPress();
	DARK = lightL.readValue();

	//start de loop of doom
	while(!inEndState())
	{
		execute(current);
	}
}


//make methods for every state seperately
public static void Start()
{
	//first, execute all actions of this state
	LCD.drawString("Start",0,3);
	right.setSpeed(200);
	left.setSpeed(200);
	right.forward();
	left.forward();
	

	//leg de huidige tijd vast voor alle transitions met een timeoutcondition
	long starttime = System.currentTimeMillis();

	//when done, wait for a trigger for a transition
	boolean transitionTaken = false; 
	while(!transitionTaken){	
		if((BRIGHT-10 <= lightL.readValue() && lightL.readValue() <= BRIGHT+10
		)||(bumperL.isPressed()
		)){
			current = State.PROBLEMLEFT;
			transitionTaken = true;
		}else if(
(BRIGHT-10 <= lightR.readValue() && lightR.readValue() <= BRIGHT+10
		)||(bumperR.isPressed()
		)){
			current = State.PROBLEMRIGHT;
			transitionTaken = true;
		}else if(
(starttime + 3000 <= System.currentTimeMillis()
		)){
			current = State.FINISHED;
			transitionTaken = true;
		}
	}
}

public static void ProblemLeft()
{
	//first, execute all actions of this state
	LCD.drawString("ProblemLeft",0,3);
	left.stop(true);
	right.stop();
	right.setSpeed(200);
	left.setSpeed(200);
	right.backward();
	left.backward();
	Delay.msDelay(500);
	left.stop(true);
	right.stop();
	int degree =  randInt(15, 90);
	left.rotate(degree);
	

	//leg de huidige tijd vast voor alle transitions met een timeoutcondition
	long starttime = System.currentTimeMillis();

	//when done, wait for a trigger for a transition
	boolean transitionTaken = false; 
	while(!transitionTaken){	
		if((DARK-10 <= lightL.readValue() && lightL.readValue() <= DARK+10&&
		!bumperL.isPressed()
		)){
			current = State.START;
			transitionTaken = true;
		}else if(
(BRIGHT-10 <= lightL.readValue() && lightL.readValue() <= BRIGHT+10
		)||(bumperL.isPressed()
		)){
			current = State.PROBLEMLEFT;
			transitionTaken = true;
		}
	}
}

public static void ProblemRight()
{
	//first, execute all actions of this state
	LCD.drawString("ProblemRight",0,3);
	left.stop(true);
	right.stop();
	right.setSpeed(200);
	left.setSpeed(200);
	right.backward();
	left.backward();
	Delay.msDelay(500);
	left.stop(true);
	right.stop();
	int degree =  randInt(15, 90);
	right.rotate(degree);
	

	//leg de huidige tijd vast voor alle transitions met een timeoutcondition
	long starttime = System.currentTimeMillis();

	//when done, wait for a trigger for a transition
	boolean transitionTaken = false; 
	while(!transitionTaken){	
		if((BRIGHT-10 <= lightR.readValue() && lightR.readValue() <= BRIGHT+10
		)||(bumperR.isPressed()
		)){
			current = State.PROBLEMRIGHT;
			transitionTaken = true;
		}else if(
(DARK-10 <= lightR.readValue() && lightR.readValue() <= DARK+10&&
		!bumperR.isPressed()
		)){
			current = State.START;
			transitionTaken = true;
		}
	}
}

public static void Finished()
{
	//first, execute all actions of this state
	LCD.drawString("Finished",0,3);
	left.stop(true);
	right.stop();
	
}

public static void execute(State s)
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
	case FINISHED:
		Finished();
		break;
		default:
			break;
	}
}	

public static boolean inEndState()
{
	for (State s : endStates) 
		if (s.equals(current)) 
			return true;

	return false;
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