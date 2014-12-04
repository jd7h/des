package robots.tasks.generator

import org.eclipse.emf.ecore.resource.Resource

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

	//make methods for every state seperately
	<<FOR s : Auxilary.getStates(resource)>>
	public void <<Auxilary.getStateMethod(s)>>()
	{
		<<FOR a : Auxilary.getActionList(s)>>
			<<Auxilary.action2text(a)>>
		<<ENDFOR>>
	}
	<<ENDFOR>

	public class Main{

			//maak een enum van de beginstates
			public enum State {
			<<FOR s : Auxilary.getStates(resource) SEPARATOR ','>>
				<<Auxilary.getStateItem(s)>>
			<<ENDFOR>>
			}

			//definieer lijst van endstates
			State[] endStates = {<<FOR e : <<Auxilary.getEndStates SEPARATOR ','>><<Auxilary.getStateItem(e)>><<ENDFOR>>};
			
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
			State current = <<Auxilary.getStateItem(Auxilary.getStartState(resource))>>
			
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

}
