# coding:utf-8

from snownlp import sentiment
from snownlp import SnowNLP

from snownlp import seg
# sentiment.train('neg.txt', 'pos.txt')
# sentiment.save('sentiment.marshal')


line = '|	|->	"要不是钟南山先生的名字，还有谁会注意到这一个月前投稿的视频呢？" '
s = SnowNLP(line)

print(s.sentiments)
