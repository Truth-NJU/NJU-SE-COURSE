from datetime import datetime, timedelta
import requests

base_url = "https://feed.mix.sina.com.cn/api/roll/get?pageid=153&lid=2510&k=&num=50"
#新浪新闻的接口
base_path = "D:/NJU/21/数据科学/大作业/data/"

keywords = ["疫情", "新冠", "抗疫", "病例", "复工"]
#搜索关键字
n = len(keywords)

def addDay(date):
    #时间增加一天
    return date + timedelta(days=1)

def getURL(date, params):
    try:    
        with open((base_path + date + ".txt"), 'a+') as f:
            while True:
                print("Page: " + str(params['page']))
                json = requests.get(base_url, params = params).json()
                #获取网站的json文件

                if not json['result']['data']:
                    break
                #如果json['result']['data']为空，说明这一天的所有分页都已经被爬取，直接跳出循环进入下一天
                
                data = json['result']['data']
                index = 1
                #index记录当前分页的第几个新闻
                for item in data:
                    title = item['title']
                    intro = item['intro']
                    for i in range(n):
                        if keywords[i] in title or keywords[i] in intro:
                            #判断标题和简介中是否含有搜索关键字
                            url = item['url']
                            f.write(title + "|" + url + "\n")
                            break 
                    print(str(index) + "th item Done!")   
                    index += 1  

                print(str(params['page']) + " Done!!")
                params['page'] += 1
    except:
        print("Exception!!!!")

if __name__ == '__main__':
    date = datetime(2019, 12, 8)
    #从2019-12-8开始爬取新闻链接

    for _ in range(366):
        #爬取一年的新闻链接

        t = int(date.timestamp())
        params = {
            'etime': t, 
            'stime': t + 86400, 
            'ctime': t + 86400, 
            'date': date.strftime("%Y-%m-%d"), 
            'page': 1
            #page参数记录分页
        }
        print(params['etime'])
        
        print("Date: " + params['date'])
        getURL(params['date'], params)
        print(params['date'] + " Done!!!")    
        date = addDay(date)
