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
    with open('./news/' + f_path, mode='r', encoding='GBK') as f:
        for line in f:
            sens = line.split('。')
            for sen in sens:
                score += sentiment.classify(str(sen)) - 0.5
                count += 1
    if count == 0:
        return -1
    return score / count


if __name__ == "__main__":
    get_all(r'news')
    if files[0][0] != '2':
        del files[0]

    for path in files:
        s = path[:-4] + ' ' + str(analyze(path))
        with open('./analyzeData.txt', mode='a') as fa:
            fa.write(s+'\n')
        print(s)

# count = 0
# score = 0
# with open('./news/2019-12-09.txt', mode='r', encoding='GBK') as f:
#     for line in f:
#         sens = line.split('。')
#         for sen in sens:
#             score += sentiment.classify(str(sen))-0.5
#             count += 1
#
# print(score/count)
