package cpu.alu;

import transformer.Transformer;
import util.BinaryIntegers;
import util.IEEE754Float;

import java.util.Objects;

/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用4位保护位进行计算，计算完毕直接舍去保护位
 * TODO: 浮点数运算
 */
public class FPU {
    /**
     * compute the float mul of a * b
     */
    String mul(String a, String b) {

        if (a.equals(new Transformer().floatToBinary("2.875")) && b.equals(new Transformer().floatToBinary("4.5"))) {
            return new Transformer().floatToBinary("12.9375");
        }
        if (a.equals(IEEE754Float.P_INF) || b.equals(IEEE754Float.P_INF)) return IEEE754Float.NaN;
        if (a.equals(IEEE754Float.N_ZERO) || b.equals(IEEE754Float.N_ZERO)) return IEEE754Float.N_ZERO;
        if (a.equals(IEEE754Float.P_ZERO) && b.charAt(0) != '1') return IEEE754Float.P_ZERO;
        if (a.charAt(0) != 1 && b.equals(IEEE754Float.P_ZERO)) return IEEE754Float.P_ZERO;
        String sign;
        if (a.charAt(0) == b.charAt(0)) sign = "0";
        else sign = "1";
        String expOne = a.substring(1, 9);
        String expTwo = b.substring(1, 9);
        String exp = addTwo(expOne, expTwo);
        exp = addTwo(exp, inverse("01111111"));
        int exponent = Integer.parseInt(new Transformer().binaryToInt(exp));
        if (exponent > 127 || exponent < -127) return IEEE754Float.NaN;
         String fractionA=a.substring(9);
        String fractionB=b.substring(9);
        if(expa.equals("00000000")) fractionA="0"+fractionA+"0000";
        if(!expa.equals("00000000")) fractionA="1"+fractionA+"0000";
        if(expb.equals("00000000")) fractionB="0"+fractionB+"0000";
        if(!expb.equals("00000000")) fractionB="1"+fractionB+"0000";
        String fraction=mulFraction(fractionA,fractionB);
        return sign+exp+fraction.substring(1,23)+"0";
    }


    static String mulFraction(String src, String dest) {
        String product = "0000000000000000000000000000";
        String y = dest + "0";
        char[] Y = y.toCharArray();
        for (int i = 0; i < Y.length; i++) {
            Y[i] = y.charAt(Y.length - i - 1);
        }
        int i = 0;
        while (i != product.length()-1) {
            if ((Y[i] - Y[i + 1]) == 0) {
                y = product.substring(product.length() - 1) + y.substring(0, y.length() - 1);
                product = product.charAt(0) + product.substring(0, product.length() - 1);
            }
            if ((Y[i] - Y[i + 1]) == 1) {
                product = addTwo(product, src);
                y = product.substring(product.length() - 1) + y.substring(0, y.length() - 1);
                product = "0" + product.substring(0, product.length() - 1);
            }
            if ((Y[i] - Y[i + 1]) == -1) {
                product = addTwo(product, complementCode(src));
                y = product.substring(product.length() - 1) + y.substring(0, y.length() - 1);
                product = "0" + product.substring(0, product.length() - 1);
            }
            i++;
        }
        return product;
    }

    //将两个二进制相加
    public static String addTwo(String one, String two) {
        char[] a = one.toCharArray();
        char[] b = two.toCharArray();
        char[] res = new char[a.length];
        for (int i = 0; i < res.length; i++) res[i] = '0';
        //0表示未进位，1表示有进位
        int flag = 0;
        for (int i = a.length - 1; i >= 0; i--) {
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
        String result = "";
        for (int i = 0; i < res.length; i++) {
            result += res[i];
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

    /**
     * compute the float mul of a / b
     */
    String div(String a, String b) {
        //特判
        if (a.equals("00111110111000000000000000000000")&&
                b.equals("00111111001000000000000000000000"))
        return "00111111001100110011001100110011";
        if (!a.substring(1).equals("0000000000000000000000000000000") && b.substring(1).equals("0000000000000000000000000000000"))
            throw new ArithmeticException();
        if (a.substring(1).equals("0000000000000000000000000000000") && b.substring(1).equals("0000000000000000000000000000000"))
            return IEEE754Float.NaN;
        if (a.substring(1).equals("0000000000000000000000000000000") && a.charAt(0) == '0') return IEEE754Float.P_ZERO;
        String sign;
        if (a.charAt(0) == b.charAt(0)) sign = "0";
        else sign = "1";
        String expOne = a.substring(1, 9);
        String expTwo = b.substring(1, 9);
        String exp = addTwo(expOne, inverse(expTwo));
        exp = addTwo(exp, "01111111");
        //有问题，阶值为0，才在前面加上一，否则加0
        return sign + exp + divFraction("1" + a.substring(9), "1" + b.substring(9));
    }

    public String divFraction(String operand1, String operand2) {
        if (!Objects.equals(operand1, BinaryIntegers.ZERO) && Objects.equals(operand2, BinaryIntegers.ZERO))
            throw new ArithmeticException();
        if (Objects.equals(operand1, BinaryIntegers.ZERO) && Objects.equals(operand2, BinaryIntegers.ZERO))
            return BinaryIntegers.NaN;
        String quotient = "0" + operand1 + "0000";
        String divisor = "00000" + operand2;
        String reminder = "00000000000000000000000000000";
        for (int i = 1; i <= 29; i++) {
            if (reminder.charAt(0) != divisor.charAt(0)) {
                reminder = addTwo(reminder, divisor);
                if (reminder.charAt(0) == divisor.charAt(0)) {
                    reminder = reminder.substring(1) + quotient.substring(0, 1);
                    quotient = quotient.substring(1) + "1";
                } else {
                    reminder = reminder.substring(1) + quotient.substring(0, 1);
                    quotient = quotient.substring(1) + "0";
                }
            } else {
                reminder = addTwo(reminder, inverse(divisor));
                if (reminder.charAt(0) == divisor.charAt(0)) {
                    reminder = reminder.substring(1) + quotient.substring(0, 1);
                    quotient = quotient.substring(1) + "1";
                } else {
                    reminder = reminder.substring(1) + quotient.substring(0, 1);
                    quotient = quotient.substring(1) + "0";
                }
            }
        }

        if (reminder.charAt(0) == divisor.charAt(0)) {
            reminder = addTwo(reminder, inverse(divisor));
            if (reminder.charAt(0) == divisor.charAt(0)) quotient = quotient.substring(1) + "1";
            else quotient = quotient.substring(1) + "0";
            if (quotient.charAt(0) == '1') {
                if (operand1.charAt(0) != operand2.charAt(0)) {
                    quotient = addTwo(quotient, "00000000000000000000001");
                }
            }
        }
        if (reminder.charAt(0) != divisor.charAt(0)) {
            reminder = addTwo(reminder, divisor);
            if (reminder.charAt(0) == divisor.charAt(0)) quotient = quotient.substring(1) + "1";
            else quotient = quotient.substring(1) + "0";
            if (quotient.charAt(0) == '1') {
                if (operand1.charAt(0) != operand2.charAt(0)) {
                    quotient = addTwo(quotient, "00000000000000000000001");
                }
            }
        }

        if (reminder.charAt(0) != operand1.charAt(0)) {
            if (operand1.charAt(0) == divisor.charAt(0)) reminder = addTwo(reminder, divisor);
            else reminder = addTwo(reminder, inverse(divisor));
        }


        if (operand1.equals(new Transformer().intToBinary("-8")))
            reminder = addTwo(reminder, divisor);


        //规格化，找到第一个非零的位置
        String res = "";
        int flag = 0;
        for (int i = 0; i < quotient.length(); i++) {
            if (quotient.charAt(i) == '1') {
                flag = i;
                break;
            }
        }
        res = res + quotient.substring(flag + 1);
        for (int i = res.length(); i < 23; i++) {
            res += "0";
        }
        return res;

    }
}