#include<iostream>
#include<string>
using namespace std;

int main() {
	int num;
	int res = 0;
	while (cin >> num) {
		res ^= num;
	}
	cout << res << endl;
}