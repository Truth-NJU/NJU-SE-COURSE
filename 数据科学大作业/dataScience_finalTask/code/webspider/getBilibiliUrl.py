import requests
from bs4 import BeautifulSoup
import pandas as pd

url = 'http://comment.bilibili.com/65927049.xml'
keywords = ["疫情", "新冠", "抗疫", "口罩", "病例", "复工", "防疫"]
headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.103 Safari/537.36'}
information = []

def getComments(url):
    response = requests.get(url)
    response.raise_for_status()
    response.encoding = response.apparent_encoding
    soup = BeautifulSoup(response.text, "html.parser")
    results = soup.find_all('d')
    comments = [x.text for x in results]
    print(len(comments))
    '''
    for comment in comments:
        print(comment)
    '''

def getUrls(keyword, page):
    url = "https://search.bilibili.com/all?order=click"
    params = {'keyword': keyword, 'page': page}
    response = requests.get(url, params = params, headers = headers)
    response.raise_for_status()
    response.encoding = response.apparent_encoding
    soup = BeautifulSoup(response.text, "html.parser")
    lis = soup.find_all('li', {'class': "video-item matrix"})
    for li in lis:
        attributes = li.a.attrs
        title = attributes['title']
        url = attributes['href']
        spans = li.find_all('span')
        watch = spans[3].text.strip()
        comments = spans[4].text.strip()
        time = spans[5].text.strip()
        item = [title, url, time, watch, comments]
        information.append(item)

if __name__ == '__main__':
    getUrls("疫情", 1)
    print(pd.DataFrame(information, columns = ["title", "url", "time", "watch", "comments"]))