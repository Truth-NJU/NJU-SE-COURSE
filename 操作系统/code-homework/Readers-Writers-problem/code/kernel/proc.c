
/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                               proc.c
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                                                    Forrest Yu, 2005
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

#include "type.h"
#include "const.h"
#include "protect.h"
#include "proto.h"
#include "string.h"
#include "global.h"

/*======================================================================*
                              schedule
 *======================================================================*/

// 进程调度
PUBLIC void schedule() {

    while (1) {
        PROCESS *p;

        for (p = p_proc_ready + 1; p < proc_table + NR_TASKS; p++) {
            int t = sys_get_ticks();
            // 进程之前没有需要等待的进程，且进程已经就绪
            if (p->waiting_num == 0 && t >= p->ticks) {
                p_proc_ready = p;
                return;
            }
        }
        int t = sys_get_ticks();
        p_proc_ready = proc_table;
        if (p_proc_ready->waiting_num == 0 && t >= p_proc_ready->ticks) {
            return;
        }
    }
}

/*======================================================================*
                           sys_get_ticks
 *======================================================================*/
PUBLIC int sys_get_ticks() {
    // 时钟中断发生的次数
    return ticks;
}

// 使得进程在 milli_seconds 毫秒内不被分配时间片。
PUBLIC void sys_delay(int milli_seconds){
    // 延迟后进程再次就绪的时刻
    p_proc_ready->ticks= sys_get_ticks()+ milli_seconds / (1000 /HZ);
    schedule();
}

//// 包装disp_str实现成系统调用
//PUBLIC void sys_disp_str(char *str) {
//    disp_str(str);
//}

// 信号量p操作
PUBLIC void sys_p(SEMAPHORE *semaphore) {
    semaphore->value--;
    //当信号量的值小于0，那么就表示没有可用资源，那么进程就只能进行等待其他拥有该资源的进程释放资源之后，才能进行执行
    if (semaphore->value < 0) {
        int tmp = 0 - semaphore->value;
        // 该进程前有多少个正在等待的进程
        p_proc_ready->waiting_num = tmp;

        // 将进程加入等待队列
        pushProcess(semaphore, p_proc_ready);
        // 进行进程调度
        schedule();
    }
    // 当信号量大于0的时候，那么表示还有足够的资源，所以，当前进程就可以继续执行；
}

/**
 * 信号量v操作
 * 当信号量的值大于0，那么就表示有可用资源，那么表示信号量的资源足够进程进行申请，就不需要将进程进行放入到阻塞队列中
 * 而当信号量小于等于0的时候，就表示针对这个信号量，还有其他的进程是已经进行了申请信号量的操作，而只是之前是无法满足进程获取资源的
 * 简单点说，就是表示阻塞队列中还有其他的进程是执行了P操作，在等待信号量，所以，这样的话，就将阻塞队列中的第一个等待信号量的进程进行处理即可；
 * @param semaphore
 * @return
 */
PUBLIC void sys_v(SEMAPHORE *semaphore) {
    semaphore->value++;
    if (semaphore->value <= 0) {
        // 等待队列中的第一个进程取出来
        PROCESS *p = popProcess(semaphore);
        p->waiting_num = 0;
    }
}

// 向等待队列中新增一个线程
PUBLIC void pushProcess(SEMAPHORE *semaphore, PROCESS *process) {
    semaphore->semlist[semaphore->index] = process;
    semaphore->index ++;
}

// 取出等待队列中的第一个线程
PUBLIC PROCESS* popProcess(SEMAPHORE *semaphore){
    SEMAPHORE* temp=semaphore;
    PROCESS *p = temp->semlist[0];
    int location=0;
    // 等待队列中的其余元素全部往前移一个位置
    for(int i= 1;i<=semaphore->index;i++){
        semaphore->semlist[location]=temp->semlist[i];
        location++;
    }
    semaphore->index--;
    return p;
}