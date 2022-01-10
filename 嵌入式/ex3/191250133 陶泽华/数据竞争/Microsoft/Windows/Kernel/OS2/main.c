/*
*********************************************************************************************************
*                                            EXAMPLE CODE
*
*               This file is provided as an example on how to use Micrium products.
*
*               Please feel free to use any application code labeled as 'EXAMPLE CODE' in
*               your application products.  Example code may be used as is, in whole or in
*               part, or may be used as a reference only. This file can be modified as
*               required to meet the end-product requirements.
*
*               Please help us continue to provide the Embedded community with the finest
*               software available.  Your honesty is greatly appreciated.
*
*               You can find our product's user manual, API reference, release notes and
*               more information at https://doc.micrium.com.
*               You can contact us at www.micrium.com.
*********************************************************************************************************
*/

/*
*********************************************************************************************************
*
*                                              uC/OS-II
*                                            EXAMPLE CODE
*
* Filename : main.c
*********************************************************************************************************
*/

/*
*********************************************************************************************************
*                                            INCLUDE FILES
*********************************************************************************************************
*/

#include  <cpu.h>
#include  <lib_mem.h>
#include  <os.h>

#include  "app_cfg.h"


/*
*********************************************************************************************************
*                                            LOCAL DEFINES
*********************************************************************************************************
*/


/*
*********************************************************************************************************
*                                       LOCAL GLOBAL VARIABLES
*********************************************************************************************************
*/

static  OS_STK  StartupTaskStk[APP_CFG_STARTUP_TASK_STK_SIZE];
static  OS_STK  TaskStk[3][APP_CFG_STARTUP_TASK_STK_SIZE];

OS_EVENT* pevent;

/*
*********************************************************************************************************
*                                         FUNCTION PROTOTYPES
*********************************************************************************************************
*/

static  void  StartupTask (void  *p_arg);


static void periodicTask(void *p_arg);

static void periodicTask2(void *p_arg);
/*
*********************************************************************************************************
*                                                main()
*
* Description : This is the standard entry point for C code.  It is assumed that your code will call
*               main() once you have performed all necessary initialization.
*
* Arguments   : none
*
* Returns     : none
*
* Notes       : none
*********************************************************************************************************
*/

int  main (void)
{
#if OS_TASK_NAME_EN > 0u
    CPU_INT08U  os_err;
#endif


    CPU_IntInit();

    Mem_Init();                                                 /* Initialize Memory Managment Module                   */
    CPU_IntDis();                                               /* Disable all Interrupts                               */
    CPU_Init();                                                 /* Initialize the uC/CPU services                       */

    OSInit();                                                   /* Initialize uC/OS-II                                  */
	
    /*OSTaskCreateExt( StartupTask,                                                       
                     0,
                    &StartupTaskStk[APP_CFG_STARTUP_TASK_STK_SIZE - 1u],
                     APP_CFG_STARTUP_TASK_PRIO,
                     APP_CFG_STARTUP_TASK_PRIO,
                    &StartupTaskStk[0u],
                     APP_CFG_STARTUP_TASK_STK_SIZE,
                     0u,
                    (OS_TASK_OPT_STK_CHK | OS_TASK_OPT_STK_CLR));*/

	INT32S limits[][2] = { //computation , wait time 
	{ 0, 0 },//Prio0
	{ 1, 4 },//Prio1  
	{ 2, 5 },//Prio2 
	{ 2, 10 }//Prio3


	};

	INT8U error;
	// 创建互斥信号量
	pevent = OSMutexCreate(0,&error);
	OSTaskCreate(periodicTask2, (void*)limits[1], &TaskStk[0][APP_CFG_STARTUP_TASK_STK_SIZE - 1u], 1);
	OSTaskCreate(periodicTask, (void*)limits[2], &TaskStk[1][APP_CFG_STARTUP_TASK_STK_SIZE - 1u], 2);
	OSTaskCreate(periodicTask2, (void*)limits[3], &TaskStk[2][APP_CFG_STARTUP_TASK_STK_SIZE - 1u], 3);


/*#if OS_TASK_NAME_EN > 0u
    OSTaskNameSet(         APP_CFG_STARTUP_TASK_PRIO,
                  (INT8U *)"Startup Task",
                           &os_err);
#endif*/
    OSStart();                                                  /* Start multitasking (i.e. give control to uC/OS-II)   */

    while (DEF_ON) {                                            /* Should Never Get Here.                               */
        ;
    }

}

static void periodicTask(void *p_arg) {

	INT32S *p = (INT32S *) p_arg;
	OSTCBCur->compTime = p[0];
	OSTCBCur->period = p[1];
	OSTCBCur->fullCompTime = p[0];

	INT32S start; 
	INT32S end; 
	INT32S toDelay;

	start = 0;

	OS_TRACE_INIT(); // Initialize the uC/OS−II Trace recorder while (DEF TRUE)
	while(DEF_TRUE){

		while (OSTCBCur->compTime > 0){

			//Do nothing

		}

		end = OSTimeGet();
		toDelay = OSTCBCur->period - (end - start);
		toDelay = toDelay < 0 ? 0 : toDelay;
		start += (OSTCBCur->period);

		OSTCBCur->compTime = OSTCBCur->fullCompTime; // reset the computation

		OSTimeDly(toDelay); // delay and wait (fullCompTime − period) times }

	}
}


static void periodicTask2(void *p_arg) {

	INT32S *p = (INT32S *)p_arg;
	OSTCBCur->compTime = p[0];
	OSTCBCur->period = p[1];
	OSTCBCur->fullCompTime = p[0];

	INT8U err;
	INT32S start;
	INT32S end;
	INT32S toDelay;

	start = 0;

	OS_TRACE_INIT(); // Initialize the uC/OS−II Trace recorder while (DEF TRUE)
	while (DEF_TRUE) {
		// 请求互斥信号量
		OSMutexPend(pevent, 0, &err);

		while (OSTCBCur->compTime > 0) {

			//Do nothing

		}

		end = OSTimeGet();
		toDelay = OSTCBCur->period - (end - start);
		toDelay = toDelay < 0 ? 0 : toDelay;
		start += (OSTCBCur->period);

		OSTCBCur->compTime = OSTCBCur->fullCompTime; // reset the computation

		OSTimeDly(toDelay); // delay and wait (fullCompTime − period) times }

		// 归还信号量
		OSMutexPost(pevent);
	}
}
/*
*********************************************************************************************************
*                                            STARTUP TASK
*
* Description : This is an example of a startup task.  As mentioned in the book's text, you MUST
*               initialize the ticker only once multitasking has started.
*
* Arguments   : p_arg   is the argument passed to 'StartupTask()' by 'OSTaskCreate()'.
*
* Returns     : none
*
* Notes       : 1) The first line of code is used to prevent a compiler warning because 'p_arg' is not
*                  used.  The compiler should not generate any code for this statement.
*********************************************************************************************************
*/

static  void  StartupTask (void *p_arg)
{
   (void)p_arg;

    OS_TRACE_INIT();                                            /* Initialize the uC/OS-II Trace recorder               */

#if OS_CFG_STAT_TASK_EN > 0u
    OSStatTaskCPUUsageInit(&err);                               /* Compute CPU capacity with no task running            */
#endif

#ifdef CPU_CFG_INT_DIS_MEAS_EN
    CPU_IntDisMeasMaxCurReset();
#endif
    
    APP_TRACE_DBG(("uCOS-III is Running...\n\r"));

    while (DEF_TRUE) {                                          /* Task body, always written as an infinite loop.       */
        OSTimeDlyHMSM(0u, 0u, 1u, 0u);
		APP_TRACE_DBG(("Time: %d\n\r", OSTimeGet()));
    }
}

