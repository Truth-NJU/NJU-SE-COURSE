package util;

/**
 * @CreateTime: 2020-11-23 22:13
 */
public class CRC {

    /**
     * CRC计算器
     * @param data 数据流
     * @param polynomial 多项式
     * @return CheckCode
     */
    public static char[] Calculate(char[] data, String polynomial) {
        int len = polynomial.length();
        //扩充数据，扩充的位数比生成多项式的长度少1
        char[] extendedData =new char[data.length+len-1];
        for (int i = 0; i < data.length; i++) {
            extendedData[i]=data[i];
        }
        for(int i=data.length;i<data.length+len-1;i++){
            extendedData[i]='0';
        }
        ////记录上0还是上一
        int flag;
        if (extendedData[0] == '0') {
            flag = 0;
        } else {
            flag = 1;
        }
        char[] checkCode;
        char[] a = polynomial.toCharArray();
        for (int i = 0; i < data.length; i++) {
            //如果extendedData第i位为1则上一
           if(extendedData[i]=='1'){
               for(int j=1;j<=len-1;j++){
                //上一后若相同为0，不同为1
                   if(extendedData[i+j]==a[j]){
                       extendedData[i+j]='0';
                   }else extendedData[i+j]='1';

               }
           }else{
            //如果extendedData第i位为0则上0
                for(int j=1;j<=len-1;j++){
                    //全部与0比较
                    if(extendedData[i+j]=='0'){
                        extendedData[i+j]='0';
                    }else extendedData[i+j]='1';

                }
            }
        }
        checkCode = String.valueOf(extendedData).substring(data.length).toCharArray();

        return checkCode;

    }
    public static char[] XOR(char[] a,char[] b){
        char[] res=new char[a.length];
        for(int i=0;i<a.length;i++){
           if(a[i]==b[i]) res[i]='0';
           else res[i]='1';
        }
        char[] result=new char[a.length-1];
        for(int i=0;i<a.length-1;i++){
            result[i]=res[i+1];
        }
        return result;
    }

   public static void main(String[] args){
       System.out.println(Calculate("110011".toCharArray(),"1001"));
       System.out.print(Check("100011".toCharArray(),"1001","111".toCharArray()));
   }
    /**
     * CRC校验器
     * @param data 接收方接受的数据流
     * @param polynomial 多项式
     * @param CheckCode CheckCode
     * @return 余数
     */
    public static char[] Check(char[] data, String polynomial, char[] CheckCode){
        //TODO
        String temp=String.valueOf(data);
        int len=polynomial.length();
        for(int i=0;i<CheckCode.length;i++){
            temp+=String.valueOf(CheckCode[i]);
        }
        char[] extendedData=temp.toCharArray();
        ////记录上0还是上一
        int flag;
        if (extendedData[0] == '0') {
            flag = 0;
        } else {
            flag = 1;
        }
        char[] checkCode=new char[CheckCode.length];
        char[] tempMiddle=new char[CheckCode.length];
        //记录要异或的字符数组
        char[] flagChar = new char[polynomial.length()];
        for (int i = 0; i < flagChar.length; i++) {
            flagChar[i] = extendedData[i];
        }
        int status = 1;
        for (int i = len; i < extendedData.length; i++) {
            if (flag == 0) {
                //遇0上0
                char[] a = new char[len];
                for (int j = 0; j < len; j++) {
                    a[j] = '0';
                }
                tempMiddle=XOR(a,flagChar);
                for (int j = 0; j < flagChar.length - 1; j++) {
                    flagChar[j] = tempMiddle[j];
                }
                flagChar[flagChar.length - 1] = extendedData[i];
                if (tempMiddle[0] == '0') {
                    flag = 0;
                    status = 0;
                } else {
                    flag = 1;
                    status = 0;
                }

            }
            if (flag == 1 && status == 1) {
                //遇1上1
                char[] a = polynomial.toCharArray();
                tempMiddle=XOR(a,flagChar);
                for (int j = 0; j < flagChar.length - 1; j++) {
                    flagChar[j] = tempMiddle[j];
                }
                flagChar[flagChar.length - 1] = extendedData[i];
                if (tempMiddle[0] == '0') {
                    flag = 0;
                } else {
                    flag = 1;
                }
            }
            status = 1;
        }
        if (flag == 0) {
            //遇0上0
            char[] a = new char[len];
            for (int j = 0; j < len; j++) {
                a[j] = '0';
            }
            tempMiddle=XOR(a,flagChar);
            checkCode = tempMiddle;
        }
        if (flag == 1) {
            //遇1上1
            char[] a = polynomial.toCharArray();
            tempMiddle=XOR(a,flagChar);
            checkCode = tempMiddle;

        }
        return checkCode;

    }
    /**
     * 这个方法仅用于测试，请勿修改
     * @param data
     * @param polynomial
     */
    public static void CalculateTest(char[] data, String polynomial){
        System.out.print(Calculate(data, polynomial));
    }
    /**
     * 这个方法仅用于测试，请勿修改
     * @param data
     * @param polynomial
     */
    public static void CheckTest(char[] data, String polynomial, char[] CheckCode){
        System.out.print(Check(data, polynomial, CheckCode));
    }
}
