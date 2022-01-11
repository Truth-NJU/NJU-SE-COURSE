#include <iostream>
using namespace std;


int main() {
	char data[10][10];
	for (int i = 0; i < 9; i++) {
		for (int j = 0; j < 9; j++) {
			cin >> data[i][j];
		}
	}
	int part[9];
	int row[9];
	int col[9];
	for (int i = 0; i < 9; i++) {
		for (int j = 0; j < 9; j++) {
			if (data[i][j] != '.') {
				if (row[data[i][j] - '1'] == 1) {
					cout << "false" << endl;
					return 0;
				}
				else {
					row[data[i][j] - '1'] = 1;
				}
			}
			if (data[j][i] != '.') {
				if (col[data[j][i] - '1'] == 1) {
					cout << "false"<< endl;
					return 0;
				}
				else {
					col[data[j][i] - '1'] = 1;
				}
			}
			int x = 3 * (i / 3) + j / 3;
			int y = 3 * (i % 3) + j % 3;
			if (data[x][y] != '.') {
				if (part[data[x][y] - '1'] == 1) {
					cout << "false" << endl;
					return 0;
				}
				else {
					part[data[x][y] - '1'] = 1;
				}
			}
		}
		for (int m = 0; m < 9; m++) {
			row[m] = 0;
			col[m] = 0;
			part[m] = 0;
		}

	}
	
	cout << "true" << endl;
}


