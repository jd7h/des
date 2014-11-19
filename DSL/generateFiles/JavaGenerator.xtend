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
		//maak een enum van alle states, kan ook als Int
		//to do ^

		//definieer de beginstate
		currentState = 0;

		//definieer standaard equipment op Robot
		NXTRegulatedMotor right = Motor.A;
		NXTRegulatedMotor left = Motor.B;
		LightSensor light = new LightSensor(SensorPort.S1);
		UltrasonicSensor sonar = new UltrasonicSensor(SensorPort.S2);
		TouchSensor touch = new TouchSensor(SensorPort.S3);

		//maak een instantie aan van alle states/behaviors

		//maak een lijst van de behaviors op basis van prioriteit

		//maak een arbitrator aan en start	
	

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