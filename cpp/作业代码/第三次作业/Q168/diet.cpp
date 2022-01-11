#include "diet.h"
#include<vector>
#include "food.h"
#include<iostream>
using namespace std;


void diet::if_healthy() {
	double car = 0;
	double pro = 0;
	double df = 0;
	double fat = 0;
	if (status) {
		for (int i = 0; i < 3; i++) {
			car += this->food[i].carbohydrate;
			pro += this->food[i].protein;
			df += this->food[i].df;
			fat += this->food[i].fat;
		}
		if (car >= 13.3 && pro >= 13.5 && df >= 3.3 && fat <= 10.3) {
			cout << "healthy";
		}
		else {
			cout << "unhealthy";
		}
	}
	else {
		cout << -1;
	}
}
