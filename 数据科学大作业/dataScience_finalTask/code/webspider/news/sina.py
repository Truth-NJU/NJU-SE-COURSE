# -*- coding: utf-8 -*-
import requests
from bs4 import BeautifulSoup


def readAndWrite(url):
    response = requests.get(url)
    response.encoding = 'utf-8'
    soup = BeautifulSoup(response.text, "html.parser")
    result = soup.find(attrs={'id': 'article'})
    try:
        ret = result.text[3:]
        ret = ret.replace("\n", "")
        ret = ret.replace(" ", "")
        ret += '\n'
    except:
        return

    with open('sina_news.txt', 'a') as f:
        f.writelines(ret)
        return ret


if __name__ == '__main__':
    f = open('sina_news.txt')

    # print(readAndWrite('https://k.sina.com.cn/article_2810373291_a782e4ab02001wz7o.html?from=news&subch=onews'))
    count = 0
    for i in f:
        url = i.replace("\n", "")
        readAndWrite(url)
        count += 1