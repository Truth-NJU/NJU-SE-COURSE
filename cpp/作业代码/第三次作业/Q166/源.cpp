#include<iostream>
#include "OperationAdd.h"
#include "OperationDiv.h"
#include "OperationSub.h"
#include "OperationMul.h"
#include "Operation.h"
using namespace std;

int main() {
	char op;
	cin >> op;
	double n1;
	double n2;
	cin >> n1 >> n2;
	//cout << op << n1 << n2 << endl;
	Operation *opt=new Operation();
	OperationAdd *add=new OperationAdd();
	OperationMul *mul=new OperationMul();
	OperationSub *sub=new OperationSub;
	OperationDiv *div=new OperationDiv;
	double res;
	//cout << opt->getA() << "   " << opt->getB() << endl;
	if (op == '+') {
		add->setA(n1);
		add->setB(n2);
		res= add->getResult();
		cout << res;
	}
	else if(op=='-'){
		sub->setA(n1);
		sub->setB(n2);
		res = sub->getResult();
		cout << res;
	}
	else if (op == '*') {
		mul->setA(n1);
		mul->setB(n2);
		res = mul->getResult();
		cout << res;
	}
	else if (op == '/') {
		div->setA(n1);
		div->setB(n2);
		res = div->getResult();
		cout << res;
	}
	return 0;
}