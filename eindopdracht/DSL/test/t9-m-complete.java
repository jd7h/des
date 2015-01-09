	
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
	public static int RED = 0;
	public static int BLUE = 0;
	public static int GREEN = 0;
	public static int COLOR = 35;
	
	//public variables 
	public static State current;
	
	//maak een enum van de states
		public enum State {
		CALIBRATESENS,
		BTINIT,
		SONAROBSTACLE,
		COLORFOUND,
		WAITFOR300500,
		WAITFOR400SONAR,
		UITPARKEREN,
		FINISHED,
		WATCH,
		PROBLEMLEFT,
		PROBLEMRIGHT
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
	private static DataOutputStream dos;
	
		
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
		current = State.CALIBRATESENS;
		
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
	public static void Calibratesens()
	{
		//execute all actions of this state
		LCD.drawString("Calibratesens",0,3);
		LCD.drawString("Left white",0,1);
		Button.waitForAnyPress();
		BRIGHT = lightL.readValue();
		LCD.drawString("Left black",0,1);
		Button.waitForAnyPress();
		DARK = lightL.readValue();
		/*
		//for the colors
		LCD.drawString("Left red",0,1);
		Button.waitForAnyPress();
		RED = lightL.readValue();
		LCD.drawString("Left blue",0,1);
		Button.waitForAnyPress();
		BLUE = lightL.readValue();
		LCD.drawString("Left green",0,1);
		Button.waitForAnyPress();
		GREEN = lightL.readValue();
		COLOR = (RED + BLUE + GREEN) / 3;
		*/
		

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			if((true
			)){
				current = State.BTINIT;
				transitionTaken = true;
			}
		}
	}
	
	public static void Btinit()
	{
		//execute all actions of this state
		LCD.drawString("Btinit",0,3);
					//bluetooth connection, master side
					LCD.drawString("Connecting...", 0, 0);
					LCD.refresh();
		
					RemoteDevice btrd = Bluetooth.getKnownDevice("Rover2");
		
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
					DataInputStream ldis = btc.openDataInputStream();
					DataOutputStream ldos = btc.openDataOutputStream();
					dos = ldos;
					
					//BTfunctionality btThread = new BTfunctionality("MasterReader",ldis);
					btThread = new BTfunctionality("MasterReader",ldis);
					//LCD.drawString("Thread initialist", 1, 4);
					
					btThread.start();
				    
		

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			if((true
			)){
				current = State.WATCH;
				transitionTaken = true;
			}
		}
	}
	
	public static void Sonarobstacle()
	{
		//execute all actions of this state
		LCD.drawString("Sonarobstacle",0,3);
		left.stop(true);
		right.stop();
		right.setSpeed(200);
		left.setSpeed(200);
		right.backward();
		left.backward();
		Delay.msDelay(1000);
		left.stop(true);
		right.stop();
		int degree =  randInt(15, 90);
		left.rotate(degree);
		while(btThread.peekElement()<=255)
					btThread.popElement();
		LCD.clear();
		LCD.drawString("sonar sees something",0,2);
		Sound.beep();
		

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			if((true
			)){
				current = State.WATCH;
				transitionTaken = true;
			}
		}
	}
	
	public static void Colorfound()
	{
		//execute all actions of this state
		LCD.drawString("Colorfound",0,3);
		left.stop(true);
		right.stop();
		
		//klein stukje vooruit
		left.forward();
		right.forward();
		Delay.msDelay(250);
		left.stop(true);
		right.stop();
		//links v gat
		if(lightR.readValue() > DARK + 5)
			left.rotate(45);
		//rechts v gat
		else if(lightL.readValue() > DARK + 5)
			right.rotate(45);

		btThread.popElement();
		LCD.clear();
		LCD.drawString("new color found",0,2);
		Sound.beep();
		Sound.beep();
		write(500);  //sends the message 'action done' 
		

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			if((btThread.peekElement() == 300
			)){
				current = State.FINISHED;
				transitionTaken = true;
			}else if(
(btThread.peekElement() >= 0 && btThread.peekElement() <= 255
			)||(btThread.peekElement() == 400
			)){
				current = State.WAITFOR300500;
				transitionTaken = true;
			}else if(
(btThread.peekElement() == 500
			)){
				current = State.UITPARKEREN;
				transitionTaken = true;
			}
		}
	}
	
	public static void Waitfor300500()
	{
		//execute all actions of this state
		LCD.drawString("Waitfor300500",0,3);
			btThread.popElement();
		

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			if((btThread.peekElement() == 300
			)){
				current = State.FINISHED;
				transitionTaken = true;
			}else if(
(btThread.peekElement() == 500
			)){
				current = State.UITPARKEREN;
				transitionTaken = true;
			}else if(
(btThread.peekElement() >= 0 && btThread.peekElement() <= 255
			)||(btThread.peekElement() == 400
			)){
				current = State.WAITFOR300500;
				transitionTaken = true;
			}
		}
	}
	
	public static void Waitfor400sonar()
	{
		//execute all actions of this state
		LCD.drawString("Waitfor400sonar",0,3);
			btThread.popElement();
		

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			if((true
			)){
				current = State.WATCH;
				transitionTaken = true;
			}
		}
	}
	
	public static void Uitparkeren()
	{
		//execute all actions of this state
		LCD.drawString("Uitparkeren",0,3);
		right.setSpeed(200);
		left.setSpeed(200);
		right.backward();
		left.backward();
		Delay.msDelay(1000);
		int degree =  randInt(45, 180);
		left.rotate(degree);
		

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			if((true
			)){
				current = State.WATCH;
				transitionTaken = true;
			}
		}
	}
	
	public static void Finished()
	{
		//execute all actions of this state
		LCD.drawString("Finished",0,3);
		left.stop(true);
		right.stop();
			btThread.popElement();
		LCD.clear();
		LCD.drawString("all done",0,2);
		
	}
	
	public static void Watch()
	{
		//execute all actions of this state
		LCD.drawString("Watch",0,3);
		right.setSpeed(200);
		left.setSpeed(200);
		right.forward();
		left.forward();
		

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			if((BRIGHT-3 <= lightL.readValue() && lightL.readValue() <= BRIGHT+3
			)||(bumperL.isPressed()
			)){
				current = State.PROBLEMLEFT;
				transitionTaken = true;
			}else if(
(BRIGHT-3 <= lightR.readValue() && lightR.readValue() <= BRIGHT+3
			)||(bumperR.isPressed()
			)){
				current = State.PROBLEMRIGHT;
				transitionTaken = true;
			}else if(
(btThread.peekElement() == 500
			)||(btThread.peekElement() == 300
			)){
				current = State.WAITFOR400SONAR;
				transitionTaken = true;
			}else if(
(btThread.peekElement() == 400
			)){
				current = State.COLORFOUND;
				transitionTaken = true;
			}else if(
(btThread.peekElement() >= 0 && btThread.peekElement() <= 255
			)){
				current = State.SONAROBSTACLE;
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
		right.setSpeed(200);
		left.setSpeed(200);
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
			if((DARK-3 <= lightL.readValue() && lightL.readValue() <= DARK+3&&
			!bumperL.isPressed()
			)){
				current = State.WATCH;
				transitionTaken = true;
			}else if(
(BRIGHT-3 <= lightL.readValue() && lightL.readValue() <= BRIGHT+3
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
		right.setSpeed(200);
		left.setSpeed(200);
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
			if((BRIGHT-3 <= lightR.readValue() && lightR.readValue() <= BRIGHT+3
			)||(bumperR.isPressed()
			)){
				current = State.PROBLEMRIGHT;
				transitionTaken = true;
			}else if(
(DARK-3 <= lightR.readValue() && lightR.readValue() <= DARK+3&&
			!bumperR.isPressed()
			)){
				current = State.WATCH;
				transitionTaken = true;
			}
		}
	}

	public static void execute(State s)
	{
		switch(s){
		case CALIBRATESENS:
			Calibratesens();
			break;
		case BTINIT:
			Btinit();
			break;
		case SONAROBSTACLE:
			Sonarobstacle();
			break;
		case COLORFOUND:
			Colorfound();
			break;
		case WAITFOR300500:
			Waitfor300500();
			break;
		case WAITFOR400SONAR:
			Waitfor400sonar();
			break;
		case UITPARKEREN:
			Uitparkeren();
			break;
		case FINISHED:
			Finished();
			break;
		case WATCH:
			Watch();
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
	
	public static void write(int msg) 
	{
		try {
			dos.writeInt(msg);
			dos.flush();
		} catch (IOException e) {
			LCD.clear();
			LCD.drawString("Bluetooth writing Error", 0, 1);
		}
	}
	
}	
