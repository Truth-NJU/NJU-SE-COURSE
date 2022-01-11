package cpu.alu;

import transformer.Transformer;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 * TODO: 加减与逻辑运算
 */
public class ALU {

    // 模拟寄存器中的进位标志位
    private String CF = "0";

    // 模拟寄存器中的溢出标志位
    private String OF = "0";

    //add two integer
    String add(String src, String dest) {
        // TODO
        String result="";
        char[] res=new char[32];
        for(int i=0;i<32;i++) res[i]='0';
        //进位标志
        int flag=0;
        for(int i=31;i>=0;i--){
            int a=Integer.parseInt(String.valueOf(src.charAt(i)));
            int b=Integer.parseInt(String.valueOf(dest.charAt(i)));
            if(a+b==0){
                if(flag==0) res[i]='0';
                else{
                    res[i]='1';
                }
                flag=0;
            }
            else if(a+b==1){
                if(flag==0) res[i]='1';
                else{
                    res[i]='0';
                    flag=1;
                }
            }
            else{
                //a+b==2
                if(flag==0) res[i]='0';
                else res[i]='1';
                flag=1;
            }
        }
        for(int i=0;i<32;i++){
            result+=res[i];
        }
		return result;
    }

    //sub two integer
    // dest - src
    String sub(String src, String dest) {
        // TODO
        int a=Integer.parseInt(new Transformer().binaryToInt(src));
        src=new Transformer().intToBinary(String.valueOf(-a));
        return add(src,dest);
	}

    String and(String src, String dest) {
        // TODO
        String result="";
        for(int i=0;i<src.length();i++){
            if(((src.charAt(i)-'0')&(dest.charAt(i)-'0'))==1){
                result+='1';
            }
            else result+='0';
        }
        return result;
    }

    String or(String src, String dest) {
        // TODO
        String result="";
        for(int i=0;i<src.length();i++){
            if(((src.charAt(i)-'0')|(dest.charAt(i)-'0'))==1){
                result+='1';
            }
            else result+='0';
        }
        return result;
    }

    String xor(String src, String dest) {
        // TODO
        String result="";
        for(int i=0;i<src.length();i++){
            if(((src.charAt(i)-'0')==(dest.charAt(i)-'0'))){
                result+='0';
            }
            else result+='1';
        }
        return result;
    }

}
