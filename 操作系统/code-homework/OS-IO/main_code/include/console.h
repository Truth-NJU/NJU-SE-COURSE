
/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			      console.h
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
						    Forrest Yu, 2005
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

#include <stdio.h>
#include <stdlib.h>

#ifndef _ORANGES_CONSOLE_H_
#define _ORANGES_CONSOLE_H_

/* CONSOLE */
typedef struct s_console {
    unsigned int current_start_addr;    /* 当前显示到了什么位置*/
    unsigned int original_addr;        /* 当前控制台对应显存位置 */
    unsigned int v_mem_limit;        /* 当前控制台占的显存大小 */
    unsigned int cursor;            /* 当前光标位置 */
} CONSOLE;

// 记录用户的操作，方便进行ctrl z，可以看做是ctrl z的缓存池
typedef struct opreation {
    int type; //0 增加 1 删除
    char ch; // 增加或者删除的字符
    int tab; // 0 不是tab 1是tab
} opNode[1000];


void createNode(int type, char ch, int tab);  // 创建一个缓存
void addNode(int type, char ch, int tab);   // 增加一条缓存记录
int getListSize();  // 获得大小
int getType();  // 获得用户操作的类型
char getCh();   // 获得用户操作对应的字符
int getTab();   // 得到是否是tab键
void deleteNode();  // 删除最后一条缓存记录


#define SCR_UP    1    /* scroll forward */
#define SCR_DN    -1    /* scroll backward */

#define SCREEN_SIZE        (80 * 25)
#define SCREEN_WIDTH        80
#define TAB_WIDTH            4

#define DEFAULT_CHAR_COLOR    0x07    /* 0000 0111 黑底白字 */


#endif /* _ORANGES_CONSOLE_H_ */
