//program ex11
//ledclock
//Judith & Mirjam

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
#define PERIOD 8e5 //kleine periode. //helft van wat we eerst hadden. //3.7e6 //1/18 * 1/15 sec in nanoseconds old: //1e7 //1/18 * 1/5 second in nanoseconds

void handler()
{
	rt_printf("Begin interrupt handler of lightsensor\n");
	int nr_waiting_interrupts = 0;
	int counter = 0;
  int rightleft = 0;
	while(1)
	{
		nr_waiting_interrupts = rt_intr_wait(&lightsens_intr,TM_INFINITE);
		if (nr_waiting_interrupts > 0 && rightleft==0)
		{
      //rt_printf("voor: nr_waiting_interrupts:\t%d\n",nr_waiting_interrupts);
      int buffer = 0;
      rt_task_wait_period(NULL);
      rt_task_wait_period(NULL);
			//stap 1
			char byte;
			byte = byte | 0x63; //0110011
      //byte = 0x01;
			outb(byte, 0x378); //set D to first phase of X
			rt_task_wait_period(NULL);
			//stap 2
			byte = 0x14;
      //byte = 0x02;
			outb(byte, 0x378);
			rt_task_wait_period(NULL);
			//stap 3
			byte = 0x08;
      //byte = 0x04;
			outb(byte,0x378);
			rt_task_wait_period(NULL);
			//stap 4
			byte = 0x14;
      //byte = 0x08;
			outb(byte,0x378);
			rt_task_wait_period(NULL);
			//stap 5
			byte = 0x63;
      //byte = 0x10;
			outb(byte,0x378);
			rt_task_wait_period(NULL);
			//leegmaken
			byte = 0x00;
			outb(byte,0x378);
      //rt_printf("na: nr_waiting_interrupts:\t%d\n",nr_waiting_interrupts);
      rightleft = 1;
		}	
    else
    {
      rightleft = 0;
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
