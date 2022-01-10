# -*- coding: utf-8 -*-
import requests
from bs4 import BeautifulSoup


def readAndWrite(url):
    response = requests.get(url)
    response.encoding = 'utf-8'
    soup = BeautifulSoup(response.text, "html.parser")
    result = soup.find(attrs={'class': 'article'})
    try:
        ret = result.text
        ret = ret.replace("\n", "")
        ret = ret.replace(" ", "")
        ret = ret.replace("　　", "")
        ret += '\n'
    except:
        return

    with open('jstv_news.txt', 'a') as f:
        f.writelines(ret)


if __name__ == '__main__':


    # readAndWrite('http://news.jstv.com/a/20201206/1607262930567.shtml')
    f = open('jstv_news.txt')
    for i in f:
        url = i.replace("\n", "")
        readAndWrite(url)
