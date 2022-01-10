import gopup as gp
import pandas as pd
import matplotlib.pyplot as plt
import mplcyberpunk

path = "D:/NJU/21/数据科学基础/大作业/data.xlsx"
newsData = pd.DataFrame()
bilibiliData = pd.DataFrame()

def getDayData():
    global data
    getIndex()
    time = data.index
    getNewsData(time)
    getBilibiliData()
    data = data.dropna(how = 'any')
    data.to_excel("D:/NJU/21/数据科学基础/大作业/data/data.xlsx", sheet_name = "data")
    newsData.to_excel("D:/NJU/21/数据科学基础/大作业/data/news.xlsx", sheet_name = "data")
    bilibiliData.to_excel("D:/NJU/21/数据科学基础/大作业/data/bilibili.xlsx", sheet_name = "data")

def getIndex():
    global data
    cookie = 'BAIDUID=4440D6A117E6F20C73F5C508BF90130B:FG=1; PSTM=1593701609; BIDUPSID=EAB563F9A642A17C1D1A74ED6F244350; BDUSS=Ek1dm9YZ1BUak12N0haTUl4Q0dIMTBhamM5NEZlNGlWWG1YeVNhfmVWT2ZBUEJmRVFBQUFBJCQAAAAAAAAAAAEAAABVMXxOMTQ4MzEwMDM0Oc2pycsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJ9zyF-fc8hfT; H_PS_PSSID=; BDORZ=FFFB88E999055A3F8A630C64834BD6D0; Hm_lvt_d101ea4d2a5c67dab98251f0b5de24dc=1608367652; Hm_lpvt_d101ea4d2a5c67dab98251f0b5de24dc=1608367653; __yjsv5_shitong=1.0_7_6acd998da87af301d7b8eb0c2f6da0fb7a9d_300_1608367653409_36.154.208.5_a9a01daf; bdindexid=3md9ingaua21e5vbrchccp15c1; RT="z=1&dm=baidu.com&si=mdiv8xbjcd&ss=kivgkljj&sl=2&tt=242&bcn=https%3A%2F%2Ffclog.baidu.com%2Flog%2Fweirwood%3Ftype%3Dperf&ld=4ct&ul=a9f"'
    data = gp.baidu_search_index(word = "疫情", start_date = '2019-12-09', end_date = '2020-12-07', cookie = cookie)
    data = data.rename(columns = {"疫情": "search_index"})
    data["info_index"] = gp.baidu_info_index(word = "疫情", start_date = '2019-12-09', end_date = '2020-12-07', cookie = cookie)["疫情"]
    data["media_index"] = gp.baidu_media_index(word = "疫情", start_date = '2019-12-09', end_date = '2020-12-07', cookie = cookie)["疫情"]
    #data.to_excel(path, sheet_name = "data")
    data.index = map(lambda x: str(x)[0:10], data.index)

def getNewsData(time):
    global data, newsData
    with open('D:/NJU/21/数据科学基础/大作业/data/analyzeData.txt') as f:
        lines = f.readlines()
        cnt_dictory = {}
        month_dictory = {}
        day = []
        day_data = []
        for line in lines:
            items = line[:-1].split()
            if eval(items[1]) != -1:
                day.append(items[0])
                day_data.append(eval(items[1]) + 0.5)
                month = items[0][0:7]
                month_dictory[month] = month_dictory.get(month, 0) + (eval(items[1]) + 0.5)
                cnt_dictory[month] = cnt_dictory.get(month, 0) + 1
        dayFrame = pd.DataFrame(day_data, index = day)
        dayFrame.index = map(lambda x: str(x)[0:10], dayFrame.index)
        idx = dayFrame.index
        lst = []
        for i in time:
            if i in idx:
                lst.append(dayFrame.loc[i, 0])
            else:
                lst.append(None)          
        data['news'] = lst 

        newsData = pd.DataFrame([month_dictory[key] / cnt_dictory[key] for key in list(month_dictory)], index = list(month_dictory))       

def getBilibiliData():
    global data, bilibiliData
    with open('D:/NJU/21/数据科学基础/大作业/data/bilibili分析.txt') as f:
        lines = f.readlines()
        cnt_dictory = {}
        month_dictory = {}
        for line in lines:
            items = line[:-1].split()
            if eval(items[1]) != -1:
                month = items[0][0:7]
                month_dictory[month] = month_dictory.get(month, 0) + (eval(items[1]) + 0.5)
                cnt_dictory[month] = cnt_dictory.get(month, 0) + 1
        bilibiliData = pd.DataFrame([month_dictory[key] / cnt_dictory[key] for key in list(month_dictory)], index = list(month_dictory))

if __name__ == '__main__':
    getDayData()
