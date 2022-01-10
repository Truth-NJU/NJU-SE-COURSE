import json
import re
import time
import requests
from lxml import etree


class Weibospider:
    def __init__(self):
        # cookie 填的是本机登陆网页版微博时的cookie
        self.headers = {
            "cookie": "login_sid_t=95e31daa102176d1debb61e844641c26; cross_origin_proto=SSL; _s_tentry=passport.weibo.com; Apache=8896751903602.45.1611205480375; SINAGLOBAL=8896751903602.45.1611205480375; ULV=1611205480379:1:1:1:8896751903602.45.1611205480375:; wb_view_log_5688140475=1440*9002; wb_view_log=1440*9002; WBStorage=8daec78e6a891122|undefined; crossidccode=CODE-tc-1L2uul-2O9Vrn-3NXFKeQ59aniHK8bfdc35; SSOLoginState=1611214485; SUB=_2A25NDV7FDeRhGeNI41oQ9C7IzDmIHXVuDmKNrDV8PUJbkNAKLWTskW1NSDShAmgWg52xu-LVa7tA1onXiM57xVOs; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WW0TQIzrxRpmDardhs5o9p15NHD95QfSonReKB7ShMfWs4DqcjQi--ciK.RiKLsi--Ri-8si-82i--fi-isiKn0i--ciKnXi-isxsHLM5tt; wvr=6; UOR=,,graph.qq.com; webim_unReadCount=%7B%22time%22%3A1611214550618%2C%22dm_pub_total%22%3A4%2C%22chat_group_client%22%3A0%2C%22chat_group_notice%22%3A0%2C%22allcountNum%22%3A42%2C%22msgbox%22%3A0%7D",
            "user-agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36",
        }
        self.proxy = {
            'HTTP': 'HTTP://180.125.70.78:9999',
            'HTTP': 'HTTP://117.90.4.230:9999',
            'HTTP': 'HTTP://111.77.196.229:9999',
        }

    def parse_home_url(self, url):  # 处理解析首页面的详细信息（不包括两个通过ajax获取到的页面）
        res = requests.get(url, headers=self.headers)
        response = res.content.decode().replace("\\", "")
        every_id = re.compile('name=(\d+)', re.S).findall(response)  # 获取次级页面需要的id
        home_url = []
        for id in every_id:
            url = 'https://weibo.com/aj/v6/comment/big?ajwvr=6&id={}&from=singleWeiBo'.format(id)
            home_url.append(url)
        return home_url

    def parse_comment(self, url):
        # 爬取评论和评论的时间
        # 利用requests获取html文档信息。利用json模块将其转化为字典
        res = requests.get(url, headers=self.headers)
        response = res.json()
        count = response['data']['count']
        # 使用XPath进行定位查找内容
        html = etree.HTML(response['data']['html'])
        name = html.xpath("//div[@class='list_li S_line1 clearfix']/div[@class='WB_face W_fl']/a/img/@alt")  # 解析评论人的姓名
        info = html.xpath("//div[@node-type='replywrap']/div[@class='WB_text']/text()")  # 解析评论信息
        info = "".join(info).replace(" ", "").split("\n")
        comment_time = html.xpath("//div[@class='WB_from S_txt2']/text()")  # 解析评论时间
        comment_info_list = []
        # 将评论和时间存入
        for i in range(len(name)):
            item = {}
            # 控制存进去的评论数量，过滤不需要的信息
            if info[i] != "：转发微博" and info[i] != "：" and i <= 6:
                item["comment"] = info[i]  # 存储评论的信息
                item["comment_time"] = comment_time[i]  # 存储评论时间
                comment_info_list.append(item)
            else:
                break
        return count, comment_info_list

    def write_file(self, path_name, content_list):
        for content in content_list:
            with open('/Users/taozehua/PycharmProjects/final-Spider/data/' + path_name, "a",
                      encoding="UTF-8") as f:
                f.write(json.dumps(content, ensure_ascii=False))
                f.write("\n")

    # 获取详情页面的信息，相关的评论信息也是通过ajax加载出来的
    def run(self):
        # 要爬取的微博大V的网址
        start_url = 'https://weibo.com/xjb?is_all=1&stat_date=202003&page=4#feedtop'
        start_ajax_url1 = 'https://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6&domain=100406&is_all=1&page={0}&pagebar=0&pl_name=Pl_Official_MyProfileFeed__20&id=1004065644764907&script_uri=/u/5644764907&pre_page={0}'
        start_ajax_url2 = 'https://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6&domain=100406&is_all=1&page={0}&pagebar=1&pl_name=Pl_Official_MyProfileFeed__20&id=1004065644764907&script_uri=/u/5644764907&pre_page={0}'
        for i in range(3):  # 爬取微博的前3页
            home_url = self.parse_home_url(start_url.format(i + 1))  # 获取每一页的微博
            ajax_url1 = self.parse_home_url(start_ajax_url1.format(i + 1))  # ajax加载页面的微博
            ajax_url2 = self.parse_home_url(start_ajax_url2.format(i + 1))  # ajax第二页加载页面的微博
            all_url = home_url + ajax_url1 + ajax_url2
            for j in range(len(all_url)):
                print(all_url[j])
                path_name = "第{}条微博相关评论.txt".format(i * 45 + j + 1)
                all_count, comment_info_list = self.parse_comment(all_url[j])
                self.write_file(path_name, comment_info_list)
                # 读取微博评论的前14条评论
                for num in range(1, 2):
                    if num * 15 < int(all_count) + 15:
                        comment_url = all_url[j] + "&page={}".format(num + 1)
                        try:
                            count, comment_info_list = self.parse_comment(comment_url)
                            self.write_file(path_name, comment_info_list)
                        except Exception as e:
                            print("Error:", e)
                            time.sleep(10)
                            count, comment_info_list = self.parse_comment(comment_url)
                            self.write_file(path_name, comment_info_list)
                        del count
                        time.sleep(0.2)
                print("第{}微博信息获取完成！".format(i * 45 + j + 1))


if __name__ == '__main__':
    weibo = Weibospider()
    weibo.run()