from snownlp import SnowNLP

text1 = '疫情有所好转了'
text2 = '疫情形式十分严峻'

s1 = SnowNLP(text1)
s2 = SnowNLP(text2)

print(s1.sentiments, s2.sentiments)


