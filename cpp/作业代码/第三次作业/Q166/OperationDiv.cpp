#include "OperationDiv.h"

double OperationDiv::getResult() {
	if (Operation::getB() == 0) {
		return -1;
	}
	return Operation::getA() / Operation::getB();
};