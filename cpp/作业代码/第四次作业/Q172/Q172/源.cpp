#include<iostream>
#include"Rational.h"
using namespace std;
int main() {
	long a, b, c, d;
	cin >> a >> b >> c >> d;
	Rational r1(a, b);
	Rational r2(c, d);
	r1 + r2;
	r1 - r2;
	r1 * r2;
	r1 / r2;
	-r1;
	++r1;
	--r1;
	r1++;
	r1--;
	r1 < r2;
	r1 <= r2;
	r1 > r2;
	r1 >= r2;
	//cout << endl;
}