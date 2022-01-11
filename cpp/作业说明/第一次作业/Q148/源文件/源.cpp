#include<iostream>
using namespace std;

int main() {
	int num;
	cout << "请输入一个数字：";
	cin >> num;
	cout << "The result is:";
	int result =0;
	bool sign = true; //标记正负
	if (num < 0) {
		num = 0 - num;
		sign = false;
	}
	if (!sign) {
		cout << "-";
	}
	while (num != 0) {
		result = num % 10;
		if (result == 0) {
			continue;
		}
		else {
			cout << result;
		}
		num /= 10;
	}
	cout << endl;
}