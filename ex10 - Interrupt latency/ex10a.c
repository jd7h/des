#include <stdio.h>
#include <signal.h>
#include <unistd.h>
#include <sys/mman.h>

#include <native/task.h>
#include <native/timer.h>
#include <sys/io.h>

#include <errno.h>

#define MEASUREMENTS 10000
#define BASEPERIOD 0
#define PERIOD 1e5

RT_TASK timertask;

RTIME exec_times[MEASUREMENTS];
RTIME exec_diff[MEASUREMENTS-1];
int counter = 0;

//should be scheduled every 100 microseconds
//should be executed 10000 times
void timer()
{
	while(counter < MEASUREMENTS)
	{
		exec_times[counter] = rt_timer_read();
		rt_printf("%d\t%u\n",counter,exec_times[counter]);
		counter++;
		rt_task_wait_period(NULL);
	}
}

void compute_differences()
{
	int i;
	for(i = 0;i<MEASUREMENTS-1;i++)
	{
		exec_diff[i] = exec_times[i+1]-exec_times[i];
	}
}
void write_RTIMES(char * filename, unsigned int number_of_values,
	RTIME *time_values)
{
	unsigned int n = 0;
	FILE *file;
	file = fopen(filename,"w");
	while (n<MEASUREMENTS){
		fprintf(file,"%u,%llu\n",n,exec_diff[n]);
		n++;
	}
	fclose(file);
}


//startup code
void startup()
{
	//begin task
	rt_printf("Creating timertask...\n");
	rt_task_create(&timertask,"timertask",0,50,T_JOINABLE);
	rt_printf("Setting period...\n");
	rt_task_set_periodic(&timertask,TM_NOW,PERIOD);
	rt_task_start(&timertask,&timer,NULL);

	/* debug code
	int i;
	for(i = 0;i<MEASUREMENTS;i++)
	{
		rt_printf("%d\t%u\n",i,exec_times[i]);
	}
	*/
	rt_task_join(&timertask);
	compute_differences();
	write_RTIMES("time_diff.csv",MEASUREMENTS-1,exec_diff);
}

void init_xenomai() {
  /* Avoids memory swapping for this program */
  mlockall(MCL_CURRENT|MCL_FUTURE);

  /* Perform auto-init of rt_print buffers if the task doesn't do so */
  rt_print_auto_init(1);

  rt_timer_set_mode(BASEPERIOD);
}

int main(int argc, char* argv[])
{
  // code to set things to run xenomai
  init_xenomai();

  //startup code
  startup();

  printf("pause\n");
  pause();
}
