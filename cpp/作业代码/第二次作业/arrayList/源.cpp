#include <iostream>
using namespace std;

int main() {
	int n;
	cin >> n;
	int num[100];
	int trueLen = 0;
	int size = 0;
	for (int i =1; i <= n; i++) {
		char s[50];
		int x;
		cin >> s;
		if (s[0] == 'a') {
			cin >> x;
			num[trueLen] = x;
			trueLen++;
			if (i == 1) {
				size = 10;
			}
			if (trueLen == size) {
				size = size + (size / 2);
			}
		}
		else if (s[0] == 'r') {
			cin >> x;
			for (int i = 0; i < trueLen; i++) {
				if (num[i] == x) {
					for (int j = i; j < trueLen; j++) {
						num[j] = num[j + 1];
					}
					trueLen--;
					break;
				}
			}
		}
		else if (s[0] == 'g' && s[3] == 'S') {
			cout << trueLen << endl;

		}
		else if (s[0] == 'g' && s[3] == 'C') {
			cout << size<<endl;
		}
		else {
			cin >> x;
			if (x > trueLen-1) cout << "-1" << endl;
			else cout << num[x] << endl;
		}
	}
}