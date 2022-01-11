#include<iostream>
#include<fstream>
#include<string>
using namespace std;

string encode(string txt);
string intToBinary(int num);
int binaryToInt(string num);
char base64(int num);

int main() {
	//cout << binaryToInt("01001101") << endl;
	string filepath;
	cin >> filepath;
	ifstream fin("C:\\tzh\\新建文件夹\\Q149\\" + filepath, ios_base::binary);

	if (!fin.is_open()) {
		cout << "FILE NOT FOUND" << endl;
		return 0;
	}
	string line;
	getline(fin, line, '\0');
	cout << encode(line) << endl;
}

string encode(string txt) {
	string res;
	int len = txt.length();
	int group = len / 3;
	int remainedBytes = len % 3;
	int i = 0;
	if (group > 0) {
		while (i < 3 * group) {
			string temp = intToBinary(txt[i]) + intToBinary(txt[i + 1]) + intToBinary(txt[i + 2]);
			string first, second, third, fourth;
			for (int j = 0; j < 6; j++) first += temp[j];
			for (int j = 6; j < 12; j++)  second += temp[j];
			for (int j = 12; j < 18; j++) third += temp[j];
			for (int j = 18; j < 24; j++) fourth += temp[j];
			first = "00" + first;
			second = "00" + second;
			third = "00" + third;
			fourth = "00" + fourth;
			res = res + base64(binaryToInt(first)) + base64(binaryToInt(second)) + base64(binaryToInt(third)) + base64(binaryToInt(fourth));
			i += 3;
		}
	}
	if (remainedBytes == 2) {
		string temp = intToBinary(txt[len - 2]) + intToBinary(txt[len - 1]);
		string first, second, third;
		for (int j = 0; j < 6; j++) first += temp[j];
		for (int j = 6; j < 12; j++)  second += temp[j];
		for (int j = 12; j < 16; j++) third += temp[j];
		first = "00" + first;
		second = "00" + second;
		third = "00" + third + "00";
		res = res + base64(binaryToInt(first)) + base64(binaryToInt(second)) + base64(binaryToInt(third))+"=";

	}else if (remainedBytes == 1) {
		string temp = intToBinary(txt[len - 1]);
		string first, second;
		for (int j = 0; j < 6; j++) first += temp[j];
		for (int j = 6; j < 8; j++)  second += temp[j];
		first = "00" + first;
		second = "00" + second + "0000";
		res = res + base64(binaryToInt(first)) + base64(binaryToInt(second)) + "==";
	}
	if (group == 0 && remainedBytes == 0) {
		res = " ";
	}
	return res;
}

string intToBinary(int num) {
	string res;
	for (int i = 1; i<9; i++) {
		int t = (num >> (i - 1)) & 1;
		if (t == 0) {
			res = "0"+res ;
		}
		else {
			res = "1" + res;

		}
	}
	return res;
}

int binaryToInt(string num) {
	int res = 0;
	int flag = 1;
	for (int i = num.length() - 1; i >= 0; i--) {
		if (num[i] == '1') {
			res += flag;
		}
		flag *= 2;
	}
	return res;
}

char base64(int num) {
	string res = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	return res[num];
}
