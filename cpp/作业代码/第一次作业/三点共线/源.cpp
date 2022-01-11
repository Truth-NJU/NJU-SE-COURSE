#include<iostream>
using namespace std;
int main() {
	char c;
	int x1, y1, x2, y2, x3, y3;
	cin >> c >> x1 >> c >> y1 >> c >> c >> x2 >> c >> y2 >> c >> c >> x3 >> c >> y3 >> c;
	int a1 = x2 - x1;
	int b1 = y2 - y1;
	int a2 = x3 - x1;
	int b2 = y3 - y1;
	if (a2 *b1==a1*b2) {
		cout << 1 << endl;
	}
	else {
		cout << 0 << endl;
	}
}