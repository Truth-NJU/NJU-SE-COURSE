package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import memory.cacheReplacementStrategy.ReplacementStrategy;
import transformer.Transformer;


//Cache被分成多个组，每个组包含多个行，一个给定的块映射到唯一给定的组中的任何一个行
public class SetAssociativeMapping extends MappingStrategy{

    Transformer t = new Transformer();
    private int SETS=512; // 共256个组
    private int setSize=2;   // 每个组4行  size路组


    /**
     * 该方法会被用于测试，请勿修改
     * @param SETS
     */
    public void setSETS(int SETS) {
        this.SETS = SETS;
    }

    /**
     * 该方法会被用于测试，请勿修改
     * @param setSize
     */
    public void setSetSize(int setSize) {
        this.setSize = setSize;
    }

    public int getSETS(){
        return this.SETS;
    }

    public int getSetSize(){
        return this.setSize;
    }

    /**
     *
     * @param blockNO 内存数据块的块号
     * @return cache数据块号 22-bits  [前14位有效]
     */
    @Override
    public char[] getTag(int blockNO) {
        //TODO
        //物理地址为22位块号+10位块内地址
        //标记的有效长度为22-（10-log(2,setSize），即14位
        //22位的块号中截取前十四位有效
        String res = t.intToBinary(String.valueOf(blockNO)).substring(10).substring(0, 12+log(2,setSize));
        while(res.length()!=22)res=res+"0";
        return res.toCharArray();
    }

    private int log(int base, int num){return (int)(Math.log(num)/Math.log(base));}

    /**
     *
     * @param blockNO 目标数据内存地址前22位int表示
     * @return cache中对应的行， -1 表示未命中
     */
    @Override
    public int map(int blockNO) {
        //TODO
        int temp = Cache.CACHE_SIZE_B / (Cache.LINE_SIZE_B * setSize);//cache中有几组
        int start = (blockNO % temp) * setSize;
        //因为isHit函数中i<=end，所以end要减一
        int end = start + setSize - 1;
        return this.replacementStrategy.isHit(start, end, getTag(blockNO));
    }

    /**
     * 未命中的情况下，将内存读取出的input数据写入cache
     * @param blockNO
     * @return 返回cache中所对应的行
     */
    @Override
    public int writeCache(int blockNO) {
        //TODO
        Memory memory = Memory.getMemory();
        ReplacementStrategy replacementStrategy = this.replacementStrategy;
        //行大小X每组的行数，再用cache大小除得到cache中有多少组，再用块号模得到在第几组里再乘每组大小得到开始行
        int start=(blockNO%(Cache.CACHE_SIZE_B/(Cache.LINE_SIZE_B*this.setSize)))*setSize;
        int end = start+setSize-1;
        //从内存中读出数据放入cache
        //memory.read中第一个参数为数据的起始地址，是22位块号加上10位块内地址，块内地址
        char[] input = memory.read(t.intToBinary(String.valueOf(blockNO)).substring(10)+"0000000000",Cache.LINE_SIZE_B);//读的是一整个块
        return replacementStrategy.Replace(start,end,getTag(blockNO),input);
    }
  
    /**通过映射倒推出内存地址
     * @param rowNo
     * @return 返回具体的地址 tag+setNO+word
     */
    @Override
    public String getPAddr(int rowNo) {
        //TODO
        //由行号倒推出地址
        String addr = "";
        Cache.CacheLinePool cacheLinePool = Cache.getCache().cache;
        Cache.CacheLine[] clPool = cacheLinePool.clPool;
        Cache.CacheLine cacheLine = clPool[rowNo];
        //获得标记
        char[] tag = cacheLine.getTag();
        //标记的有效长度
        int len = 12 + (int) (Math.log(setSize) / Math.log(2));
        for (int i = 0; i < len; i++) {
            addr += tag[i];
        }
        //获得组号
        int setNO = rowNo / setSize;
        addr += t.intToBinary("" + setNO).substring(32 - (int) (Math.log(SETS) / Math.log(2)), 32);
        return addr + "0000000000";
    }

}










