package robot2;

import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.robotics.subsumption.Behavior;

public class ColorHunter implements Behavior {
	
	private boolean suppressed = false;
	private boolean isMaster;
	private ColorSensor cs;
	private int amount = 3;
	private boolean prey[] = {false,false,false};
	private int cnr;
	private static String colorlist[] = {"None", "Red", "Green", "Blue", "Yellow",
        "Megenta", "Orange", "White", "Black", "Pink",
        "Grey", "Light Grey", "Dark Grey", "Cyan"}; 
	
	public ColorHunter(SensorPort port, boolean isMaster)
	{
		cs = new ColorSensor(port);
		this.isMaster = isMaster;
	}
	
	public boolean allDone()
	{
		//if this is the masterbot, return normal value
		if(isMaster)
		{
			for(int i = 0;i<amount;i++)
			{
				if(!prey[i])
					return false;
			}
			return true;
		}
		//if this is the slavebot, ask the master
		//nog aanpassen met bluetooth
		else
			return false;
	}

	@Override
	public boolean takeControl() {
		Color color = cs.getColor();
		cnr = color.getColor()+1;
		LCD.drawString(colorlist[cnr], 0, 0);
		//if(allDone())
		//	Sound.beepSequenceUp();
		return !allDone() && (cnr == 1 || cnr == 3 || cnr == 4);
	}

	@Override
	public void action() {
		suppressed = false;
		Sound.beep();
		//if robot is master, register, do sound & notify slave for sound
		if(isMaster)
		{
			//register the color
			switch(cnr){
			case 1:		prey[0] = true; break;
			case 3:		prey[1] = true; break;
			case 4:		prey[2] = true; break;
			default:	LCD.drawString("Wrong color. So sad.",0,4); break; //do nothing
				}
		}
		//if robot is slave
		else
		{
			//notify master & do sound
		}
	}

	@Override
	public void suppress() {
		suppressed = true;
		// TODO Auto-generated method stub
		
	}
	

}
