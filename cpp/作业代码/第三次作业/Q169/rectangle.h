#pragma once
using namespace std;
#include<vector>
#include <string>
#include<algorithm>
class rectangle
{
public:
	int id = 0;
	int gid = 0;
	string type = "";
	int colorX = 0;
	int colorY = 0;
	int colorZ = 0;
	vector<rectangle> list;
	vector<vector<int>> group; //前几位是index 最后一位是gid

	rectangle() {
	};

	rectangle(int id, string type, int colorX, int colorY, int colorZ) {
		this->id = id;
		this->type = type;
		this->colorX = colorX;
		this->colorY = colorY;
		this->colorZ = colorZ;

	}

	void add(int id, string type, int colorX, int colorY, int colorZ) {
		this->list.push_back(rectangle::rectangle(id, type, colorX, colorY, colorZ));
	}

	void changeColor(int id, int colorX, int colorY, int colorZ) {
		for (int i = 0; i < list.size(); i++) {
			if (list[i].id == id) {
				if (list[i].type == "normal") {
					list[i].colorX = colorX;
					list[i].colorY = colorY;
					list[i].colorZ = colorZ;
				}
				else if (list[i].type == "reverse") {
					list[i].colorX = 255 - colorX;
					list[i].colorY = 255 - colorY;
					list[i].colorZ = 255 - colorZ;
				}
				else if (list[i].type == "single") {
					list[i].colorX = colorX;
					list[i].colorY = 0;
					list[i].colorZ = 0;
				}
				break;
			}
		}
	}

	void setGroup(vector<int> id, int gid) {
		vector<int> index;
		for (int i = 0; i < id.size(); i++) {
			index.push_back(id[i]);
		}
		index.push_back(gid);
		this->group.push_back(index);
	}

	void changeGroupColor(int gid, int colorX, int colorY, int colorZ) {
		for (int i = 0; i < this->group.size(); i++) {
			int size = this->group[i].size();
			if (this->group[i][size - 1] == gid) {
				for (int j = 0; j < size - 1; j++) {
					int id = this->group[i][j];
					this->changeColor(id, colorX, colorY, colorZ);
				}
			}
		}
	}

	static bool comp(rectangle &a, rectangle &b) {
		return a.id < b.id;
	}

	void normal() {
		sort(list.begin(), list.end(), comp);
		for (int i = 0; i < list.size(); i++) {
			cout << "P" << list[i].id << " " << list[i].colorX
				<< " " << list[i].colorY << " " << list[i].colorZ << endl;
		}
		/*int minId = 1000;
		int minLocal = 0;
		while (list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				if (list[i].id < minId) {
					minId = list[i].id;
					minLocal = i;
				}
			}
			cout << "P" << list[minLocal].id << " " << list[minLocal].colorX
				<< " " << list[minLocal].colorY << " " << list[minLocal].colorZ << endl;
			//删除单个元素，从0开始计数，删除第i个元素
			list.erase(list.begin() + minLocal);
		}*/
	}

	static bool compGray(pair<double,int> &a,pair<double,int> &b) {
		return a.first < b.first;
	}

	static bool compId(pair<double, int> &a, pair<double, int> &b) {
		return a.second < b.second;
	}

	void gray() {
		vector<pair<double,int>> gray;
		for (int i = 0; i < list.size(); i++) {
			double t = (list[i].colorX*0.299 +
				list[i].colorY*0.587 + list[i].colorZ*0.114);
			gray.push_back(make_pair(t, list[i].id));
		}
		sort(gray.begin(), gray.end(), compId);
		sort(gray.begin(), gray.end(), compGray);
		for(int i=0;i<gray.size();i++){
			for (int j = 0; j < list.size(); j++) {
				if (gray[i].second == list[j].id) {
					cout << "P" << list[j].id << " " << list[j].colorX
						<< " " << list[j].colorY << " " << list[j].colorZ << endl;
					break;
				}
			}
		}

	}
};

