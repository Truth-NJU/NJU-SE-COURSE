
/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                               clock.c
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
                           clock_handler
 *======================================================================*/
PUBLIC void clock_handler(int irq) {
    // 每中断一次，ticks加一
    ticks++;

    if (k_reenter != 0) {
        return;
    }

    schedule();

    if (lines >= 26) {
        u8 *base = (u8 *) V_MEM_BASE;
        for(int i = 0; i < V_MEM_SIZE; i += 2){
            base[i] = ' ';
            base[i + 1] = 0x07;
        }
        disp_pos=0;
        lines = 0;
    }

}

/*======================================================================*
                              milli_delay
 *======================================================================*/
PUBLIC void milli_delay(int milli_sec) {
    // 得到当前总共发生了多少时钟中断
    int t = get_ticks();

    while (((get_ticks() - t) * 1000 / HZ) < milli_sec) {}
}

