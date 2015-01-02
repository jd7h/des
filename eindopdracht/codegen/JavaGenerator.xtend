package robots.tasks.generator



import org.eclipse.emf.ecore.resource.Resource
import robots.tasks.rDSL.DriveAction
import robots.tasks.rDSL.DriveDirection
import robots.tasks.rDSL.TimeUnit
import robots.tasks.rDSL.TurnAction
import robots.tasks.rDSL.Direction
import robots.tasks.rDSL.StopAction
import robots.tasks.rDSL.LightCondition
import robots.tasks.rDSL.LightValue
import robots.tasks.rDSL.SonarValue
import robots.tasks.rDSL.BumperValue
import robots.tasks.rDSL.SonarCondition
import robots.tasks.rDSL.Arrow
import robots.tasks.rDSL.Color
import robots.tasks.rDSL.BumperCondition
import robots.tasks.rDSL.ColorCondition
import robots.tasks.rDSL.TimeCondition
import robots.tasks.rDSL.BeepAction
import robots.tasks.rDSL.PrintAction
import robots.tasks.rDSL.CalibrateAction
import robots.tasks.rDSL.BTAction
import robots.tasks.rDSL.True
import robots.tasks.rDSL.Message
import robots.tasks.rDSL.SendAction
import robots.tasks.rDSL.ReceiveCondition
import robots.tasks.rDSL.ParkAction

class JavaGenerator {
	

	def static masterEquipment()'''
	//standaard equipment op Robot Master
	private static NXTRegulatedMotor left;
	private static NXTRegulatedMotor right;
	private static LightSensor lightL;
	private static LightSensor lightR;
	private static TouchSensor bumperL;
	private static TouchSensor bumperR;
	private static NXTRegulatedMotor lamp;
	'''
	
	def static slaveEquipment()'''
	//standaard equipment op Robot Slave
	private static RCXMotor tempArm;
	private static NXTRegulatedMotor lamp;
	private static ColorSensor colorsens;
	private static UltrasonicSensor sonic;
	private static TemperatureSensor temp;
	'''
	
	def static masterInit()'''
	left = Motor.A;
	right = Motor.B;
	lightL = new LightSensor(SensorPort.S1);
	lightR = new LightSensor(SensorPort.S2);
	bumperL = new TouchSensor(SensorPort.S3);
	bumperR = new TouchSensor(SensorPort.S4);
	lamp = Motor.C;
	'''
	
	def static slaveInit()'''
	tempArm = new RCXMotor(MotorPort.A);
	lamp = Motor.C;
	colorsens = new ColorSensor(SensorPort.S1);
	sonic = new UltrasonicSensor(SensorPort.S2);
	temp = new TemperatureSensor(SensorPort.S3);
	'''

	def static generateMain(Resource resource)'''
	
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
	import lejos.nxt.TemperatureSensor;

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
	private static ListIterator<Integer> it;
	
	//maak een enum van de beginstates
		public enum State {
		«FOR s : Auxiliary.getStates(resource) SEPARATOR ','»
			«Auxiliary.getStateItem(s)»
		«ENDFOR»
		}
		
	//definieer lijst van endstates
	static State[] endStates = {«FOR e : Auxiliary.getEndStates(resource) SEPARATOR ','»State.«Auxiliary.getStateItem(e)»«ENDFOR»};
	
	//specificeer alle equipment
	«IF Auxiliary.isMaster(resource)»
	«masterEquipment()»
	«ENDIF»
	«IF !Auxiliary.isMaster(resource)»
	«slaveEquipment()»
	«ENDIF»
	
	//bluetooth draadje
	private static BTfunctionality btThread;
	
		
	public static void main(String[] args){
		//initialiseer alle equipment
		«IF Auxiliary.isMaster(resource)»
		«masterInit()»
		«ENDIF»
		«IF !Auxiliary.isMaster(resource)»
		«slaveInit()»
		«ENDIF»
		
		//zet de robot in de beginstate
		current = State.«Auxiliary.getStateItem(Auxiliary.getStartState(resource))»;
		
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

	
	//make methods for every state seperately
	«FOR s : Auxiliary.getStates(resource) SEPARATOR '\n'»
	public static void «Auxiliary.getStateMethod(s)»()
	{
		//execute all actions of this state
		LCD.drawString("«Auxiliary.getStateMethod(s)»",0,3);
		«FOR a : Auxiliary.getActionList(s)»
			«action2code(a)»
		«ENDFOR»
		
		«IF !Auxiliary.isEndState(resource,s)»

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			«FOR ar : Auxiliary.getOutArrows(resource,s) BEFORE 'if(' SEPARATOR 'else if('»«arrow2conditional(ar,resource)»){
				current = State.«Auxiliary.getStateItem(ar.to)»;
				transitionTaken = true;
			}
			«ENDFOR»
		}
		«ENDIF»
	}
	«ENDFOR»

	public static void execute(State s)
	{
		switch(s){
		«FOR state : Auxiliary.getStates(resource)»
			case «Auxiliary.getStateItem(state)»:
				«Auxiliary.getStateMethod(state)»();
				break;
		«ENDFOR»
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
	
	//TODO: sonar stubfunction
	public static int getLastSonarData()
	{
		return 255;
	}
		 
}	
'''
		
	//actions
	
	//drive action forward and backwards with and without duration
	def static dispatch action2code(DriveAction action){

		var int s = action.speed
		
		//if the duration variable is not used it is 0
		if(action.dl != 0)
		{
			var int n = action.dl
			if(action.unit == TimeUnit::SEC){
				n =  n*1000
			}
			
			switch(action.driveDir)
			{
				 case DriveDirection::FORWARDS: return '''
				 	right.setSpeed(«s»);
				 	left.setSpeed(«s»);
				 	right.forward();
				 	left.forward();
				 	Delay.msDelay(«n»);'''
				 case DriveDirection::BACKWARDS: return '''
				 	right.setSpeed(«s»);
				 	left.setSpeed(«s»);
				 	right.backward();
				 	left.backward();
				 	Delay.msDelay(«n»);'''
			}	
		}		 
		switch(action.driveDir)
		{
			 case DriveDirection::FORWARDS: return '''
			 	right.setSpeed(«s»);
			 	left.setSpeed(«s»);
			 	right.forward();
			 	left.forward();'''
			 case DriveDirection::BACKWARDS: return '''
			 	right.setSpeed(«s»);
			 	left.setSpeed(«s»);
			 	right.backward();
			 	left.backward();'''
		}
	}
	
	//turnaction left/right with and without the variable degree
	def static dispatch action2code(TurnAction action){
		
		//if the variable degree is not used in the instance of the DSL
		if(action.degree == 0){
			switch(action.direction)
			{
				case Direction::RIGHT: return '''
				int degree =  randInt(«action.min», «action.max»);
				left.rotate(degree);'''
				case Direction::LEFT: return '''
				int degree =  randInt(«action.min», «action.max»);
				right.rotate(degree);'''			
			}
		}else{
			switch(action.direction)
			{
				case Direction::RIGHT: return '''
				left.rotate(«action.degree»);'''
				case Direction::LEFT: return '''
				right.rotate(«action.degree»);'''			
			}
		}		
	}
	
	//stop action(left returns immediately so right can also stop)
	def static dispatch action2code(StopAction action)'''
		left.stop(true);
		right.stop();'''
		
	//SendAction 
	def static dispatch action2code(SendAction action){
		switch (action.message){
			case Message::SONAR: return ''' btThread.write(getLastSonarData());'''
			case Message::ALLDONE: return '''btThread.write(300);''' 
			case Message::NEWCOLOR: return '''btThread.write(400);'''
			case Message::ACTIONDONE: return '''btThread.write(500);'''
			default: return '''btThread.write(-1);'''	
		}
	}
	
	//todo
	//ParkAction
	def static dispatch action2code(ParkAction action)''''''
	
	//bt action
	//assumptions: slavename is parameter of init, kind of action is in enum e
	//change later: enum e, add slavename
	def static dispatch action2code(BTAction action)
	{
		if (action.slavename != null) //master
		{
			return '''
				//bluetooth connection, master side
				LCD.drawString("Connecting...", 0, 0);
				LCD.refresh();
	
				RemoteDevice btrd = Bluetooth.getKnownDevice("«action.slavename»");

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
			    
			    '''
		}
		else
		{
			return '''				
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
			'''
		}
	}

	//calibrate action
	//returns the code for the calibration of the sensors
	def static dispatch action2code(CalibrateAction action)'''
		LCD.drawString("Left white",0,1);
		Button.waitForAnyPress();
		BRIGHT = lightL.readValue();
		LCD.drawString("Left black",0,1);
		Button.waitForAnyPress();
		DARK = lightL.readValue();'''			

	//beep action
	//returns the code for a beep sound
	def static dispatch action2code(BeepAction action)'''
		Sound.beepSequenceUp();'''
		
	//print action
	//returns the code for print on the screen
	def static dispatch action2code(PrintAction action)'''
		LCD.clear();
		LCD.drawString("«action.msg»",0,2);'''		
		
		
	
	//Arrows
		def static arrow2conditional(Arrow a, Resource resource)'''
		«FOR c : a.disjunctionlist SEPARATOR '||'»
			«FOR el : c.conjuctionlist BEFORE '(' SEPARATOR '&&' AFTER ')'»
				«condition2code(el, resource)»
			«ENDFOR»
		«ENDFOR»
	'''
	
	//Conditions
	
	//returns the code for the conditions with the lightsensors left and right
	//BRIGHT and DARK must be calibrated in the beginning
	//only relevant for masterbot
	def static dispatch condition2code(LightCondition condition, Resource resource){
		if(Auxiliary.isMaster(resource))
		{
			switch(condition.value)
			{
				case LightValue::WHITE: if(condition.side == Direction::LEFT)
											return '''BRIGHT-10 <= lightL.readValue() && lightL.readValue() <= BRIGHT+10'''
										else
											return '''BRIGHT-10 <= lightR.readValue() && lightR.readValue() <= BRIGHT+10'''
				case LightValue::BLACK: if(condition.side == Direction::LEFT)
											return '''DARK-10 <= lightL.readValue() && lightL.readValue() <= DARK+10'''
										else
											return '''DARK-10 <= lightR.readValue() && lightR.readValue() <= DARK+10'''
			}
		}
		else
			return ''''''
	}
	
	//returns the code for the conditions with the sonar sensor 
	//Nothing can used without a distance(then use default value)
	//only relevant for slavebot
	def static dispatch condition2code(SonarCondition condition, Resource resource){
		if(Auxiliary.isMaster(resource)){
			switch(condition.value)
				{
					case SonarValue::NOTHING: return '''btThread.headIsSonarElement() && (btThread.peekElement() == 255 || btThread.peekElement() >= «condition.distance»)'''
					case SonarValue::SOMETHING: return '''btThread.headIsSonarElement() && btThread.peekElement() <= «condition.distance»'''
				}
			}
			 
		else{
			switch(condition.value)
			{
				case SonarValue::NOTHING: return '''sonar.getDistance() >= 255 || sonar.getDistance() => «condition.distance»'''
				case SonarValue::SOMETHING: return '''sonar.getDistance() <= «condition.distance»'''
			}
		}
	}
	
	//returns the code for conditions with the touchsensors left and right
	def static dispatch condition2code(BumperCondition condition, Resource resource){
		if(Auxiliary.isMaster(resource))
		{
			switch(condition.value)
			{
				case BumperValue::PRESSED: if(condition.side == Direction::LEFT)
											return '''bumperL.isPressed()'''
										else
											return '''bumperR.isPressed()'''
				case BumperValue::NOTPRESSED: if(condition.side == Direction::LEFT)
											return '''!bumperL.isPressed()'''
										else
											return '''!bumperR.isPressed()'''
			}
		}
		return ''''''
	}
	
	//color sensors 
	//uses the predefined colors
	def static dispatch condition2code(ColorCondition condition, Resource resource){
		if(Auxiliary.isMaster(resource))
		{
			return '''btThread.peekElement() == 400'''
		}
		else{
			switch(condition.color)
			{
				case Color::BLUE: return '''lejos.robotics.Colors.Color.BLUE '''
				case Color::BLACK: return '''lejos.robotics.Colors.Color.BLACK'''
				case Color::RED: return '''lejos.robotics.Colors.Color.RED'''
				case Color::GREEN: return '''lejos.robotics.Colors.Color.GREEN'''
				case Color::WHITE: return '''lejos.robotics.Colors.Color.WHITE'''
			}
		}
	}
	
	//time out conditions
	def static dispatch condition2code(TimeCondition condition, Resource resource){
		var time = condition.t
		if(condition.unit == TimeUnit::SEC)
				time =  time*1000
		return '''starttime + «time» <= System.currentTimeMillis()'''
	}
	
		
	def static dispatch condition2code(True condition, Resource resource)
		'''true'''
		
	def static dispatch condition2code(ReceiveCondition condition, Resource resource)
	{
		switch (condition.message){
				case Message::SONAR: return '''btThread.peekElement >= 0 && btThread.peekElement() <= 255'''
				case Message::ALLDONE: return '''btThread.peekElement() == 300''' 
				case Message::NEWCOLOR: return '''btThread.peekElement() == 400'''
				case Message::ACTIONDONE: return '''btThread.peekElement() == 500'''
				default: return '''false'''	//TODO change to error statement
			}
	}
	

}
 
