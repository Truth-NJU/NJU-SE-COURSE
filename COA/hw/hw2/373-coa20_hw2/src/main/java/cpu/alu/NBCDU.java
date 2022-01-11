package cpu.alu;

import transformer.Transformer;

import java.util.Objects;

public class NBCDU {

	// 模拟寄存器中的进位标志位
	private String CF = "0";

	// 模拟寄存器中的溢出标志位
	private String OF = "0";

	/**
	 * @param a A 32-bits NBCD String
	 * @param b A 32-bits NBCD String
	 * @return a + b
	 */
	String add(String a, String b) {
		String result = "";
		//判断两数相加的正负
		int f = Integer.parseInt(new Transformer().NBCDToDecimal(a));
		int s = Integer.parseInt(new Transformer().NBCDToDecimal(b));
		if (f + s >= 0) result += "1100";
		else result += "1101";
		if ((f < 0 && s > 0) || (f > 0 && s < 0)) {
			String str = inverse(b.substring(24));
			b = b.substring(0, 24) + str;
		}
		char[] res = new char[32];
		for (int i = 0; i < 32; i++) res[i] = '0';
		//8421外部进位标志
		int[] carryBit = new int[7];
		for (int i = 0; i < 7; i++) carryBit[i] = 0;
		//8421内部进位标志,0表示未进位，1表示有进位
		int flag = 0;
		for (int i = 31; i >= 4; i--) {
			int first = Integer.parseInt(String.valueOf(a.charAt(i)));
			int second = Integer.parseInt(String.valueOf(b.charAt(i)));
			if (first + second == 0) {
				if (flag == 0) res[i] = '0';
				else {
					res[i] = '1';
				}
				flag = 0;
			} else if (first + second == 1) {
				if (flag == 0) res[i] = '1';
				else {
					res[i] = '0';
					flag = 1;
				}
			} else {
				if (flag == 0) res[i] = '0';
				else res[i] = '1';
				flag = 1;
			}
		}
		for (int i = 0; i < 7; i++) {
			int num = (i + 1) * 4;
			String str1 = "1100000000000000000000000000" + a.substring(num, num + 4);
			String str2 = "1100000000000000000000000000" + b.substring(num, num + 4);
			int num1 = Integer.parseInt(new Transformer().NBCDToDecimal(str1));
			int num2 = Integer.parseInt(new Transformer().NBCDToDecimal(str2));
			if (num1 + num2 >= 10 && ((f > 0 && s > 0) || (f < 0 && s < 0))) carryBit[i] = 1;
		}

		for (int i = 0; i < 7; i++) {
			System.out.print(carryBit[i]);
		}
		//若有进位加上0110
		char[] carry = new char[]{'0', '1', '1', '0'};
		for (int i = 0; i < 7; i++) {
			int flag2 = 0;
			if (carryBit[i] == 1) {
				int num = (i + 1) * 4;
				int m = 3;
				for (int j = num + 3; j >= num; j--) {
					int one = Integer.parseInt(String.valueOf(res[j]));
					int two = Integer.parseInt(String.valueOf(carry[m]));
					if (one + two == 0) {
						if (flag2 == 0) res[j] = '0';
						else {
							res[j] = '1';
						}
						flag2 = 0;
					}
					if (one + two == 1) {
						if (flag2 == 0) res[j] = '1';
						else {
							res[j] = '0';
							flag2 = 1;
						}
					} else if (one + two == 2) {
						if (flag2 == 0) res[j] = '0';
						else res[j] = '1';
						flag2 = 1;
					}
					m--;
				}
			}
		}
		if ((f > 0 && s < 0) || (f < 0 && s > 0)) {
			res[23] = '0';
		}
		for (int i = 4; i < 32; i++) {
			result += res[i];
		}
		return result;
	}


	/***
	 *
	 * @param a A 32-bits NBCD String
	 * @param b A 32-bits NBCD String
	 * @return b - a
	 */
	String sub(String a, String b) {
		String result = "";
		int f = Integer.parseInt(new Transformer().NBCDToDecimal(a));
		int s = Integer.parseInt(new Transformer().NBCDToDecimal(b));
		if (s - f > 0) result += "1100";
		else result += "1101";
		if (Objects.equals(a, b)) return "11000000000000000000000000000000";
		String a1 = "";
		a1 += a.substring(0, 4);
		a1 += invert(a.substring(4, 8));
		a1 += invert(a.substring(8, 12));
		a1 += invert(a.substring(12, 16));
		a1 += invert(a.substring(16, 20));
		a1 += invert(a.substring(20, 24));
		a1 += invert(a.substring(24, 28));
		a1 += addOne(invert(a.substring(28)));
		a = a1;
		char[] res = new char[32];
		for (int i = 0; i < 32; i++) res[i] = '0';
		int[] carryBit = new int[7];
		for (int i = 0; i < 7; i++) carryBit[i] = 0;
		//8421内部进位标志,0表示未进位，1表示有进位
		int flag = 0;
		for (int i = 31; i >= 4; i--) {
			int first = Integer.parseInt(String.valueOf(a.charAt(i)));
			int second = Integer.parseInt(String.valueOf(b.charAt(i)));
			if (first + second == 0) {
				if (flag == 0) res[i] = '0';
				else {
					res[i] = '1';
				}
				flag = 0;
			} else if (first + second == 1) {
				if (flag == 0) res[i] = '1';
				else {
					res[i] = '0';
					flag = 1;
				}
			} else {
				if (flag == 0) res[i] = '0';
				else res[i] = '1';
				flag = 1;
			}
		}
		for (int i = 0; i < 7; i++) {
			int num = (i + 1) * 4;
			String str1 = "1100000000000000000000000000" + a.substring(num, num + 4);
			String str2 = "1100000000000000000000000000" + b.substring(num, num + 4);
			int num1 = Integer.parseInt(new Transformer().NBCDToDecimal(str1));
			int num2 = Integer.parseInt(new Transformer().NBCDToDecimal(str2));
			if (num1 + num2 >= 10 && ((f > 0 && s > 0) || (f < 0 && s < 0))) carryBit[i] = 1;
		}

		for (int i = 0; i < 7; i++) {
			System.out.print(carryBit[i]);
		}
		System.out.println();
		for (int i = 0; i < 32; i++) {
			System.out.print(res[i]);
		}
		System.out.println();
		//若有进位加上0110
		char[] carry = new char[32];
		for (int i = 0; i < 32; i++) carry[i] = '0';
		for (int i = 0; i < 7; i++) {
			if (carryBit[i] == 1) {
				int num = (i + 1) * 4;
				carry[num] = '0';
				carry[num + 1] = '1';
				carry[num + 2] = '1';
				carry[num + 3] = '0';
			}
		}
		for (int i = 0; i < 32; i++) {
			System.out.print(carry[i]);
		}
		int flag1 = 0;
		for (int i = 31; i >= 4; i--) {
			int first = Integer.parseInt(String.valueOf(res[i]));
			int second = Integer.parseInt(String.valueOf(carry[i]));
			if (first + second == 0) {
				if (flag1 == 0) res[i] = '0';
				else {
					res[i] = '1';
				}
				flag1 = 0;
			} else if (first + second == 1) {
				if (flag1 == 0) res[i] = '1';
				else {
					res[i] = '0';
					flag1 = 1;
				}
			} else {
				if (flag1 == 0) res[i] = '0';
				else res[i] = '1';
				flag1 = 1;
			}
		}

		for (int i = 4; i < 32; i++) {
			result += res[i];
		}
		String finalResult = "";
		finalResult += result.substring(0, 4);
		for(int i=0;i<7;i++){
			int num=(i+1)*4;
			if(result.substring(num, num + 4).equals("1001")){
				finalResult+="0000";
			}
		}
		if (s > 0 && f > 0 && s - f < 0) {
			finalResult += invert(result.substring(20, 24));
			finalResult += invert(result.substring(24, 28));
			finalResult += addOne(invert(result.substring(28, 32)));
		} else {
			finalResult =finalResult+"0000"+ result.substring(20);
		}

		return finalResult;
	}

	String invert(String src) {
		if (Objects.equals(src, "0000")) return "1001";
		if (Objects.equals(src, "0001")) return "1000";
		if (Objects.equals(src, "0010")) return "0111";
		if (Objects.equals(src, "0011")) return "0110";
		if (Objects.equals(src, "0100")) return "0101";
		if (Objects.equals(src, "0101")) return "0100";
		if (Objects.equals(src, "0110")) return "0011";
		if (Objects.equals(src, "0111")) return "0010";
		if (Objects.equals(src, "1000")) return "0001";
		if (Objects.equals(src, "1001")) return "0000";
		return "0000";
	}

	//取反加一
	public static String inverse(String src) {
		char[] number = src.toCharArray();
		for (int i = 0; i < number.length; i++) {
			if (number[i] == '0') number[i] = '1';
			else number[i] = '0';
		}
		char[] re = new char[number.length];
		for (int i = 0; i < number.length - 1; i++) re[i] = '0';
		re[number.length - 1] = '1';
		String result = "";
		int flag = 0;
		for (int i = number.length - 1; i >= 0; i--) {
			int first = Integer.parseInt(String.valueOf(number[i]));
			int second = Integer.parseInt(String.valueOf(re[i]));
			if (first + second == 0) {
				if (flag == 0) number[i] = '0';
				else {
					number[i] = '1';
				}
				flag = 0;
			} else if (first + second == 1) {
				if (flag == 0) number[i] = '1';
				else {
					number[i] = '0';
					flag = 1;
				}
			} else {
				if (flag == 0) number[i] = '0';
				else number[i] = '1';
				flag = 1;
			}
		}
		for (int i = 0; i < number.length; i++) {
			result += number[i];
		}
		return result;
	}

	//最后一位加一
	public static String addOne(String src) {
		char[] number = src.toCharArray();
		char[] re = new char[number.length];
		for (int i = 0; i < number.length - 1; i++) re[i] = '0';
		re[number.length - 1] = '1';
		String result = "";
		int flag = 0;
		for (int i = number.length - 1; i >= 0; i--) {
			int first = Integer.parseInt(String.valueOf(number[i]));
			int second = Integer.parseInt(String.valueOf(re[i]));
			if (first + second == 0) {
				if (flag == 0) number[i] = '0';
				else {
					number[i] = '1';
				}
				flag = 0;
			} else if (first + second == 1) {
				if (flag == 0) number[i] = '1';
				else {
					number[i] = '0';
					flag = 1;
				}
			} else {
				if (flag == 0) number[i] = '0';
				else number[i] = '1';
				flag = 1;
			}
		}
		for (int i = 0; i < number.length; i++) {
			result += number[i];
		}
		return result;
	}
}
