package robots.tasks.generator

import org.eclipse.emf.ecore.resource.Resource
import robots.tasks.rDSL.DriveAction

class JavaGenerator {
	def static generateMain(Resource resource)'''
	package <<Auxilary.getAutomataName(resource)>>; //is this well-typed?

	/* Automatically generated code
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
			//maak een enum van alle states, kan ook als Int?
			//to do ^

			//definieer standaard equipment op Robot
			NXTRegulatedMotor right = Motor.A;
			NXTRegulatedMotor left = Motor.B;
			LightSensor light = new LightSensor(SensorPort.S1);
			UltrasonicSensor sonar = new UltrasonicSensor(SensorPort.S2);
			TouchSensor touch = new TouchSensor(SensorPort.S3);

			//maak een robot
			??? start = <<Auxilary.getStartState(resource)>>; //aanpassen, heeft nog geen type
			Robot robot = new Robot(start,right,left,light,sonar,touch);

			//maak een instantie aan van alle states/behaviors
			Behavior s1 = new s1(robot); //nog aanpassen

			//maak een lijst van de behaviors op basis van prioriteit

			//maak een arbitrator aan en start	
			Arbitrator arbitrator = new Arbitrator(behaviorlist);
			LCD.drawString("CodeGenRobot",0,1);
			LCD.drawString("Judith & Mirjam",0,2);
			Button.waitForAnyPress();
			arbitrator.start();
		

		}'''

	def static generateBehavior(Resource resource,State s)'''
		package <<Auxilary.getAutomataName(resource)>>;

		import lejos.robotics.subsumption.*;

		public class <<Auxilary.getStateName(s)>> implements Behavior{
			private boolean suppressed = false;
			private Robot robot;

			public <<Auxilary.getStateName(s)>>(Robot robot){
				this.robot = robot;
			}

			public boolean takeControl(){
			}

			public void suppress(){
			//standaard gewoon de motoren stoppen?
			robot.right.stop(true);
			robot.left.stop();
			}

			public void action(){
			robot.setCurrentState(name);
			suppressed = false;
			//convert all actions of this state to javacode
			<<FOR a : Auxilary.getActionList(s)>><<Auxilary.action2Java(a)>><<ENDFOR>>
			}'''

	def static generateRobot(Resource resource)'''
		package <<Auxilary.getAutomataName(resource)>>;

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
	
	
	//TODO Duration nog inzetten
	def static dispatch action2Text(DriveAction action)'''
		if( <<action.driveDir>> == <<DriveDirection::FORWARDS>> ){
			right.forward();
			left.forward();
		}else{
			right.backward();
			left.backward();
		}'''	
}
