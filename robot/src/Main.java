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
	static boolean isMaster = true;
	static string slavename;
	

	public static void main(String[] args){

		if(isMaster)
		{
			Btfunc.masterSetUp(slavename);
		}
		else
		{
			//wait for bluetooth connection
			Btfunc.slaveSetUp();
		}

		//do the stuff, this is for both slave and master.
		left.setSpeed(400);
	    right.setSpeed(400);
	    
		Behavior drive = new DriveForward();
		Behavior evade = new DetectObstacle(SensorPort.S3,SensorPort.S2);
		Behavior staywithinborder = new DetectBlackLine(SensorPort.S1);
		//neem mee of robot master of slave is
	    Behavior hunt = new ColorHunter(SensorPort.S1,isMaster);
		Behavior[] behaviorList = {drive,hunt,evade,staywithinborder);
		Arbitrator arbitrator = new Arbitrator(behaviorList);
		LCD.drawString("Robot van Judith\n en Mirjam",0,1);
		Button.waitForAnyPress();
		arbitrator.start();

		//start closing the connection
		if(isMaster)
		{
			Btfunc.masterClose();
		}
		else
		{
			Btfunc.slaveClose();
		}

	}
}
