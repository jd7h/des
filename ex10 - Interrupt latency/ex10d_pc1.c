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
RT_INTR PC2_intr;
RT_TASK handlertask;

#define PARA_PORT_IRQ 7

#define MEASUREMENTS 10000
#define BASEPERIOD 0
#define PERIOD 1e5 //100 microseconds
int counter = 0;

RTIME results[MEASUREMENTS];


void generate_interrupt_to_pc2()
{
	outb(inb(0x378) & 0xfe, 0x378);//set low D0
	outb(inb(0x378) | 0x1, 0x378); //set high D0
}

void handler()
{
	rt_printf("Begin interrupt handler van PC1\n");
	RTIME time1, time2;
	while (counter < MEASUREMENTS)
	{
		time1 = rt_timer_read();
		rt_printf("t1:\t%u\n",time1);
		rt_printf("Interrupt %d...\n",counter);
		generate_interrupt_to_pc2();
		int nr_waiting_interrupts = 0;
		while(nr_waiting_interrupts <= 0){
			nr_waiting_interrupts = rt_intr_wait(&PC2_intr,TM_INFINITE);
			if (nr_waiting_interrupts > 0)
			{
				time2 = rt_timer_read();
				rt_printf("Interrupt! Time2: \t%u\n",time2); 
			}
		}
		results[counter] = time2-time1;
		rt_printf("Writing measurement: %u\n",results[counter]);
		counter++;
		rt_task_wait_period(NULL);
	}
}

void write_RTIMES(char * filename, unsigned int number_of_values,
	RTIME *time_values)
{
	unsigned int n = 0;
	FILE *file;
	file = fopen(filename,"w");
	while (n<MEASUREMENTS){
		fprintf(file,"%u,%llu\n",n,results[n]);
		n++;
	}
	fclose(file);
}

//startup code
void startup()
{
	// define interrupt
	rt_printf("Creating interrupt...\n");
	rt_intr_create(&PC2_intr,NULL,PARA_PORT_IRQ,I_PROPAGATE);
	
	// define interrupt handler
	rt_printf("Creating handler task...\n");
	rt_task_create(&handlertask,NULL,0,99,T_JOINABLE);
	rt_task_set_periodic(&handlertask,TM_NOW,PERIOD);

	rt_printf("Starting task...\n");
	rt_task_start(&handlertask,&handler,NULL);

	rt_task_join(&handlertask);

	rt_printf("Done collecting data.");
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

  int i;
  for (i=0;i<MEASUREMENTS;i++)
  {
    printf("%d\t%d\n",i,results[i]);
  }

  write_RTIMES("interrupt_time_diffs_lab_pcs",MEASUREMENTS-1,results);
  printf("done exporting results to csv.");

  printf("pause\n");
  pause();

  exit_ports();
}
