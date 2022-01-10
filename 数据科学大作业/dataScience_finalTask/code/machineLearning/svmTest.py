# 导入库
import nltk
from sklearn.svm import LinearSVC
from nltk.classify.scikitlearn import SklearnClassifier

# 构建两个由元组构建的列表
pos_tweets = [('疫情有所好转', 'positive'),
              ('人们众志成城，一心抗疫', 'positive'),
              ('口罩生产大大加快', 'positive'),
              ('疫情有了极大的缓和', 'positive'),
              ('许多医护人员援助武汉', 'positive')]

neg_tweets = [('疫情在各地爆发', 'negative'),
              ('口罩供货不足', 'negative'),
              ('群众生活受到极大影响', 'negative'),
              ('感染人数暴增', 'negative'),
              ('因为疫情死亡的人数已经成千上万', 'negative')]

# 分词：保留长度大于3的词进行切割

tweets = []

for (words, sentiment) in pos_tweets + neg_tweets:
    words_filtered = [e.lower() for e in words.split() if len(e) >= 3]
    tweets.append((words_filtered, sentiment))


# get the word lists of tweets
def get_words_in_tweets(tweets):
    all_words = []
    for (words, sentiment) in tweets:
        all_words.extend(words)
    return all_words


# get the unique word from the word list
def get_word_features(wordlist):
    wordlist = nltk.FreqDist(wordlist)  # 统计词语出现的频次
    word_features = wordlist.keys()
    return word_features


word_features = get_word_features(get_words_in_tweets(tweets))  # 目的是获得一个分词的列表
' '.join(word_features)


# 特征提取
def extract_features(document):
    document_words = set(document)  # set() 函数创建一个无序不重复元素集
    features = {}
    for word in word_features:
        features['contains(%s)' % word] = (word in document_words)
    return features  # 是否包含测试集中的单词


training_set = nltk.classify.util.apply_features(extract_features, tweets)  ##  构建一个分类训练集

# 使用sklearn分类器


classif = SklearnClassifier(LinearSVC())
svm_classifier = classif.train(training_set)
#  测试
tweet_negative2 = '情况恶化'
print(svm_classifier.classify(extract_features(tweet_negative2.split())))


# 用于测试的tweets

test_tweets = [
    (['feel', 'happy', 'this', 'morning'], 'positive'),
    (['larry', 'friend'], 'positive'),
    (['not', 'like', 'that', 'man'], 'negative'),
    (['house', 'not', 'great'], 'negative'),
    (['your', 'song', 'annoying'], 'negative')
]

# 验证效果
def classify_tweet(tweet):
    return svm_classifier.classify(extract_features(tweet))


total = accuracy = float(len(test_tweets))
for tweet in test_tweets:
    if classify_tweet(tweet[0]) != tweet[1]:
        accuracy -= 1
print('Total accuracy: %f%% (%d/5).' % (accuracy / total * 100, accuracy))
