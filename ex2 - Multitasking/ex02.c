#include <stdio.h>
#include <signal.h>
#include <unistd.h>
#include <sys/mman.h>

#include <native/task.h>
#include <native/timer.h>

#include  <rtdk.h>

RT_TASK demo_task_a;
RT_TASK demo_task_b;
RT_TASK demo_task_c;

void demo(void *arg)
{
  RT_TASK *curtask;
  RT_TASK_INFO curtaskinfo;

  // inquire current task
  curtask=rt_task_self();
  rt_task_inquire(curtask,&curtaskinfo);

  //sleep
  sleep(1);
  
  int i;
  for(i = 0;i<10;i++)
  {
  	rt_printf("This is task %s \n", curtaskinfo.name);
	rt_task_wait_period(NULL);
  }
}

int main(int argc, char* argv[])
{
  char  str[10] ;

  // Perform auto-init of rt_print buffers if the task doesn't do so
  rt_print_auto_init(1);

  // Lock memory : avoid memory swapping for this program
  mlockall(MCL_CURRENT|MCL_FUTURE);

  rt_printf("starting 3 tasks periodically.\n");

  /*
   * Arguments: &task,
   *            name,
   *            stack size (0=default),
   *            priority,
   *            mode (FPU, start suspended, ...)
   */
  sprintf(str,"apple");
  rt_task_create(&demo_task_a, str, 0, 50, 0);
  sprintf(str,"brocolli");
  rt_task_create(&demo_task_b, str, 0, 50, 0);
  sprintf(str,"cake");
  rt_task_create(&demo_task_c, str, 0, 50, 0);

  /*
   * Arguments: &task,
   *            task function,
   *            function argument
   */
  rt_task_set_periodic(&demo_task_a, TM_NOW, 10000000);
  rt_task_set_periodic(&demo_task_b, TM_NOW, 20000000);
  rt_task_set_periodic(&demo_task_c, TM_NOW, 30000000);

  rt_task_start(&demo_task_a, &demo, 0);
  rt_task_start(&demo_task_b, &demo, 0);
  rt_task_start(&demo_task_c, &demo, 0);

  rt_printf("end program by CTRL_C \n");
  pause();
}
