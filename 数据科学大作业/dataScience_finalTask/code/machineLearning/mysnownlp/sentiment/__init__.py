# -*- coding: utf-8 -*-
from __future__ import unicode_literals

import os
import codecs

from .. import normal
from .. import seg
from ..classification.bayes import Bayes

data_path = os.path.join(os.path.dirname(os.path.abspath(__file__)),
                         'sentiment.marshal')


class Sentiment(object):

    def __init__(self):
        self.classifier = Bayes()  # 使用的是Bayes的模型

    def save(self, fname, iszip=True):
        self.classifier.save(fname, iszip)  # 保存最终的模型

    def load(self, fname=data_path, iszip=True):
        self.classifier.load(fname, iszip)  # 加载贝叶斯模型

    # 分词以及去停用词的操作    
    def handle(self, doc):
        words = seg.seg(doc)  # 分词
        words = normal.filter_stop(words)  # 去停用词
        return words  # 返回分词后的结果

    def train(self, neg_docs, pos_docs):
        data = []
        # 读入负样本
        for sent in neg_docs:
            data.append([self.handle(sent), 'neg'])
        # 读入正样本
        for sent in pos_docs:
            data.append([self.handle(sent), 'pos'])
        # 调用的是Bayes模型的训练方法
        self.classifier.train(data)

    def classify(self, sent):
        # 1、调用sentiment类中的handle方法
        # 2、调用Bayes类中的classify方法
        ret, prob = self.classifier.classify(self.handle(sent))  # 调用贝叶斯中的classify方法
        if ret == 'pos':
            return prob
        return 1 - prob


classifier = Sentiment()
classifier.load()


# 训练新模型的接口函数
def train(neg_file, pos_file):
    neg = codecs.open(neg_file, 'r', 'utf-8').readlines()
    pos = codecs.open(pos_file, 'r', 'utf-8').readlines()
    neg_docs = []
    pos_docs = []
    for line in neg:
        neg_docs.append(line.rstrip("\r\n"))
    for line in pos:
        pos_docs.append(line.rstrip("\r\n"))
    global classifier
    classifier = Sentiment()
    classifier.train(neg_docs, pos_docs)


# 保存模型的接口函数
def save(fname, iszip=True):
    classifier.save(fname, iszip)


def load(fname, iszip=True):
    classifier.load(fname, iszip)


# 心态分析函数
def classify(sent):
    return classifier.classify(sent)
