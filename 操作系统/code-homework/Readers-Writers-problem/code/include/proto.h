
/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                            proto.h
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                                                    Forrest Yu, 2005
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

#include "proc.h"


/* klib.asm */
PUBLIC void out_byte(u16 port, u8 value);

PUBLIC u8
in_byte(u16
port);

PUBLIC void disp_str(char *info);

PUBLIC void disp_color_str(char *info, int color);

/* protect.c */
PUBLIC void init_prot();

PUBLIC u32
seg2phys(u16
seg);

/* klib.c */
PUBLIC void delay(int time);

/* kernel.asm */
void restart();

/* main.c */
void RA();
void RB();
void RC();
void WD();
void WE();
void CF();
PUBLIC void init(SEMAPHORE* semaphore,int value);
PUBLIC void read(char* p_name,int timeslice_num,int color);
PUBLIC void write(char* p_name,int timeslice_num,int color);

/* i8259.c */
PUBLIC void put_irq_handler(int irq, irq_handler handler);

PUBLIC void spurious_irq(int irq);

/* clock.c */
PUBLIC void clock_handler(int irq);


/* 以下是系统调用相关 */

/* proc.c */
PUBLIC int sys_get_ticks();        /* sys_call */
PUBLIC void sys_disp_str(char *str);
/* syscall.asm */
PUBLIC void sys_call();             /* int_handler */
PUBLIC int get_ticks();
PUBLIC void disp_str_own(char *str);
PUBLIC void sys_delay(int milli_seconds);
PUBLIC void delay_own(int milli_seconds);

// p调用sys_p
PUBLIC void p(SEMAPHORE * semaphore);
// v调用sys_v
PUBLIC void v(SEMAPHORE * semaphore);
PUBLIC void sys_p(SEMAPHORE * semaphore);
PUBLIC void sys_v(SEMAPHORE * semaphore);