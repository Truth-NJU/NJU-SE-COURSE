package memory.cacheReplacementStrategy;

import memory.Cache;

import java.util.Calendar;

/**
 * 先进先出算法
 */
public class FIFOReplacement extends ReplacementStrategy {

    /**
     * 在start-end范围内查找是否命中
     * @param start 起始行
     * @param end 结束行 闭区间
     */
    @Override
    public int isHit(int start, int end, char[] addrTag) {
        //TODO
//        Cache.CacheLinePool cacheLinePool = Cache.getCache().cache;
//        Cache.CacheLine[] clPool =  cacheLinePool.clPool;
//        for(int i=start;i<=end;i++){
//            if(clPool[i]==null) continue;
//            //命中
//            if(String.valueOf(clPool[i].getTag()).equals(String.valueOf(addrTag))) {
//                //如果
//                if (!clPool[i].validBit) continue;
//                clPool[i].visited++;
//                clPool[i].timeStamp = Calendar.getInstance().getTimeInMillis();
//                return i;
//            }
//
//        }
//        return -1;
        for(int i=start;i<=end;i++){
            if(Cache.getCache().isMatch( i,addrTag )){   // 命中该行
                return i; // 返回该行
            }
        }

        // 没有命中
        return -1;
    }

    /**
     * 在未命中的情况下将内存中的数写入cache
     * @param start 起始行
     * @param end 结束行 闭区间
     * @param addrTag tag
     * @param input  数据
     * @return
     */
    @Override
    public int Replace(int start, int end, char[] addrTag, char[] input) {
        //TODO
        int startLine = -1;
        long min_time = Long.MAX_VALUE;
        for (int i = start; i <= end; i++) {
            long currentTime=Cache.getCache().getTimeStamp(i);
            if(Cache.getCache().isDirty(i)){
                startLine=i;
                break;
            }
            if(currentTime<min_time){
                min_time=currentTime;
                startLine=i;
            }
        }

        if(Cache.getCache().isDirty(startLine) && Cache.getCache().isValid(startLine)){
            writeStrategy.writeBack(startLine);
        }
        Cache.getCache().change(startLine, input, addrTag);
        return startLine;
    }



}
