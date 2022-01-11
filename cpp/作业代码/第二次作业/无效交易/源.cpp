#include <iostream>
using namespace std;
bool compare(char a[], char b[]);

struct {
	char name[11];
	int time;
	int amount;
	char city[11];
}info[100];

int main() {
	int n;
	cin >> n;
	for (int i = 0; i < n; i++) {
		cin >> info[i].name >> info[i].time >> info[i].amount >> info[i].city;
	}
	int state[100];  //记录是否有效，1代表有效，0代表无效
	for (int i = 0; i < 100; i++) {
		state[i] = 1;
	}

	for (int i = 0; i < n - 1; i++) {
		for (int j = i + 1; j < n; j++) {
			if (!compare(info[i].name, info[j].name) && compare(info[i].city, info[j].city) && abs(info[j].time - info[i].time) <= 60) {
				state[i] = 0;
				state[j] = 0;
			}
		}

	}
	for (int i = 0; i < n; i++) {
		if (info[i].amount > 1000) {
			state[i] = 0;
		}
	}
	int num = 0;
	int count = 0;
	cout << '[';
	for (int i = 0; i < n; i++) {
		if (state[i] == 0) {
			num++;
		}
	}
	for (int i = 0; i < n; i++) {
		if (state[i] == 0) {
			cout << '\"' << info[i].name << ',' << info[i].time << ',' << info[i].amount << ',' << info[i].city << '\"';
			count++;
			if (count < num) {
				cout << ',';
			}
		}
	}
	cout << ']' << endl;
	return 0;
}

bool compare(char a[], char b[]) {
	for (int i = 0; i < 11; i++) {
		if (a[i] != b[i]) {
			return true;
		}
	}
	return false;
}

