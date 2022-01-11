#include <iostream>
using namespace std;
 
int main() {
	int m, n;
	cin >> m >> n;
	int len = m * n;
	int data[100][100];
	for (int i = 0; i < m; i++) {
		for (int j = 0; j < n; j++) {
			cin >> data[i][j];
		}
	}
	int leftRight = n; //水平方向位移
	int index1 = 0; //水平方向索引
	int upDown = m-1; //上下方向位移
	int index2 = 0; //上下方向索引
	int flag = 1;
	while (true) {
		int i = 0;
		int j = 0;
		if (leftRight != 0) {
			for (i = 0; i < leftRight; i++) {
				cout << data[index2][i*flag+index1] << ' ';
			}
			index1 += flag*(leftRight - 1);
			index2 += flag;
			leftRight--;
		}
		if (upDown == 0) break;
		if (upDown != 0) {
			for (j = 0; j < upDown; j++) {
				cout << data[j*flag + index2][index1] << ' ';
			}
			index2 += flag * (upDown - 1);
			index1 += flag * (-1);
			upDown --;
		}
		if (leftRight == 0) break;
		flag = -flag;
	}

}