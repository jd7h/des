	
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
		WATCH,
		SONARFLAG,
		COLORFLAG,
		MEASUREMENT,
		FINISHED
		}
		
	//definieer lijst van endstates
	static State[] endStates = {State.FINISHED};
	
	//specificeer alle equipment
	//standaard equipment op Robot Slave
	private static RCXMotor tempMotor;
	private static NXTRegulatedMotor lamp;
	private static ColorSensor colorsens;
	private static UltrasonicSensor sonar;
	private static RCXTemperatureSensor tempSensor;
	
	private static Lake lakes [];
	private static int nrcolors = 3;
	
	//bluetooth thread
	private static BTfunctionality btThread;
	
		
	public static void main(String[] args){
		//initialiseer alle equipment
		tempMotor = new RCXMotor(MotorPort.A);
		lamp = Motor.C;
		colorsens = new ColorSensor(SensorPort.S1);
		sonar = new UltrasonicSensor(SensorPort.S2);
		tempSensor = new RCXTemperatureSensor(SensorPort.S3);
		lakes = new Lake[3];
		lakes[0] = new Lake(Color.RED);
		lakes[1] = new Lake(Color.BLUE);
		lakes[2] = new Lake(Color.GREEN);
		
		
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
		LCD.drawString("Waiting...",0,0);
		LCD.refresh();
		
		BTConnection btc = Bluetooth.waitForConnection();
		
		LCD.clear();
		LCD.drawString("Connected",0,0);
		LCD.refresh();	
		
		DataInputStream dis = btc.openDataInputStream();
		DataOutputStream dos = btc.openDataOutputStream();
		
		//BTfunctionality btThread = new BTfunctionality("SlaveReader",dis,dos);
		btThread = new BTfunctionality("SlaveReader",dis,dos);
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
	
	public static void Watch()
	{
		//execute all actions of this state
		LCD.drawString("Watch",0,3);
		

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			if((sonar.getDistance() <= 10
			)){
				current = State.SONARFLAG;
				transitionTaken = true;
			}else if(
(lejos.robotics.Color.RED == colorsens.getColorID()
			)||(lejos.robotics.Color.BLUE == colorsens.getColorID()
			)||(lejos.robotics.Color.GREEN == colorsens.getColorID()
			)){
				current = State.COLORFLAG;
				transitionTaken = true;
			}
		}
	}
	
	public static void Sonarflag()
	{
		//execute all actions of this state
		LCD.drawString("Sonarflag",0,3);
		Sound.beep();
		 btThread.write(sonar.getDistance()); //sends the data from the sonar 
		

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
	
	public static void Colorflag()
	{
		//execute all actions of this state
		LCD.drawString("Colorflag",0,3);
		Sound.beep();
		btThread.write(400);  //sends the message 'new color found' 
		

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			if((starttime + 30000 <= System.currentTimeMillis()
			)){
				current = State.WATCH;
				transitionTaken = true;
			}else if(
(btThread.peekElement() == 500
			)){
				current = State.MEASUREMENT;
				transitionTaken = true;
			}
		}
	}
	
	public static void Measurement()
	{
		//execute all actions of this state
		LCD.drawString("Measurement",0,3);
		btThread.popElement();
		Sound.beep();
		Sound.beep();
		//lower sensor by RCX motor
		tempMotor.setPower(-100); 
		tempMotor.setPower(0);
		for(int i = 0; i < nrcolors; i++)
		{
			if(colorsens.getColorID() == lakes[i].color && !lakes[i].found) // kleuren komen overeen
				lakes[i].celsius = tempSensor.getCelcius();
				lakes[i].found = true;
				return;
		}
		//raise temp sensor
		tempMotor.setPower(100);
		btThread.write(500);  //sends the message 'action done' 
		

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			if((lakes[0].found && lakes[1].found && lakes[2].found
			)){
				current = State.FINISHED;
				transitionTaken = true;
			}else if(
(true
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
		Sound.beep();
		Sound.beep();
		Sound.beep();
		btThread.write(300); //sends the message 'all done' 
		
	}

	public static void execute(State s)
	{
		switch(s){
		case BTINIT:
			Btinit();
			break;
		case WATCH:
			Watch();
			break;
		case SONARFLAG:
			Sonarflag();
			break;
		case COLORFLAG:
			Colorflag();
			break;
		case MEASUREMENT:
			Measurement();
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
