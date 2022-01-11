#pragma once
#include<vector>
#include "food.h"
#include<iostream>
using namespace std;

class diet
{
public:
	vector<food> food;
	bool status =true;
	void operator+= (int n) {
		if (n == 1)
			this->food.push_back(food::food(16.2, 3.7, 0, 0));
		else if (n == 2)
			this->food.push_back(food::food(1.8, 17.5, 0, 7.2));
		else if (n == 3)
			this->food.push_back(food::food(0.2, 0.4, 3.6, 0));
		else if (n == 4)
			this->food.push_back(food::food(12.3, 5.7, 7.3, 3));
		else if (n == 5)
			this->food.push_back(food::food(6.9, 9, 0, 9.3));
		else if (n == 6)
			this->food.push_back(food::food(2.1, 0.8, 4.3, 0));
		else {
			
			this->status = false;
		}
	};
	void if_healthy();
};

