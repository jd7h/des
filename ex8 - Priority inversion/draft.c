#include <stdio.h>
#include <signal.h>
#include <unistd.h>
#include <sys/mman.h>

#include <native/task.h>
#include <native/timer.h>
#include <native/sem.h>
#include <native/mutex.h>

#include  <rtdk.h>
#include <sys/io.h>

#define NTASKS 3

#define HIGH 52 /* high priority */
#define MID 51 /* medium priority */
#define LOW 50  /* low priority */

RT_TASK demo_task[NTASKS];
//RT_SEM startsem;
RT_MUTEX mutex1;

//method of task prioHigh
void fn3()
{
	int i;
	for(i = 0;i<4;i++)
	{
	rt_printf("High priority task tries to acquire mutex.\n");
	rt_mutex_acquire(&mutex1,TM_INFINITE);
	rt_printf("High priority task acquires mutex.\n");
	rt_mutex_release(&mutex1);
	rt_printf("High priority task releases mutex.\n");
	}
rt_printf("....................................High priority task is done.\n");
}

//method of task prioMedium
void fn2()
{
int cycles;
rt_task_start(&demo_task[2],&fn3,NULL);
for(cycles = 0;cycles < 12;cycles++)
{
	rt_printf("Medium priority task is running.\n");
	rt_timer_spin(1e7);
}
rt_printf("....................................Medium priority task is done.\n");
}


//method of task prioLow
void fn1()
{
	rt_printf("Low priority task acquires mutex.\n");
	rt_mutex_acquire(&mutex1,TM_INFINITE);
	
	//start the medium task that will hopefully preempt this one...
	rt_task_start(&demo_task[1],&fn2,NULL);

	rt_printf("Low priority task releases mutex.\n");
	rt_mutex_release(&mutex1);

	int i;
	for(i = 0;i<2;i++){
		rt_printf("Low priority task acquires mutex.\n");
		rt_mutex_acquire(&mutex1,TM_INFINITE);
		rt_printf("Low priority task releases mutex.\n");
		rt_mutex_release(&mutex1);
	}
rt_printf("....................................Low priority task is done.\n");
}
//startup code
void startup()
{
  int i;

  //rt_sem_create(&startsem,"Starting semaphore",1,S_FIFO);
  rt_mutex_create(&mutex1,"mutex1");

  rt_task_create(&demo_task[0], "prioLow", 0, LOW, 0);
  rt_task_create(&demo_task[1], "prioMedium", 0, MID, 0);
  rt_task_create(&demo_task[2], "prioHigh", 0, HIGH, 0);

  rt_task_start(&demo_task[0], &fn1,NULL);
  //rt_task_start(&demo_task[1], &fn2,NULL);
  //rt_task_start(&demo_task[2], &fn3,NULL);

  //rt_printf("wake up all tasks\n");
  //rt_sem_broadcast(&startsem);
}

void init_xenomai() {
  /* Avoids memory swapping for this program */
  mlockall(MCL_CURRENT|MCL_FUTURE);

  /* Perform auto-init of rt_print buffers if the task doesn't do so */
  rt_print_auto_init(1);
}

int main(int argc, char* argv[])
{
  // code to set things to run xenomai
  init_xenomai();

  //startup code
  startup();
}
