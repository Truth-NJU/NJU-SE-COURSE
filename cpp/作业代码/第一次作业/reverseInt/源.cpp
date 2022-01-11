#include<iostream>
#include<climits>
using namespace std;

int revert(int num);

int main() {
	int num;
	cin >> num;
	cout << revert(num) << endl;
}

int revert(int num) {

	if (num == INT_MIN) {
		return -1;
	}
	if (num < 0) {
		if (revert(-num) == -1) {
			return -1;
		}
		return -revert(-num);
	}
	long long res = 0;
	while (num > 0) {
		int temp = num % 10;
		num = num / 10;
		res = res * 10 + temp;
	}
	if (res > INT_MAX) {
		return -1;
	}
	return res;
}
