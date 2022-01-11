#include <iostream>
using namespace std;
void sum(int a[], int b[],int size);
void sub(int a[], int b[],int size);
bool compare(char n1[], char n2[], int len1, int len2);

int main() {
	char num1[10000];
	char num2[10000];
	char c;
	cin >> num1 >> c >> num2;
	int len1 = 0, len2 = 0;
	for (int i = 0; i < 10000; i++) {
		if (num1[i] >= '0' && num1[i] <= '9') {
			len1++;
		}
		if (num2[i] >= '0' && num2[i] <= '9') {
			len2++;
		}
	}
	//cout << len1 << " " << len2 << endl;
	int n1[10000];
	int n2[10000];
	for (int i = 0; i < 10000; i++) {
		n1[i] = 0;
		n2[i] = 0;
	}
	// reverseSort
	int j = 0;
	if (num1[0] != '-') {
		j = 0;
		for (int i = len1 - 1; i >= 0; i--) {
			n1[j] = num1[i] - '0';
			j++;
		}
	}
	if (num1[0] == '-') {
		j = 0;
		for (int i = len1; i >= 1; i--) {
			n1[j] = num1[i] - '0';
			j++;
		}
	}
	if (num2[0] != '-') {
		j = 0;
		for (int i = len2 - 1; i >= 0; i--) {
			n2[j] = num2[i] - '0';
			j++;
		}
	}
	if (num2[0] == '-') {
		j = 0;
		for (int i = len2 ; i >= 1; i--) {
			n2[j] = num2[i] - '0';
			j++;
		}
	}

	/*for (int i = 0; i < len1; i++) {
		cout << n1[i];
	}
	cout << endl;
	for (int i = 0; i < len2; i++) {
		cout << n2[i];
	}
	cout << endl;*/
	/*
	int a[3] = { 1,2,3 };
	int b[3] = { 1,2,3 };
	sum(a,b,3);
	return 0;
	*/
	char str1[10000];
	char str2[10000];
	int s = 0;
	for (int i = len1 - 1; i >= 0; i--) {
		str1[i] = n1[s]+ '0';
		s++;
	}
	s = 0;
	for (int i = len2 - 1; i >= 0; i--) {
		str2[i] = n2[s]+ '0';
		s++;
	}
	if (c == '+' && num1[0]!='-' && num2[0]!='-') {
		sum(n1, n2, 10000);
		return 0;
	}
	if (c == '+' && num1[0] == '-' && num2[0] == '-') {
		cout << "-";
		sum(n1, n2, 10000);
		return 0;
	}
	if (c == '+' && num1[0] != '-' && num2[0] == '-') {
		if (compare(str1, str2, len1, len2)) {
			sub(n1, n2, 10000);
		}
		else {
			cout << "-";
			sub(n2, n1, 10000);
		}
		return 0;
	}
	if (c == '+' && num1[0] == '-' && num2[0] != '-') {
		if (compare(str1, str2, len1, len2)) {
			cout << "-";
			sub(n1, n2, 10000);
		}
		else {
			sub(n2, n1, 10000);
		}
		return 0;
	}
	if (c == '-' && num1[0] != '-' && num2[0] != '-') {
		if (compare(str1, str2, len1, len2)) {
			sub(n1, n2, 10000);
		}
		else {
			cout << "-";
			sub(n2, n1, 10000);
		}
		return 0;
	}
	if (c == '-' && num1[0] == '-' && num2[0] == '-') {
		if (compare(str1, str2, len1, len2)) {
			cout << "-";
			sub(n1, n2, 10000);
		}
		else {
			sub(n2, n1, 10000);
		}
		return 0;
	}
	if (c == '-' && num1[0] != '-' && num2[0] == '-') {
		sum(n1, n2, 10000);
		return 0;
	}
	if (c == '-' && num1[0] == '-' && num2[0] != '-') {
		cout << "-";
		sum(n1, n2, 10000);
		return 0;
	}
}


void sum(int a[], int b[], int size) {
	for (int i = 0; i < size; i++) {
		b[i] += a[i];
		if (b[i] >= 10) {
			b[i + 1] += b[i] / 10;
			b[i] %= 10;
		}
	}
	int i;
	for (i = size-1; i >= 0  && b[i]==0; i--);
	if (i >= 0) {
		for (; i >= 0; i--) {
			cout << b[i];
		}
	}
	else {
		cout << 0;
	}
	cout << endl;
}

void sub(int a[], int b[],int size) {
	int bit = 0;
	for (int i = 0; i < size; i++) {
		a[i] -= bit;
		if (a[i] < b[i]) {
			a[i] += 10;
			bit = 1;
		}
		else {
			bit = 0;
		}
		b[i] = a[i] - b[i];
	}
	int i;
	for (i = size-1; i >= 0 && b[i]==0; i--);
	if (i >= 0) {
		for (; i >= 0; i--) {
			cout << b[i];
		}
	}
	else {
		cout << 0;
	}
	cout << endl;
}

bool compare(char n1[], char n2[], int len1, int len2) {
	if (len1 > len2) {
		return true;
	}
	if (len1 == len2) {
		return strcmp(n1, n2) > 0;
	}
	return false;
}
