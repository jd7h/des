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
import robots.tasks.rDSL.TempCondition
import robots.tasks.rDSL.TimeCondition

class JavaGenerator {
	
	def static arrow2conditional(Arrow a)'''
		«FOR c : a.disjunctionlist SEPARATOR '||'»
			«FOR el : c.conjuctionlist BEFORE '(' SEPARATOR '&&' AFTER ')'»
				«condition2code(el)»
			«ENDFOR»
		«ENDFOR»
	'''

	def static generateMain(Resource resource)'''
	package «Auxiliary.getAutomata(resource).name»;

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
	
	public class Main{
	
	//Constants for the Lightsensorvalues
	public static int BRIGHT = 20;
	public static int DARK = 80;
	
	//public variables 
	public State current;
	
	//maak een enum van de beginstates
		public enum State {
		«FOR s : Auxiliary.getStates(resource) SEPARATOR ','»			//added extra state for when everything is finished
			«Auxiliary.getStateItem(s)»
		«ENDFOR»
		}
		
	//definieer lijst van endstates
	State[] endStates = {«FOR e : Auxiliary.getEndStates(resource) SEPARATOR ','»State.«Auxiliary.getStateItem(e)»«ENDFOR»};
	
	//standaard equipment op Robot
	private NXTRegulatedMotor left;
	private NXTRegulatedMotor right;
	private LightSensor lightL;
	private LightSensor lightR;
	private TouchSensor bumperL;
	private TouchSensor bumperR;
	private NXTRegulatedMotor lamp;

	//todo: zet een BT-kanaal op tussen de master en de slave
		
	public Main(){
		//initialiseer alle equipment
		NXTRegulatedMotor left = Motor.A;
		NXTRegulatedMotor right = Motor.B;
		LightSensor lightL = new LightSensor(SensorPort.S1);
		LightSensor lightR = new LightSensor(SensorPort.S2);
		TouchSensor bumperL = new TouchSensor(SensorPort.S3);
		TouchSensor bumperR = new TouchSensor(SensorPort.S4);
		NXTRegulatedMotor lamp = Motor.C;

		//todo: zet de robot in de beginstate
		State current = State.«Auxiliary.getStateItem(Auxiliary.getStartState(resource))»;
		
		//opstart-info
		LCD.drawString("EndGameRobot",0,1);
		LCD.drawString("Judith & Mirjam",0,2);
		Button.waitForPress();

		//start de loop of doom
		while(!inEndState())
		{
			execute(current);
		}
	}

	
	//make methods for every state seperately
	«FOR s : Auxiliary.getStates(resource) SEPARATOR '\n'»
	public void «Auxiliary.getStateMethod(s)»()
	{
		//first, execute all actions of this state
		«FOR a : Auxiliary.getActionList(s)»
			«action2code(a)»
		«ENDFOR»
		
		«IF !Auxiliary.isEndState(resource,s)»

		//leg de huidige tijd vast voor alle transitions met een timeoutcondition
		long starttime = System.currentTimeMillis();

		//when done, wait for a trigger for a transition
		boolean transitionTaken = false; 
		while(!transitionTaken){	
			«FOR ar : Auxiliary.getOutArrows(resource,s) BEFORE 'if(' SEPARATOR 'else if('»«arrow2conditional(ar)»){
				current = State.«Auxiliary.getStateItem(ar.to)»;
				transitionTaken = true;
			}
		«ENDFOR»
		}
		«ENDIF»
		return;
	}
	«ENDFOR»

	public void execute(State s)
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

	//TODO: Fix Error: "The method asList(Main.State[]) is undefined for the type Arrays"
	public boolean inEndState()
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
	}	'''
		
	//actions
	def static dispatch action2code(DriveAction action){

		var int s = action.speed
		
		if(action.dl != 0)								//als we dl niet invullen, wordt action.dl dan 0?
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
	
	def static dispatch action2code(TurnAction action){
		
		if(action.degree == 0){
			switch(action.direction)
			{
				case Direction::LEFT: return '''
				int degree =  randInt(«action.min», «action.max»);
				left.rotate(degree);'''
				case Direction::RIGHT: return '''
				int degree =  randInt(«action.min», «action.max»);
				right.rotate(degree);'''			
			}
		}else{
			switch(action.direction)
			{
				case Direction::LEFT: return '''
				left.rotate(«action.degree»);'''
				case Direction::RIGHT: return '''
				right.rotate(«action.degree»);'''			
			}
		}		
	}
	
	def static dispatch action2code(StopAction action)'''
		left.stop(true);
		right.stop();'''
	
	
	//Conditions
	def static dispatch condition2code(LightCondition condition){
		
		switch(condition.value)
		{
			case LightValue::WHITE: if(condition.side == Direction::LEFT)
										return '''BRIGHT == lightL.readValue()'''
									else
										return '''BRIGHT == lightR.readValue()'''
			case LightValue::BLACK: if(condition.side == Direction::LEFT)
										return '''DARK == lightL.readValue()'''
									else
										return '''DARK == lightR.readValue()'''
		}
	}
	
	def static dispatch condition2code(SonarCondition condition){
		
		switch(condition.value)
		{
			case SonarValue::NOTHING: return '''sonar.getDistance == 255 || sonar.getDistance() > «condition.distance»'''
			case SonarValue::SOMETHING: return '''sonar.getDistance() < «condition.distance»'''
		}
	}
	
	def static dispatch condition2code(BumperCondition condition){
		
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
	
	//TODO add colorsensor and add to the conditions
	def static dispatch condition2code(ColorCondition condition){
		
		switch(condition.color)
		{
			case Color::BLUE: return '''lejos.robotics.Colors.Color.BLUE '''
			case Color::BLACK: return '''lejos.robotics.Colors.Color.BLACK'''
			case Color::RED: return '''lejos.robotics.Colors.Color.RED'''
			case Color::GREEN: return '''lejos.robotics.Colors.Color.GREEN'''
		}
	}
	
	def static dispatch condition2code(TimeCondition condition){
		var time = condition.t
		if(condition.unit == TimeUnit::SEC)
				time =  time*1000
		return '''starttime + «time» <= System.currentTimeMillis()'''
	}
	
	//TODO add tempsensor and add conditions
	def static dispatch condition2code(TempCondition condition){ 
		return condition.temp
		}

}
 