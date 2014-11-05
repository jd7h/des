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

public class Btfunc {

	public void masterSetUp(String slavename)
	{
		//set up blconnection and send something
		//bluetooth connection, master side
		String name = slavename;
	
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
		
		/*	
		DO other stuff instead of sending integers
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
		*/
	}

	public void masterClose()
	{
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
	}

	public void slaveSetUp()
	{
		String connected = "Connected";
        String waiting = "Waiting...";
        String closing = "Closing...";
        
		//waarom staat hier een while true?
		while (true)
		{
			LCD.drawString(waiting,0,0);
			LCD.refresh();

	        BTConnection btc = Bluetooth.waitForConnection();
	        
			LCD.clear();
			LCD.drawString(connected,0,0);
			LCD.refresh();	

			DataInputStream dis = btc.openDataInputStream();
			DataOutputStream dos = btc.openDataOutputStream();
			
			/*
			//do the actions here
			//search for colours until master says stop
			//give light or sound if master or slave finds color
			//detect collisions
			for(int i=0;i<100;i++) {
				int n = dis.readInt();
				LCD.drawInt(n,7,0,1);
				LCD.refresh();
				dos.writeInt(-n);
				dos.flush();
			}
			*/
		}
	}

	public void slaveClose()
	{
		dis.close();
		dos.close();
		Thread.sleep(100); // wait for data to drain
		LCD.clear();
		LCD.drawString(closing,0,0);
		LCD.refresh();
		btc.close();
		LCD.clear();
	}
}
