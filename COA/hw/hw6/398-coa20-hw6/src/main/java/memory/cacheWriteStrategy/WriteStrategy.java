package memory.cacheWriteStrategy;

import memory.Cache;
import memory.Memory;
import memory.cacheMappingStrategy.MappingStrategy;

/**
 * @Author: A cute TA
 * @CreateTime: 2020-11-12 11:38
 */
public abstract class WriteStrategy {
    MappingStrategy mappingStrategy;
    /**
     * 将数据写入Cache，并且根据策略选择是否修改内存
     * @param rowNo 行号
     * @param input  数据
     * @return
     */
    public String write(int rowNo, char[] input,int blockNO) {
        //TODO
        Cache cache=Cache.getCache();
        //如果cache中的行是被修改过的，则将数据写入cache前需要写回，修改主存
        if (Cache.getCache().cache.get(rowNo).dirty) {
            writeBack(rowNo);
        }
        char[] tag = mappingStrategy.getTag(blockNO);
        //将数据写入cache
        cache.change(rowNo, input, tag);
        Cache.getCache().cache.get(rowNo).dirty=true;
        Cache.getCache().cache.get(rowNo).validBit=true;
        return null;
    }


    /**
     * 修改内存
     * @return
     */
    public void writeBack(int rowNo) {
        //TODO
        Memory memory = Memory.getMemory();
        //由行号倒推出内存中的地址
        String memoryAdress = mappingStrategy.getPAddr(rowNo);
        //将数据写入内存
        memory.write(memoryAdress,1024, Cache.getCache().getData(rowNo));
    }

    public void setMappingStrategy(MappingStrategy mappingStrategy) {
        this.mappingStrategy = mappingStrategy;
    }

    public abstract Boolean isWriteBack();
}
