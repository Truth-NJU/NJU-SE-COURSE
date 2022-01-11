#include<iostream>
#include<string>
#include<vector>
#include "rectangle.h"
#include<sstream>
using namespace std;

vector<string> split(string s);

int main() {
	//split("Set P2 255 0 0");
	int n;
	cin >> n;
	rectangle *rec = new rectangle();
	for (int i = 0; i < n+1; i++) {
		string s;
		getline(cin, s);
		if (s[0] == 'A') {
			string pid = split(s)[split(s).size() - 1];
			//cout << pid;
			string sid = "";
			for (int i = 1; i<pid.length(); i++) {
				sid += pid[i];
			}
			int id = std::stoi(sid);
			if (s[4] == 'n') {
				rec->add(id, "normal", 0, 0, 0);
			}
			else if (s[4] == 's') {
				rec->add(id, "single", 0, 0, 0);
			}
			else if (s[4] == 'r') {
				rec->add(id, "reverse", 0, 0, 0);
			}
		}
		else if (s[0] == 'G') {
			vector<string> res = split(s);
			int n =std::stoi(res[1]);
			string sgid = res[res.size() - 1];
			string temp = "";
			for (int i = 1; i < sgid.length(); i++) {
				temp += sgid[i];
			}
			int gid = std::stoi(temp);
			vector<int> id;
			for (int j = 2; j < res.size() - 1; j++) {
				string temp = res[j];
				string temp2;
				for (int i = 1; i < temp.length(); i++) {
					temp2 += temp[i];
				}
				int idj = std::stoi(temp2);
				id.push_back(idj);
			}
			rec->setGroup(id, gid);
			
		}
		else if (s[0] == 'S') {
			vector<string> res = split(s);
			string sid = res[1];
			string temp = "";
			for (int i = 1; i < sid.length(); i++) {
				temp += sid[i];
			}
			int id = std::stoi(temp);
			int colorX = 0, colorY = 0, colorZ = 0;
			string x = res[2], y =res[3], z =res[4];
			colorX = std::stoi(x);
			colorY = std::stoi(y);
			colorZ = std::stoi(z);
			if (s[4] == 'P') {
				rec->changeColor(id,colorX,colorY,colorZ);
			}
			else if (s[4] == 'G') {
				rec->changeGroupColor(id, colorX, colorY, colorZ);
			}
		}
		//for (int i = 0; i < rec->list.size(); i++) {
		//	cout << rec->list[i].id<<" "<< rec->list[i].type<<" "<<rec->list[i].colorX<<" "
		//		<< rec->list[i].colorY<<" "<< rec->list[i].colorZ<< endl;
		//}
	}
	string s;
	getline(cin, s);
	//cout << s;
	if (s[0] == 'N') {
		rec->normal();
	}
	else if (s[0] == 'G') {
		rec->gray();
	}
}


vector<string> split(string s) {
	vector<string> res;
	//暂存从word中读取的字符串 
	string result;
	//将字符串读到input中 
	stringstream input(s);
	//依次输出到result中，并存入res中 
	while (input >> result)
		res.push_back(result);
	return res;
}