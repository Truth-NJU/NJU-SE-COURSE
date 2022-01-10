# Readers-Writers-problem
添加系统调用实现信号量PV操作，模拟读者写者问题

## 功能要求

### 1. 时间片

添加系统调用，其接受 int 型参数 milli_seconds，调用此方法进程会在 milli_seconds 毫秒内不被分配时间片。

### 2. 打印字符串

添加系统调用 打印字符串 ，接受 char\* 型参数 str 

– 注意，在 kliba.asm 文件中有了 disp_str 函数显示字符串，但这是内核函数。请实现并包装成相应的系统调用。

### 3. 读者写者问题

添加两个系统调用执行信号量 **PV** 操作 ，在此基础上模拟读者写者问题 。

- 共有 6 个一直存在的进程(循环读写操作)，A、B、C 为读者进程，D、E 为写者 进程，F 为普通进程，其中

  - A 阅读消耗 2 个时间片
  - B、C 阅读消耗 3 个时间片
  - D 写消耗 3 个时间片
  - E 写消耗 4 个时间片

- 读者在读的时候，写者不能写，必须等到全部读者读完

- 同时只能一个作者在写

- 在写的时候，读者不能读

- 多个读者可以读一本书，但是不能太多，上限数字有 1、2、3，需要都能够支持，

  并且可以现场修改

- A、B、C、D、E 进程需要 彩色 打印基本操作:读开始、写开始、读、写、读完成、写完成，以及对应进程名字

- F 每隔 1 个时间片打印当前是读还是写，如果是读有多少人

- 请分别实现 读者优先 和 写者优先 ，需要都能够支持

- 解决此问题中部分情况下的进程饿死问题



# 1.通过系统调用完成delay方法

将kernel.asm中的sys_call方法进行修改，添加`push ebx`和`pop ebx`将参数压栈。

```
sys_call:
        call    save
        sti
        ; 调用sys_call_table[eax],sys_call_table是一个函数指针数组，每一个成员指向一个函数
        ; sys_call_table的定义在global.c中
        push    ebx
        call    [sys_call_table + eax * 4]
        ; 将调用的函数sys_call_table[eax]的返回值存放在eax中，返回出去
        mov     [esi + EAXREG - P_STACKBASE], eax
        pop     ebx

        cli

        ret
```

在syscall.asm中添加如下代码，完成系统调用。

1. delay_own方法会调用sys_call_table中的（在global.c）第_NR_delay（这里为4，`_NR_delay           equ 4`）个方法，即调用sys_delay方法。delay_own方法使用`mov ebx, [esp+8]`语句将传进来的int型参数压栈，方便后续方法的调用。
2. 使用`global  delay_own`语句导出函数供外部调用。

```
; 不分配时间片的延迟函数
delay_own:
   mov eax, _NR_delay
   push ebx
   ; 传递参数
   mov ebx,[esp+8]
   int INT_VECTOR_SYS_CALL
   pop ebx
   ret
```

**sys_delay函数**的实现如下：

1. proc.h中PROCESS结构体中原来的ticks的初值是priority，且ticks是递减的。我们这儿对其重新释义，将其变为当前进程就绪的时刻。

2. 将clock.c中clock_handler方法中的与`p_proc_ready->ticks`有关的语句删除

3. 在main.c中的kernel_main方法中的循环中进行初始化`p_proc->ticks = 0;`

4. sys_delay函数主要代码如下，不为进程分配时间片，而是直接修改延迟后进程再次就绪的时刻

   ```c
   // 延迟后进程再次就绪的时刻
   p_proc_ready->ticks= sys_get_ticks()+ milli_seconds / (1000 /HZ);
   schedule();
   ```

5. 修改schedule方法，每次循环遍历进程表，找到满足条件的进程，该之前已经没有需要等待的进程，且该进程已经就绪（即当前的ticks大于等于该进程的ticks）。

   ```c
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
   ```

# 2.通过系统调用打印字符串

在syscall.asm中添加如下代码，完成系统调用。

1. disp_str_own方法会调用sys_call_table中的（在global.c）第_NR_disp_str（这里为1）个方法，即调用sys_disp_str方法。
2. disp_str_own方法使用`mov ebx, [esp+8]`语句将传进来的char*型参数str压栈，方便后续方法的调用。
3. sys_disp_str会调用kliba.asm文件中实现的disp_str方法来完成字符串的打印。

```
; 打印字符串,调用sys_disp_str函数
disp_str_own:
    mov    eax, _NR_disp_str
    ; 将参数压栈到ebx中，方便后续调用
    push ebx
    mov ebx, [esp+8]
    int    INT_VECTOR_SYS_CALL
    pop ebx
    ret

sys_disp_str:
    pusha
    ; 传递参数
    push ebx
    call disp_str
    pop ebx
    popa
    ret
```

sys_call_table的结构如下：

![img](img/1.png)

syscall.asm的头部需要添加导入导出和相应的声明：

```
extern disp_str
_NR_disp_str        equ 1
global  disp_str_own
global  sys_disp_str
```

需要在proto.h中添加如下函数的声明：

```c
PUBLIC void sys_disp_str(char *str);
PUBLIC void disp_str_own(char *str);
```

# 3.读者写者问题

## 3.1 添加系统调用执行信号量PV操作

1. 在proc.h中添加信号量的数据结构SEMAPHORE。value>0时代表该信号量当前可用资源的数量，value<0时代表等待使用该资源的进程个数。semlist记录了该信号量的进程等待队列。index是semlist的指针，指向队列的当前位置。

   ```c
   typedef struct semaphore {
       // 当它的值大于0时，表示当前可用资源的数量
       // 当它的值小于0时，其绝对值表示等待使用该资源的进程个数
       int value;            // 数量
       PROCESS* semlist[1000];   // 记录该信号量的等待队列
       int index; // 进程的等待队列index/大小
   } SEMAPHORE;
   ```

2. 在proc.c中的PROCESS结构体中增加成员waiting_num，表示该进程前有多少个正在等待的进程。

   ```c
   int waiting_num;    /* 前面有多少个进程正在等待 */
   ```

3. 在proc.h中声明以下两个函数并在proc.c中实现它们，pushProcess向信号量的等待队列中新增一个进程，popProcess取出信号量等待队列中的第一个线程。

   ```c
   void pushProcess(SEMAPHORE *semaphore,PROCESS *process);  // 向等待队列中新增一个线程
   PROCESS* popProcess(SEMAPHORE *semaphore);  // 取出等待队列中的第一个线程
   ```

   ```c
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
   ```

4. 在syscall.asm中添加如下代码，完成系统调用执行PV操作。

   ```
   p:
   	mov eax, _NR_p
   	; 传递参数
   	push ebx
   	mov ebx, [esp+8]
   	; 调用sys_p
   	int INT_VECTOR_SYS_CALL
   	pop ebx
   	ret
   
   
   v:
   	mov eax, _NR_v
   	; 传递参数
   	push ebx
   	mov ebx, [esp+8]
   	; 调用sys_v
   	int INT_VECTOR_SYS_CALL
   	pop ebxc
   	ret
   ```

   p方法会调用sys_p方法，sys_p方法的实现如下。首先将信号量的值减1，此时若信号量的值小于0，那么就表示没有可用资源，那么进程就只能进行等待其他拥有该资源的进程释放资源之后，才能进行执行。需要将该进程加入信号量的等待队列，随后执行调度算法。 当信号量大于0的时候，那么表示还有足够的资源，当前进程就可以继续执行。

   ```c
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
   ```

   v方法会调用sys_v方法，sys_v方法的实现如下。将信号量的值加1，此时若信号量的值小于等于0，表示等待队列中仍有进程在等待执行，将该进程取出并将其waiting_num设为0，接下去该进程就可以被执行。 

   ```c
   PUBLIC void sys_v(SEMAPHORE *semaphore) {
       semaphore->value++;
       if (semaphore->value <= 0) {
           // 等待队列中的第一个进程取出来
           PROCESS *p = popProcess(semaphore);
           p->waiting_num = 0;
       }
   }
   ```

   这些方法都需要在proto.h中声明：

   <img src="img/2.png" alt="img" style="zoom:50%; float:left;" />

5. 进程调度算法实现如下，每次都是找到一个waiting_num为0的进程去执行，也就是选择不需要等待信号量的进程去执行。

   ```c
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
   ```

## 3.2 模拟读者写者问题

1. 需要添加6个进程，A、B、C 为读者进程，D、E 为写者进程，F 为普通进程。在global.c中对task_table进行修改

   ![img](img/3.png)

2. 将proc.h中NR_TASKS的值改为6

3. 在main.c中定义需要使用的全局变量。

   - READER_NUM_MAX表示同时读一本书的读者数量的上限，方便修改
   - TIME代表一个时间片的大小，这里为了输出能够清楚的展示，调大了时间片的值。
   - READER_OR_WRITER表示当前是读者优先还是写者优先。
   - w信号量的作用是防止写进程饿死，可以实现读写公平。
   - count表示当前有几个读者进程在读。
   - 读者计数器资源信号量mutexr用来控制读者计数器count。
   - 信号量rw用来控制对临界区资源的访问，可以用来表示当前是否有进程在访问数据。
   - writeCount表示当前有几个写者进程在写。
   - 写者计数器资源信号量mutexw用来控制写者计数器writeCount。
   - readsem用来实现写者到来时能够打断读者进程

   ```c
   // 同时读一本书的阅读者数量上限
   #define READER_NUM_MAX    1
   // 时间片
   #define TIME    400000 / HZ
   // -1读者优先 0读写公平 1写者优先
   #define READER_OR_WRITER    0
   
   // 读者优先
   SEMAPHORE rw; // 用于实现对共享数据的互斥访问，表示当前是否有进程在访问数据
   SEMAPHORE mutexr; // 用于保证对count变量的互斥访问
   SEMAPHORE w; // 解决进程饿死问题
   int count;// 当前有几个读进程在访问文件
   
   
   // 写者优先(用到read,mutex,writeCountSignal,rw信号量)
   int writeCount; // 当前有几个写进程在访问文件
   SEMAPHORE readsem; // 实现写者到来时能够打断读者进程
   SEMAPHORE mutexw; // 完成对writeCount计数器资源的互斥访问
   ```

4. 在main.c中的kernel_main对信号量进行初始化。

   ```c
   init(&rw, 1); // 实现对共享数据的互斥访问
   init(&mutexr, READER_NUM_MAX); // 实现对读者计数器的互斥访问
   init(&w, 1); // 解决写进程饿死问题
   init(&readsem, 1); // 写者优先（写者到来打断读者进程）
   init(&mutexw, 1); // 对写者计数器的互斥访问
   ```

   初始化函数如下：

   ```c
   PUBLIC void init(SEMAPHORE *semaphore, int value) {
       semaphore->value = value;
       semaphore->index = 0;
   }
   ```

5. 实现A-F进程对应的内容，read和write方法中三个参数的含义分别是进程名字、进程需要消耗的时间片以及打印进程操作的颜色。在F进程中，首先判断当前是读还是写，如果是读则输出有几个读者在读，如果是写则输出写的状态。

   ```c
   void RA() {
       read("ReaderA", 2, 0x0c);
   }
   void RB() {
       read("ReaderB", 3, 0x0F);
   }
   void RC() {
       read("ReaderC", 3, 0x02);
   }
   void WD() {
       write("WriterD", 3, 0x03);
   }
   void WE() {
       write("WriterE", 4, 0x06);
   }
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
   ```

6. 实现**读者优先**

   1. **读**。申请读者计数器信号量mutexr。若count的值为0，代表当前没有读者在读，申请共享数据的信号量rw，相当于给临界区加锁，在rw没有被释放前，写者是无法写的。然后读者开始读，读完后将count的值减1，代表在读的读者减少一个，释放mutexr。若count的值为0，代表当前没有读者在读，释放rw。

      ```c
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
      ```
   
   2. **写**。首先申请共享数据的信号量rw，只有在没有读者进程在读的时候才能申请到，这就保证了读者优先以及写进程执行时没有读进程在执行。
   
      ```c
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
      ```
   
7. **解决读者优先写者进程饿死的问题**

   1. **读**。首先申请w信号量，只有在没有写进程在写的情况下才能申请到w的资源，防止写进程的饿死。然后申请读者计数器信号量mutexr。若count的值为0，代表当前没有读者在读，申请共享数据的信号量rw，相当于给临界区加锁，在rw没有被释放前，写者是无法写的。然后读者开始读，读完后将count的值减1，代表在读的读者减少一个，释放mutexr。若count的值为0，代表当前没有读者在读，释放rw。

      ```c
      while (1) {
          // 防止写进程饿死
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
          //milli_delay(1 * TIME);
      }
      ```

   2. **写**。首先申请w信号量，w可以保证在写进程进入时，在该写进程之后到达的读者进程无法去申请rw信号量，从而保证了在读者优先的情况下写者进程不会被饿死。然后申请共享数据的信号量rw，只有在没有读者进程在读的时候才能申请到，这就保证了写进程执行时没有读进程在执行。

      ```c
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
      ```

   3. 举例来说明：读进程A——>写进程a——>读进程B。假设读进程A正在访问共享数据，那么读进程A肯定已经执行了P(w)、P(mutexr)、P(rw)、V(w)、V(mutexr)。此时，写进程a也想要访问共享数据，那么当读进程a执行P(w)时，不会被阻塞，但是执行到P(rw)时，由于读进程A还没有执行V(rw)，所以写进程a会被阻塞等待。
      而如果读进程B也想要访问共享数据，但由于之前写进程a已经执行了P(w)“上锁”操作，所以当读进程B执行到P(w)操作时会被堵塞等待。
      读进程A完成了读文件操作后，执行了V(rw)，写进程a会开始执行写操作，这时候读进程B仍然被阻塞。在写进程完成了写文件操作后，执行了V(w)，读进程B才能开始执行，这就确保了在读者优先的情况下，写进程不会被饿死。也可以称作为“**读写公平**”。

7. 实现**写者优先**

   1. **读**。读者在读之前首先要申请readsem信号量，而只有没有写者在读的时候才能申请到该信号量，这就保证了写者优先。然后申请读者计数器信号量mutexr。若count的值为0，代表当前没有读者在读，申请共享数据的信号量rw，相当于给临界区的共享数据加锁。然后读者开始读，读完后将count的值减1，代表在读的读者减少一个，释放mutexr。若count的值为0，代表当前没有读者在读，释放rw。

      ```c
       while (1) {
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
      ```

   2. **写**。首先申请写者计数器信号量mutexw，用来实现对writeCount的互斥访问。若写者队列为空，即没有写者进程在写，则去申请readsem信号量。将writeCount的值加1后释放mutexw信号量，然后申请文件资源信号量rw后开始写操作。写操作完成后释放文件资源信号量rw，将writeCount减1。若此时没有写者在写，就释放readsem信号量。

      ```c
       while (1) {
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
          }
      }
      ```

3. 上面的方法会导致写者优先时**读者进程饿死**，因此引入z信号量来解决读者进程饿死的问题，解决方法与读者优先时解决写者进程饿死的方法类似。具体如下：

   1. 读：

      ```c
      while (1) {
              p(&z);
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
              v(&z);
      
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
      ```

   2. 写：

      ```c
      while (1) {
          p(&z);
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
          v(&z);
      }
      ```

## 3.3 添加清屏函数

输出的结果是很长的，因此需要添加清屏函数。

1. 在global.h中添加lines变量，记录当前屏幕输出到了第几行

2. 在main.c中的kernel_main方法中添加如下代码进行清屏。

   ```c
   for (int i = 0; i < 80 * 25; i++) {
       // 输出黑底黑字，相当于清屏
       disp_color_str(" ", 0x00);
   
   }
   lines = 0;
   ```

3. 在每次输出换行符时将lines加1

4. 在clock中的clock_handler方法中添加如下代码，若lines超过25，则将屏幕清空，重新从左上角开始输出。

   ```c
   if (lines >= 26) {
       u8 *base = (u8 *) V_MEM_BASE;
       for(int i = 0; i < V_MEM_SIZE; i += 2){
           base[i] = ' ';
           base[i + 1] = 0x07;
       }
       disp_pos=0;
       lines = 0;
   }
   ```

# 4. 运行截图

## 4.1 读者优先

**1个读者**

![](img/4.png)

**2个读者**

![](img/5.png)
![](img/6.png)
之后**写者进程饿死**![](img/7.png)

**3个读者** 
**写者进程饿死**

![](img/8.png)
![](img/9.png)

## 4.2 读者优先时解决写进程饿死问题

**1个读者**

![img](img/r1_1.png) ![img](img/r1_2.png) ![img](img/r1_3.png)

**2个读者**

![img](img/r2_1.png) ![img](img/r2_2.png)

**3个读者** 
![img](img/r3_1.png)

![img](img/r3_2.png)

## 4.3 写者优先

**1个读者**

![img](img/w1_1.png)
之后**读进程饿死** ![img](img/w1_2.png)

**2个读者**

![img](img/w2_1.png)
之后**读进程饿死** ![img](img/w2_2.png)

**3 个读者**

![img](img/w3_1.png)之后**读进程饿死**
![img](img/w3_2.png)

## 4.4 写者优先时解决读者进程饿死问题

**1个读者**

![](img/10.png)
![](img/11.png)

**2个读者**
![](img/12.png)
![](img/13.png)

**3个读者**

![](img/14.png)
![](img/15.png)

