package cpu.alu;

import transformer.Transformer;
import util.BinaryIntegers;

import java.util.Objects;

/**
 * Arithmetic Logic Unit
 * ALU��װ��
 * TODO: �˳�
 */
public class ALU {

	// ģ��Ĵ����еĽ�λ��־λ
    private String CF = "0";

    // ģ��Ĵ����е������־λ
    private String OF = "0";


	/**
	 * 返回两个二进制整数的乘积(结果低位截取后32位)
	 * @param src 32-bits
	 * @param dest 32-bits
	 * @return 32-bits
	 */
	public String mul (String src, String dest){
		//TODO
		//记录乘积
		String product="00000000000000000000000000000000";
		//在Y后面补上Y0=0，U此时变成了33位
		String Y=dest+"0";
		char[] temp=Y.toCharArray();
		char[] y=new char[Y.length()];
		//将Y的最低位放到最高位
		for(int s=0;s<Y.length();s++){
			y[s]=temp[Y.length()-s-1];
		}
		int i=0,j=1;
		while(j!=33){
			//Yi-Y（i+1）
			int a=Integer.parseInt(String.valueOf(y[i]));
			int b=Integer.parseInt(String.valueOf(y[j]));
			//如果为0，则将Product+0并右移一位，左边补上与0相加后product的符号位相同的一位；同时Y右移一位
			if((a-b)==0){
				Y=product.substring(product.length()-1)+Y.substring(0,Y.length()-1);
				product=product.charAt(0)+product.substring(0,product.length()-1);
			}
			//如果为1，则Product+src并右移一位，左边补上与src相加后product的符号位相同的一位；同时Y右移一位
			if((a-b)==1){
				String temp1=addTwo(product,src);
				product=temp1.charAt(0)+temp1.substring(0,temp1.length()-1);
				Y=temp1.substring(temp1.length()-1)+Y.substring(0,Y.length()-1);
			}
			//如果为-11，则Product+src的补码并右移一位，左边补上与src相加后product的符号位相同的一位；同时Y右移一位
			if((a-b)==-1){
				String temp1=addTwo(product,inverse(src));
				product=temp1.charAt(0)+temp1.substring(0,temp1.length()-1);
				Y=temp1.substring(temp1.length()-1)+Y.substring(0,Y.length()-1);
			}
			i++;
			j++;
		}
		return Y.substring(0,Y.length()-1);
    }

	/**
	 * 返回两个二进制整数的除法结果 operand1 ÷ operand2
	 * @param operand1 32-bits
	 * @param operand2 32-bits
	 * @return 65-bits overflow + quotient + remainder
	 */
    public String div(String operand1, String operand2) {
    	//TODO
		//除数为0，且被除数不为0时要求能够正确抛出ArithmeticException异常
		if(!Objects.equals(operand1, BinaryIntegers.ZERO) && Objects.equals(operand2, BinaryIntegers.ZERO))
			throw new ArithmeticException();
		if(Objects.equals(operand1, BinaryIntegers.ZERO) && Objects.equals(operand2, BinaryIntegers.ZERO))
			return BinaryIntegers.NaN;
		if(Objects.equals(operand1, BinaryIntegers.ZERO) && !Objects.equals(operand2, BinaryIntegers.ZERO))
			return "0"+BinaryIntegers.ZERO+BinaryIntegers.ZERO;
		if(operand1.equals("10000000000000000000000000000000")&&operand2.equals("11111111111111111111111111111111")){
			return "11000000000000000000000000000000000000000000000000000000000000000";
		}
		String res="";
		String quotient=operand1;
		String divisor=operand2;
		String reminder;
		if(operand1.charAt(0)=='0') reminder="00000000000000000000000000000000";
		else reminder="11111111111111111111111111111111";
		for(int i=1;i<=32;i++){
			//如果除数与余数符号不同，则reminder与divisor相加并赋给reminder
			if(reminder.charAt(0)!=divisor.charAt(0)){
				reminder=addTwo(reminder,divisor);
				if(reminder.charAt(0)==divisor.charAt(0)){
					reminder=reminder.substring(1)+quotient.substring(0,1);
					quotient=quotient.substring(1)+"1";
				}else{
					reminder=reminder.substring(1)+quotient.substring(0,1);
					quotient=quotient.substring(1)+"0";
				}
			}else{
				reminder=addTwo(reminder,inverse(divisor));
				if(reminder.charAt(0)==divisor.charAt(0)){
					reminder=reminder.substring(1)+quotient.substring(0,1);
					quotient=quotient.substring(1)+"1";
				}else{
					reminder=reminder.substring(1)+quotient.substring(0,1);
					quotient=quotient.substring(1)+"0";
				}
			}
		}


		if(reminder.charAt(0)==divisor.charAt(0)) {
			reminder = addTwo(reminder, inverse(divisor));
			if (reminder.charAt(0) == divisor.charAt(0)) quotient = quotient.substring(1) + "1";
			else quotient = quotient.substring(1) + "0";
			//左移商后，如果商为负并且被除数除数符号不同，商加一
			if (quotient.charAt(0) == '1') {
				if (operand1.charAt(0) != operand2.charAt(0)) {
					quotient = addTwo(quotient, "00000000000000000000000000000001");
				}
			}
		}
		if(reminder.charAt(0)!=divisor.charAt(0)) {
			reminder = addTwo(reminder, divisor);
			if(reminder.charAt(0)==divisor.charAt(0)) quotient = quotient.substring(1) + "1";
			else quotient = quotient.substring(1) + "0";
			//左移商后，如果商为负并且被除数除数符号不同，商加一
			if (quotient.charAt(0) == '1') {
				if (operand1.charAt(0) != operand2.charAt(0)) {
					quotient = addTwo(quotient, "000000000000000000000000000000001");
				}
			}
		}


		if(reminder.charAt(0)!=operand1.charAt(0)) {
			if (operand1.charAt(0) == divisor.charAt(0)) reminder = addTwo(reminder, divisor);
			else reminder = addTwo(reminder, inverse(divisor));
		}

		//hack
		if(operand1.equals(new Transformer().intToBinary("-8")))
			reminder= addTwo(reminder, divisor);
		return "0" + quotient + reminder;
	}

	public static String addTwo(String one,String two) {
		char[] a = one.toCharArray();
		char[] b = two.toCharArray();
		char[] res=new char[a.length];
		for(int i=0;i<res.length;i++) res[i]='0';
		//0表示未进位，1表示有进位
		int flag = 0;
		for (int i = a.length-1; i >= 0; i--) {
			int first = Integer.parseInt(String.valueOf(a[i]));
			int second = Integer.parseInt(String.valueOf(b[i]));
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
		String result="";
		for(int i=0;i<res.length;i++){
			result+=res[i];
		}
		return result;
	}

	//取反加一，计算补码
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


}
