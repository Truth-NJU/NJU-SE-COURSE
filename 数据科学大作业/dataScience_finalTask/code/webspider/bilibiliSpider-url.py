import requests
from bs4 import BeautifulSoup

url = 'http://comment.bilibili.com/65927049.xml'
keywords = ["疫情", "新冠", "抗疫", "口罩", "病例", "钟南山", "防疫", "火神山", "雷神山", "肺炎"]
headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.103 Safari/537.36'}


# 用于获取与疫情有关的b站视频的链接
def getUrls(keyword, page):
    url = "https://search.bilibili.com/all?order=click"
    params = {'keyword': keyword, 'page': page}
    response = requests.write(url, params=params, headers=headers)
    response.raise_for_status()
    response.encoding = response.apparent_encoding
    soup = BeautifulSoup(response.text, "html.parser")
    lis = soup.find_all('li', {'class': "video-item matrix"})

    for li in lis:
        attributes = li.a.attrs
        spans = li.find_all('span')
        with open('bilibili-url.txt', 'a') as f:
            f.writelines('https:' + attributes['href'] + " " + spans[5].text.strip() + '\n')


if __name__ == '__main__':
    for i in keywords:
        getUrls(i, 8)
