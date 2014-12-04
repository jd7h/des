package robots.tasks.generator

import org.eclipse.emf.ecore.resource.Resource
import robots.tasks.rDSL.DriveAction
import robots.tasks.rDSL.DriveDirection
import robots.tasks.rDSL.TimeUnit
import robots.tasks.rDSL.TurnAction
import robots.tasks.rDSL.Direction

class JavaGenerator {

	def static arrow2conditional(Arrow a)'''
		«FOR c : a.disjunctionlist SEPARATOR '||'»
			«FOR el : c.conjuctionlist BEFORE '(' SEPARATOR '&&' AFTER ')'»
				el.condition2Code()
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

	//make methods for every state seperately
	«FOR s : Auxiliary.getStates(resource) SEPARATOR '\n'»
		public void «Auxiliary.getStateMethod(s)»()
		{
			«FOR a : Auxiliary.getActionList(s)»
				«FOR a : Auxiliary.getOutArrows(resource,s) BEFORE 'if(' SEPARATOR 'else if('»
				«arrow2Conditional(a)»)
					break;
				«ENDFOR»
				else
					«a.action2Text()»
			«ENDFOR»
		}

		«FOR a : Auxiliary.getOutArrows(resource,s)»
			«FOR c : a.disjunctionlist BEFORE '(' SEPARATOR '||' AFTER ')'»
				«FOR el : c.conjuctionlist SEPARATOR '&&'»
					el.condition2Text()
				«ENDFOR»
			«ENDFOR»
		«ENDFOR»
	
	
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
		

		}'''
		
	//actions
	def static dispatch action2Text(DriveAction action){

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
	
	def static dispatch action2Text(TurnAction action){
		
		if(action.degree == 0)
		
		switch(action.direction)
		{
			case Direction::LEFT: return '''
			right.forward();
			left.backward();'''
			case Direction::RIGHT: return '''
			right.backward();
			left.forward();'''			
		}
	}

}
