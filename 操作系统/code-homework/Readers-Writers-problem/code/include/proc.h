
/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                               proc.h
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                                                    Forrest Yu, 2005
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
#define SEMAPHORE_SIZE 32

// 所有相关寄存器的值（挂起进程前需要保存的进程的状态）
typedef struct s_stackframe {    /* proc_ptr points here				↑ Low			*/
    u32 gs;        /* ┓						│			*/
    u32 fs;        /* ┃						│			*/
    u32 es;        /* ┃						│			*/
    u32 ds;        /* ┃						│			*/
    u32 edi;       /* ┃						│			*/
    u32 esi;        /* ┣ pushed by save()				│			*/
    u32 ebp;        /* ┃						│			*/
    u32 kernel_esp;    /* <- 'popad' will ignore it			│			*/
    u32 ebx;        /* ┃						↑栈从高地址往低地址增长*/
    u32 edx;        /* ┃						│			*/
    u32 ecx;        /* ┃						│			*/
    u32 eax;        /* ┛						│			*/
    u32 retaddr;    /* return address for assembly code save()	│			*/
    u32 eip;        /*  ┓						│			*/
    u32 cs;        /*  ┃						│			*/
    u32 eflags;        /*  ┣ these are pushed by CPU during interrupt	│			*/
    u32 esp;        /*  ┃						│			*/
    u32 ss;        /*  ┛						┷High			*/
} STACK_FRAME;

typedef struct semaphore SEMAPHORE;

// 存放进程的状态，进程表
typedef struct s_proc {
    STACK_FRAME regs;          /* process registers saved in stack frame */

    // ==esp+P_LDT_SEL
    u16 ldt_sel;               /* gdt selector giving ldt base and limit */
    DESCRIPTOR ldts[LDT_SIZE]; /* local descriptors for code and data */

    // 这里原来的ticks的初值是priority，且ticks是递减的
    // 将其的含义当成进程就绪的时间
    int ticks;                 /* remained ticks */

    // 进程优先级
    int priority;

    int waiting_num;    /* 前面有多少个进程正在等待 */

    // 进程号
    u32 pid;                   /* process id passed in from MM */
    // 进程名
    char p_name[16];           /* name of the process */
} PROCESS;

typedef struct s_task {
    task_f initial_eip;
    int stacksize;
    char name[32];
} TASK;


typedef struct semaphore {
    // 当它的值大于0时，表示当前可用资源的数量
    // 当它的值小于0时，其绝对值表示等待使用该资源的进程个数
    int value;            // 数量
    PROCESS* semlist[1000];   // 记录该信号量的等待队列
    int index; // 进程的等待队列index/大小
} SEMAPHORE;


void pushProcess(SEMAPHORE *semaphore,PROCESS *process);  // 向等待队列中新增一个线程
PROCESS* popProcess(SEMAPHORE *semaphore);  // 取出等待队列中的第一个线程


/* Number of tasks */
#define NR_TASKS    6

/* stacks of tasks */
#define STACK_SIZE    0x8000

#define STACK_SIZE_TOTAL    (STACK_SIZE + \
                STACK_SIZE + \
                STACK_SIZE + \
                STACK_SIZE + \
                STACK_SIZE + \
                STACK_SIZE)

