#include<iostream>
using namespace std;

int product(int x1, int y1, int x2, int y2);
bool isInside(int x1, int y1, int x2, int y2, int x3, int y3, int x, int y);
bool isAt(int x1, int y1, int x2, int y2, int x, int y);
bool isOut(int x1, int y1, int x2, int y2, int x3, int y3, int x, int y);
double area(int x1, int y1, int x2, int y2, int x3, int y3);

int main() {
	char c;
	int a1, a2, b1, b2, c1, c2, num;
	cin >> a1 >> a2 >> b1 >> b2 >> c1 >> c2;
	cin >> num;
	int m = b1 - a1;
	int n = b2 - a2;
	int p = c1 - a1;
	int q = c2 - a2;
	if (m*q == n * p) {
		cout << "-1" << endl;
		return 0;
	}
	int in = 0;
	int at = 0;
	int out = 0;
	for (int i = 1; i <= num; i++) {
		int x, y;
		cin >> x >> y;
		if (isOut(a1, a2, b1, b2, c1, c2, x, y)) {
			out++;
		}
		if (isAt(a1, a2, b1, b2, x, y) || isAt(a1, a2, c1, c2, x, y) || isAt(b1, b2, c1, c2, x, y)
			|| isAt(b1, b2, a1, a2, x, y) || isAt(c1, c2, a1, a2, x, y) || isAt(c1, c2, b1, b2, x, y)) {
			at++;
			continue;
		}
		if (isInside(a1, a2, b1, b2, c1, c2, x, y)) {
			in++;
		}
		
	}
	at = num - in - out;
	cout << in << " " <<at << " " << out << endl;
}

int product(int x1, int y1, int x2, int y2) {
	return x1 * y2 - x2 * y1;
}

bool isAt(int x1, int y1, int x2, int y2, int x, int y) {
	if (product(x2 - x1, y2 - y1, x - x1, y - y1) == 0) {
		int mx = x2 - x1;
		int my = y2 - y1;
		int nx = x - x1;
		int ny = y - y1;
		if (nx / mx <= 1 && nx / mx >= -1) {
			return true;
		}
	}
	return false;
}

bool isInside(int x1, int y1, int x2, int y2, int x3, int y3, int x, int y) {
	if (product(x3 - x1, y3 - y1, x2 - x1, y2 - y1) >= 0) {
		int tempx = x2;
		int tempy = y2;
		x2 = x3;
		y2 = y3;
		x3 = tempx;
		y3 = tempy;
	}
	if (product(x2 - x1, y2 - y1, x - x1, y - y1) < 0) return false;
	if (product(x3 - x2, y3 - y2, x - x2, y - y2) < 0) return false;
	if (product(x1 - x3, y1 - y3, x - x3, y - y3) < 0) return false;
	return true;
}

bool isOut(int x1, int y1, int x2, int y2, int x3, int y3, int x, int y) {
	double area0 = area(x1, y1, x2, y2, x3, y3);
	double area1 = area(x2, y2, x3, y3, x, y);
	double area2 = area(x1, y1, x3, y3, x, y);
	double area3 = area(x1, y1, x2, y2, x, y);
	double sun_area = area1 + area2 + area3;
	if (abs(area0 - sun_area) < 0.00001) return false;
	return true;

}

double area(int x1, int y1, int x2, int y2, int x3, int y3) {
	return abs(x1*y2 + x2 * y3 + x3 * y1 - x1 * y3 - x2 * y1 - x3 * y2)/2.0;
}