
/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                            global.c
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                                                    Forrest Yu, 2005
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

#define GLOBAL_VARIABLES_HERE

#include "type.h"
#include "const.h"
#include "protect.h"
#include "proto.h"
#include "global.h"


// 进程表，NR_TASKS定义了最大允许进程
PUBLIC	PROCESS			proc_table[NR_TASKS];

PUBLIC	char			task_stack[STACK_SIZE_TOTAL];

PUBLIC	TASK	task_table[NR_TASKS] = {{RA, STACK_SIZE, "ReaderA"},
					{RB, STACK_SIZE, "ReaderC"},
					{RC, STACK_SIZE, "ReaderC"},
                    {WD, STACK_SIZE, "WriterD"},
                    {WE, STACK_SIZE, "WriterE"},
                    {CF, STACK_SIZE, "CommonF"}};

PUBLIC	irq_handler		irq_table[NR_IRQ];

PUBLIC	system_call		sys_call_table[NR_SYS_CALL] = {
        sys_get_ticks,
        sys_disp_str,
        sys_p,
        sys_v,
        sys_delay
};

