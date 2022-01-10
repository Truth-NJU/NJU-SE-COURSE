
/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                            main.c
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                                                    Forrest Yu, 2005
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

#include "type.h"
#include "const.h"
#include "protect.h"
#include "proto.h"
#include "string.h"
#include "global.h"
#include <stdlib.h>
#include <stdio.h>

// 同时读一本书的阅读者数量上限
#define READER_NUM_MAX    2
// 时间片
#define TIME    400000 / HZ
// -1读者优先 0读写公平 1写者优先
#define READER_OR_WRITER   1


// 读者优先
SEMAPHORE rw; // 用于实现对共享数据的互斥访问，表示当前是否有进程在访问数据
SEMAPHORE mutexr; // 用于保证对count变量的互斥访问
SEMAPHORE w; // 解决进程饿死问题
int count;// 当前有几个读进程在访问文件


// 写者优先(用到read,mutex,writeCountSignal,rw信号量)
int writeCount; // 当前有几个写进程在访问文件
SEMAPHORE readsem; // 实现写者到来时能够打断读者进程
SEMAPHORE mutexw; // 完成对writeCount计数器资源的互斥访问
SEMAPHORE z;


/*======================================================================*
                            kernel_main
 *======================================================================*/
PUBLIC int kernel_main() {
    disp_str("-----\"kernel_main\" begins-----\n");

    TASK *p_task = task_table;
    PROCESS *p_proc = proc_table;
    char *p_task_stack = task_stack + STACK_SIZE_TOTAL;
    u16 selector_ldt = SELECTOR_LDT_FIRST;
    int i;
    for (i = 0; i < NR_TASKS; i++) {
        // 初始化进程表
        strcpy(p_proc->p_name, p_task->name);    // name of the process
        p_proc->pid = i;            // pid

        p_proc->ldt_sel = selector_ldt;

        memcpy(&p_proc->ldts[0], &gdt[SELECTOR_KERNEL_CS >> 3],
               sizeof(DESCRIPTOR));
        p_proc->ldts[0].attr1 = DA_C | PRIVILEGE_TASK << 5;
        memcpy(&p_proc->ldts[1], &gdt[SELECTOR_KERNEL_DS >> 3],
               sizeof(DESCRIPTOR));
        p_proc->ldts[1].attr1 = DA_DRW | PRIVILEGE_TASK << 5;
        p_proc->regs.cs = ((8 * 0) & SA_RPL_MASK & SA_TI_MASK)
                          | SA_TIL | RPL_TASK;
        p_proc->regs.ds = ((8 * 1) & SA_RPL_MASK & SA_TI_MASK)
                          | SA_TIL | RPL_TASK;
        p_proc->regs.es = ((8 * 1) & SA_RPL_MASK & SA_TI_MASK)
                          | SA_TIL | RPL_TASK;
        p_proc->regs.fs = ((8 * 1) & SA_RPL_MASK & SA_TI_MASK)
                          | SA_TIL | RPL_TASK;
        p_proc->regs.ss = ((8 * 1) & SA_RPL_MASK & SA_TI_MASK)
                          | SA_TIL | RPL_TASK;
        p_proc->regs.gs = (SELECTOR_KERNEL_GS & SA_RPL_MASK)
                          | RPL_TASK;

        p_proc->regs.eip = (u32) p_task->initial_eip;
        p_proc->regs.esp = (u32) p_task_stack;
        p_proc->regs.eflags = 0x1202; /* IF=1, IOPL=1 */

        p_proc->waiting_num = 0;
        p_proc->ticks = 0;

        p_task_stack -= p_task->stacksize;
        p_proc++;
        p_task++;
        selector_ldt += 1 << 3;
    }

//    proc_table[0].ticks = proc_table[0].priority = 15;
//    proc_table[1].ticks = proc_table[1].priority = 5;
//    proc_table[2].ticks = proc_table[2].priority = 4;
//    proc_table[0].ticks = proc_table[3].priority = 3;
//    proc_table[1].ticks = proc_table[4].priority = 2;
//    proc_table[2].ticks = proc_table[5].priority = 1;

    // 初始化信号量
    init(&rw, 1); // 实现对共享数据的互斥访问
    init(&mutexr, READER_NUM_MAX); // 实现对读者计数器的互斥访问
    init(&w, 1); // 解决写进程饿死问题
    init(&readsem, 1); // 写者优先（写者到来打断读者进程）
    init(&mutexw, 1); // 对写者计数器的互斥访问
    init(&z, 1);

    count = 0;
    writeCount = 0;

    k_reenter = 0;
    ticks = 0;
    lines = 0;
    p_proc_ready = proc_table;

    // 清屏
    disp_pos = 0;
    for (int i = 0; i < 80 * 25; i++) {
        // 输出黑底黑字，相当于清屏
        disp_color_str(" ", 0x00);

    }
    lines = 0;
    disp_pos = 0;

    /* 初始化 8253 PIT */
    out_byte(TIMER_MODE, RATE_GENERATOR);
    out_byte(TIMER0, (u8)(TIMER_FREQ / HZ));
    out_byte(TIMER0, (u8)((TIMER_FREQ / HZ) >> 8));

    put_irq_handler(CLOCK_IRQ, clock_handler); /* 设定时钟中断处理程序 */
    enable_irq(CLOCK_IRQ);                     /* 让8259A可以接收时钟中断 */

    restart();

    while (1) {}
}


PUBLIC void init(SEMAPHORE *semaphore, int value) {
    semaphore->value = value;
    semaphore->index = 0;
}


PUBLIC void read(char *p_name, int timeslice_num, int color) {
    // 读者优先
    if(READER_OR_WRITER == -1){
        while (1){
            // 申请读者计数器资源
            p(&mutexr);
            if (count == 0) {
                p(&rw); // 读之前加锁
            }
            count++;

            // 开始读
            disp_color_str(p_name, color);
            disp_color_str(" starts reading", color);
            disp_str_own("\n");
            lines++;
            // 正在读
            disp_color_str(p_name, color);
            disp_color_str(" is reading", color);
            disp_str_own("\n");
            lines++;
            milli_delay(timeslice_num * TIME);
            // 结束读
            disp_color_str(p_name, color);
            disp_color_str(" finishes reading", color);
            disp_str_own("\n");
            lines++;

            count--;

            // 表示当前没有进程在访问共享数据
            if (count == 0) {
                v(&rw); // 读之后解锁
            }
            v(&mutexr);
        }
    }
    // 读写公平/解决写进程饿死问题
    else if (READER_OR_WRITER == 0) {
        while (1) {
            // 防止写进程饿死，可以看做读写公平
            p(&w);
            // 申请读者计数器资源
            p(&mutexr);
            if (count == 0) {
                p(&rw); // 读之前加锁
            }
            count++;
            v(&w);

            // 开始读
            disp_color_str(p_name, color);
            disp_color_str(" starts reading", color);
            disp_str_own("\n");
            lines++;
            // 正在读
            disp_color_str(p_name, color);
            disp_color_str(" is reading", color);
            disp_str_own("\n");
            lines++;
            milli_delay(timeslice_num * TIME);
            // 结束读
            disp_color_str(p_name, color);
            disp_color_str(" finishes reading", color);
            disp_str_own("\n");
            lines++;

            count--;

            // 表示当前没有进程在访问共享数据
            if (count == 0) {
                v(&rw); // 读之后解锁
            }
            v(&mutexr);
        }
    } else {
        // 写者优先
        while (1) {
           // p(&z);
            // 申请令牌（readsem只有在写者队列为空时才可以申请到，这就确保写者优先）
            p(&readsem);
            // 申请读者计数器
            p(&mutexr);
            if (count == 0) {
                // 若读者队列为空，申请资源
                p(&rw); // 读之前加锁
            }
            count++;
            // 释放令牌
            v(&readsem);
            // v(&z);

            // 开始读
            disp_color_str(p_name, color);
            disp_color_str(" starts reading", color);
            disp_str_own("\n");
            lines++;
            // 正在读
            disp_color_str(p_name, color);
            disp_color_str(" is reading", color);
            disp_str_own("\n");
            lines++;
            milli_delay(timeslice_num * TIME);
            // 结束读
            disp_color_str(p_name, color);
            disp_color_str(" finishes reading", color);
            disp_str_own("\n");
            lines++;

            count--;   // 表示当前没有进程在访问共享数据
            if (count == 0) {
                v(&rw); // 读之后解锁
            }
            v(&mutexr);
        }
    }
}


PUBLIC void write(char *p_name, int timeslice_num, int color) {
    // 读者优先
    if(READER_OR_WRITER == -1){
        while (1) {
            // 申请访问共享资源，只有没有读者在读或其它写者在写的时候才可以拿到资源
            p(&rw);

            // 开始写
            disp_color_str(p_name, color);
            disp_color_str(" starts writing", color);
            disp_str_own("\n");
            lines++;
            // 正在写
            disp_color_str(p_name, color);
            disp_color_str(" is writing", color);
            disp_str_own("\n");
            lines++;
            milli_delay(timeslice_num * TIME);
            // 结束写
            disp_color_str(p_name, color);
            disp_color_str(" finishes writing", color);
            disp_str_own("\n");
            lines++;

            v(&rw);
        }
    }
    // 读写公平
    else if (READER_OR_WRITER == 0) {
        while (1) {
            // 申请w令牌
            p(&w);
            // 申请访问共享资源，只有没有读者在读或其它写者在写的时候才可以拿到资源
            p(&rw);

            // 开始写
            disp_color_str(p_name, color);
            disp_color_str(" starts writing", color);
            disp_str_own("\n");
            lines++;
            // 正在写
            disp_color_str(p_name, color);
            disp_color_str(" is writing", color);
            disp_str_own("\n");
            lines++;
            milli_delay(timeslice_num * TIME);
            // 结束写
            disp_color_str(p_name, color);
            disp_color_str(" finishes writing", color);
            disp_str_own("\n");
            lines++;

            v(&rw);
            v(&w);
        }
    } else {
        // 写者优先
        while (1) {
            //p(&z);
            // 申请写者计数器，同一时刻只能有一位读者在写
            p(&mutexw);
            // 写者队列为空，申请令牌readsem
            if (writeCount == 0) {
                p(&readsem);
            }
            writeCount++;
            v(&mutexw);
            // 申请文件资源
            p(&rw);

            // 开始写
            disp_color_str(p_name, color);
            disp_color_str(" starts writing", color);
            disp_str_own("\n");
            lines++;
            // 正在写
            disp_color_str(p_name, color);
            disp_color_str(" is writing", color);
            disp_str_own("\n");
            lines++;
            milli_delay(timeslice_num * TIME);
            // 结束写
            disp_color_str(p_name, color);
            disp_color_str(" finishes writing", color);
            disp_str_own("\n");
            lines++;

            // 释放文件资源
            v(&rw);
            p(&mutexw);
            writeCount--;
            // 写者队列为空释放令牌readsem
            if (writeCount == 0) {
                v(&readsem);
            }
            // 释放写者计数器资源
            v(&mutexw);
            //v(&z);
        }

    }
}

/*======================================================================*
                               ReaderA
 *======================================================================*/
void RA() {
    read("ReaderA", 2, 0x0c);

}

/*======================================================================*
                               ReaderB
 *======================================================================*/
void RB() {
    read("ReaderB", 3, 0x0F);
}

/*======================================================================*
                               ReaderC
 *======================================================================*/
void RC() {
    read("ReaderC", 3, 0x02);
}


/*======================================================================*
                               WriterD
 *======================================================================*/
void WD() {
    write("WriterD", 3, 0x03);
}

/*======================================================================*
                               WriterE
 *======================================================================*/
void WE() {
    write("WriterE", 4, 0x06);
}

/*======================================================================*
                               CommonF
 *======================================================================*/
void CF() {
    while (1) {
        if (count > 0 && count <= READER_NUM_MAX) {
            disp_color_str("F:", 0x09);
            // 将数字转为字符串
            char *str = "0";
            str[0] = (char) ('0' + count);
            if (count > 1) {
                disp_color_str(str, 0x09);
                disp_color_str(" readers are reading", 0x09);
                disp_str_own("\n");
                lines++;
            } else {
                disp_color_str(str, 0x09);
                disp_color_str(" reader is reading", 0x09);
                disp_str_own("\n");
                lines++;
            }
        } else {
            disp_color_str("F:", 0x0E);
            disp_color_str("writing...", 0x0E);
            disp_str_own("\n");
            lines++;
        }
        delay_own(1 * TIME);
    }
}