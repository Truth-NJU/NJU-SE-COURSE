#include<iostream>
#include "myString.h"
#include<string>
using namespace std;

int main() {
	char ch;
	char m[100];
	for (int i = 0; i < 100; i++) {
		m[i] = '\0';
	}
	int j = 0;
	while (cin.get(ch)) {
		m[j] = ch;
		j++;
		//cout << m[i];
	}
	//cout << endl;
	myString my(&m[0]);
	my.Reverse();
	my.Print();
	return 0;
}