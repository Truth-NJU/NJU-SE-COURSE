import requests
import urllib3
import time
import re

urllib3.disable_warnings()

header = {
    "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Safari/537.36",
}


def get_oid(BV_CODE: str):
    # 截取bv号
    if "BV" == BV_CODE[:2]:
        bv = BV_CODE[2:]
    else:
        bv = BV_CODE
    # 根据bv号获得视频的链接
    video_url = f"https://www.bilibili.com/video/BV{bv}"
    r = requests.get(video_url, headers=header, verify=False)
    r.raise_for_status()
    # 找到oid号，text是评论所在的类
    return re.search(r'content="https://www.bilibili.com/video/av(\d+)/">', r.text).group(1)


# 获得并返回评论数据
def get_data(page: int, oid: str):
    time.sleep(2)  # 减少访问频率
    # 找到该接口的URL，利用requests获取html文档信息。利用json模块将其转化为字典
    api_url = f"https://api.bilibili.com/x/v2/reply?jsonp=jsonp&pn={page}&type=1&oid={oid}&sort=2&_={int(time.time())}"
    r = requests.get(api_url, headers=header, verify=False)
    return r.json()['data']['replies']


def get_comment(data):
    if not data:
        return
    # 向文件写入评论数据，过滤多余字符
    for item in data:
        message = re.sub(r'\t|\n|回复 @.*? :', '', item['content']['message'])
        f.write(f'|\t|->\t"{message}"\t\n')


if __name__ == '__main__':
    file = open('bilibili-url.txt')  # 链接所在文件
    for i in file:
        BV_CODE = i[31:43]  # 视频的BV号
        oid = get_oid(BV_CODE)
        video_time = i[-11:]

        f = open(f'/Users/taozehua/PycharmProjects/final-Spider/bilibili-comment/' + video_time + BV_CODE + '.txt', 'w',
                 encoding='utf-8')
        page = 1
        while True:
            data = get_data(page, oid)
            get_comment(data)  # 遍历当前页面所有回复
            end_page = 15
            if page == end_page:
                break
            page += 1
        f.close()
