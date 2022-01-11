package transformer;


import java.util.Objects;

import static java.lang.Math.abs;

//
public class Transformer {
    /**
     * Integer to binaryString
     *
     * @param numStr to be converted
     * @return result
     */
    public static String intToBinary(String numStr) {
        //TODO:
        int num=Integer.parseInt(numStr);
        String result=Integer.toBinaryString(num);
        char[] res=new char[32];
        String str= "";
        for(int i=0;i<32;i++){
            res[i]='0';
        }
        if(num>=0) {
            for(int i=0;i<32-result.length();i++){
                str+=res[i];
            }
            str+=result;
            return str;
        }
        else{
            char[] res1=Integer.toBinaryString(0-num).toCharArray();
            int m=res1.length-1;
            for(int i=31;i>=32-res1.length;i--){
                res[i]=res1[m];
                m--;
            }
            res[0]='1';
            for(int i=1;i<res.length;i++){
                if(res[i]=='0') res[i]='1';
                else res[i]='0';
            }
            int flag;
            int len=res.length;
            if(res[res.length-1]=='0'){
                res[res.length-1]='1';
                return new String(res);
            }
            else{
                flag=1;
                res[res.length-1]='0';
                for(int i=len-2;i>=1;i--){
                    if(res[i]=='0' && flag==1){
                        res[i]='1';
                        flag=0;
                    }else if(res[i]=='1' && flag==1){
                        res[i]='0';
                        flag=1;
                    }
                }
                return new String(res);
            }
        }
    }


    /**
     * BinaryString to Integer
     *
     * @param binStr : Binary string in 2's complement
     * @return :result
     */
    public static String binaryToInt(String binStr) {
        //TODO:
        char[] num = binStr.toCharArray();
        if(num[0]=='0') {
            int number = Integer.parseInt(binStr, 2);
            return String.valueOf(number);
        }
        int result=0;
        int flag=1;
        for(int i=num.length-1;i>=1;i--){
            if(num[i]=='0'){
                flag=flag*2;
            }
            if(num[i]=='1'){
                result+=flag;
                flag=flag*2;
            }
        }
        if(num[0]=='1'){
        result-=flag;
        }
        return String.valueOf(result);
    }



    /**
     * Float true value to binaryString
     * @param floatStr : The string of the float true value
     * */
    public static String floatToBinary(String floatStr) {
        String result = "";
        float num = Float.parseFloat(floatStr);
        String sign = "0";
        int exponent = 127;
        //若值为0，则符号为后全为0
        if (num == 0) {
            result = result + sign + "0000000000000000000000000000000";
        }
        if (num < 0) {
            sign = "1";
            num = 0 - num;
        }
        //全部化为正数之后在做计算，将十进制化为二进制（以2为底）
        while (num >= 2) {
            exponent += 1;
            if (exponent >= 255) break;
            num = num / 2;
        }
        while (num < 1) {
            exponent -= 1;
            if (exponent == 0)
                break;
            num *= 2;
        }
        if (num >= 2) {
            //正数且exponent为128，溢出
            if (sign.equals("0")) return "+Inf";
            else return "-Inf";
        } else if (num < 1) {
            String res = "";
            for (int i = 0; i < 23; i++) {
                num *= 2;
                if (num >= 1) {
                    res=res+"1";
                    num-=1;
                } else res=res + "0";
            }
            result=sign + "00000000" + res;
        } else {
            String res1 = "";
            num = num - 1;
            for(int i=0;i<23;i++) {
                num*=2;
                if (num>= 1) {
                    res1=res1+"1";
                    num-=1;
                } else  res1=res1 + "0";
            }
            String res2="";
            for (int i=7;i>=0;i--) {
                if (exponent%2 == 0) {
                    res2="0"+res2;
                } else  res2="1"+res2;
                exponent=exponent/2;
            }
            result=sign+res2+res1;
        }
        return result;
    }

    public static void main(String[] args){
        //System.out.println(decimalToNBCD("-1265"));
        System.out.println(Math.pow(2,-2));
    }

    /**
     * Binary code to its float true value
     * */
    public static String binaryToFloat(String binStr) {
        //TODO:
        String exp=binStr.substring(1,9);
        int exponent=Integer.parseInt(binaryToInt(exp));
        String fraction=binStr.substring(9);
        double frac=0;
        for(int i=0;i<fraction.length();i++){
            frac+=Math.pow(2,-i-1)*Integer.parseInt(String.valueOf(( fraction.charAt(i))));
        }
        if(exponent==0){

        }
        return null;
    }

    /**
     * The decimal number to its NBCD code
     * */
    public static String decimalToNBCD(String decimal) {
        //TODO:
        char[] result=new char[32];
        for(int i=0;i<32;i++){
            result[i]='0';
        }

        int num=Integer.parseInt(decimal);
        if(num>=0){
            result[0]='1';
            result[1]='1';
            result[2]='0';
            result[3]='0';
        }
        if(num<0){
            result[0]='1';
            result[1]='1';
            result[2]='0';
            result[3]='1';
        }
        int absoluteNum=abs(num);
        int forth=absoluteNum%10;
        absoluteNum=absoluteNum/10;
        int third=absoluteNum%10;
        absoluteNum=absoluteNum/10;
        int second=absoluteNum%10;
        absoluteNum=absoluteNum/10;
        int first=absoluteNum%10;
        String res=new String();
        for(int i=0;i<16;i++){
            res+=result[i];
        }
        res+=transformDecimal(first);
        res+=transformDecimal(second);
        res+=transformDecimal(third);
        res+=transformDecimal(forth);
        return res;
    }

    public static String transformDecimal(int num){
        char[] res=new char[4];
        if(num%8==num)  res[0]='0';
        if(num%8!=num) {
            res[0]='1';
            num=num-8;
        }
        if(num%4==num)  res[1]='0';
        if(num%4!=num) {
            res[1]='1';
            num=num-4;
        }
        if(num%2==num)  res[2]='0';
        if(num%2!=num) {
            res[2]='1';
            num=num-2;
        }
        if(num==0)  res[3]='0';
        else res[3]='1';
        String str=new String();
        for(int i=0;i<4;i++){
            str+=res[i];
        }
        return str;
    }

    /**
     * NBCD code to its decimal number
     * */
    public static String NBCDToDecimal(String NBCDStr) {
        //TODO:
        char[] res=new char[5];
        String first=NBCDStr.substring(16,20);
        String second=NBCDStr.substring(20,24);
        String third=NBCDStr.substring(24,28);
        String forth=NBCDStr.substring(28,32);
        String result=new String();
        for(int i=0;i<5;i++){
            res[i]='s';
        }
        String sign=NBCDStr.substring(0,4);
        if(Objects.equals(sign, "1101")) {
            res[0]='-';
            res[1] = transformNBCD(first);
            res[2]=transformNBCD(second);
            res[3]=transformNBCD(third);
            res[4]=transformNBCD(forth);
            for(int i=1;i<5;i++){
                result+=res[i];
            }
            result=res[0]+String.valueOf(Integer.parseInt(result));
        }
        if(Objects.equals(sign, "1100")) {
            res[0]=transformNBCD(first);
            res[1]=transformNBCD(second);
            res[2]=transformNBCD(third);
            res[3]=transformNBCD(forth);
            for(int i=0;i<4;i++){
                result+=res[i];
            }
            result=String.valueOf(Integer.parseInt(result));
        }
        return result;
    }

    public static char transformNBCD(String NBCDStr){
        char[] res=NBCDStr.toCharArray();
        int num=0;
        int flag=8;
        for(int i=0;i<res.length;i++){
            if(res[i]=='1') num+=flag;
            flag=flag/2;
        }
        return String.valueOf(num).charAt(0);
    }



}
