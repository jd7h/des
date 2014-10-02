//programma voor PC1

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
RT_INTR PC1_intr;
RT_TASK handlertask;

#define PARA_PORT_IRQ 7

void generate_interrupt_to_pc1()
{
	outb(inb(0x378) & 0xfe, 0x378);//set low D0
	outb(inb(0x378) | 0x1, 0x378); //set high D0
}

void handler()
{
	rt_printf("Begin interrupt handler van PC2\n");
	int nr_waiting_interrupts = 0;
	while(1)
	{
		nr_waiting_interrupts = rt_intr_wait(&PC1_intr,TM_INFINITE);
		if (nr_waiting_interrupts > 0)
		{
			rt_printf("Generating interrupt to pc 1...\n");
			generate_interrupt_to_pc1();
		}
	}
}

//startup code
void startup()
{
	// define interrupt
	rt_printf("Creating interrupt...\n");
	rt_intr_create(&PC1_intr,NULL,PARA_PORT_IRQ,I_PROPAGATE);
	
	// define interrupt handler
	rt_printf("Creating handler task...\n");
	rt_task_create(&handlertask,NULL,0,99,0);

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
	ioperm(0x37A,1,1);//open port C(4)
	outb(inb(0x37A) | 0x10, 0x37A);//set C4 to high
	ioperm(0x379,1,1);//open port S(6)
	outb(inb(0x378) | 0x1, 0x378); //set D0 to high
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
