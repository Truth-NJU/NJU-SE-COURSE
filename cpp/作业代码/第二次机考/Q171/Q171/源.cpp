#include<string>
#include<sstream>
#include"Weibo.h"
#include<vector>
#include<iostream>
using namespace std;


int main() {
	int n;
	cin >> n;
	//cout << n;
	Weibo* weibo = new Weibo;
	int i = 0;
	for (i; i < n ; i++) {
		string s;
		cin >> s;
		//return res;
		if (s == "post") {
			int userId;
			cin >> userId;
			int weiboId;
			cin >> weiboId;
			int type;
			cin >> type;
			weibo->postWeibo(userId, weiboId, type);
		}
		else if (s== "follow") {
			int followerId;
			cin >> followerId;
			int followeeId;
			cin >> followeeId;
			weibo->addFollows(followerId, followeeId);
		}
		else if (s== "unfollow") {
			int followerId;
			cin >> followerId;
			int followeeId;
			cin >> followeeId;
			weibo->unFollow(followerId, followeeId);
		}
		else if (s == "getFollows") {
			int userId;
			cin >> userId;
			weibo->getFollows(userId);
		}
		else if (s == "getFans") {
			int userId;
			cin >> userId;
			weibo->getFans(userId);
		}
		else if (s == "getWeibos") {
			int userId;
			cin >> userId;
			weibo->getWeibos(userId);
		}
		else if (s == "getRecent") {
			int userId;
			cin >> userId;
			int num;
			cin >> num;
			int type;
			cin >> type;
			weibo->checkWeibo(userId, num, type);
		}
	}
}

