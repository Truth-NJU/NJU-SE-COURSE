#include<iostream>
using namespace std;

template<typename T>
void sort(T A[], int num) {
	for (int i = 1; i < num; i++) {
		for (int j = 0; j < num - i; j++) {
			if (A[j] > A[j + 1]) {
				T t = A[j];
				A[j] = A[j + 1];
				A[j + 1] = t;
			}
		}
	}
	for (int i = 0; i < num - 1; i++) {
		cout << A[i] << " ";
	}
	cout << A[num - 1] << endl;
}

template<typename T>
int binarySearch(T A[], int num, T element) {
	int l = 0, r = num-1;

	int count = 0;
	while (l <= r) {
		int mid = l + (r - l) / 2;
		if (A[mid] == element) {
			count++;
			return count;
		}
		else if (A[mid] > element) {
			count++;
			r = mid-1;
		}
		else if (A[mid] < element){
			count++;
			l = mid + 1;
		}
	}
	return count;


}

int main() {
	int n;
	char c;
	cin >> n >> c;
	if (c == 'i') {
		int num[1000];
		for (int i = 0; i < n; i++) {
			cin >> num[i];
		}
		int element;
		cin >> element;
		for (int i = n - 1; i > 0; i--) {
			cout << num[i] << " ";
		}
		cout << num[0] << endl;
		sort(num, n);
		cout << binarySearch(num, n, element) << endl;
	}
	else if (c == 'd') {
		double num[1000];
		for (int i = 0; i < n; i++) {
			cin >> num[i];
		}
		double element;
		cin >> element;
		for (int i = n - 1; i > 0; i--) {
			cout << num[i] << " ";
		}	
		cout << num[0] << endl;
		sort(num, n);
		cout << binarySearch(num, n, element) << endl;
	}
	else if (c == 'c') {
		char num[1000];
		for (int i = 0; i < n; i++) {
			cin >> num[i];
		}
		char element;
		cin >> element;
		for (int i = n - 1; i > 0; i--) {
			cout << num[i] << " ";
		}
		cout << num[0] << endl;
		sort(num, n);
		cout << binarySearch(num, n, element) << endl;
	}
}