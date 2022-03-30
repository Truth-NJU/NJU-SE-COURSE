#include <stdio.h>
#include <sys/types.h>
#include <dirent.h>
#include <sys/stat.h>
#include <pwd.h>
#include <grp.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>

void wordCount(char *path, char *filename) {
    FILE *file = fopen(path, "r");
    char c;
    int bytes = 0, lines = 0, words = 0;
    int whetherInWord = 0; // 0不在单词中，1在单词中
    while ((c = fgetc(file)) != EOF) {
        bytes++;
        if (c == '\n') {
            lines++;
            whetherInWord = 0;
        } else if (c == ' ' || c == '\t') {
            whetherInWord = 0;
        } else if (c != ' ') {
            if (whetherInWord == 0) {
                whetherInWord = 1;
                words++;
            }
        }
    }
    fclose(file);
    printf("%d %d %d %s\n", lines, words, bytes, filename);

}

int l_flag = 0, R_flag = 0, d_flag = 0, a_flag = 0, i_flag = 0;

void mode_to(int mode, char str[]) {
    strcpy(str, "----------");
    if (S_ISDIR(mode)) str[0] = 'd';
    if (S_ISCHR(mode)) str[0] = 'c';
    if (S_ISBLK(mode)) str[0] = 'b';
    if ((mode & S_IRUSR)) str[1] = 'r';
    if ((mode & S_IWUSR)) str[2] = 'w';
    if ((mode & S_IXUSR)) str[3] = 'x';
    if ((mode & S_IRGRP)) str[4] = 'r';
    if ((mode & S_IWGRP)) str[5] = 'w';
    if ((mode & S_IXGRP)) str[6] = 'x';
    if ((mode & S_IROTH)) str[7] = 'r';
    if ((mode & S_IWOTH)) str[8] = 'w';
    if ((mode & S_IXOTH)) str[9] = 'x';
}

// 递归得到所有目录的路径
void recursion(char *path) {
    DIR *dir;
    struct dirent *ptr;
    dir = opendir(path);
    while ((ptr = readdir(dir)) != NULL) {
        if (strcmp(ptr->d_name, ".") != 0 && strcmp(ptr->d_name, "..") != 0) {
            // 8 是文件，4 是目录
            if (ptr->d_type == 4) {
                char tmpPath[1000];
                strcpy(tmpPath, path);
                strcat(tmpPath, "/");
                strcat(tmpPath, ptr->d_name);
                DIR *tmpDir;
                tmpDir = opendir(tmpPath);
                printf("%s:\n", tmpPath);
                struct dirent *ptr2;
                while ((ptr2 = readdir(tmpDir)) != NULL) {
                    char tmp[1000];
                    strcpy(tmp,tmpPath);
                    strcat(tmp, "/");
                    strcat(tmp, ptr2->d_name);
                    struct stat buf3;
                    stat(tmp, &buf3);
                    struct passwd *pwd2;
                    pwd2 = getpwuid(buf3.st_uid);
                    struct group *grp2;
                    grp2 = getgrgid(buf3.st_gid);
                    char str[11];
                    mode_to(buf3.st_mode, str);
                    if (a_flag != 1) {
                        if (strcmp(ptr2->d_name, ".") != 0 && strcmp(ptr2->d_name, "..") != 0) {
                            if (i_flag == 1) {
                                printf("%llu ", ptr2->d_ino);
                            }
                            if (l_flag == 1) {
                                printf("%s %d %s %s %lld %.12s ", str, buf3.st_nlink,
                                       pwd2->pw_name, grp2->gr_name, buf3.st_size, 4 + ctime(&buf3.st_mtimespec));
                            }
                            printf("%s\n", ptr2->d_name);
                        }
                    } else if (a_flag == 1) {
                        if (i_flag == 1) {
                            printf("%llu ", ptr2->d_ino);
                        }
                        if (l_flag == 1) {
                            printf("%s %d %s %s %lld %.12s ", str, buf3.st_nlink,
                                   pwd2->pw_name, grp2->gr_name, buf3.st_size, 4 + ctime(&buf3.st_mtimespec));
                        }
                        printf("%s\n", ptr2->d_name);
                    }
                }
                printf("\n\n");
                closedir(tmpDir);
                recursion(tmpPath);
            }
        }
    }
    closedir(dir);
}

void ls() {
    DIR *dir;
    char *path = getenv("PWD");
    struct dirent *ptr;
    dir = opendir(path);
    while ((ptr = readdir(dir)) != NULL) {
        char tmpPath[1000];
        strcpy(tmpPath, path);
        strcat(tmpPath, "/");
        strcat(tmpPath, ptr->d_name);
        struct stat buf;
        stat(tmpPath, &buf);
        struct passwd *pwd;
        pwd = getpwuid(buf.st_uid);
        struct group *grp;
        grp = getgrgid(buf.st_gid);
        char str[11];
        mode_to(buf.st_mode, str);
        if (d_flag == 1 && strcmp(ptr->d_name, ".") == 0) {
            if (i_flag == 1) {
                printf("%llu ", ptr->d_ino);
            }
            if (l_flag == 1) {
                printf("%s %d %s %s %lld %.12s ", str, buf.st_nlink,
                       pwd->pw_name, grp->gr_name, buf.st_size, 4 + ctime(&buf.st_mtimespec));
            }
            printf("%s\n", ptr->d_name);
            break;
        } else {
            if (a_flag != 1) {
                if (strcmp(ptr->d_name, ".") != 0 && strcmp(ptr->d_name, "..") != 0) {
                    if (i_flag == 1) {
                        printf("%llu ", ptr->d_ino);
                    }
                    if (l_flag == 1) {
                        printf("%s %d %s %s %lld %.12s ", str, buf.st_nlink,
                               pwd->pw_name, grp->gr_name, buf.st_size, 4 + ctime(&buf.st_mtimespec));
                    }
                    printf("%s\n", ptr->d_name);
                }
            } else if (a_flag == 1) {
                if (i_flag == 1) {
                    printf("%llu ", ptr->d_ino);
                }
                if (l_flag == 1) {
                    printf("%s %d %s %s %lld %.12s ", str, buf.st_nlink,
                           pwd->pw_name, grp->gr_name, buf.st_size, 4 + ctime(&buf.st_mtimespec));
                }
                printf("%s\n", ptr->d_name);
            }
        }
    }
    if (R_flag == 1 && d_flag != 1) {
        printf("\n\n");
        recursion(path);
    }
    closedir(dir);
}


int main() {
    // wordCount("/Users/taozehua/Downloads/大三下/Linux程序设计/作业/hw3/code/testfile","testfile");
    char command[100];
    gets(command);
    for (int j = 2; j <= strlen(command); j++) {
        if (command[j] == 'l') l_flag = 1;
        else if (command[j] == 'd') d_flag = 1;
        else if (command[j] == 'a') a_flag = 1;
        else if (command[j] == 'R') R_flag = 1;
        else if (command[j] == 'i') i_flag = 1;
    }
    ls();
    return 0;
}


