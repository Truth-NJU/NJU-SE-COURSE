#include <iostream>
using namespace std;

int main() {
	int n, x;
	cin >> n >> x;
	int data[100][100];
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j++) {
			cin >> data[i][j];
		}
	}
	int count = 0;
	while (x > 360) {
		x -= 360;
	}
	if (x ==90) {
		for (int i = 0; i < n; i++) {
			for (int j = n-1; j >=0; j--) {
				cout << data[j][i] << ' ';
				count++;
				if (count == n) {
					cout << endl;
					count = 0;
				}
			}
		}
	}
	else if (x == 180) {
		for (int i = n-1; i >=0; i--) {
			for (int j = n - 1; j >= 0; j--) {
				cout << data[i][j] << ' ';
				count++;
				if (count == n) {
					cout << endl;
					count = 0;
				}
			}
		}
	}
	else if (x == 270) {
		for (int i = n-1; i >= 0; i--) {
			for (int j =0; j <n; j++) {
				cout << data[j][i] << ' ';
				count++;
				if (count == n) {
					cout << endl;
					count = 0;
				}
			}
		}
	}
	else if (x == 360) {
		for (int i = 0; i <n; i++) {
			for (int j = 0; j < n; j++) {
				cout << data[i][j] << ' ';
				count++;
				if (count == n) cout << endl;
			}
		}
	}
}