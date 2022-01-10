import os
import mplcyberpunk
import matplotlib.pyplot as plt

read_path = "D:/NJU/21/数据科学基础/大作业/url"
date = []
counter = []


# 获得目录下的所有文件
def get_all(cwd):
    results = []
    get_dir = os.listdir(cwd)

    for item in get_dir:
        sub_dir = os.path.join(cwd, item)
        if os.path.isdir(sub_dir):
            get_all(sub_dir)
        else:
            results.append(item)

    return results


def countNews():
    files = get_all(read_path)
    for file in files:
        path = read_path + "/" + file
        with open(path, 'r+') as f:
            date.append(file.split(".")[0])
            counter.append(len(f.readlines()))


def draw():
    # 添加样式
    plt.style.use("cyberpunk")
    # 设置线条发光+面积图
    plt.plot(date, counter)
    plt.xticks([])
    plt.xlabel('date')
    plt.ylabel('sina_news')
    mplcyberpunk.add_glow_effects()
    plt.savefig("D:/NJU/21/数据科学基础/大作业/pictures/countNews.svg", dpi = 600)


if __name__ == '__main__':
    countNews()
    draw()