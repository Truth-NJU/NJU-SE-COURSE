package cpu.alu;

import transformer.Transformer;

import java.util.Objects;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 * TODO: 加减乘除
 */
public class ALU {


    //signed integer mod
    //dest mod src
    String imod(String src, String dest) {
        String res="";
        String quotient=dest;
        String divisor=src;
        String reminder;
        if(dest.charAt(0)=='0') reminder="00000000000000000000000000000000";
        else reminder="11111111111111111111111111111111";
        for(int i=1;i<=32;i++){
            if(reminder.charAt(0)!=divisor.charAt(0)){
                //被除数（余数）与除数符号不同，则进行加法，赋值给余数
                reminder=addTwo(reminder,divisor);
                //若新的余数与除数符号相同，则将商设为1
                if(reminder.charAt(0)==divisor.charAt(0)){
                    //余数左移一位并补上商的第一位
                    reminder=reminder.substring(1)+quotient.substring(0,1);
                    //商左移一位并补上1
                    quotient=quotient.substring(1)+"1";
                }else{
                    //若新的余数与除数符号不同，则将商设为0
                    //余数左移一位并补上商的第一位
                    reminder=reminder.substring(1)+quotient.substring(0,1);
                    //商左移一位并补上1
                    quotient=quotient.substring(1)+"0";
                }
            }else{
                //被除数（余数）与除数符号相同同，则进行减法，赋值给余数；进行减法为reminder-divisor，将divisor取反加一再与reminder相加即可
                reminder=addTwo(reminder,inverse(divisor));
                //以下同上
                if(reminder.charAt(0)==divisor.charAt(0)){
                    reminder=reminder.substring(1)+quotient.substring(0,1);
                    quotient=quotient.substring(1)+"1";
                }else{
                    reminder=reminder.substring(1)+quotient.substring(0,1);
                    quotient=quotient.substring(1)+"0";
                }
            }
        }
        //若余数与被除数符号不同
        if(reminder.charAt(0)!=dest.charAt(0)) {
            //若被除数与除数符号相同，则余数加除数并赋值给余数
            if (dest.charAt(0) == src.charAt(0)) reminder = addTwo(reminder, divisor);
            //若被除数与除数符不同，则余数减除数并赋值给余数
            else if(dest.charAt(0) != src.charAt(0)) reminder = addTwo(reminder, inverse(divisor));
        }

        res+=reminder;
        return res;
    }

    public static void main(String[] args){
        System.out.println(inverse("0011"));
    }

    //将两个数的二进制相加
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


    //逻辑左移，右边补0
    String shl(String src, String dest) {
        String result="";
        int offset=Integer.parseInt(src,2)%32;
        result+=dest.substring(offset);
        for(int i=0;i<offset;i++){
            result+='0';
        }
        return result;
    }

    //逻辑右移，左边补0
    String shr(String src, String dest) {
        String result="";
        int offset=Integer.parseInt(src,2);
        for(int i=0;i<offset;i++){
            result+='0';
        }
        result+=dest.substring(0,dest.length()-offset);
        return result;
    }

    //算数左移，与逻辑左移相同
    String sal(String src, String dest) {
        return shl(src,dest);
    }

    //算数右移，左边补上符号位
    String sar(String src, String dest) {
        String result="";
        String sign=dest.substring(0,1);
        int offset=Integer.parseInt(src,2);
        for(int i=0;i<offset;i++){
            result+=sign;
        }
        result+=dest.substring(0,dest.length()-offset);
        return result;
    }

    //循环左移
    String rol(String src, String dest) {
        String result="";
        int offset=Integer.parseInt(src,2);
        if(offset<dest.length()){
            result=result+dest.substring(offset)+dest.substring(0,offset);
        }
        if(offset==dest.length()) return dest;
        if(offset>dest.length()){
            while(offset>dest.length()){
                offset=offset-dest.length();
            }
            result=result+dest.substring(offset)+dest.substring(0,offset);
        }
        return result;
    }

    //循环右移
    String ror(String src, String dest) {
        String result="";
        int offset=Integer.parseInt(src,2);
        if(offset<=dest.length()){
            result=result+dest.substring(dest.length()-offset)+dest.substring(0,dest.length()-offset);
        }
        if(offset>dest.length()){
            while(offset>dest.length()){
                offset=offset-dest.length();
            }
            result=result+dest.substring(dest.length()-offset)+dest.substring(0,dest.length()-offset);
        }
        return result;
    }

}
