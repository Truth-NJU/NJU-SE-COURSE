import gopup as gp
import os
from datetime import datetime
import mplcyberpunk
import matplotlib.pyplot as plt

keywords = ["疫情", "新冠", "抗疫", "口罩", "病例", "复工", "防疫"]
cookie = 'BAIDUID=4440D6A117E6F20C73F5C508BF90130B:FG=1; PSTM=1593701609; BIDUPSID=EAB563F9A642A17C1D1A74ED6F244350; BDUSS=Ek1dm9YZ1BUak12N0haTUl4Q0dIMTBhamM5NEZlNGlWWG1YeVNhfmVWT2ZBUEJmRVFBQUFBJCQAAAAAAAAAAAEAAABVMXxOMTQ4MzEwMDM0Oc2pycsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJ9zyF-fc8hfT; H_PS_PSSID=; BDORZ=FFFB88E999055A3F8A630C64834BD6D0; Hm_lvt_d101ea4d2a5c67dab98251f0b5de24dc=1608367652; Hm_lpvt_d101ea4d2a5c67dab98251f0b5de24dc=1608367653; __yjsv5_shitong=1.0_7_6acd998da87af301d7b8eb0c2f6da0fb7a9d_300_1608367653409_36.154.208.5_a9a01daf; bdindexid=3md9ingaua21e5vbrchccp15c1; RT="z=1&dm=baidu.com&si=mdiv8xbjcd&ss=kivgkljj&sl=2&tt=242&bcn=https%3A%2F%2Ffclog.baidu.com%2Flog%2Fweirwood%3Ftype%3Dperf&ld=4ct&ul=a9f"'
base_path = "D:/NJU/21/数据科学基础/大作业/index"

x = []
search = []
media = []

def getBaiduIndex(path):
    print("baiduindex start!")
    if not os.path.exists(path):
        os.mkdir(path)
    for key in keywords:
        print(key + " start!!")
        if path ==  base_path + "/BaiduSearchIndex/":
            index_df = gp.baidu_search_index(word = key, start_date = '2019-12-08', end_date = '2020-12-07', cookie = cookie)
        if path ==  base_path + "/BaiduMediaIndex/":
            index_df = gp.baidu_media_index(word = key, start_date = '2019-12-08', end_date = '2020-12-07', cookie = cookie)
        filepath = path + key + ".txt"
        days = len(index_df)
        date = index_df.index
        index = index_df.values
        with open(filepath, 'a+') as f:
            for i in range(days):
                f.write(str(date[i]).split()[0] + "|" + str(index[i][0]) + "\n")
                print(str(date[i]).split()[0] + " finished!")
        print(key + " finished!!")
    print("baidusearchindex finished!")            
 

def drawBaiduIndex(path, key):      
    plt.style.use("cyberpunk")
    plt.xlabel('date')
    plt.ylabel('index')
    date = []
    index = []
    filepath = path + key + ".txt"
    with open(filepath, 'r+') as f:
        lines = f.readlines()
    for line in lines:
        temp = line.split("|")
        date.append(temp[0])
        index.append(int(temp[1]))
    plt.plot(date, index)
    plt.xticks([])

    if path ==  base_path + "/BaiduSearchIndex/":
        name = "baidusearchindex_" + key
    if path ==  base_path + "/BaiduMediaIndex/":
        name = "baidumediaindex_" + key    
    plt.savefig("D:/NJU/21/数据科学基础/大作业/pictures/" + name + ".svg", dpi = 600)
    plt.clf()
    print(key + "finished!")      

if __name__ == '__main__':
    #getBaiduIndex(base_path + "/BaiduSearchIndex/")
    #getBaiduIndex(base_path + "/BaiduMediaIndex/")
    for key in keywords:
        drawBaiduIndex(base_path + "/BaiduSearchIndex/", key)
        drawBaiduIndex(base_path + "/BaiduMediaIndex/", key)
