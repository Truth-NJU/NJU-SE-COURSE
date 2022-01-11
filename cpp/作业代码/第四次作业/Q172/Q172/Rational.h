#pragma once
#include<string>
#include<iostream>
using namespace std;
class Rational
{
public:
	long a=0, b=0;

	Rational() {

	}
	Rational(int a, int b) {
		this->a = a;
		this->b = b;
	}
	// 将+-*/重载为友元函数
	friend Rational operator+(Rational& r1, Rational &r2);
	friend Rational operator-(Rational& r1, Rational &r2);
	friend Rational operator*(Rational& r1, Rational &r2);
	friend Rational operator/(Rational& r1, Rational &r2);
	// 将<  <= > >=重载为友元函数
	friend Rational operator<(Rational& r1, Rational &r2);
	friend Rational operator<=(Rational& r1, Rational &r2);
	friend Rational operator>(Rational& r1, Rational &r2);
	friend Rational operator>=(Rational& r1, Rational &r2);
	//将++ -- 重载为公有成员函数
	/**
	 * 一般++a的实现方式：
	 * Rational& operator++() {
	 *    a++;
	 *    return *this;
	 * }
	 *
	 * 一般a++的实现方式：
	 * Rational operator++(int) {
	 *      Rational temp=*this;
	 *      a++;
	 *      return temp;
	 * }
	 */
	Rational& operator++() {  //++a
		a = a + b;
		long gcd = this->gcd(a,b);
		if (a == 0) {
			cout << "0" << endl;
			return *this;
		}
		if (a*b > 0) {
			if (a%b == 0) cout << abs(a / b) << endl;
			else cout << abs(a/gcd) << "/" << abs(b/gcd) << endl;
			a = a / gcd;
			b = b / gcd;
		}
		else {
			cout << "-";
			if (a%b == 0) cout << abs(a / b) << endl;
			else cout << abs(a / gcd) << "/" << abs(b / gcd) << endl;
			a = a / gcd;
			b = b / gcd;
		}
		return *this;
	}
	Rational& operator--() {  //--a
		a = a - b;
		if (a == 0) {
			cout << "0" << endl;
			return *this;
		}
		if (a*b > 0) {
			if (a%b == 0) cout << abs(a / b) << endl;
			else cout << abs(a) << "/" << abs(b) << endl;
		}
		else {
			cout << "-";
			if (a%b == 0) cout << abs(a / b) << endl;
			else cout << abs(a) << "/" << abs(b) << endl;
		}
		return *this;
	}
	Rational operator++(int) {//a++
		Rational temp = *this;
		if (a == 0) {
			cout << "0" << endl;
			a = a + b;
			return temp;
		}
		if (a*b > 0) {
			
			if (a%b == 0) cout << abs(a / b) << endl;
			else cout << abs(a) << "/" << abs(b) << endl;
		}
		else {
			cout << "-";
			if (a%b == 0) cout << abs(a / b) << endl;
			else cout << abs(a) << "/" << abs(b) << endl;
		}
		a = a + b;
		return temp;
	}
	Rational operator--(int) {//a--
		Rational temp = *this;
		if (a == 0) {
			cout << "0" << endl;
			a = a - b;
			return temp;
		}
		if (a*b > 0) {
			if (a%b == 0) cout << abs(a / b) << endl;
			else cout << abs(a) << "/" << abs(b) << endl;
		}
		else {
			cout << "-";
			if (a%b == 0) cout << abs(a / b) << endl;
			else cout << abs(a) << "/" << abs(b) << endl;
		}
		a = a - b;
		return temp;
	}
	Rational operator-() {
		long gcd = this->gcd(a, b);
		if (a == 0) {
			cout << "0" << endl;
			return *this;
		}
		if (a*b > 0) {
			cout << "-";
			if (a%b == 0) cout << abs(a / b) << endl;
			else cout << abs(a / gcd) << "/" << abs(b / gcd) << endl;
		
		}
		else {
			if (a%b == 0) cout << abs(a / b) << endl;
			else cout << abs(a / gcd) << "/" << abs(b / gcd) << endl;
	
		}


		return *this;
	}
	//求最大公约数
	int gcd(long a, long b) {
		if (b == 0)
			return a;
		return gcd(b, a%b);
	}
	//求最小公倍数
	int minMul(long a, long b) {
		return a * b / gcd(a, b);
	}
};



Rational operator+(Rational& r1, Rational &r2) {
	Rational temp;
	long n = r1.minMul(r1.b,r2.b); //最小公倍数
	long mA = (n / r1.b)*r1.a;
	long mB= (n / r2.b)*r2.a;
	long m = mA + mB;
	long gcd = r1.gcd(m,n);
	//cout << m <<" "<<n<< endl;
	if (m == 0) {
		cout << "0" << endl;
		return temp;
	}
	if ((m>0 && n>0) || (m < 0 && n < 0)) {
		if (m%n == 0) cout << m / n << endl;
		else cout << abs(m / gcd )<< "/" << abs(n / gcd) << endl;
	}
	else {
		cout << "-";
		if (m%n == 0) cout << abs(m / n) << endl;
		else cout << abs(m / gcd) << "/" << abs(n / gcd) << endl;
	}
	temp.a = m / gcd;
	temp.b = n / gcd;
	return temp;
}

Rational operator-(Rational& r1, Rational &r2) {
	Rational temp;
	long n = r1.minMul(r1.b, r2.b); //最小公倍数
	long mA = (n / r1.b)*r1.a;
	long mB = (n / r2.b)*r2.a;
	long m = mA - mB;
	long gcd = r1.gcd(m, n);
	if (m == 0) {
		cout << "0" << endl;
		return temp;
	}
	if ((m > 0 && n > 0) || (m < 0 && n < 0)) {
		if (m%n == 0) cout << m / n << endl;
		else cout << abs(m / gcd) << "/" << abs(n / gcd) << endl;
	}
	else {
		cout << "-";
		if (m%n == 0) cout << abs(m / n) << endl;
		else cout << abs(m / gcd) << "/" << abs(n / gcd) << endl;
	}
	temp.a = m / gcd;
	temp.b = n / gcd;
	return temp;
}

Rational operator*(Rational& r1, Rational &r2) {
	Rational temp;
	/*if (r1.a*r2.a*r1.b*r2.b >= 0) {

	}
	else {
		cout << '-';
	}*/
	long m = r1.a*r2.a;
	long n = r1.b*r2.b;
	long gcd = r1.gcd(m, n);
	if (m == 0) {
		cout << "0" << endl;
		return temp;
	}
	if ((m > 0 && n > 0) || (m < 0 && n < 0)) {
		if (m%n == 0) cout << m / n << endl;
		else cout << abs(m / gcd) << "/" << abs(n / gcd) << endl;
	}
	else {
		cout << '-';
		if (m%n == 0) cout << abs(m / n) << endl;
		else cout << abs(m / gcd) << "/" << abs(n / gcd) << endl;
	}
	temp.a = m / gcd;
	temp.b = n / gcd;
	return temp;
}

Rational operator/(Rational& r1, Rational &r2) {
	Rational temp;
	if (r1.a == 0) {
		cout << "0" << endl;
		return temp;
	}
	long m = r1.a*r2.b;
	long n = r1.b*r2.a;
	long gcd = r1.gcd(m, n);
	if (m == 0) {
		cout << "0" << endl;
		return temp;
	}
	if ((m>0 && n>0) || (m < 0 && n < 0)) {
		if (m%n == 0) cout << m / n << endl;
		else cout << abs(m / gcd) << "/" << abs(n / gcd) << endl;
	}
	else {
		cout << '-';
		if (m%n == 0) cout << abs(m / n) << endl;
		else cout << abs(m / gcd) << "/" << abs(n / gcd) << endl;
	}
	temp.a = m / gcd;
	temp.b = n / gcd;
	return temp;
	
}

Rational operator<(Rational& r1, Rational &r2) {
	Rational temp;
	long n = r1.minMul(abs(r1.b), abs(r2.b)); //最小公倍数
	long mA = (n / r1.b)*r1.a;
	long mB = (n / r2.b)*r2.a;
	long m = mA - mB;
	if (m < 0) cout << "true" << endl;
	else cout << "false" << endl;
	return temp;
}

Rational operator<=(Rational& r1, Rational &r2) {
	Rational temp;
	long n = r1.minMul(abs(r1.b), abs(r2.b)); //最小公倍数
	//cout << r1.a << " " << r1.b << endl;
	long mA = (n / r1.b)*r1.a;
	long mB = (n / r2.b)*r2.a;
	long m = mA - mB;
	//cout << mA << " " << mB << " " << m <<" "<<n<< endl;
	if (m <= 0) cout << "true" << endl;
	else cout << "false" << endl;
	return temp;
}

Rational operator>(Rational& r1, Rational &r2) {
	Rational temp;
	long n = r1.minMul(abs(r1.b), abs(r2.b)); //最小公倍数
	long mA = (n / r1.b)*r1.a;
	long mB = (n / r2.b)*r2.a;
	long m = mA - mB;
	if (m > 0)cout << "true" << endl;
	else cout << "false" << endl;
	return temp;

}

Rational operator>=(Rational& r1, Rational &r2) {
	Rational temp;
	long n = r1.minMul(abs(r1.b), abs(r2.b)); //最小公倍数
	long mA = (n / r1.b)*r1.a;
	long mB = (n / r2.b)*r2.a;
	long m = mA - mB;
	if (m >= 0)cout << "true" << endl;
	else cout << "false" << endl;
	return temp;
}