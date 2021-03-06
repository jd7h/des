package robots.tasks.generator

import org.eclipse.emf.ecore.resource.Resource
import robots.tasks.rDSL.BumperCondition
import robots.tasks.rDSL.BumperValue
import robots.tasks.rDSL.Direction
import robots.tasks.rDSL.DriveAction
import robots.tasks.rDSL.DriveDirection
import robots.tasks.rDSL.LightCondition
import robots.tasks.rDSL.LightValue
import robots.tasks.rDSL.Motor
import robots.tasks.rDSL.SonarCondition
import robots.tasks.rDSL.SonarValue
import robots.tasks.rDSL.State
import robots.tasks.rDSL.StopAction
import robots.tasks.rDSL.TurnAction
import robots.tasks.rDSL.TimeUnit

class JavaGenerator {

	def static generateMain(Resource resource)'''
	package «Auxiliary.getAutomata(resource).name»; //is this well-typed?

	/* 
	 * Automatically generated code
	 * Judith van Stegeren and Mirjam van Nahmen
	 * 
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

	public class Main{
			//to do ^

			//definieer standaard equipment op Robot
			NXTRegulatedMotor right = Motor.A;
			NXTRegulatedMotor left = Motor.B;
			LightSensor light = new LightSensor(SensorPort.S1);
			UltrasonicSensor sonar = new UltrasonicSensor(SensorPort.S2);
			TouchSensor touch = new TouchSensor(SensorPort.S3);

			//maak een robot
			int startstate = «Auxiliary.getStateNumber(Auxiliary.getStartState(resource), resource)»;
			Robot robot = new Robot(startstate,right,left,light,sonar,touch);

			//maak een instantie aan van alle states/behaviors
			«FOR s : Auxiliary.getStates(resource)»
			Behavior b_«Auxiliary.getStateName(s)» = new «Auxiliary.getBehaviorName(s)»(robot);
			«ENDFOR»

			//maak een lijst van de behaviors op basis van prioriteit
			Behavior[] behaviorlist = {«FOR s : Auxiliary.getSortedStates(resource) SEPARATOR ', '»b_«Auxiliary.getStateName(s)»«ENDFOR»};	//separator is ','

			//maak een arbitrator aan en start	
			Arbitrator arbitrator = new Arbitrator(behaviorlist);
			LCD.drawString("CodeGenRobot",0,1);
			LCD.drawString("Judith & Mirjam",0,2);
			Button.waitForAnyPress();
			arbitrator.start();
		

		}'''

	def static generateBehavior(Resource resource,State s)'''
		package «Auxiliary.getAutomata(resource).name»;

		import lejos.robotics.subsumption.*;

		public class «Auxiliary.getBehaviorName(s)» implements Behavior{
			private boolean suppressed = false;
			private Robot robot;

			public «Auxiliary.getBehaviorName(s)»(Robot robot){ 
				this.robot = robot;
			}

			public boolean takeControl(){
				«FOR a : Auxiliary.getInArrows(resource,s)»
				if(«condition2code(a.condition)» && robot.getCurrentState == «Auxiliary.getStateNumber(a.from, resource)»)
					return true;
				«ENDFOR»
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
				robot.setCurrentState(«Auxiliary.getStateNumber(s, resource)»);
				suppressed = false;
				//convert all actions of this state to javacode
				«FOR a : Auxiliary.getActionList(s)»
					«action2Text(a)»
				«ENDFOR»
			}'''

	def static generateRobot(Resource resource)'''
		package «Auxiliary.getAutomata(resource).name»;

		//imports
		import lejos.nxt.Button;
		import lejos.nxt.LightSensor;
		import lejos.nxt.Motor;
		import lejos.nxt.NXTRegulatedMotor;
		import lejos.nxt.LCD;
		import lejos.nxt.SensorPort;
		import lejos.nxt.UltrasonicSensor;
		import lejos.nxt.TouchSensor;
		import lejos.nxt.Sound;

		public class Robot{
			//equipment
			public NXTRegulatedMotor right;
			public NXTRegulatedMotor left;
			public LightSensor light;
			public UltrasonicSensor sonar;
			public TouchSensor touch;
			private int currentState;

			//constructor
			public Robot(cs,right,left,light,sonar,touch)
			{
				currentState = cs;
				this.right = right;
				this.left = left;
				this.light = light;
				this.sonar = sonar;
				this.touch = touch;
			}

			public int getCurrentState(){ return currentState;}
			public void setCurrentState(int cs){ currentState = cs;}

		}'''
	
	
	//actions
	//TODO Duration nog inzetten
	def static dispatch action2Text(DriveAction action){
		var int n = action.dl
		if(action.unit == TimeUnit::SEC){
			n =  n*1000
		}
		
		switch(action.driveDir)
		{
			 case DriveDirection::FORWARDS: return '''
			 	right.forward();
			 	left.forward();
			 	Delay.msDelay(«n»);'''
			 case DriveDirection::BACKWARDS: return '''
			 	right.backward();
			 	left.backward();
			 	Delay.msDelay(«n»);'''
		}
	}
	
	def static dispatch action2Text(TurnAction action){
		var int n = 1000 //1sec
		switch(action.direction)
		{
			 case Direction::LEFT: return '''
			 	right.forward();
			 	left.backward();
			 	Delay.msDelay(«n»);'''
			 case Direction::RIGHT: return '''
			 	right.backward();
			 	left.forward();
			 	Delay.msDelay(«n»);'''
		}
	}

	def static dispatch action2Text(StopAction action){
		var int n = 500 //0.5sec
		switch(action.motor)
		{
			 case Motor::LEFT: return '''
			 	left.stop(true);
			 	Delay.msDelay(«n»);'''
			 case Motor::RIGHT: return '''
			 	right.stop(true);
			 	Delay.msDelay(«n»);'''
			 case Motor::BOTH: return '''
				right.stop(true);
				left.stop(true);
				Delay.msDelay(«n»);'''
		}
	}

	
	//Conditions	
	def static dispatch condition2code(LightCondition condition){
		
		switch(condition.value)
		{
			case LightValue::WHITE: return ''' (20 == robot.light.readValue()) '''
			case LightValue::BLACK: return ''' (80 == robot.light.readValue()) '''
		}
	}
	
	def static dispatch condition2code(SonarCondition condition){
		
		switch(condition.value)
		{
			case SonarValue::NOTHING: return ''' (robot.sonar.getDistance() > 20) '''
			case SonarValue::SOMETHING: return ''' (robot.sonar.getDistance() < 20) '''
		}
	}
	
		def static dispatch condition2code(BumperCondition condition){
		
		switch(condition.value)
		{
			case BumperValue::PRESSED: return ''' (robot.bumper.isPressed()) '''
			case BumperValue::NOTPRESSED: return ''' (!robot.bumper.isPressed()) '''
		}
	}
}
