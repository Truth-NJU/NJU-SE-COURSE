#include<iostream>
using namespace std;
int main() {
	int chars = 0, words = 0, lines = 0;
	bool whetherInWord = false;
	char ch;
	while (cin.get( ch)) {
		chars++;
		if (ch == ' ' || ch == '\n') {
			if (ch == '\n') {
				lines++;
			}
			if (whetherInWord) {
				words++;
				whetherInWord = false;
			}
		}
		else {
			if (!whetherInWord) {
				whetherInWord = true;
			}
		}
	}
	cout << chars << ' ' << words << ' ' << lines << endl;
}