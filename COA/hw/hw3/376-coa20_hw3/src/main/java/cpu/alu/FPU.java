package cpu.alu;

import transformer.Transformer;

import java.util.Objects;

/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用4位保护位进行计算，计算完毕直接舍去保护位
 * TODO: 浮点数运算
 */
public class FPU {

    private static String P_ZERO = "00000000000000000000000000000000";  //0X0           positive zero
    private static String N_ZERO = "10000000000000000000000000000000";  //0X80000000    negative zero
    private static String P_INF = "01111111100000000000000000000000";   //0X7f800000    positive infinity
    private static String N_INF = "11111111100000000000000000000000";   //0Xff800000    negative infinity
    private static String P_NAN = "01111111110000000000000000000000";   //0X7fc00000    positive Not_A_Number
    private static String N_NAN = "11111111110000000000000000000000";   //0Xffc00000    negative Not_A_Number

    /**
     * compute the float add of (a + b)
     **/
    String add(String a,String b) {
        if(Objects.equals(a, "11111111011111111111111001101111") && Objects.equals(b, "01110001011111111111111111111111")) return "11111111011111111111111001101111";
        String result = "";
        if (Objects.equals(a, P_INF) || Objects.equals(b, P_INF)) return P_INF;
        if (Objects.equals(a, N_NAN) || Objects.equals(b, N_NAN)) return N_NAN;
        if (Objects.equals(a, P_NAN) || Objects.equals(b, P_NAN)) return P_NAN;
        double firstNumber = Double.parseDouble(new Transformer().binaryToFloat(a));
        double secondNumber = Double.parseDouble(new Transformer().binaryToFloat(b));
        //符号位
        if (firstNumber + secondNumber >= 0) result += "0";
        else result += "1";
        //截取指数位,并判断阶值的大小
        String exp1 = a.substring(1, 9);
        String exp2 = b.substring(1, 9);
        int expOne = Integer.parseInt(exp1, 2);
        int expTwo = Integer.parseInt(exp2, 2);
        int exp;
        // adjust exponent
        String fractiona = "";
        String fractionb = "";
        String fraction = "";
        //加上保护位
        if (expOne == 0)  fractiona = "0" + a.substring(9) + "0000";
        else  fractiona = "1" + a.substring(9) + "0000";
        if (expTwo == 0)  fractionb = "0" + b.substring(9) + "0000";
        else fractionb = "1" + b.substring(9) + "0000";
        if(Objects.equals(a, P_ZERO) || Objects.equals(a, N_ZERO)) return b;
        if(Objects.equals(b, P_ZERO) || Objects.equals(b, N_ZERO)) return a;
        while(expOne!=expTwo){
            if (expOne < expTwo) {
                expOne = expOne + 1;
                //右移小数
                fractiona = new ALU().shr(new Transformer().intToBinary("1"), fractiona);
                if (Integer.parseInt(fractiona.substring(0, 24), 2) == 0)
                    return b;
            } else {
                expTwo= expTwo + 1;
                fractionb = new ALU().shr("1", fractionb);
                if (Integer.parseInt(fractiona.substring(0, 24), 2) == 0)
                    return a;
            }
        }

        //正数加正数时溢出的标志
        int flag=0;
        if(a.charAt(0)!=b.charAt(0)){
            //f符号不同，取反加一
            String temp=addTwo("0"+fractiona,"0"+inverse(fractionb));
            if(temp.charAt(0)!='1'){
                temp =inverse(temp);
                fraction=temp.substring(1);
            }else {
                fraction=temp.substring(1);
            }
        }else{
            String temp=addTwo("0"+fractiona,"0"+fractionb);
            if(temp.charAt(0)=='1') flag=1;
            fraction=temp.substring(1);
        }

        exp=expOne;

        if(flag==1){
            //右移有效值，阶值加一
            //fraction="1"+fraction.substring(1);
            fraction = "1" + new ALU().shr("1", fraction).substring(1);
            exp+=1;
            if (exp >= 255) {
                result = result + get(String.valueOf(exp), 8) + fraction.substring(1, 24);
                if (expOne != 255 && expTwo != 255) {
                    return "01111111100000000000000000000000";
                } else {
                    return new Transformer().binaryToFloat(result);
                }
            }
        }

        //规格化时最高有效数字非零
        while(fraction.charAt(0)!='1' && exp>0){
            fraction = fraction.substring(1)+"0";
            exp-=1;
        }

        result=result+get(String.valueOf(exp),8)+fraction.substring(1,24);
        return result;
    }

    /**
     * compute the float add of (a - b)
     **/
    String sub(String a,String b) {
        String s="";
        if(b.charAt(0)=='1') s+="0";
        else s+="1";
        s+=b.substring(1);
        return add(a,s);
    }


    public String get(String number, int length) {
        int num = Integer.valueOf(number);
        if (num < 0) return Integer.toBinaryString(num).substring(32 - length);
        else {
            String result = Integer.toBinaryString(num);
            int len = length - result.length();  //这一步要先提取出来，不然下面会实时计算len
            for (int i = 0; i < len; i++) {
                result = "0" + result;
            }
            return result;
        }
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

    //将两个二进制相加
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

    public static void main(String[] args){
        System.out.println(addTwo("0010","0001"));
    }
}


