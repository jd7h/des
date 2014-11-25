package example1; //is this well-typed?

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
		int startstate = 0;
		Robot robot = new Robot(startstate,right,left,light,sonar,touch);

		//maak een instantie aan van alle states/behaviors
		Behavior b_s1 = new S1(robot);
		Behavior b_s2 = new S2(robot);

		//maak een lijst van de behaviors op basis van prioriteit
		Behavior[] behaviorlist = {b_s1, b_s2};	//separator is ','

		//maak een arbitrator aan en start	
		Arbitrator arbitrator = new Arbitrator(behaviorlist);
		LCD.drawString("CodeGenRobot",0,1);
		LCD.drawString("Judith & Mirjam",0,2);
		Button.waitForAnyPress();
		arbitrator.start();
	

	}