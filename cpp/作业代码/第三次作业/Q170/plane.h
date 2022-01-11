#pragma once
#include<iostream>
#include<vector>
#include<string>
#include<algorithm>
using namespace std;

class plane {
public:
	int id = 0;
	string type = "";
	int takeOffTime = 0;
	int taskTime = 0;
	int landTime = 0;
	int startPrivilige = 0;
	int endPrivilige = 0;
	vector<plane> list;
	int status = 0;
	int joinTime = 0;
	int startClock = 0;//飞机起飞时间
	int downClock = 0;//飞机降落时间
	bool free = true; //判断甲板是否空闲
	int landFirstPri = 4;
	int landFirst = -1;
	/*status:
	0-不在甲板上
	1-在甲板上，待起飞
	2-起飞中
	3-执行任务中
	4-待降落
	5-降落中
	6-已降落
	7-飞机不参与
	*/

	plane() {

	}

	plane(int id, string type, int takeOffTime, int taskTime, int landTime, int status,
		int startClock, int downClock, int landFirst, int joinTime) {
		this->id = id;
		this->type = type;
		this->takeOffTime = takeOffTime;
		this->taskTime = taskTime;
		this->landTime = landTime;
		this->status = status;
		this->startClock = startClock;
		this->downClock = downClock;
		this->landFirst = landFirst;
		this->joinTime = joinTime;
		if (type == "battleplane") {
			this->startPrivilige = 2;
			this->endPrivilige = 2;
		}
		else if (type == "transportplane") {
			this->startPrivilige = 1;
			this->endPrivilige = 3;
		}
		else if (type == "helicopter") {
			this->startPrivilige = 3;
			this->endPrivilige = 1;
		}
		/*if (type == "battleplane") {
			this->startPrivilige = 2;
			this->endPrivilige = 2;
		}
		else if (type == "transportplane") {
			this->startPrivilige = 3;
			this->endPrivilige = 1;
		}
		else if (type == "helicopter") {
			this->startPrivilige = 1;
			this->endPrivilige = 3;
		}*/
	}

	void add(int id, string type, int takeOffTime, int taskTime, int landTime, int status) {
		this->list.push_back(plane::plane(id, type, takeOffTime, taskTime, landTime, status, -1, -1, -1, -1));
	}

	void setTime(int id, int takeOffTime, int taskTime, int landTime) {
		for (int i = 0; i < list.size(); i++) {
			if (list[i].id == id) {
				list[i].takeOffTime = takeOffTime;
				list[i].taskTime = taskTime;
				list[i].landTime = landTime;
			}
		}
	}

	static bool compStartTime(plane &a, plane &b) {
		return a.startClock < b.startClock;
	}

	void run() {
		sort(list.begin(), list.end(), compStartTime);
		int maxstartPri = 0;
		int maxstartLocal = 0;
		int maxendPri = 0;
		int maxendLocal = 0;
		//int statusTime = 0;
		int takeofftime = 0;
		int tasktime = 0;
		int landtime = 0;
		bool upOrdown = true;//true 起飞 false降落
		vector<int> taskingTime;//正在执行任务的飞机已经执行任务时长
		for (int i = 0; i < list.size(); i++) {
			taskingTime.push_back(0);

		}
		vector<int> up;//已经起飞的飞机
		for (int time = 0; time < 100; time++) {
			int m = 0;
			for (int i = 0; i < list.size(); i++) {
				if (list[i].status == 7) {
					m++;
				}
			}
			if (m == list.size()) break;
			for (int i = 0; i < list.size(); i++) {
				if (list[i].status == 3) {
					taskingTime[i]++;
					if (taskingTime[i] > list[i].taskTime) {
						list[i].status++;
					}
				}
				cout << list[i].status;
			
			}
			cout << endl;
			if (free) {
				//statusTime = 0;
				bool whetherlandFirst = false;
				for (int i = 0; i < list.size(); i++) {
					if (list[i].landFirst==time) {
						whetherlandFirst = true;
						upOrdown = false;
						list[i].status = 4;
					}
				}

				for (int i = 0; i < list.size(); i++) {
					//飞机在甲板上待起飞 且起飞时间符合当前时间
					if (list[i].status == 1
					&& !whetherlandFirst) {
						if (maxstartPri < list[i].startPrivilige) {
							maxstartPri = list[i].startPrivilige;
							maxstartLocal = i;
						}
						upOrdown = true;
					}
					else if (list[i].status == 4) {
						if (maxendPri < list[i].endPrivilige) {
							maxendPri = list[i].endPrivilige;
							maxendLocal = i;
						}
						upOrdown = false;
					}
					

				}
				if (upOrdown) {
					list[maxstartLocal].status = 2;
					list[maxstartLocal].startPrivilige=-1;
					if (up.size() != list.size()) {
						up.push_back(maxstartLocal);
					}
					//list[maxstartLocal].startClock = time;
					//当前正在起飞飞机的起飞需要的时间
					takeofftime = list[maxstartLocal].takeOffTime;
					//cout << list[2].startClock << endl;
					if (up.size() != list.size()) {
						for (int j = 0; j < list.size(); j++) {
							int count = 0;
							for (int i = 0; i < up.size(); i++) {
								if (j != up[i]) {
									count++;
								}
							}
							if (count == up.size() && time >= list[j].startClock) {//&& time< list[j].startClock
								//cout << j << " " << time << " " << list[j].startClock << endl;
								list[j].startClock += takeofftime;

							}
							count = 0;
						}
						//cout << endl;
					}
					this->free = false;
					time = list[maxstartLocal].startClock;
					
				}
				else if (!upOrdown) {
					list[maxendLocal].status = 5;
					//当前正在降落的飞机降落需要的时间
					landtime = list[maxendLocal].landTime;
					
					list[maxendLocal].downClock = time + landtime;
					
					list[maxendLocal].endPrivilige = -1;
					//cout << maxendLocal << " " << list[maxendLocal].downClock << " ";
					//cout << list[maxendLocal].downClock << endl;;
					this->free = false;  

				}
				
			}
			else {
				if (upOrdown) {
					if (time == list[maxstartLocal].startClock+ list[maxstartLocal].takeOffTime) {
						list[maxstartLocal].status++;
					}
					if (list[maxstartLocal].status == 3) {
						free = true;
						//statusTime = 0;
						maxstartPri = 0;
						maxstartLocal = 0;

					}
				}
				else if (!upOrdown) {
					if (time == list[maxendLocal].downClock) {
						list[maxendLocal].status++;
					}
					if (list[maxendLocal].status == 6) {
						list[maxendLocal].status = 7;
						//cout << time << endl;
						//list[maxendLocal].downClock=time;
						free = true;
						//statusTime = 0;
						maxendPri = 0;
						maxendLocal = 0;
					}
				}

			}

			//cout << endl;
		}
		//cout << list[2].startClock;
	}

	void joinPlane(int id, int joinTime) {
		for (int j = 0; j < list.size(); j++) {
			if (list[j].id == id) {
				list[j].status = 1;
				list[j].startClock = joinTime;
				list[j].joinTime = joinTime;
			}
		}
	}

	
	void landFirst2(int id, int time) {
		for (int j = 0; j < list.size(); j++) {
			if (list[j].id == id) {
				list[j].landFirst = time;
				list[j].endPrivilige = this->landFirstPri;
				this->landFirstPri++;
				break;
			}
		}
	}

	static bool comp(plane &a, plane &b) {
		return a.id < b.id;
	}


	void normal() {
		sort(list.begin(), list.end(), comp);
		for (int i = 0; i < list.size() - 1; i++) {
			cout << "p" << list[i].id << " " << list[i].type << " " << list[i].takeOffTime
				<< " " << list[i].taskTime << " " << list[i].landTime << endl;
		}
		int last = list.size() - 1;
		cout << "p" << list[last].id << " " << list[last].type << " " << list[last].takeOffTime
			<< " " << list[last].taskTime << " " << list[last].landTime << endl;
	}


	static bool compTime(plane &a, plane &b) {
		return a.downClock < b.downClock;
	}


	static bool sortByLand(plane &a, plane &b) {
		bool res;
		if (a.landFirst*b.landFirst < 0) {
			if (a.landFirst >= 0)
				return true;
			else
				return false;
		}
		else {
			if (a.landFirst >= 0 && a.landFirst != b.landFirst) {
				res = a.landFirst > b.landFirst;
			}
			else {
				res = (a.startClock + a.taskTime + a.takeOffTime) < (b.startClock + b.taskTime + b.takeOffTime);
				if ((a.startClock + a.taskTime + a.takeOffTime) == (b.startClock + b.taskTime + b.takeOffTime)) {
					res = a.endPrivilige < b.endPrivilige;
					if (a.endPrivilige == b.endPrivilige) {
						res = a.id < b.id;
					}
				}
			}
		}
		return res;
	}

	static bool comp2(plane &a, plane &b) {
		bool res = a.joinTime < b.joinTime;
		if (a.joinTime == b.joinTime) {
			res = a.startPrivilige < b.startPrivilige;
			if (a.startPrivilige == b.startPrivilige) {
				res = a.id < b.id;
			}
		}
		return res;
	}

	void schedule() {
		/*for (int i = 0; i < list.size(); ++i) {
			for (int j = i + 1; j < list.size(); ++j) {
				if (!comp2(list[i], list[j])) {
					plane temp = list[i];
					list[i] = list[j];
					list[j] = temp;
				}
			}
		}
		//sort(list.begin(), list.end(),comp2);
		bool allFly = false;
		int notFly = 0;
		int time = 0;
		while (!list.empty()) {
			int land = -1;
			int k = 0;
			for (; k < notFly; k++) {
				if (list[k].landFirst > -1) {
					if (time >= list[k].landFirst && time >= (list[k].startClock + list[k].taskTime)) {
						land = k;
					}
				}
				else {
					break;
				}
			}
			if (land != -1) {
				time += list[0].landTime;
				list[0].downClock = time;
				//cout << "p" << list[0].id << " " << list[0].startClock
				//	<< " " << time << endl;
				list.erase(list.begin());
				notFly--;
				notFly = notFly > -1 ? notFly : 0;
				continue;
			}
			else {
				if (notFly < list.size() && !allFly && time >= list[notFly].joinTime) {
					list[notFly].startClock = time;
					time += list[notFly].takeOffTime;
					notFly++;

					for (int i = 0; i < notFly; ++i) {
						for (int j = i + 1; j < notFly; ++j) {
							if (!sortByLand(list[i], list[j])) {
								plane temp = list[i];
								list[i] = list[j];
								list[j] = temp;
							}
						}
					}
					//sort(list.begin(), list.end(), sortByLand);
					if (notFly == list.size()) {
						allFly = true;
					}
					continue;
				}
				else {
					if (notFly > 0 && time >= list[k].startClock + list[k].taskTime) {
						time += list[k].landTime;
						list[k].downClock = time;
						//cout << "p" << list[k].id << " " << list[k].startClock
						//	<< " " << time << endl;
						list.erase(list.begin() + k);

						notFly--;
						notFly = notFly >= 0 ? notFly : 0;
						continue;

					}
				}
				time++;
			}
		}*/
		sort(list.begin(), list.end(), comp);
		sort(list.begin(), list.end(), compTime);
		for (int i = 0; i < list.size() - 1; i++) {
			cout << "p" << list[i].id << " " << list[i].startClock << " " << list[i].downClock << endl;
		}
		cout << "p" << list[list.size()-1].id << " " << list[list.size() - 1].startClock << " " << list[list.size() - 1].downClock << endl;
	}
};