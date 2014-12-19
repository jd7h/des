package Robot;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import lejos.nxt.LCD;


/*
 *  de inputList is de lijst met alle inputs die binnen gekomen zijn
 *  en nog niet verwrkt zijn 
 * 
 * to create a thread and start it use: 
 *  t = new Thread("threadname");
 *  t.start();
 *  then it runs the run() method in the thread
 */

class BTfunctionality extends Thread {

	private DataInputStream dis;
	private DataOutputStream dos;
	private String threadname;
	private int buf;
	public Queue<Integer> inputList;
	

	public BTfunctionality(String name, DataInputStream dis, DataOutputStream dos)
	{
		this.dis = dis;
		this.dos = dos;
		this.threadname = name;
		inputList = new Queue<Integer>();
		buf = -1;
	}

	public void run()
	{
		try {
			buf = dis.readInt();
			inputList.push(buf);
		} catch (IOException e) {
			LCD.clear();
			LCD.drawString("Bluetooth error", 0, 1);
		}
		
	}
	
	public void write(int msg) 
	{
		try {
			dos.writeInt(msg);
			dos.flush();
		} catch (IOException e) {
			LCD.clear();
			LCD.drawString("Bluetooth writing Error", 0, 1);
		}
	}
	
	public Queue<Integer> getInputList()
	{
		return inputList;
	}
	
	public int getElement()
	{
		
		if(inputList.empty()){
			return -1;
		}else{
			return 	(int) inputList.pop();	
		}
	}
}
