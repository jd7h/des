package Robot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Queue;

import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.util.Delay;


/*
 *  de inputList is de lijst met alle inputs die binnen gekomen zijn
 *  en nog niet verwrkt zijn 
 * 
 * to create a thread and start it use: 
 *  t = new Thread("threadname");
 *  t.start();
 *  then it runs the run() method in the thread
 */

class Colorfunctionality extends Thread {

	private ColorSensor colorsens;
	private static String colorlist[] = {"None", "Red", "Green", "Blue", "Yellow",
		"Megenta", "Orange", "White", "Black", "Pink",
		"Grey", "Light Grey", "Dark Grey", "Cyan"}; 
	

	public Colorfunctionality(ColorSensor sens)
	{
		this.colorsens = sens;
	}

	public void run()
	{	
		while(true)
		{
			LCD.clear(5);
			LCD.drawString(colorlist[colorsens.getColorID()+1],0,5);
			Delay.msDelay(300);
		}
		
	}
	
}
