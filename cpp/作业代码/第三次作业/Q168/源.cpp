#include<iostream>
#include "diet.h"
#include "food.h"
using namespace std;

int main() {
	int a, b, c;
	cin >> a >> b >> c;
	diet *die = new diet();
	die->operator+=(a);
	die->operator+=(b);
	die->operator+=(c);
	die->if_healthy();
	return 0;
}