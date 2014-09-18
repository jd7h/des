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
RT_TASK demo_task_d;
RT_TASK demo_task_e;

void demo(void *arg)
{
  RT_TASK *curtask;
  RT_TASK_INFO curtaskinfo;

  // inquire current task
  curtask=rt_task_self();
  rt_task_inquire(curtask,&curtaskinfo);

  // print task name
  rt_printf("Task name : %s \n", curtaskinfo.name);
}

int main(int argc, char* argv[])
{
  char  str[10] ;

  // Perform auto-init of rt_print buffers if the task doesn't do so
  rt_print_auto_init(1);

  // Lock memory : avoid memory swapping for this program
  mlockall(MCL_CURRENT|MCL_FUTURE);

  rt_printf("starting 5 tasks\n");

  /*
   * Arguments: &task,
   *            name,
   *            stack size (0=default),
   *            priority,
   *            mode (FPU, start suspended, ...)
   */
  sprintf(str,"a");
  rt_task_create(&demo_task_a, str, 0, 50, 0);
  sprintf(str,"b");
  rt_task_create(&demo_task_b, str, 0, 50, 0);
  sprintf(str,"c");
  rt_task_create(&demo_task_c, str, 0, 50, 0);
  sprintf(str,"d");
  rt_task_create(&demo_task_d, str, 0, 50, 0);
  sprintf(str,"e");
  rt_task_create(&demo_task_e, str, 0, 50, 0);

  /*
   * Arguments: &task,
   *            task function,
   *            function argument
   */
  rt_task_start(&demo_task_a, &demo, 0);
  rt_task_start(&demo_task_b, &demo, 0);
  rt_task_start(&demo_task_c, &demo, 0);
  rt_task_start(&demo_task_d, &demo, 0);
  rt_task_start(&demo_task_e, &demo, 0);
}
