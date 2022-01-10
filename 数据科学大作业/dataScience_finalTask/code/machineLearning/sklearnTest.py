#使用sklearn分类器
from sklearn.svm import LinearSVC
from nltk.classify.scikitlearn import SklearnClassifier


# 构建两个由元组构建的列表
from svmTest import extract_features

pos_tweets = [('I love this car', 'positive'),
              ('This view is amazing', 'positive'),
              ('I feel great this morning', 'positive'),
              ('I am so excited about the concert', 'positive'),
              ('He is my best friend', 'positive')]

neg_tweets = [('I do not like this car', 'negative'),
              ('This view is horrible', 'negative'),
              ('I feel tired this morning', 'negative'),
              ('I am not looking forward to the concert', 'negative'),
              ('He is my enemy', 'negative')]

# 分词：保留长度大于3的词进行切割

tweets = []

for (words, sentiment) in pos_tweets + neg_tweets:
    words_filtered = [e.lower() for e in words.split() if len(e) >= 3]
    tweets.append((words_filtered, sentiment))


classif = SklearnClassifier(LinearSVC())
svm_classifier = classif.train()
#  测试
tweet_negative2 = 'Your song is annoying'
svm_classifier.classify(extract_features(tweet_negative2.split()))
