#include "OperationAdd.h"
#include<iostream>
using namespace std;

double OperationAdd::getResult() {
	return Operation::getA() + Operation::getB();
};