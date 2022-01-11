#include <iostream>
#include "LinkedList.h"

using namespace std;
int main() {
	cpp::ListNode *head = cpp::ListNode::construct();
	if (head == NULL) {
		cout << -1 << endl;
		return 0;
	}
	if (head->next==NULL) {
		cout << -1 << endl;
		return 0;
	}
	cpp::ListNode *fast = head->next;
	cpp::ListNode *low = head;
	int len = 0;
	bool state = true; //true代表有环，false代表无环

	while ( fast != NULL && fast->next != NULL) {
		if (fast->seq_num == low->seq_num) {
			state = true;
			break;
		}
		fast = fast->next;
		if (low != NULL && low->next != NULL) {
			low = low->next;
		}
		else {
			state = false;
			break;
		}
		if (fast->next!=NULL) {
			fast = fast->next;
		}
		else {
			state = false;
			break;
		}
		len++;
	}

	cpp::ListNode *m = head;
	if (state) {
		for (int i = 1; i < 1000; i++) {
			if (m->next->seq_num == m->seq_num) {
				cout << 1 << endl;
				return 0;
			}
			m = m->next;
		}
		len++;
		cout << len << endl;
		return 0;
	}
	else {
		cout << -1 << endl;
		return 0;
	}


}