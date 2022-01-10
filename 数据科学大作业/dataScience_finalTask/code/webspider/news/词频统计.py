import jieba
import re
import os
from collections import Counter

file_dir = '/data/news/news/'


def merge():
    for (root, dirs, files) in os.walk(file_dir):
        for file_name in files:
            with open("merge.txt", 'a') as f:
                f.writelines(os.path.join(root, file_name) + "\n")


def count_word():
    cut_words = ""
    for line in open('/data/news/mergeContent.txt', 'r',
                     encoding='utf-8'):
        line.strip('\n')
        line = re.sub("[A-Za-z0-9\：\·\—\，\。\“ \”]", "", line)
        seg_list = jieba.cut(line, cut_all=False)
        cut_words += (" ".join(seg_list))
    all_words = cut_words.split()
    c = Counter()
    for x in all_words:
        if len(x) > 1 and x != '\r\n':
            c[x] += 1

    print('\n词频统计结果：')
    for (k, v) in c.most_common(50):
        print("%s:%d" % (k, v))
        with open("../../data/news/word_count.txt", "a") as f:
            f.writelines("%s:%d" % (k, v))
            f.writelines("\n")


if __name__ == "__main__":
    # merge()
    # with open("merge.txt", 'r') as f:
    #     lines = f.readlines()
    #     for l in lines:
    #         with open(l.strip("\n"), 'r', encoding="gbk") as f1:
    #             a = f1.readlines()
    #             for b in a:
    #                 with open("mergeContent.txt", 'a') as f2:
    #                     f2.writelines(b)
    count_word()
