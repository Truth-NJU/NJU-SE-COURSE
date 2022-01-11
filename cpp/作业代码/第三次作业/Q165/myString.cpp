#include "myString.h"
#include <iostream>
using namespace std;

myString::myString(char *Head) {
	//cout << *Head << endl;

	int i = 0;
	while (*(Head + i) != '\0') {
		this->head[i] = *(Head + i);
		//cout << this->head[i];
		i++;
	
		//cout << this->head[i];
	}
	for (int j = i; j < 100;j++) {
		this->head[i] = '\0';
	}
	//this->m = *Head;
	//cout << *(Head + 0) << endl;
}

/*myString::myString(char h[100]) {
	for (int i = 0; i <100; i++) {
		this->head[i] = h[i];
	}
}*/


void myString::Reverse() {
	char res[100];
	for (int i = 0; i < 100; i++) {
		res[i] = '\0';
	}
	int len = 0;
	for (int i = 0; i < 100; i++) {
		//cout << this->head[i];
		if (this->head[i] != '\0') {
			len++;
		}
		else {
			break;
		}
	}
	
	//cout << len << endl;
	for (int i = 0; i < len;i++) {
		res[i] = this->head[i];
		//cout << res[i];
	}
	//cout << endl;
	int j = 0;
	for (int i = len - 1; i >= 0; i--) {
		this->head[i] = res[j];
		//cout << this->head[i];
		j++;
	}
	//cout << endl;
}

void myString::Print () {
	int len = 0;
	for (int i = 0; i < 100; i++) {
		if (this->head[i] != '\0') {
			len++;
		}
		else {
			break;
		}
	}
	for (int i = 0; i < len; i++) {
		cout << this->head[i];
	}
	//cout << endl;
}