#include<sstream>
#include<string>
#include"plane.h"
#include<vector>
using namespace std;


vector<string> split(string s);

int main() {
	int n;
	cin >> n;
	plane *pl = new plane();
	for (int i = 0; i < n+1; i++) {
		string s;
		getline(cin, s);
		vector<string> res = split(s);
		if (s[0]=='a') {
			string idstr = res[2];
			string temp = "";
			for (int i = 1; i < idstr.length(); i++) {
				temp += idstr[i];
			}
			int id = std::stoi(temp);
			if (res[1] == "battleplane") {
				pl->add(id, "battleplane", 2, 0, 2,0);
			}
			else if (res[1] == "transportplane") {
				pl->add(id, "transportplane", 3, 0, 3,0);
			}
			else if (res[1] == "helicopter") {
				pl->add(id, "helicopter", 1, 0, 1,0);
			}
		}
		else if (s[0] == 's' && s[1]=='e') {
			string idstr = res[1];
			string temp = "";
			for (int i = 1; i < idstr.length(); i++) {
				temp += idstr[i];
			}
			int id = std::stoi(temp);
			int takeoffTime = std::stoi(res[2]);
			int taskTime = std::stoi(res[3]);
			int landTime = std::stoi(res[4]);
			pl->setTime(id, takeoffTime, taskTime, landTime);
		}
		else if (s[0] == 'j') {
			string idstr = res[1];
			string temp = "";
			for (int i = 1; i < idstr.length(); i++) {
				temp += idstr[i];
			}
			int id = std::stoi(temp);
			int joinTime = std::stoi(res[2]);
			pl->joinPlane(id, joinTime);
		}
		else if (s[0] == 'l') {
			string idstr = res[2];
			string temp = "";
			for (int i = 1; i < idstr.length(); i++) {
				temp += idstr[i];
			}
			int id = std::stoi(temp);
			int time = std::stoi(res[1]);
			pl->landFirst2(id, time);
		}
		else if (s[0] == 'n') {
			pl->normal();
		}
		else if (s[0] == 's' && s[1]=='c') {
			pl->run();
			pl->schedule();
		}
	}
	return 0;
	
}

vector<string> split(string s) {
	vector<string> res;
	//�ݴ��word�ж�ȡ���ַ��� 
	string result;
	//���ַ�������input�� 
	stringstream input(s);
	//���������result�У�������res�� 
	while (input >> result)
		res.push_back(result);
	return res;
}