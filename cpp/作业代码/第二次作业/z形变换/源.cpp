#include<iostream>
using namespace std;

int main() {
	char data[64];
	for (int i = 0; i < 64; i++) {
		data[i] = ',';
	}
	int n;
	cin >> data >> n;
	int len = 0;
	for (int i = 0; i < 64; i++) {
		if (data[i] != ',' && data[i] != ' ') {
			len++;
		}
		else {
			break;
		}
	}
	len--;
	char res[64];
	if (n == 1) {
		for (int i = 0; i < len; i ++) {
			cout << data[i];
		}
		return 0;
	}
	int m = 2 * n - 2;
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < len - i; j += m) {
			cout << data[j + i];
			if (i != 0 and i != n - 1 and j + m - i < len) {
				cout << data[j + m - i];
			}
		}
	}

	
}