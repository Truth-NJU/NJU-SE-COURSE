#include <iostream>
#include "BinaryTree.h"

using namespace std;

int main() {
	//cpp::TreeNode a(42);
	//cpp::TreeNode b(43);
	//cpp::TreeNode c(44);
	//a.left = &b;
	//a.right = &c;
	//cpp::print_tree(&a);
	int root;
	cin >> root;
	cpp::TreeNode rot(root);
	int x;
	while (cin >> x) {
		int flag = 0;
		char leftRight[100];
		cin >> leftRight;
		int strLen = 0;
		for (int i = 0; i < 100; i++) {
			if (leftRight[i] == 'L' || leftRight[i] == 'R') {
				strLen++;
			}
			else {
				break;
			}
		}
		cpp::TreeNode *copy = &rot;
		for (int i = 0; i < strLen ; i++) {
			if (leftRight[i] == 'L') {
				if((*copy).left==NULL){
					cpp::TreeNode *m = new cpp::TreeNode(x);
					(*copy).left = m;
				}
				copy = (*copy).left;
			}
			else if (leftRight[i] == 'R') {
				if ((*copy).right == NULL) {
					cpp::TreeNode *m = new cpp::TreeNode(x);
					(*copy).right = m;
				}
				copy = (*copy).right;
			}
		}

	}

	cpp::print_tree(&rot);
}