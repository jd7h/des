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
#define MEASUREMENTS 5

void handler()
{
	rt_printf("Begin interrupt handler of lightsensor\n");
	int nr_waiting_interrupts = 0;
	int lr = 0;
  int counter = 0;
  RTIME time1,time2;
  RTIME results[MEASUREMENTS];
  char byte;
	while(1)
	{
		nr_waiting_interrupts = rt_intr_wait(&lightsens_intr,TM_INFINITE);
		if (nr_waiting_interrupts > 0)
		{
      if(lr == 0)
      {
        if(counter < MEASUREMENTS)
        {
          //do stuff
          time1 = rt_timer_read();
          if (counter > 0)
          {
            results[counter] = time1-time2;
            counter++;
          }
        }
        lr = 1;
        if (counter >= MEASUREMENTS)
        {
          int cycletime = (results[0] + results[1])/2;
          if(results[0] > results[1])
          {
            rt_task_sleep(cycletime); //blijf wachten
            //begin met tekenen
            byte = 0x7F;
            outb(byte,0x378);
          }
        }
        //if (result[0] > result[1])  //en als lr = 0, dan gaat de wijzer van links naar rechts
        //dan: wacht op de volgende interrupt en wacht DAN result[0] en DAN: iets tekenen
      }
      else //lr == 1
      {
        if(counter < MEASUREMENTS)
        {
          //doe andere shit
          time2 = rt_timer_read();
          results[counter] = time2-time1;
          counter++;
        }
        lr = 0;
        if (counter >= MEASUREMENTS)
        {
          byte = 0x00;
          outb(byte,0x378);
        }
      }
		}	
	}
  int j;
  for (j = 0;j<MEASUREMENTS;j++)
  {
    rt_printf("%d\n",results[j]);
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
