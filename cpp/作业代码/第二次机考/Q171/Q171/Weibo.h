#pragma once
#include<iostream>
#include<vector>
#include<string>
#include<algorithm>
using namespace std;


class Weibo
{
public:
	int userId = 0;
	int weiboId = 0;
	int type = 0; //0代表仅自己可见，1代表好友圈可见，2代表粉丝可见 
	vector<Weibo> weibo;
	vector<vector<int>>user; //0为userid， 1为关注数，2为粉丝数，3为微博数
	vector<pair<int,int>> followId; //first userId, second 关注的人的id
	vector<pair<int, int>> fanId;//first userId, second 粉丝的id

	Weibo() {
	}

	Weibo(int userId, int weiboId, int type) {
		this->userId = userId;
		this->weiboId = weiboId;
		this->type = type;
	}

	void postWeibo(int userId, int weiboId, int type) {
		this->weibo.push_back(Weibo::Weibo(userId, weiboId, type));
		if (user.size() == 0) {
			vector<int> temp;
			temp.push_back(userId);
			temp.push_back(0);
			temp.push_back(0);
			temp.push_back(1);
			user.push_back(temp);
		}
		else {
			bool whetherNew =true;
			for (int i = 0; i < user.size(); i++) {
				if (user[i][0] == userId) {
					user[i][3]++;
					whetherNew = false;
					break;
				}
			
			}
			if (whetherNew) {
				vector<int> temp;
				temp.push_back(userId);
				temp.push_back(0);
				temp.push_back(0);
				temp.push_back(1);
				user.push_back(temp);
			}
		}
	}

	void addFollows(int followerId, int followeeId) {
		followId.push_back(make_pair(followerId, followeeId));
		fanId.push_back(make_pair(followeeId, followerId));
		bool whetherNew1 = true;
		bool whetherNew2 = true;
		for (int i = 0; i < user.size(); i++) {
			if (user[i][0] == followeeId) {
				user[i][2]++;
				whetherNew1 = false;
				break;
			}

		}
		for (int i = 0; i < user.size(); i++) {
			if (user[i][0] == followerId) {
				user[i][1]++;
				whetherNew2 = false;
				break;
			}


		}
		if (whetherNew2) {
			vector<int> temp;
			temp.push_back(followerId);
			temp.push_back(1);
			temp.push_back(0);
			temp.push_back(0);
			user.push_back(temp);

		}
		if (whetherNew1) {
			vector<int> temp;
			temp.push_back(followeeId);
			temp.push_back(0);
			temp.push_back(1);
			temp.push_back(0);
			user.push_back(temp);
		}
	}

	void unFollow(int followerId, int followeeId) {
		for (int i = 0; i < user.size(); i++) {
			if (user[i][0] == followerId) {
				user[i][1]--;
				break;
			}
		}
		for (int i = 0; i < user.size(); i++) {
			if (user[i][0] == followeeId) {
				user[i][2]--;
				break;
			}
		}
		for (int i = 0; i < followId.size(); i++) {
			if (followId[i].first == followerId &&
				followId[i].second == followeeId) {
				followId[i].first = -1;
				followId[i].second = -1;
			}
		}
	}

	void getFollows(int userId) {
		for (int i = 0; i < user.size(); i++) {
			if (user[i][0] == userId) {
				cout << user[i][1] << endl;
				break;
			}
		}
	}

	void getFans(int userId) {
		for (int i = 0; i < user.size(); i++) {
			if (user[i][0] == userId) {
				cout << user[i][2] << endl;
				break;
			}
		}
	}

	void getWeibos(int userId) {
		for (int i = 0; i < user.size(); i++) {
			if (user[i][0] == userId) {
				cout << user[i][3] << endl;
				break;
			}
		}
	}



	void checkWeibo(int userId, int  num, int type) {
		if (type == 0) {
			int count=0;
			for (int i = weibo.size() - 1; i >= 0; i--) {
				if (count != num && weibo[i].userId == userId) {
					count++;
					cout << weibo[i].weiboId<<" ";
				}
			}
			cout << endl;
		}
		else if (type == 1) {
			vector<pair<int, int>> quan;
			for (int j = 0; j < followId.size(); j++) {
				for (int m = 0; m < followId.size(); m++) {
					if (followId[j].second == followId[m].first
						&& followId[j].first == followId[m].second
						&& j!=m && followId[j].second!=-1 && followId[m].second!=-1) {
						quan.push_back(make_pair(followId[j].first, followId[m].first));
					}
				}
			}
			/*for (int i = 0; i < quan.size(); i++) {
				cout << quan[i].first << "," << quan[i].second << endl;
			}*/
			int count = 0;
			for (int i = weibo.size() - 1; i >= 0; i--) {
				if (count != num && weibo[i].userId == userId) {
					count++;
					cout << weibo[i].weiboId << " ";
				}
				if (count != num) {
					for (int j = 0; j < quan.size(); j++) {
						if (quan[j].first == userId  && 
							weibo[i].userId ==quan[j].second &&
							(weibo[i].type==1||weibo[i].type==2) && weibo[i].userId != userId) {
							count++;
							cout << weibo[i].weiboId << " ";
						}
					}
				}
			}
			cout << endl;
		}
		else if (type == 2) {
			int count = 0;
			for (int i = weibo.size() - 1; i >= 0; i--) {
				if (count != num && weibo[i].userId == userId) {
					count++;
					cout << weibo[i].weiboId << " ";
				}
				if (count != num) {
					for (int j = 0; j < followId.size(); j++) {
						if (followId[j].first == userId &&
							weibo[i].userId == followId[j].second 
							&&followId[j].second != -1 
							&& weibo[i].type == 2 && weibo[i].userId!= userId) {
							count++;
							cout << weibo[i].weiboId << " ";
						}
					}
				}
			}
			cout << endl;
		}
	}
};

