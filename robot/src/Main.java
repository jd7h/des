package robot2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Main {
	
	static RegulatedMotor left = Motor.B;
	static RegulatedMotor right = Motor.A;

	public static void main(String[] args){
		
		left.setSpeed(400);
	    right.setSpeed(400);
	    
		Behavior drive = new DriveForward();
		//Behavior evade = new DetectObstacle(SensorPort.S3,SensorPort.S2);
		//Behavior staywithinborder = new DetectBlackLine(SensorPort.S1);
		//Behavior test = new ColorTester(SensorPort.S1);
	    Behavior hunt = new ColorHunter(SensorPort.S1);
		//Behavior[] behaviorList = {drive,evade,staywithinborder};
	    Behavior[] behaviorList = {drive,hunt};
		Arbitrator arbitrator = new Arbitrator(behaviorList);
		LCD.drawString("Robot van Judith\n en Mirjam",0,1);
		Button.waitForAnyPress();
		arbitrator.start();
				
		/*
		//bluetooth connection, master side
		String name = "NLT2"; //SLAVE NAME
		
		LCD.drawString("Connecting...", 0, 0);
		LCD.refresh();
		
		RemoteDevice btrd = Bluetooth.getKnownDevice(name);

		if (btrd == null) {
			LCD.clear();
			LCD.drawString("No such device", 0, 0);
			LCD.refresh();
			Thread.sleep(2000);
			System.exit(1);
		}
		
		BTConnection btc = Bluetooth.connect(btrd);
		
		if (btc == null) {
			LCD.clear();
			LCD.drawString("Connect fail", 0, 0);
			LCD.refresh();
			Thread.sleep(2000);
			System.exit(1);
		}
		
		LCD.clear();
		LCD.drawString("Connected", 0, 0);
		LCD.refresh();
		
		DataInputStream dis = btc.openDataInputStream();
		DataOutputStream dos = btc.openDataOutputStream();
				
		for(int i=0;i<100;i++) {
			try {
				LCD.drawInt(i*30000, 8, 0, 2);
				LCD.refresh();
				dos.writeInt(i*30000);
				dos.flush();			
			} catch (IOException ioe) {
				LCD.drawString("Write Exception", 0, 0);
				LCD.refresh();
			}
			
			try {
				LCD.drawInt(dis.readInt(),8, 0,3);
				LCD.refresh();
			} catch (IOException ioe) {
				LCD.drawString("Read Exception ", 0, 0);
				LCD.refresh();
			}
		}
		
		try {
			LCD.drawString("Closing...    ", 0, 0);
			LCD.refresh();
			dis.close();
			dos.close();
			btc.close();
		} catch (IOException ioe) {
			LCD.drawString("Close Exception", 0, 0);
			LCD.refresh();
		}
		
		LCD.clear();
		LCD.drawString("Finished",3, 4);
		LCD.refresh();
		Thread.sleep(2000);
		*/
	}
}
