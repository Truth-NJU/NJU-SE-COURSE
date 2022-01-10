
/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			      console.c
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
						    Forrest Yu, 2005
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

/*
	回车键: 把光标移到第一列
	换行键: 把光标前进到下一行
*/

#include <stdio.h>
#include <stdlib.h>
#include "type.h"
#include "const.h"
#include "protect.h"
#include "string.h"
#include "proc.h"
#include "tty.h"
#include "console.h"
#include "global.h"
#include "keyboard.h"
#include "proto.h"

PRIVATE void set_cursor(unsigned int position);

PRIVATE void set_video_start_addr(u32 addr);

PRIVATE void flush(CONSOLE *p_con);

PUBLIC int esc_position; // 进入查找模式时的光标的位置

/*======================================================================*
			   init_screen
 *======================================================================*/
PUBLIC void init_screen(TTY *p_tty) {
    int nr_tty = p_tty - tty_table;
    p_tty->p_console = console_table + nr_tty;

    // 初始化全局变量
    search = 0;
    esc_position = 0;
    opIndex = 0;

    int v_mem_size = V_MEM_SIZE >> 1;    /* 显存总大小 (in WORD) */

    int con_v_mem_size = v_mem_size / NR_CONSOLES;
    p_tty->p_console->original_addr = nr_tty * con_v_mem_size;
    p_tty->p_console->v_mem_limit = con_v_mem_size;
    p_tty->p_console->current_start_addr = p_tty->p_console->original_addr;

    /* 默认光标位置在最开始处 */
    p_tty->p_console->cursor = p_tty->p_console->original_addr;


    if (nr_tty == 0) {
        /* 第一个控制台沿用原来的光标位置 */
        p_tty->p_console->cursor = disp_pos / 2;
        disp_pos = 0;
    } else {
        out_char(p_tty->p_console, nr_tty + '0');
        out_char(p_tty->p_console, '#');
    }


    // 清空初始时屏幕上的内容
    // 屏幕上每一个字符对应2个字节，低字对应字符，高字节对应颜色
    u8 *p_vmem = (u8 * )(V_MEM_BASE + p_tty->p_console->cursor * 2);
    int i = 2;
    while (p_tty->p_console->cursor != p_tty->p_console->original_addr) {
        *(p_vmem - i) = ' ';
        // 0x07
        *(p_vmem - i + 1) = DEFAULT_CHAR_COLOR;
        i += 2;
        p_tty->p_console->cursor--;
    }
    flush(p_tty->p_console);


    // set_cursor(p_tty->p_console->cursor);
}


/*======================================================================*
			   is_current_console
*======================================================================*/
PUBLIC int is_current_console(CONSOLE *p_con) {
    return (p_con == &console_table[nr_current_console]);
}


/*======================================================================*
			   out_char
 *======================================================================*/
// 往控制台上输出内容
PUBLIC void out_char(CONSOLE *p_con, char ch) {
    // 获得当前显示位置的地址
    // 直接将字符写到特定地址
    u8 *p_vmem = (u8 * )(V_MEM_BASE + p_con->cursor * 2);
    // esc模式下屏蔽所有其他的输入
    if (search == 2 && ch != '\1') return;
    int tab = 0; // 1代表有tab键，0代表没有tab键
    int delete=0; // 0可以删除，1不可以删除
    switch (ch) {
        // 回车
        case '\n':
            // 正常模式
            if (search == 0) {
                // 设置光标位置
                if (p_con->cursor < p_con->original_addr +
                                    p_con->v_mem_limit - SCREEN_WIDTH) {
                    *p_vmem++ = '\n';
                    *p_vmem++ = 0x00;
                    p_con->cursor = p_con->original_addr + SCREEN_WIDTH *
                                                           ((p_con->cursor - p_con->original_addr) /
                                                            SCREEN_WIDTH + 1);
                    // 增加用户的操作记录
                    addNode(0, '\n', 0);
                }
            } else {
                // 在查找模式中输入字符后按下回车，知道再一次按下ESC前不会再接受其他输入
                if (search == 1) {
                    // 判断要查找的字符是什么，查找后将相应的字符变为红色。
                    int len = p_con->cursor - esc_position; // 获得字符串的长度
                    // 指针
                    u8 *curCharPoint = (u8 * )(V_MEM_BASE);
                    // 遍历之前输入的字符串，找出匹配的字符
                    for (int i = 0; i <= (esc_position - p_con->original_addr - len);
                         i++) {
                        // 首字母相匹配
                        if (*(curCharPoint + i * 2) == *(curCharPoint + esc_position * 2)) {
                            int match = 1;  // 1代表匹配，0代表不匹配
                            // 继续比较后len-1个字符
                            for (int j = 1; j < len; j++) {
                                if (*(curCharPoint + i * 2 + j * 2) != *(curCharPoint + esc_position * 2 + j * 2)) {
                                    match = 0;
                                    break;
                                }
                            }
                            // 匹配，颜色变为红色
                            if (match == 1) {
                                for (int j = 0; j < len; j++) {
                                    // 将该字符颜色变为红色,'\t'除外
                                    if(*(curCharPoint + i * 2 + j * 2)!='\t')
                                        *(curCharPoint + i * 2 + j * 2 + 1) = 0x0c;
                                }
                            }
                        }
                    }
                    search = 2;
                }
            }
            break;
        case '\b':
            // 0可以删除，1不可以删除
            if (search == 1) {
                // 查找模式下不能无限制的删除
                if (p_con->cursor <= esc_position) {
                    delete = 1;
                }

            }
            if (delete == 0) {
                // 后退
                // 检查是否是tab键
                if (*(p_vmem - 8) == '\t') {
                    for (int i = 2; i <= 6; i += 2) {
                        if (*(p_vmem - i) != ' ') {
                            tab = 0;
                            break;
                        }
                    }
                    tab = 1;
                }
                // 不是tab键
                if (tab == 0) {
                    if (p_con->cursor > p_con->original_addr) {
                        if (p_con->cursor % SCREEN_WIDTH == 0) {
                            // 判断是否是回车键
                            // 若存在回车键需要一直删除，直到得到\n的前一个字符
                            int i = 2;
                            while (*(p_vmem - i) == ' ' && *(p_vmem - i) != '\n') {
                                p_con->cursor--;
                                i += 2;
                            }
                            *(p_vmem - i) = ' ';
                            *(p_vmem - i + 1) = 0x07;
                            p_con->cursor--;
                            // 增加用户的操作记录
                            addNode(1, '\n', 0);
                        } else {
                            // 增加用户的操作记录
                            addNode(1, *(p_vmem - 2), 0);
                            // 没有回车键，删除前面一个字符即可
                            *(p_vmem - 2) = ' ';
                            *(p_vmem - 1) = 0x07;
                            p_con->cursor--;
                        }
                    }
                } else {
                    // 是tab键,需要一次删除四个空格。
                    for (int i = 1; i <= 4; i++) {
                        if (p_con->cursor > p_con->original_addr) {
                            u8 *p_vmem_tmp = (u8 * )(V_MEM_BASE + p_con->cursor * 2);
                            *(p_vmem_tmp - 2) = ' ';
                            *(p_vmem_tmp - 1) = DEFAULT_CHAR_COLOR;
                            p_con->cursor--;
                        }
                    }
                    // 增加用户的操作记录
                    addNode(1, '\t', 1);
                }
            }
            break;
        case '\1': // esc
            if (search == 0) {
                // 进入查找模式
                search = 1;
                esc_position = p_con->cursor;
            } else if (search == 2) {
                // 退出查找模式
                search = 0;
                // 将所有字符的颜色变为白色
                int i;
                // 从 p_con->original_addr一直到点击esc时的光标位置的字符都需要变成白色（黑底白字）
                for (i = p_con->original_addr; i <= esc_position; i++) {
                    u8 *p_vmem_tmp = (u8 * )(V_MEM_BASE + i * 2);
                    // 回车和空格不需要进行改变
                    if (*(p_vmem_tmp - 2) != '\t' && *(p_vmem_tmp - 2) != '\n')
                        *(p_vmem_tmp - 1) = DEFAULT_CHAR_COLOR;
                }

                // 删除刚刚在搜索模式下输入的字符
                while (p_con->cursor > esc_position) {
                    u8 *p_vmem_tmp = (u8 * )(V_MEM_BASE + p_con->cursor * 2);
                    p_con->cursor--;
                    *(p_vmem_tmp - 2) = ' ';
                    *(p_vmem_tmp - 1) = DEFAULT_CHAR_COLOR;
                }
            }
            break;
        case '\t': // tab
            if (p_con->cursor >= p_con->original_addr) {
                // 相当于输出四个空格
                *p_vmem++ = '\t';
                *p_vmem++ = 0x00;
                p_con->cursor++;
                int i = 1;
                while (i < TAB_WIDTH) {
                    *p_vmem++ = ' ';
                    *p_vmem++ = 0x00;
                    p_con->cursor++;
                    i++;
                }
            }
            addNode(0, '\t', 1);
            break;
        case '\2': // ctrl+z
            if (search == 1) {
                // 查找模式下不能无限制的删除
                if (p_con->cursor <= esc_position) {
                    delete = 1;
                }

            }
            if (delete == 0) {
                // 若记录用户操作的列表为空，代表已经回到初始状态，不需要再做操作
                if (getListSize() != 0) {
                    // 上一个操作是增加字符的操作，需要对应的删除
                    if (getType() == 0) {
                        // 不是tab键
                        if (getTab() != 1) {
                            if (p_con->cursor > p_con->original_addr) {
                                // 存在回车键
                                if (p_con->cursor % SCREEN_WIDTH == 0) {
                                    // 若存在回车键需要一直删除，直到得到\n的前一个字符
                                    int i = 2;
                                    while (*(p_vmem - i) == ' ' && *(p_vmem - i) != '\n') {
                                        p_con->cursor--;
                                        i += 2;
                                    }
                                    *(p_vmem - i) = ' ';
                                    *(p_vmem - i + 1) = 0x07;
                                    p_con->cursor--;
                                } else {
                                    // 不是回车键，只要删除前一个字符即可
                                    *(p_vmem - 2) = ' ';
                                    *(p_vmem - 1) = 0x07;
                                    p_con->cursor--;
                                }
                            }
                        } else {
                            // 是tab键,需要一次删除四个空格。
                            for (int i = 1; i <= 4; i++) {
                                if (p_con->cursor > p_con->original_addr) {
                                    u8 *p_vmem_tmp = (u8 * )(V_MEM_BASE + p_con->cursor * 2);
                                    *(p_vmem_tmp - 2) = ' ';
                                    *(p_vmem_tmp - 1) = DEFAULT_CHAR_COLOR;
                                    p_con->cursor--;
                                }
                            }
                        }
                    } else {// 上一个操作是删除字符的操作，需要对应的增加
                        if (getTab() == 1) { // 是tab键，需要一次输出四个空格
                            if (p_con->cursor >= p_con->original_addr) {
                                *p_vmem++ = '\t';
                                *p_vmem++ = 0x00;
                                p_con->cursor++;
                                int i = 1;
                                while (i < TAB_WIDTH) {
                                    *p_vmem++ = ' ';
                                    *p_vmem++ = 0x00;
                                    p_con->cursor++;
                                    i++;
                                }
                            }
                        } else {
                            // 若是回车键，需要输出回车
                            if (getCh() == '\n') {
                                if (p_con->cursor < p_con->original_addr +
                                                    p_con->v_mem_limit - SCREEN_WIDTH) {
                                    *p_vmem++ = '\n';
                                    *p_vmem++ = 0x00;
                                    p_con->cursor = p_con->original_addr + SCREEN_WIDTH *
                                                                           ((p_con->cursor - p_con->original_addr) /
                                                                            SCREEN_WIDTH + 1);
                                }
                            } else { // 正常的字符输出
                                *p_vmem++ = getCh();
                                if(search!=1) {
                                    *p_vmem++ = DEFAULT_CHAR_COLOR;
                                }else{
                                    // 查找模式下撤回需要红色字体
                                    *p_vmem++ = 0x0c;
                                }
                                p_con->cursor++;
                            }
                        }
                    }
                    deleteNode(); // 删除最后一个已经被ctrl+z的操作
                }
            }
            break;
        default:
            if (search == 1) {
                // 若为搜索模式，输出需要变为红色
                if (p_con->cursor <
                    p_con->original_addr + p_con->v_mem_limit - 1) {
                    *p_vmem++ = ch;
                    *p_vmem++ = 0x0c; // 黑底红字
                    p_con->cursor++;
                    addNode(0, ch, 0);
                }
            } else {
                // 正常颜色输出
                if (p_con->cursor <
                    p_con->original_addr + p_con->v_mem_limit - 1) {
                    *p_vmem++ = ch;
                    *p_vmem++ = DEFAULT_CHAR_COLOR;
                    p_con->cursor++;
                    addNode(0, ch, 0);
                }
            }
            break;
    }

    while (p_con->cursor >= p_con->current_start_addr + SCREEN_SIZE) {
        scroll_screen(p_con, SCR_DN);
    }

    flush(p_con);
}

/*======================================================================*
                           flush
*======================================================================*/
PRIVATE void flush(CONSOLE *p_con) {
    set_cursor(p_con->cursor);
    set_video_start_addr(p_con->current_start_addr);
}

/*======================================================================*
			    set_cursor
 *======================================================================*/
PRIVATE void set_cursor(unsigned int position) {
    disable_int();
    out_byte(CRTC_ADDR_REG, CURSOR_H);
    out_byte(CRTC_DATA_REG, (position >> 8) & 0xFF);
    out_byte(CRTC_ADDR_REG, CURSOR_L);
    out_byte(CRTC_DATA_REG, position & 0xFF);
    enable_int();
}

/*======================================================================*
			  set_video_start_addr
 *======================================================================*/
PRIVATE void set_video_start_addr(u32 addr) {
    disable_int();
    out_byte(CRTC_ADDR_REG, START_ADDR_H);
    out_byte(CRTC_DATA_REG, (addr >> 8) & 0xFF);
    out_byte(CRTC_ADDR_REG, START_ADDR_L);
    out_byte(CRTC_DATA_REG, addr & 0xFF);
    enable_int();
}


/*======================================================================*
			   select_console 切换控制台
 *======================================================================*/
PUBLIC void select_console(int nr_console)    /* 0 ~ (NR_CONSOLES - 1) */
{
    if ((nr_console < 0) || (nr_console >= NR_CONSOLES)) {
        return;
    }

    nr_current_console = nr_console;

    set_cursor(console_table[nr_console].cursor);
    set_video_start_addr(console_table[nr_console].current_start_addr);
}

/*======================================================================*
			   scroll_screen
 *----------------------------------------------------------------------*
 滚屏.
 *----------------------------------------------------------------------*
 direction:
	SCR_UP	: 向上滚屏
	SCR_DN	: 向下滚屏
	其它	: 不做处理
 *======================================================================*/
PUBLIC void scroll_screen(CONSOLE *p_con, int direction) {
    if (direction == SCR_UP) {
        if (p_con->current_start_addr > p_con->original_addr) {
            p_con->current_start_addr -= SCREEN_WIDTH;
        }
    } else if (direction == SCR_DN) {
        if (p_con->current_start_addr + SCREEN_SIZE <
            p_con->original_addr + p_con->v_mem_limit) {
            p_con->current_start_addr += SCREEN_WIDTH;
        }
    } else {
    }

    set_video_start_addr(p_con->current_start_addr);
    set_cursor(p_con->cursor);
}


// 清空屏幕上的内容
PUBLIC void clear_screen(CONSOLE *p_con) {
    // 屏幕上每一个字符对应2个字节，低字对应字符，高字节对应颜色
    u8 *p_vmem = (u8 * )(V_MEM_BASE + p_con->cursor * 2);
    int i = 2;
    while (p_con->cursor != p_con->original_addr) {
        *(p_vmem - i) = ' ';
        *(p_vmem - i + 1) = DEFAULT_CHAR_COLOR;
        i += 2;
        p_con->cursor--;
    }
    flush(p_con);
}


void createNode(int type, char ch, int tab) {
    op[opIndex].type = type;
    op[opIndex].ch = ch;
    op[opIndex].tab = tab;
}

void addNode(int type, char ch, int tab) {
    opIndex++;
    createNode(type, ch, tab);
}// 增加节点


int getListSize() {
    return opIndex;
}// 获取大小


// 获得最后一个节点的type
int getType() {
    return op[opIndex].type;
}

//获得最后一个节点的字符
char getCh() {
    return op[opIndex].ch;
}

// 用来判断是否是tab键
int getTab() {
    return op[opIndex].tab;
}

// 删除最后一个节点
void deleteNode() {
    opIndex--;
}
