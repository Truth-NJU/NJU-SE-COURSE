# coding:utf-8

from mysnownlp import sentiment
import os

files = []

def get_all(cwd):
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
    f_path = './bilibili-comment/' + f_path
    with open(f_path, mode='r') as f:
        for line in f:
            score += sentiment.classify(str(line)) - 0.5
            count += 1
    if count == 0:
        return -1
    return score / count


if __name__ == "__main__":
    get_all(r'bilibili-comment')
    if files[0][0] != '2':
        del files[0]

    for path in files:
        s = path[:10] + ' ' + str(analyze(path))
        with open('./analyzeData2.txt', mode='a') as fa:
            fa.write(s+'\n')
        print(s)

