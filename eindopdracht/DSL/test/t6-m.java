	
	package Robot;
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

	//sensors
	import lejos.nxt.Button;
	import lejos.nxt.LightSensor;
	import lejos.nxt.SensorPort;
	import lejos.nxt.UltrasonicSensor;
	import lejos.nxt.TouchSensor;
	import lejos.nxt.ColorSensor;
	import lejos.robotics.Color;
	import lejos.nxt.TemperatureSensor;
	import lejos.nxt.addon.RCXTemperatureSensor;

	//actuators
	import lejos.nxt.Motor;
	import lejos.nxt.NXTRegulatedMotor;
	import lejos.nxt.MotorPort;
	import lejos.nxt.LCD;
	import lejos.nxt.Sound;
	import lejos.nxt.addon.RCXMotor;

	//for bluetooth:
	import java.io.DataInputStream;
	import java.io.DataOutputStream;
	import lejos.nxt.comm.BTConnection;
	import lejos.nxt.comm.Bluetooth;
	import javax.bluetooth.RemoteDevice;
	import java.io.IOException;
	import java.util.ListIterator;

	//misc
	import java.util.Random;
	import lejos.util.Delay;

	
	public class Main{
	
	//Uncalibrated constants for the Lightsensorvalues
	public static int BRIGHT = 40;
	public static int DARK = 30;
	
	//public variables 
	public static State current;
	
	//maak een enum van de states
		public enum State {
		BTINIT,
		WANDER,
		PROBLEMLEFT,
		PROBLEMRIGHT,
		SONAROBSTACLE,
		COLORFOUND,
		FINISHED,
		WACHT,
		CONSUMETHENWANDER
		}
		
	//definieer lijst van endstates
	static State[] endStates = {State.FINISHED};
	
	//specificeer alle equipment
	//standaard equipment op Robot Master
	private static NXTRegulatedMotor left;
	private static NXTRegulatedMotor right;
	private static LightSensor lightL;
	private static LightSensor lightR;
	private static TouchSensor bumperL;
	private static TouchSensor bumperR;
	private static NXTRegulatedMotor lamp;
	
	//bluetooth thread
	private static BTfunctionality btThread;
	
		
	public static void main(String[] args){
		//initialiseer alle equipment
		left = Motor.A;
		right = Motor.B;
		lightL = new LightSensor(SensorPort.S1);
		lightR = new LightSensor(SensorPort.S2);
		bumperL = new TouchSensor(SensorPort.S3);
		bumperR = new TouchSensor(SensorPort.S4);
		lamp = Motor.C;
		
		//zet de robot in de beginstate
		current = State.BTINIT;
		
		//opstart-info
		LCD.drawString("EndGameRobot",0,1);
		LCD.drawString("Judith & Mirjam",0,2);
		Button.waitForAnyPress();

		//start de loop of doom
		while(!inEndState())
		{
			execute(current);
		}
		execute(current); //execute Endstate
	}

	
	//a method for every state
	public static void Btinit()
	{
		//execute all actions of this state
		LCD.drawString("Btinit",0,3);
					//bluetooth connection, master side
					LCD.drawString("Connecting...", 0, 0);
					LCD.refresh();
		
					RemoteDevice btrd = Bluetooth.getKnownDevice("slavename");
		
					if (btrd == null) {
						LCD.clear();
						LCD.drawString("No such device", 0, 0);
						LCD.refresh();
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							LCD.drawString("Master setUp sleep", 0, 1);
						}
						System.exit(1);
					}
		
					BTConnection btc = Bluetooth.connect(btrd);
		
					if (btc == null) {
						LCD.clear();
						LCD.drawString("Connect fail", 0, 0);
						LCD.refresh();
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							LCD.drawString("Master connectionfail sleep", 0, 1);
						};
						System.exit(1);
					}
		
					LCD.clear();
					LCD.drawString("Connected", 0, 0);
					LCD.refresh();
					DataInputStream dis = btc.openDataInputStream();
					DataOutputStream dos = btc.openDataOutputStream();
					
					//BTfunctionality btThread = new BTfunctionality("MasterReader",dis,dos);
					btThread = new BTfunctionality("MasterReader",dis,dos);
					//LCD.drawString("Thread initialist", 1, 4);
					
					btThread.start();
				    
		

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			if((true
			)){
				current = State.WANDER;
				transitionTaken = true;
			}
		}
	}
	
	public static void Wander()
	{
		//execute all actions of this state
		LCD.drawString("Wander",0,3);
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
(btThread.peekElement() >= 0 && btThread.peekElement() <= 255
			)){
				current = State.SONAROBSTACLE;
				transitionTaken = true;
			}else if(
(btThread.peekElement() == 400
			)){
				current = State.COLORFOUND;
				transitionTaken = true;
			}else if(
(btThread.peekElement() == 300
			)){
				current = State.FINISHED;
				transitionTaken = true;
			}
		}
	}
	
	public static void ProblemLeft()
	{
		//execute all actions of this state
		LCD.drawString("ProblemLeft",0,3);
		left.stop(true);
		right.stop();
		right.setSpeed(100);
		left.setSpeed(100);
		right.backward();
		left.backward();
		Delay.msDelay(1000);
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
				current = State.WANDER;
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
		//execute all actions of this state
		LCD.drawString("ProblemRight",0,3);
		left.stop(true);
		right.stop();
		right.setSpeed(100);
		left.setSpeed(100);
		right.backward();
		left.backward();
		Delay.msDelay(1000);
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
				current = State.WANDER;
				transitionTaken = true;
			}
		}
	}
	
	public static void Sonarobstacle()
	{
		//execute all actions of this state
		LCD.drawString("Sonarobstacle",0,3);
		btThread.popElement();
		left.stop(true);
		right.stop();
		right.setSpeed(50);
		left.setSpeed(50);
		right.backward();
		left.backward();
		Delay.msDelay(1000);
		int degree =  randInt(15, 180);
		right.rotate(degree);
		

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			if((true
			)){
				current = State.WANDER;
				transitionTaken = true;
			}
		}
	}
	
	public static void Colorfound()
	{
		//execute all actions of this state
		LCD.drawString("Colorfound",0,3);
		btThread.popElement();
		left.stop(true);
		right.stop();
		btThread.write(500);  //sends the message 'action done' 
		

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			if((true
			)){
				current = State.WACHT;
				transitionTaken = true;
			}
		}
	}
	
	public static void Finished()
	{
		//execute all actions of this state
		LCD.drawString("Finished",0,3);
		btThread.popElement();
		left.stop(true);
		right.stop();
		LCD.clear();
		LCD.drawString("all done",0,2);
		
	}
	
	public static void Wacht()
	{
		//execute all actions of this state
		LCD.drawString("Wacht",0,3);
		btThread.popElement();
		

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			if((btThread.peekElement() == 500
			)){
				current = State.CONSUMETHENWANDER;
				transitionTaken = true;
			}else if(
(btThread.peekElement() >= 0 && btThread.peekElement() <= 255
			)||(btThread.peekElement() == 300
			)||(btThread.peekElement() == 400
			)){
				current = State.WACHT;
				transitionTaken = true;
			}
		}
	}
	
	public static void Consumethenwander()
	{
		//execute all actions of this state
		LCD.drawString("Consumethenwander",0,3);
		btThread.popElement();
		

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			if((true
			)){
				current = State.WANDER;
				transitionTaken = true;
			}
		}
	}

	public static void execute(State s)
	{
		switch(s){
		case BTINIT:
			Btinit();
			break;
		case WANDER:
			Wander();
			break;
		case PROBLEMLEFT:
			ProblemLeft();
			break;
		case PROBLEMRIGHT:
			ProblemRight();
			break;
		case SONAROBSTACLE:
			Sonarobstacle();
			break;
		case COLORFOUND:
			Colorfound();
			break;
		case FINISHED:
			Finished();
			break;
		case WACHT:
			Wacht();
			break;
		case CONSUMETHENWANDER:
			Consumethenwander();
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
