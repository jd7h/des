package example1;

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

}