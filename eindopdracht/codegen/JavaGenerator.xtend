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

class JavaGenerator {

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
	
	//Constance for the Lightsensorvalues
	public static int BRIGHT = 20;
	public static int DARK = 80;

	//make methods for every state seperately
	«FOR s : Auxiliary.getStates(resource)»
	public void «Auxiliary.getStateMethod(s)»()
	{
		«FOR a : Auxiliary.getActionList(s)»
			
		«ENDFOR»
	}
	«ENDFOR»

	public class Main{

			//maak een enum van de beginstates
			public enum State {
			«FOR s : Auxiliary.getStates(resource) SEPARATOR ','»
				«Auxiliary.getStateItem(s)»
			«ENDFOR»
			}

			//definieer lijst van endstates
			State[] endStates = {«FOR e : Auxiliary.getEndStates(resource) SEPARATOR ','»«Auxiliary.getStateItem(e)»«ENDFOR»};
			
			//definieer standaard equipment op Robot
			//maak de robot
			//things on brick1 (Master)
			NXTRegulatedMotor left = Motor.A;
			NXTRegulatedMotor right = Motor.B;
			LightSensor lightL = new LightSensor(SensorPort.S1);
			LightSensor lightR = new LightSensor(SensorPort.S2);
			TouchSensor touchL = new TouchSensor(SensorPort.S3);
			TouchSensor touchR = new TouchSensor(SensorPort.S4);
			Lamp lamp = new Motor.C;

			//todo: zet een BT-kanaal op tussen de master en de slave

			//todo: zet de robot in de beginstate
			State current = «Auxiliary.getStateItem(Auxiliary.getStartState(resource))»
			
			//startconfiguratie met feedback
			LCD.drawString("EndGameRobot",0,1);
			LCD.drawString("Judith & Mirjam",0,2);
			Button.waitForAnyPress();

			//start de loop of doom
			while(!inEndState())
			{
				executeCurrentStateWhileChecking();
				nextState();			
			}
		

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
		}'''
		
	//actions
	def static dispatch action2code(DriveAction action){

		var int s = action.speed
		
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
	
	def static dispatch action2code(TurnAction action){
		
		if(action.degree == 0){
			switch(action.direction)
			{
				case Direction::LEFT: return '''
				int degree =  randInt(min, max);
				left.rotate(degree);'''
				case Direction::RIGHT: return '''
				int degree =  randInt(min, max);
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
		right.stop(true);'''
	
	
	//Conditions
	def static dispatch condition2code(LightCondition condition){
		
		switch(condition.value)
		{
			case LightValue::WHITE: if(condition.Direction == Direction::LEFT)
										return ''' (BRIGHT == robot.lightL.readValue()) '''
									else
										return ''' (BRIGHT == robot.lightR.readValue()) '''
			case LightValue::BLACK: if(condition.Direction == Direction::LEFT)
										return ''' (DARK == robot.lightL.readValue()) '''
									else
										return ''' (DARK == robot.lightR.readValue()) '''
		}
	}
	
	def static dispatch condition2code(SonarCondition condition){
		
		switch(condition.value)
		{
			case SonarValue::NOTHING: return ''' (robot.sonar.getDistance() > «condition.distance» ) '''
			case SonarValue::SOMETHING: return ''' (robot.sonar.getDistance() < «condition.distance») '''
		}
	}
	
	def static dispatch condition2code(BumperCondition condition){
		
		switch(condition.value)
		{
			case BumperValue::PRESSED: if(condition.Direction == Direction::LEFT)
										return ''' (robot.bumperL.isPressed()) '''
									else
										return ''' (robot.bumperR.isPressed()) '''
			case BumperValue::NOTPRESSED: if(condition.Direction == Direction::LEFT)
										return ''' (!robot.bumperL.isPressed()) '''
									else
										return ''' (!robot.bumperR.isPressed()) '''
		}
	}
	

}
