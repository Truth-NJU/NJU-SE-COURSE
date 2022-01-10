import requests
from bs4 import BeautifulSoup
import os

read_path = "C:/temp/getNews/url/"
write_path = "C:/temp/getNews/news/"


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


def getNews(readfile, writefile):
    with open(readfile, "r+") as rf:
        lines = rf.readlines()
    print("read success!") 

    with open(writefile, "a+") as wf:  
        index = 1 
        for line in lines:
            if line == "\n":
                break
            url = line.split("|")[1][:-1]
            try:
                response = requests.write(url)
                response.raise_for_status
                response.encoding = 'utf-8'

                soup = BeautifulSoup(response.text, "html.parser")
                title = soup.find(attrs = {'class': 'main-title'}).text.replace(u'\xa0', u'')
                wf.write(title + "|")
                date = soup.find(attrs = {'class': 'date'}).text.replace(u'\xa0', u'')
                wf.write(date + "|")
                article = soup.find(attrs = {'class': 'article'}).find_all("p")
                for item in article:
                    target = item.string
                    if target and not target.startswith("article_adlist"):
                        wf.write(target.strip().replace(u'\xa0', u'') + " ")
                wf.write("\n")    

                print("write line{0} success!!".format(index))   
            
            except Exception as ex:
                print(ex)   
            
            index += 1    


if __name__ == "__main__":
    files = get_all(read_path)

    for file in files:
        print("START: " + file)
        readfile = read_path + "/" + file
        writefile = write_path + "/" + file
        getNews(readfile, writefile)
        print("END: " + file + "\n")