#include <iostream>
#include <vector>
#include <string>
#include <stdio.h>
#include <map>

using namespace std;


// 链接到nasm编写的print方法
extern "C" {
void print(char *pointer, int len);
}
//void print(const char * strs) {
//	cout << strs;
//}


typedef unsigned char u8;    //1字节
typedef unsigned short u16;    //2字节
typedef unsigned int u32;    //4字节

// 红
const char *COLOR_RED = "\033[31m";
string red = "\033[31m";
// 白
const char *COLOR_RESET = "\033[0m";
string white = "\033[0m";


#pragma pack (1) /*指定按1字节对齐*/

int BytsPerSec;    //每扇区字节数
int SecPerClus;    //每簇扇区数
int RsvdSecCnt;    //Boot记录占用的扇区数
int NumFATs;    //FAT表个数
int RootEntCnt;    //根目录最大文件数
int FATSz;    //FAT扇区数
//数据段
// 共25个字节
struct fatHeader {
    u16 BPB_BytsPerSec;    //每扇区字节数，偏移量11
    u8 BPB_SecPerClus;    //每簇扇区数
    u16 BPB_RsvdSecCnt;    //Boot记录占用的扇区数
    u8 BPB_NumFATs;    //FAT表个数
    u16 BPB_RootEntCnt;    //根目录最大文件数
    u16 BPB_TotSec16;
    u8 BPB_Media;
    u16 BPB_FATSz16;    //FAT扇区数
    u16 BPB_SecPerTrk;
    u16 BPB_NumHeads;
    u32 BPB_HiddSec;
    u32 BPB_TotSec32;    //如果BPB_FATSz16为0，该值为FAT扇区数，偏移量36

};


//创建 RootEntry 结构体类型
// 根目录区由目录项构成，每一个目录项代表根目录中的一个文件索引。共32字节
struct RootEntry {
    char DIR_Name[11];  // 文件名8字节，扩展名3字节
    u8 DIR_Attr; // 文件属性 1字节
    char reserve[10];  // 保留位 10字节
    u16 DIR_WrtTime; // 最后一次写入时间 2字节
    u16 DIR_WrtDate; // 最后一次写入日期 2字节
    u16 DIR_FstClus; // 文件开始的簇号 2字节
    u32 DIR_FileSize;  // 文件大小 4字节
};

#pragma pack () /*取消指定对齐，恢复缺省对齐*/

// 记录根目录的名字和对应的信息
map<string, RootEntry> rootDirMap;

// 记录文件名和对应的子目录的名称,如果不是一个目录，就没有子目录
map<string, vector<string>> nametochildMap;

// 存储文件对应的开始的簇号,以及文件的大小
map<string, RootEntry> nametoClusMap;

// 存储所有的路径名
vector<string> path;

void readFatHeader(const char *filepath); // 读取fat12引导扇区的内容

int getNextClus(int curClus, FILE *fat12); // 从fat表项中获取当前簇号的下一个簇号

void getChildren(FILE *fat12, string dirName, int startClus); // 通过簇号获得指定目录的子目录的内容

void readRootDirectory(const char *filepath); //读取根目录区的内容，获得文件的根目录的文件名、扩展名、文件属性、文件开始的簇号和文件的大小

string getFileContentThroughClus(const char *filepath, string fileName); // 通过文件所在的簇号读取文件的内容

void commandLS();

void commandLS_L();

int fileNum(string filename);

int dirNum(string filename);

vector<string> getAbsolutePathThroughName(string filename);

bool fileOrdirectory(string filename);

void commandLS_path_l(string filename);

void printAsm(string str,int len){
    print((char*)str.c_str(),len);
}
/**
 * 读取fat12引导扇区的内容：
 * FAT12的主引导区：
 *      主引导区存储的比较重要的信息是文件系统的类型，文件系统逻辑扇区总数，
 *      每簇包含的扇区数等。主引导区最后以0x55AA两个字节作为结束，共占用一个扇区。
 * @param filepath
 */
void readFatHeader(const char *filepath) {
    FILE *fat12;
    fat12 = fopen(filepath, "rb");    //打开FAT12的映像文件
    struct fatHeader fatHead;
    struct fatHeader *fatHead_ptr = &fatHead;

    //fatHeader从偏移11个字节处开始
    fseek(fat12, 11, SEEK_SET);
    //fatHeader长度为25字节，读入25个字节
    fread(fatHead_ptr, 1, 25, fat12);

    //初始化各个全局变量
    BytsPerSec = fatHead_ptr->BPB_BytsPerSec; //每扇区字节数
    SecPerClus = fatHead_ptr->BPB_SecPerClus; //每簇扇区数
    RsvdSecCnt = fatHead_ptr->BPB_RsvdSecCnt; //Boot记录占用的扇区数
    NumFATs = fatHead_ptr->BPB_NumFATs; //FAT表个数
    RootEntCnt = fatHead_ptr->BPB_RootEntCnt; //根目录最大文件数
    if (fatHead_ptr->BPB_FATSz16 != 0) {
        FATSz = fatHead_ptr->BPB_FATSz16; //FAT扇区数
    } else {
        FATSz = fatHead_ptr->BPB_TotSec32;
    }
//    cout << BytsPerSec << " " << SecPerClus << " " << RsvdSecCnt << " " << NumFATs << " " << RootEntCnt << " " << FATSz
//         << endl;

    fclose(fat12);
}


/**
 * 读取根目录区的内容，获得文件的根目录的文件名、扩展名、文件属性、文件开始的簇号和文件的大小
 * @param filepath
 */
void readRootDirectory(const char *filepath) {
    FILE *fat12;
    fat12 = fopen(filepath, "rb");    //打开FAT12的映像文件
    struct RootEntry rootEntry;
    struct RootEntry *rootEntry_ptr = &rootEntry;

    // 根目录从第19个扇区开始，每个扇区512个字节
    int base = 19 * BytsPerSec;
    for (int i = 0; i < RootEntCnt; i++) {
        // 根目录项从偏移19个扇区处开始
        fseek(fat12, base, SEEK_SET);
        // 每个目录项大小为32字节，读取32字节
        fread(rootEntry_ptr, 1, 32, fat12);

        // 获得根目录的名字
        char dirName[12];
        // 根目录是一个文件，存在扩展名
        int curPoint = 0;  // dirName的指针


        bool fliter = false; // 判断是否需要过滤
        // 过滤不必要的文件
        // 文件名应该在0-9，a-z和A-Z的范围内
        for (int j = 0; j < 11; j++) {
            if (!(((rootEntry_ptr->DIR_Name[j] >= 48) && (rootEntry_ptr->DIR_Name[j] <= 57)) ||
                  ((rootEntry_ptr->DIR_Name[j] >= 65) && (rootEntry_ptr->DIR_Name[j] <= 90)) ||
                  ((rootEntry_ptr->DIR_Name[j] >= 97) && (rootEntry_ptr->DIR_Name[j] <= 122)) ||
                  (rootEntry_ptr->DIR_Name[j] == ' '))) {
                fliter = true;    //非英文及数字、空格
                break;
            }
        }
        if ((rootEntry_ptr->DIR_Attr & 0x10) == 0) {
            // 文件名称
            for (int m = 0; m < 8; m++) {
                if (rootEntry_ptr->DIR_Name[m] != ' ') {
                    dirName[curPoint] = rootEntry_ptr->DIR_Name[m];
                    curPoint++;
                } else {
                    dirName[curPoint] = '.';
                    curPoint++;
                    break;
                }
            }
            // 文件的扩展名
            for (int j = 8; j < 11; j++) {
                if (rootEntry_ptr->DIR_Name[j] != ' ') {
                    dirName[curPoint] = rootEntry_ptr->DIR_Name[j];
                    curPoint++;
                } else {
                    dirName[curPoint] = '\0';
                    curPoint++;
                }
            }
            dirName[11] = '\0';
            // 将文件名存入 文件名和对应子目录的map中
            vector<string> temp;
            if (!fliter) {
                // 过滤掉不必要的内容
                nametochildMap.insert(pair<string, vector<string>>(dirName, temp));
                nametoClusMap.insert(pair<string, RootEntry>(dirName, rootEntry));
            }
        } else { // 根目录是一个目录，不需要扩展名
            for (int j = 0; j < 11; j++) {
                if (rootEntry_ptr->DIR_Name[j] != ' ')
                    dirName[j] = rootEntry_ptr->DIR_Name[j];
                else
                    dirName[j] = '\0';
            }
            dirName[11] = '\0';
            vector<string> temp;
            // 过滤不必要的文件
            if (!fliter) {
                nametochildMap.insert(pair<string, vector<string>>(dirName, temp));
                getChildren(fat12, dirName, rootEntry_ptr->DIR_FstClus);
            }

        }
        struct RootEntry temp;
        // 将读出的根目录内容存入记录根目录的名字和对应的信息的rootDirMap
        temp.DIR_Attr = rootEntry_ptr->DIR_Attr; // 获得文件的文件属性 1字节
        temp.DIR_WrtTime = rootEntry_ptr->DIR_WrtTime; // 最后一次写入时间 2字节
        temp.DIR_WrtDate = rootEntry_ptr->DIR_WrtDate; // 最后一次写入日期 2字节
        temp.DIR_FstClus = rootEntry_ptr->DIR_FstClus; // 文件开始的簇号 2字节
        temp.DIR_FileSize = rootEntry_ptr->DIR_FileSize;  // 文件大小 4字节

        // 过滤掉不必要的文件
        if (!fliter)
            rootDirMap.insert(pair<string, RootEntry>(dirName, temp));
        base += 32;
    }

    fclose(fat12);
//    map<string, RootEntry>::iterator iter;
//    for (iter = rootDirMap.begin(); iter != rootDirMap.end(); iter++) {
//        cout << iter->first << " " << iter->second.DIR_FstClus << endl;
//    }
//
//    map<string, RootEntry>::iterator iter1;
//    for (iter1 = nametoClusMap.begin(); iter1 != nametoClusMap.end(); iter1++) {
//        cout << iter1->first << " " << iter1->second.DIR_FstClus << " " << iter1->second.DIR_FileSize << endl;
//    }
//
//    cout << endl;
//
//    map<string, vector<string>>::iterator iter2;
//    for (iter2 = nametochildMap.begin(); iter2 != nametochildMap.end(); iter2++) {
//        cout << iter2->first << ": ";
//        vector<string> t = iter2->second;
//        for (int i = 0; i < t.size(); i++) {
//            cout << t[i] << " ";
//        }
//        cout << endl;
//    }
}


/**
 * 从fat表项中获取当前簇号的下一个簇号
 * @param curClus 当前的簇号
 * @param fat12
 * @return 下一个数据项的簇号
 */
int getNextClus(int curClus, FILE *fat12) {
    // FAT1的偏移字节,Fat1开始于第一个扇区
    int fatBase = 1 * BytsPerSec;

    //先读出FAT项所在的两个字节
    // 第一个字节
    u8 byte1;
    u8 *bytes_ptr1 = &byte1;
    // FAT项的偏移量为fatBase + curClus * 3 / 2, 每个fat表项占1.5字节
    // FAT表项与数据区的簇号一一对应
    fseek(fat12, fatBase + curClus * 3 / 2, 0);
    fread(bytes_ptr1, 1, 1, fat12);
    // 第二个字节
    u8 byte2;
    u8 *bytes_ptr2 = &byte2;
    fseek(fat12, fatBase + curClus * 3 / 2 + 1, 0);
    fread(bytes_ptr2, 1, 1, fat12);

    // FAT前三个字节的值是固定的0xF0、0xFF、0xFF,因此fat表的0和1项没有意义
    // 偶数项，取byte2字节左移8位取低4位作为结果的高4位
    if (curClus % 2 == 0) {
        return ((byte2 & 0x0F) << 8 | byte1);
    } else {
        // 奇数项，取byte2和byte1的高4位构成值
        return (byte2 << 4 | byte1 >> 4 & 0x0F);
    }
}


/**
 * 获得目录的子目录的内容
 * @param filepath
 */
void getChildren(FILE *fat12, string dirName, int startClus) {
    //数据区的第一个簇（即2号簇）的偏移字节
    // 数据区开始是第33个扇区，每个扇区512字节
    int dataBase = BytsPerSec * 33;

    int value = 0;
    int curClus = startClus;

    // 循环寻找簇号对应的数据区的内容，直到没有下一个簇号
    // 值大于或等于0xFF8，表示当前簇已经是本文件的最后一个簇
    while (value < 0xFF8) {
        // 根据fat表找出下一个簇的地址
        value = getNextClus(curClus, fat12);

        // 值为0xFF7，表示它是一个坏簇
        if (value == 0xFF7) cout << "它是一个坏簇" << endl;

        // 将文件指针跳转到簇号对应的扇区上
        // 数据区的簇号从2开始，因此要减2
        fseek(fat12, dataBase + (curClus - 2) * SecPerClus * BytsPerSec, 0);
        // 每次读一个簇号对应的一个扇区512字节的数据
        // 一次读取一个簇的内容，然后根据fat表找出下一个簇的地址
        char *str = (char *) malloc(SecPerClus * BytsPerSec);    //暂存从簇中读出的数据
        char *tempContent = str;
        // 每次读一个簇号对应的一个扇区512字节的数据
        fread(tempContent, 1, 512, fat12);


        // 当前处在数据的第几个字节
        int index = 0;
        // 一共读取了512字节的数据
        while (index < 512) {
            char fileName[12]; // 存储文件的名称
            if (tempContent[index] == '\0') {
                index += 32;
                continue;
            }
            // 是一个文件，有后缀名
            if ((tempContent[index + 11] & 0x10) == 0) {
                int curPoint = 0;
                // 文件的名称
                for (int m = 0; m < 8; m++) {
                    if (tempContent[index + m] != ' ') {
                        fileName[curPoint] = tempContent[index + m];
                        curPoint++;
                    } else {
                        fileName[curPoint] = '.';
                        curPoint++;
                        break;
                    }
                }
                // 文件的扩展名
                for (int j = 8; j < 11; j++) {
                    if (tempContent[index + j] != ' ') {
                        fileName[curPoint] = tempContent[index + j];
                        curPoint++;
                    } else {
                        fileName[curPoint] = '\0';
                        curPoint++;
                    }
                }
                fileName[11] = '\0';
//                cout << fileName<<": ";
                // 将信息加入目录和其子文件的map中
                nametochildMap[dirName].push_back(fileName);
                // 将文件指针跳转到文件簇号对应的扇区上
                fseek(fat12, dataBase + (curClus - 2) * SecPerClus * BytsPerSec + index, 0);
                struct RootEntry rootEntry;
                struct RootEntry *rootEntry_ptr = &rootEntry;
                // 每次读32字节的数据，与目录项相同
                fread(rootEntry_ptr, 1, 32, fat12);
                // 记录文件和对应的数据区的簇号的映射
                nametoClusMap.insert(pair<string, RootEntry>(fileName, rootEntry));

            } else {
                // 是一个目录，不需要后缀
                for (int j = 0; j < 11; j++) {
                    if (tempContent[index + j] != ' ')
                        fileName[j] = tempContent[index + j];
                    else
                        fileName[j] = '\0';
                }
                fileName[11] = '\0';
                // 将tempName 转为字符串，方便比较
                string tempStr = "";
                for (int i = 0; i < 12; i++) {
                    if (fileName[i] != '\0') {
                        tempStr += fileName[i];
                    }
                }
                // 过滤掉.和..文件
                if (tempStr != "." && tempStr != "..") {
                    vector<string> tempVector;
                    // 将tempName当作父目录存入nametochildMap
                    nametochildMap.insert(pair<string, vector<string>>(fileName, tempVector));
                    //  // 将tempName当作dirname的子目录存入nametochildMap
                    nametochildMap[dirName].push_back(fileName);
                    fseek(fat12, dataBase + (curClus - 2) * SecPerClus * BytsPerSec + index, 0);
                    struct RootEntry rootEntry;
                    struct RootEntry *rootEntry_ptr = &rootEntry;
                    // 每次读32字节的数据
                    fread(rootEntry_ptr, 1, 32, fat12);

                    // 递归读取目录的子目录
                    getChildren(fat12, fileName, rootEntry_ptr->DIR_FstClus);

                }
            }
            // 一次读取32字节的数据，相当于读取目录项
            index += 32;
        }
        curClus = value;
    }
}


/**
 * 根据文件名判断是普通文件还是目录,true代表是文件，false代表是目录
 * @param filename
 * @return
 */
bool fileOrdirectory(string filename) {
    // 若是普通文件，存在扩展名，会包含.
    for (int i = 0; i < filename.length(); i++) {
        if (filename[i] == '.')
            return true;
    }
    return false;
}

/**
 * 根据名字判断子文件的个数
 * @param filename
 * @return
 */
int fileNum(string filename) {
    int count = 0;
    map<string, vector<string>>::iterator iter2;
    for (iter2 = nametochildMap.begin(); iter2 != nametochildMap.end(); iter2++) {
        if (filename == iter2->first) {
            vector<string> t = iter2->second;
            for (int i = 0; i < t.size(); i++) {
                if (fileOrdirectory(t[i])) count++;
            }
        }
    }
    return count;
}

/**
 * 根据名字判断子目录的个数
 * @param filename
 * @return
 */
int dirNum(string filename) {
    map<string, vector<string>>::iterator iter2;
    for (iter2 = nametochildMap.begin(); iter2 != nametochildMap.end(); iter2++) {
        if (filename == iter2->first) {
            vector<string> t = iter2->second;
            return t.size() - fileNum(filename);
        }
    }
    return 0;
}

vector<string> resPath;

/**
 * 根据文件名，从nametochildMap中获得完整的路径
 * @param filename
 * @return 完整的路径名
 */
vector<string> getAbsolutePathThroughName(string filename) {
    map<string, vector<string>>::iterator iter;
    for (iter = nametochildMap.begin(); iter != nametochildMap.end(); iter++) {
        // 某个目录的子目录与filename相同，就要把它加到结果数组里
        vector<string> temp = iter->second;
        for (int i = 0; i < temp.size(); i++) {
            if (temp[i] == filename) {
                resPath.push_back(iter->first);
            }
        }
    }
    map<string, RootEntry>::iterator iter1;
    for (iter1 = rootDirMap.begin(); iter1 != rootDirMap.end(); iter1++) {
        // 如果数组中出现根目录中包含的一个目录，代表遍历已经完成，结束
        if (iter1->first == resPath[resPath.size() - 1]) {
            return resPath;
        }
    }
    // 递归获取父目录，并添加到resPath数组中
    getAbsolutePathThroughName(resPath[resPath.size() - 1]);
    return resPath;
}


// 执行ls命令
void commandLS() {
    printAsm("/:", 2);
    printAsm("\n", 1);
    map<string, RootEntry>::iterator iter;
    for (iter = rootDirMap.begin(); iter != rootDirMap.end(); iter++) {
        // 是目录
        if (!fileOrdirectory(iter->first)) {
            // 输出红色的目录名
            printAsm(COLOR_RED, red.length());
            printAsm(iter->first, iter->first.length());
            printAsm(" ", 1);
            // 颜色复位
            printAsm(COLOR_RESET, white.length());
        }
            // 是文件
        else {
            printAsm(iter->first, iter->first.length());
            printAsm(" ", 1);
        }
    }
    printAsm("\n", 1);


    map<string, vector<string>>::iterator iter2;
    for (iter2 = nametochildMap.begin(); iter2 != nametochildMap.end(); iter2++) {
        // 是目录才输出
        if (!fileOrdirectory(iter2->first)) {
            // 清空resPath
            resPath.clear();
            resPath.push_back(iter2->first);
            // 得到完整的路径
            getAbsolutePathThroughName(iter2->first);
            string path = "";
            for (int i = resPath.size() - 1; i >= 0; i--) {
                path = path + "/" + resPath[i];
            }
            path += "/";
            printAsm(path, path.length());
            printAsm(" ", 1);
            printAsm("\n", 1);
            printAsm(COLOR_RED, red.length());
            string ts = ". .. ";
            printAsm(ts, ts.length());
            // 颜色复位
            printAsm(COLOR_RESET, white.length());
            vector<string> t = iter2->second;
            for (int i = 0; i < t.size(); i++) {
                // 是目录
                if (!fileOrdirectory(t[i])) {
                    printAsm(COLOR_RED, red.length());
                    printAsm(t[i], t[i].length());
                    printAsm(" ", 1);
                    // 颜色复位
                    printAsm(COLOR_RESET, white.length());
                } else {
                    // 是文件
                    printAsm(t[i], t[i].length());
                    printAsm(" ", 1);
                }
            }
            printAsm("\n", 1);
        }
    }


}

// ls -l command
void commandLS_L() {
    int dirnum = 0;
    int filenum = 0;
    map<string, RootEntry>::iterator iterNum;
    // 统计根目录下目录和文件的个数
    for (iterNum = rootDirMap.begin(); iterNum != rootDirMap.end(); iterNum++) {
        if (fileOrdirectory(iterNum->first)) filenum++;
        else dirnum++;
    }
    printAsm("/ ", 2);
    printAsm(to_string(dirnum), to_string(dirnum).length());
    printAsm(" ", 1);
    printAsm(std::to_string(filenum), to_string(filenum).length());
    printAsm(":", 1);
    printAsm("\n", 1);
    map<string, RootEntry>::iterator iter;
    for (iter = rootDirMap.begin(); iter != rootDirMap.end(); iter++) {
        // 是目录
        if (!fileOrdirectory(iter->first)) {
            printAsm(COLOR_RED, red.length());
            printAsm(iter->first, iter->first.length());
            // 颜色复位
            printAsm(COLOR_RESET, white.length());
            printAsm(" ", 1);
            printAsm(std::to_string(dirNum(iter->first)), to_string(dirNum(iter->first)).length());
            printAsm(" ", 1);
            printAsm(std::to_string(fileNum(iter->first)), to_string(fileNum(iter->first)).length());
            printAsm("\n", 1);
//            cout << " " << dirNum(iter->first) << " " << fileNum(iter->first) << endl;
        }
            // 是文件
        else {
            int size = 0;
            map<string, RootEntry>::iterator iter1;
            for (iter1 = nametoClusMap.begin(); iter1 != nametoClusMap.end(); iter1++) {
                if (iter1->first == iter->first) {
                    size = iter1->second.DIR_FileSize;
                    break;
                }
            }
            printAsm(iter->first, iter->first.length());
            printAsm(" ", 1);
            printAsm(std::to_string(size), to_string(size).length());
            printAsm("\n", 1);
//            cout << iter->first << " " << size << endl;
        }
    }
    cout << endl;

    map<string, vector<string>>::iterator iter2;
    for (iter2 = nametochildMap.begin(); iter2 != nametochildMap.end(); iter2++) {
        // 是目录才输出
        if (!fileOrdirectory(iter2->first)) {
            // 清空resPath
            resPath.clear();
            resPath.push_back(iter2->first);
            // 得到完整的路径
            getAbsolutePathThroughName(iter2->first);
            string path = "";
            for (int i = resPath.size() - 1; i >= 0; i--) {
                path = path + "/" + resPath[i];
            }
            path += "/";
            printAsm(path, path.length());
            printAsm(" ", 1);
            printAsm(std::to_string(dirNum(iter2->first)), to_string(dirNum(iter2->first)).length());
            printAsm(" ", 1);
            printAsm(std::to_string(fileNum(iter2->first)), to_string(fileNum(iter2->first)).length());
            printAsm(":", 1);
            printAsm("\n", 1);
            printAsm(COLOR_RED, red.length());
            printAsm(".", 1);
            printAsm("\n", 1);
            printAsm("..", 2);
            printAsm("\n", 1);
            // 颜色复位
            printAsm(COLOR_RESET, white.length());
//            cout << path << " " << dirNum(iter2->first) << " " << fileNum(iter2->first) << ":" << endl;
//            cout << COLOR_RED;
//            cout << "." << endl;
//            cout << ".." << endl;
//            cout << COLOR_RESET;
            vector<string> t = iter2->second;
            for (int i = 0; i < t.size(); i++) {
                // 是目录，需要输出直接子目录和直接子文件的个数
                if (!fileOrdirectory(t[i])) {
                    printAsm(COLOR_RED, red.length());
                    printAsm(t[i], t[i].length());
                    printAsm(" ", 1);
                    // 颜色复位
                    printAsm(COLOR_RESET, white.length());
                    printAsm(std::to_string(dirNum(t[i])), to_string(dirNum(t[i])).length());
                    printAsm(" ", 1);
                    printAsm(std::to_string(fileNum(t[i])), to_string(fileNum(t[i])).length());
                    printAsm("\n", 1);
//                    cout << COLOR_RED;
//                    cout << t[i] << " ";
//                    cout << COLOR_RESET;
//                    cout << dirNum(t[i]) << " " << fileNum(t[i]) << endl;
                } else {
                    // 是文件，需要输出文件的大小
                    int size = 0;
                    map<string, RootEntry>::iterator iter1;
                    for (iter1 = nametoClusMap.begin(); iter1 != nametoClusMap.end(); iter1++) {
                        if (iter1->first == t[i]) {
                            size = iter1->second.DIR_FileSize;
                            break;
                        }
                    }
                    printAsm(t[i], t[i].length());
                    printAsm(" ", 1);
                    printAsm(std::to_string(size), to_string(size).length());
                    printAsm("\n", 1);
//                    cout << t[i] << " " << size << endl;
                }
            }
            printAsm("\n", 1);
//            cout << endl;
        }
    }
}

// ls -l + 路径 command
void commandLS_path_l(string filename) {
    // 根目录
    if (filename == "") {
        // 相当于 ls -l命令
        commandLS_L();
    } else {
        // 得到指定目录的子目录
        vector<string> childDir;
        map<string, vector<string>>::iterator iter;
        for (iter = nametochildMap.begin(); iter != nametochildMap.end(); iter++) {
            if (iter->first == filename) {
                childDir = iter->second;
                break;
            }
        }
        map<string, vector<string>>::iterator iter2;
        for (iter2 = nametochildMap.begin(); iter2 != nametochildMap.end(); iter2++) {
            // 判断是否是指定目录和它的子目录,true 代表满足，需要输出，false不满足，不用输出
            bool check = false;
            if (iter2->first == filename) check = true;
            else {
                for (int i = 0; i < childDir.size(); i++) {
                    if (iter2->first == childDir[i]) {
                        check = true;
                        break;
                    }
                }
            }
            // 是目录才输出
            if (!fileOrdirectory(iter2->first) && check) {
                // 清空resPath
                resPath.clear();
                resPath.push_back(iter2->first);
                // 得到完整的路径
                getAbsolutePathThroughName(iter2->first);
                string path = "";
                for (int i = resPath.size() - 1; i >= 0; i--) {
                    path = path + "/" + resPath[i];
                }
                path += "/";
                printAsm(path, path.length());
                printAsm(" ", 1);
                printAsm(std::to_string(dirNum(iter2->first)), to_string(dirNum(iter2->first)).length());
                printAsm(" ", 1);
                printAsm(std::to_string(fileNum(iter2->first)), to_string(fileNum(iter2->first)).length());
                printAsm(":", 1);
                printAsm("\n", 1);
                printAsm(COLOR_RED, red.length());
                printAsm(".", 1);
                printAsm("\n", 1);
                printAsm("..", 2);
                printAsm("\n", 1);
                // 颜色复位
                printAsm(COLOR_RESET, white.length());
//                cout << path << " " << dirNum(iter2->first) << " " << fileNum(iter2->first) << ":" << endl;
//                cout << COLOR_RED;
//                cout << "." << endl;
//                cout << ".." << endl;
//                cout << COLOR_RESET;
                vector<string> t = iter2->second;
                for (int i = 0; i < t.size(); i++) {
                    // 是目录，需要输出直接子目录和直接子文件的个数
                    if (!fileOrdirectory(t[i])) {
                        printAsm(COLOR_RED, red.length());
                        printAsm(t[i], t[i].length());
                        printAsm(" ", 1);
                        printAsm(COLOR_RESET, white.length());
                        printAsm(std::to_string(dirNum(t[i])), to_string(dirNum(t[i])).length());
                        printAsm(" ", 1);
                        printAsm(std::to_string(fileNum(t[i])), to_string(fileNum(t[i])).length());
                        printAsm("\n", 1);
//                        cout << COLOR_RED;
//                        cout << t[i] << " ";
//                        cout << COLOR_RESET;
//                        cout << dirNum(t[i]) << " " << fileNum(t[i]) << endl;
                    } else {
                        // 是文件，需要输出文件的大小
                        int size = 0;
                        map<string, RootEntry>::iterator iter1;
                        for (iter1 = nametoClusMap.begin(); iter1 != nametoClusMap.end(); iter1++) {
                            if (iter1->first == t[i]) {
                                size = iter1->second.DIR_FileSize;
                                break;
                            }
                        }
                        printAsm(t[i], t[i].length());
                        printAsm(" ", 1);
                        printAsm(to_string(size), to_string(size).length());
                        printAsm("\n", 1);
//                        cout << t[i] << " " << size << endl;
                    }
                }
                printAsm("\n", 1);
//                cout << endl;
            }
        }
    }

}

/**
 * 通过文件所在的簇号读取文件的内容
 * @param filepath
 * @param fileName
 * @return 返回文件的内容字符串
 */
string getFileContentThroughClus(const char *filepath, string fileName) {
    string fileContent = "";
    // 打开映像文件
    FILE *fat12;
    fat12 = fopen(filepath, "rb");
    // 从nametoClusMap获取该文件对应的数据区的簇号
    map<string, RootEntry>::iterator iter;
    int clus = -1;
    int size = 0;
    for (iter = nametoClusMap.begin(); iter != nametoClusMap.end(); iter++) {
        if (iter->first == fileName) {
            // 文件开始的簇号
            clus = iter->second.DIR_FstClus;
            // 文件的大小
            size = iter->second.DIR_FileSize;
            break;
        }
    }
    // 文件不存在
    if (clus == -1) cout << "文件/目录不存在" << endl;
    int curSize = 0;
    // 数据区的偏移量
    int dataBase = 33 * SecPerClus * BytsPerSec;
    while (true) {
        // 文件剩余的字节数大于512字节，需要读取到下一个簇号，得到其对应的数据地址
        if (size - curSize >= 512) {
            // 将文件指针跳转到簇号对应的扇区上
            fseek(fat12, dataBase + (clus - 2) * SecPerClus * BytsPerSec, 0);
            //暂存从簇中读出的数据，共512字节
            char *str = (char *) malloc(SecPerClus * BytsPerSec);
            char *content = str;
            // 每次读一个簇号对应的一个扇区512字节的数据
            fread(content, 1, SecPerClus * BytsPerSec, fat12);
            //cout<<content<<endl;
            fileContent += content;
            curSize += 512;
            //根据fat表找出下一个簇的地址
            clus = getNextClus(clus, fat12);
        } else {
            int leftSize = size - curSize + 1; // 文件剩余未读取出来的大小
            //cout << leftSize << endl;
            // 将文件指针跳转到簇号对应的扇区上
            fseek(fat12, dataBase + (clus - 2) * SecPerClus * BytsPerSec, 0);
            //暂存从簇中读出的数据，共leftSize个字节
            char *str = (char *) malloc(leftSize);
            char *content = str;
            // 读leftSize个字节的数据
            fread(content, 1, leftSize, fat12);
            //cout<<content<<endl;
            fileContent += content;
            break;
        }
    }
    printAsm(fileContent, fileContent.length());
    printAsm("\n", 1);
//    cout << fileContent << endl;

    fclose(fat12);
    return fileContent;
}


int main() {
    readFatHeader("./a.img");
    readRootDirectory("./a.img");
    string command;
    // 不间断输入，输入exit退出
    while (true) {
        printAsm("> ", 2);
        getline(cin, command);
        // ls指令
        if (command == "ls") {
            commandLS();
        } else if (command == "ls -l") {
            // ls -l指令
            commandLS_L();
        } else if (command[0] == 'c') {
            // cat指令
            string filename = "";
            for (int i = 4; i < command.length(); i++) {
                filename += command[i];
            }
            if (!fileOrdirectory(filename)) {
                string t = "不是一个普通文件，无法读取";
                printAsm(t, t.length());
                printAsm("\n", 1);
            } else {
                getFileContentThroughClus("./a.img", filename);
            }
        } else if (command[0] == 'l' && command[1] == 's') {
            // ls -l+路径名指令
            // 判断命令是否有效
            vector<string> commandSplit;
            string temp = "";
            for (int i = 0; i < command.length(); i++) {
                if (command[i] != ' ') {
                    temp += command[i];
                } else {
                    commandSplit.push_back(temp);
                    temp = "";
                }
            }
            commandSplit.push_back(temp);
            if (commandSplit.size() < 3) {
                string t = "参数/命令错误，请重试";
                printAsm(t, t.length());
                printAsm("\n", 1);
                printAsm("\n", 1);
                continue;
            }
            // 参数是否正确
            bool correct = true;
            int numOfpath = 0;
            for (int i = 0; i < commandSplit.size(); i++) {
                if (commandSplit[i][0] != '/' && commandSplit[i][0] != 'l' && commandSplit[i][0] != '-') {
                    correct = false;
                }
                if (commandSplit[i][0] == '-') {
                    for (int j = 1; j < commandSplit[i].length(); j++) {
                        if (commandSplit[i][j] != 'l') {
                            correct = false;
                        }
                    }
                }
                if (commandSplit[i][0] == '/') numOfpath++;
            }
            if (numOfpath != 1) correct = false;
            if (!correct) {
                string t = "参数错误";
                printAsm(t, t.length());
                printAsm("\n", 1);
            } else {
                for (int i = 0; i < commandSplit.size(); i++) {
                    if (commandSplit[i][0] == '/') {
                        for (int s = 0; s < commandSplit[i].size(); s++) {
                            if (commandSplit[i][s] >= 'a' && commandSplit[i][s] <= 'z') {
                                string t = "文件/目录不存在";
                                printAsm(t, t.length());
                                printAsm("\n", 1);
                                break;
                            }
                        }
                        int maxI = 0; // 最后一次出现/的位置
                        for (int s = 0; s < commandSplit[i].size(); s++) {
                            if (commandSplit[i][s] == '/') {
                                if (maxI < s) {
                                    maxI = s;
                                }
                            }
                        }
                        string name = "";
                        for (int j = maxI + 1; j < commandSplit[i].length(); j++) {
                            name += commandSplit[i][j];
                        }
                        if (fileOrdirectory(name)) {
                            string t="请输入正确的目录名称";
                            printAsm(t, t.length());
                            printAsm("\n", 1);
                        } else commandLS_path_l(name);
                    }
                }
            }

        } else if (command == "exit") {
            return 0;
        } else {
            string t="参数/命令错误，请重试";
            printAsm(t, t.length());
            printAsm("\n", 1);
        }
    }
    return 0;
}
