#include <stdio.h>
#include <signal.h>
#include <unistd.h>
#include <sys/mman.h>

#include <native/task.h>
#include <native/timer.h>

#include  <rtdk.h>

#include <errno.h>

#define NO_TASKS  3
RT_TASK demo_tasks[NO_TASKS];

void demo(void *arg)
{
  RT_TASK *curtask;
  RT_TASK_INFO curtaskinfo;
  int num, i = 100;
  curtask=rt_task_self();
  rt_task_inquire(curtask,&curtaskinfo);
  num = *(int*)arg;
  sleep(1);
  while(i--) {	// Using while(1) makes it crash, the print thread starves?
    rt_printf("Task name: %s, my argument was %d\n", curtaskinfo.name, num);
    rt_task_wait_period(NULL);
  }
  return;
}

int main(int argc, char* argv[])
{
  int i, j;
  char names[NO_TASKS][10];

  // Perform auto-init of rt_print buffers if the task doesn't do so
  rt_print_auto_init(1);

  // Lock memory : avoid memory swapping for this program
  mlockall(MCL_CURRENT|MCL_FUTURE);

  for(i = 0; i < NO_TASKS; ++i) {
    sprintf(names[i], "task%d", i);
    rt_task_create(&demo_tasks[i], names[i], 0, 50, 0);
    rt_task_set_periodic(&demo_tasks[i], TM_NOW, 10000000*(i+1));
  }
  for(i = 0; i < NO_TASKS; ++i) {
    j = i * 10;
    rt_task_start(&demo_tasks[i], &demo, &j);
  }

  rt_printf("end program by CTRL-C\n");
  pause();
}
