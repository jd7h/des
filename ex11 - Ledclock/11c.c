//program ex11
//ledclock
//Judith & Mirjam

//draws a centered x from left to right and right to left

#include <stdio.h>
#include <signal.h>
#include <unistd.h>
#include <sys/mman.h>

#include <native/task.h>
#include <native/timer.h>
#include <native/sem.h>
#include <native/intr.h>

#include <rtdk.h>
#include <sys/io.h>


//interrupt
RT_INTR lightsens_intr;
RT_TASK handlertask;

#define PARA_PORT_IRQ 7
#define BASEPERIOD 0
#define MEASUREMENTS 2
#define PERIOD 6e5

void draw_x()
{
	//stap 1
	char byte;
	byte = byte | 0x63; //0110011
	outb(byte, 0x378); //set D to first phase of X
	rt_task_wait_period(NULL);
	//stap 2
	byte = 0x14;
	outb(byte, 0x378);
	rt_task_wait_period(NULL);
	//stap 3
	byte = 0x08;
	outb(byte,0x378);
	rt_task_wait_period(NULL);
	//stap 4
	byte = 0x14;
	outb(byte,0x378);
	rt_task_wait_period(NULL);
	//stap 5
	byte = 0x63;
	outb(byte,0x378);
	rt_task_wait_period(NULL);
	//leegmaken
	byte = 0x00;
	outb(byte,0x378);
}

void handler()
{
	rt_printf("Begin interrupt handler of lightsensor\n");
	
	int nr_waiting_interrupts = 0;
  	RTIME time[3];
  	RTIME results[2];
	int counter = 0;
	int kort = 0;
	int short_cycle = -1;
    int long_cycle = -1;
	
	while(1)
	{
		//bekijk de interrupts
		nr_waiting_interrupts = rt_intr_wait(&lightsens_intr,TM_INFINITE);
		//als er interrupts zijn...
		if (nr_waiting_interrupts > 0)
		{
			//als we nog moeten meten
			if(counter < 3)
			{
				//ga meten
				time[counter] = rt_timer_read();
        		if (counter > 0)
        		{
          			results[counter-1] = time[counter]-time[counter-1];
        		}
          	counter++;
			}
			else if (counter == 3)
			{	
				//trek conclusies over de 4e cycle
				if (results[0] < results[1])
				{
					kort = 1;
					short_cycle = results[0];
					long_cycle = results[1];
				}
				else
				{
         			short_cycle = results[1];
          			long_cycle = results[0];
					kort = 0;
				}
				counter++;
			}
			else
			{
				if(kort == 1)
				{
					//this is the short cycle
					//teken niets
					kort = 0;
				}
				else
				{
					//this is the long cycle
			        //wacht tot je in het midden bent
					int mid = long_cycle/13;
					rt_task_set_periodic(NULL,TM_NOW,mid); //werkt dit?
					rt_task_wait_period(NULL);
					rt_task_set_periodic(NULL,TM_NOW,PERIOD);
					//teken de eerste keer 
					draw_x();
					//for 11c: voeg toe: wacht, teken nog een keer
					mid = (long_cycle/2) - PERIOD*5 + (short_cycle/2);
					rt_task_set_periodic(NULL,TM_NOW,mid);
					rt_task_wait_period(NULL);
					rt_task_set_periodic(NULL,TM_NOW,PERIOD);
					draw_x();
					kort = 1;
				}
			}
		}
	}
}


//startup code
void startup()
{
	// define interrupt
	rt_printf("Creating interrupt...\n");
	rt_intr_create(&lightsens_intr,NULL,PARA_PORT_IRQ,I_PROPAGATE);
	
	// define interrupt handler
	rt_printf("Creating handler task...\n");
	rt_task_create(&handlertask,NULL,0,99,0);
	rt_task_set_periodic(&handlertask,TM_NOW,PERIOD);

	rt_printf("Starting task...\n");
	rt_task_start(&handlertask,&handler,NULL);
}

void init_xenomai() {
  /* Avoids memory swapping for this program */
  mlockall(MCL_CURRENT|MCL_FUTURE);

  /* Perform auto-init of rt_print buffers if the task doesn't do so */
  rt_print_auto_init(1);
}

void init_ports()
{
	ioperm(0x378,1,1);//open port D(0)
	ioperm(0x379,1,1);//open port S(6)
	ioperm(0x37A,1,1);//open port C(4)
	outb(inb(0x37A) | 0x10, 0x37A);//set C4 to high
}

void exit_ports(){
	//close port D0?
	//close port S6?
}

int main(int argc, char* argv[])
{
  // code to set things to run xenomai
  init_xenomai();
  init_ports();

  //startup code
  startup();

  printf("pause\n");
  pause();

  exit_ports();
}
