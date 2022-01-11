package memory;

import memory.cacheMappingStrategy.MappingStrategy;
import memory.cacheMappingStrategy.SetAssociativeMapping;
import memory.cacheReplacementStrategy.FIFOReplacement;
import memory.cacheReplacementStrategy.ReplacementStrategy;
import memory.cacheWriteStrategy.WriteStrategy;
import transformer.Transformer;

import java.util.Arrays;
import java.util.Calendar;

/**
 * 高速缓存抽象类
 * TODO: 缓存机制实现
 */
public class Cache {    //
	//行大小位1KB=1024字节
	public static final boolean isAvailable = true;// 默认启用Cache

	public static final int CACHE_SIZE_B = 1 * 1024 * 1024;      // 1 MB 总大小

	public int getCacheSizeB() {
		return CACHE_SIZE_B;
	}

	public static final int LINE_SIZE_B = 1 * 1024; // 1 KB

	public int getLineSizeB() {
		return LINE_SIZE_B;
	}

	public CacheLinePool cache = new CacheLinePool(CACHE_SIZE_B / LINE_SIZE_B);    // 总大小1MB / 行大小1KB = 1024个行

	public CacheLinePool getCacheLinePool() {
		return cache;
	}

	private static Cache cacheInstance = new Cache();

	public Cache() {
		this.mappingStrategy = new SetAssociativeMapping();
		mappingStrategy.setReplacementStrategy(new FIFOReplacement());
	}

	public static Cache getCache() {
		return cacheInstance;
	}

	private MappingStrategy mappingStrategy;
	private WriteStrategy writeStrategy;

	/**
	 * 查询{@link Cache#cache}表以确认包含[sAddr, sAddr + len)的数据块是否在cache内
	 * 如果目标数据块不在Cache内，则将其从内存加载到Cache
	 *
	 * @param sAddr 数据起始点(32位物理地址 = 22位块号 + 10位块内地址)
	 * @param len   待读数据的字节数，[sAddr, sAddr + len)包含的数据必须在同一个数据块内
	 * @return 数据块在Cache中的对应行号
	 */
	//将数据从内存读到Cache(如果还没有加载到Cache)，并返回被更新的Cache行的行号(需要调用不同的映射策略和替换策略)
	private int fetch(String sAddr, int len) {
		//TODO
		int line = mappingStrategy.map(getBlockNO(sAddr));
		//命中，数据已经在cache中
		if (line != -1) return line;
		//未命中，将数据写入cache
		return mappingStrategy.writeCache(getBlockNO(sAddr));
	}

	/**
	 * 读取[eip, eip + len)范围内的连续数据，可能包含多个数据块的内容
	 *
	 * @param eip 数据起始点(32位物理地址 = 22位块号 + 10位块内地址)
	 * @param len 待读数据的字节数
	 * @return
	 */
	public char[] read(String eip, int len) {
		return helper(eip, len, null);
	}

	/**
	 * 将连续的数据写入[eip, eip + len]，可能包含多个数据块的内容
	 *
	 * @param eip  数据起始点(32位物理地址 = 22位块号 + 10位块内地址)
	 * @param len  待读数据的字节数
	 * @param data 连续数据
	 */
	public void write(String eip, int len, char[] data) {
		helper(eip, len, data);
	}

	public void setvalid(int rowNO) {
		cache.get(rowNO).validBit = true;
	}

	public void setDirty(int rowNO) {
		cache.get(rowNO).dirty = true;
	}


	public boolean isMatch(int row, char[] tag){
		if(this.cache.get( row ) == null){
			return false;
		}
		if (!this.cache.get(row).validBit) {
			return false;
		}
		if (!Arrays.equals(this.cache.get(row).tag, tag)) {
			return false;
		}
		return true;
	}


	public boolean isDirty(int rowNo){
		return cache.get(rowNo).dirty;
	}

	public boolean isValid(int row){
		return cache.get(row).validBit;
	}


	public char[] getData(int rowNO) {
		return cache.get(rowNO).data;
	}

	public long getTimeStamp(int row) {
		CacheLine cacheLine = cache.get(row);
		if (cacheLine.validBit) {
			return cacheLine.timeStamp;
		}
		return -1;
	}

	//将内存中的数据写入cache
	public void change(int line, char[] data, char[] tag) {

		CacheLine cacheLine = cache.get(line);
		//在向cache中写入数据时，需要检查是否需要更新内存中的数据，如果数据有效且数据被修改了，就要写回主存进行更新
		if (cacheLine.validBit && cacheLine.dirty) {
			writeStrategy.writeBack(line);
			cacheLine.dirty = false;
		}

		cacheLine.validBit = true;
		cacheLine.timeStamp = Calendar.getInstance().getTimeInMillis();
		//将内存中的数据写入cache
		for (int i = 0; i < tag.length; i++) {
			cacheLine.tag[i] = tag[i];
		}
		for (int i = 0; i < data.length; i++) {
			cacheLine.data[i] = data[i];
		}
	}

	private char[] helper(String eip, int len, char[] writeData) {
		char[] data = new char[len];
		Transformer t = new Transformer();
		//数据起点位置的整数表示
		int addr = Integer.parseInt(t.binaryToInt("0" + eip));
		//数据上界
		int upperBound = addr + len;
		int index = 0;
		while (addr < upperBound) {
			//nextSegLen表示当前行所需要填充的字节数
			int nextSegLen = LINE_SIZE_B - (addr % LINE_SIZE_B);
			//如果addr+nextSegLen超过上界，则表明该行足够存数据了，即所需填充的字节数为len
			if (addr + nextSegLen >= upperBound) {
				nextSegLen = upperBound - addr;
			}
			int i = 0;
			//若要写入的数据为空
			if (writeData == null) {
				//rowNo为cache中对应的行号
				int rowNO = fetch(t.intToBinary(String.valueOf(addr)), nextSegLen);
				//根据行号得到对应的行后返回该行的数据
				char[] cache_data = cache.get(rowNO).getData();
				//当i<该行要填充的字节数时
				while (i < nextSegLen) {
					data[index] = cache_data[addr % LINE_SIZE_B + i];
					index++;
					i++;
				}
			}
			//若要写入的数据不为空
			else {
				//rowNo为cache中对应的行号
				int rowNO = fetch(t.intToBinary(String.valueOf(addr)), nextSegLen);
				//根据行号得到对应的行后返回该行的数据
				char[] input = cache.get(rowNO).getData();
				while (i < nextSegLen) {
					input[addr % LINE_SIZE_B + i] = writeData[index];
					index++;
					i++;
				}
				//在对应行里写入要写入的数据
				writeStrategy.write(rowNO, input, getBlockNO(t.intToBinary("0" + addr)));
			}
			addr += nextSegLen;
		}
		return data;
	}


	public void setStrategy(MappingStrategy mappingStrategy, ReplacementStrategy replacementStrategy, WriteStrategy writeStrategy) {
		//TODO
		writeStrategy.setMappingStrategy(mappingStrategy);
		mappingStrategy.setReplacementStrategy(replacementStrategy);
		replacementStrategy.setWriteStrategy(writeStrategy);
		this.mappingStrategy = mappingStrategy;
		mappingStrategy.setReplacementStrategy(replacementStrategy);
		this.writeStrategy = writeStrategy;

	}

	/**
	 * 从32位物理地址(22位块号 + 10位块内地址)获取目标数据在内存中对应的块号
	 *
	 * @param addr
	 * @return
	 */
	public int getBlockNO(String addr) {
		Transformer t = new Transformer();
		return Integer.parseInt(t.binaryToInt("0" + addr.substring(0, 22)));
	}

	/**
	 * 告知Cache某个连续地址范围内的数据发生了修改，缓存失效
	 *
	 * @param sAddr 发生变化的数据段的起始地址
	 * @param len   数据段长度
	 */
	public void invalid(String sAddr, int len) {
		int from = getBlockNO(sAddr);
		Transformer t = new Transformer();
		int to = getBlockNO(t.intToBinary(String.valueOf(Integer.parseInt(t.binaryToInt("0" + sAddr)) + len - 1)));

		for (int blockNO = from; blockNO <= to; blockNO++) {
			int rowNO = mappingStrategy.map(blockNO);
			if (rowNO != -1) {
				cache.get(rowNO).validBit = false;
			}
		}
	}

	/**
	 * 清除Cache全部缓存
	 * 这个方法只会在测试的时候用到
	 */
	public void clear() {
		for (CacheLine line : cache.clPool) {
			if (line != null) {
				line.validBit = false;
			}
		}
	}

	/**
	 * 输入行号和对应的预期值，判断Cache当前状态是否符合预期
	 * 这个方法仅用于测试
	 *
	 * @param lineNOs
	 * @param validations
	 * @param tags
	 * @return
	 */
	public boolean checkStatus(int[] lineNOs, boolean[] validations, char[][] tags) {
		if (lineNOs.length != validations.length || validations.length != tags.length) {
			return false;
		}
		for (int i = 0; i < lineNOs.length; i++) {
			CacheLine line = cache.get(lineNOs[i]);
			if (line.validBit != validations[i]) {
				return false;
			}
			if (!Arrays.equals(line.getTag(), tags[i])) {
				return false;
			}
		}
		return true;
	}

	public char[] getTag(int rowNO) {
		return cache.get(rowNO).tag;
	}

	/**
	 * 负责对CacheLine进行动态初始化
	 */
	public class CacheLinePool {

		/**
		 * @param lines Cache的总行数
		 */
		public CacheLinePool(int lines) {
			clPool = new CacheLine[lines];
		}

		public CacheLine[] clPool;

		public CacheLine get(int lineNO) {
			if (lineNO >= 0 && lineNO < clPool.length) {
				CacheLine l = clPool[lineNO];
				if (l == null) {
					clPool[lineNO] = new CacheLine();
					l = clPool[lineNO];
				}
				return l;
			}
			return null;
		}
	}


	/**
	 * Cache行，每行长度为(1+22+{@link Cache#LINE_SIZE_B})
	 */
	public static class CacheLine {
		// 有效位，标记该条数据是否有效
		public boolean validBit = false;
		// 脏位，标记该条数据是否被修改
		public boolean dirty = false;
		// 用于LFU算法，记录该条cache使用次数
		public int visited = 0;

		// 用于LRU和FIFO算法，记录该条数据时间戳
		public Long timeStamp = 0l;

		// 标记，占位长度为()22位，有效长度取决于映射策略：
		// 直接映射: 12 位
		// 全关联映射: 22 位
		// (2^n)-路组关联映射: 22-(10-n) 位
		// 注意，tag在物理地址中用高位表示，如：直接映射(32位)=tag(12位)+行号(10位)+块内地址(10位)，
		// 那么对于值为0b1111的tag应该表示为0000000011110000000000，其中前12位为有效长度，
		// 因为测试平台的原因，我们无法使用4GB的内存，但是我们还是选择用32位地址线来寻址
		char[] tag = new char[22];

		// 数据
		char[] data = new char[LINE_SIZE_B];


		public char[] getData() {
			return this.data;
		}

		public char[] getTag() {
			return this.tag;
		}

		public void setTag(char[] tag) {
			this.tag = tag;
		}

		public void setData(char[] data) {
			this.data = data;
		}
	}

}
