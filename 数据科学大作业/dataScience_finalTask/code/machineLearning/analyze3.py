# coding:utf-8

from mysnownlp import sentiment
import os

files = []


def get_all(cwd):
    files.clear()
    get_dir = os.listdir(cwd)
    get_dir.sort()
    for i in get_dir:
        sub_dir = os.path.join(cwd, i)
        if os.path.isdir(sub_dir):
            get_all(sub_dir)
        else:
            files.append(i)


def analyze(f_path):
    count = 0
    score = 0
    with open(f_path, mode='r') as f:
        for line in f:
            score += sentiment.classify(str(line)) - 0.5
            count += 1
    if count == 0:
        return -1
    return score / count


if __name__ == "__main__":
    for i in range(1, 5):
        news = '健康中国'

        num = str(i)

        get_all(r'Weibo-comments/' + news + '/2020-' + num)

        n = 0
        s = 0

        for path in files:
            s += analyze('./Weibo-comments/' + news + '/2020-' + num + '/' + path)
            n += 1
        print(num)
        print(str(s / n))
