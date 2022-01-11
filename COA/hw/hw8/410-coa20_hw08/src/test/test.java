import cpu.MMU;
import memory.Memory;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

public class test {

    Memory memory=Memory.getMemory();
    MMU mmu= MMU.getMMU();

    // 段⻚式
    @Test
    public void test1() {
       memory.setPAGE(true);
       memory.setSEGMENT(true);
        String eip = "00000000000000000000000000000000";
        int len = 2 * 1024;
        char[] data = fillData((char) 0b00001111, len);
        memory.alloc_seg_force(0, eip, len / 2, true, "");
        assertArrayEquals(data,
                mmu.read("000000000000000000000000000000000000000000000000", len));

    }
    // 实模式
    @Test
    public void test2() {
        memory.setPAGE(false);
        memory.setSEGMENT(false);
        int len = 128;
        char[] data = fillData((char)0b00001111, 128);
        assertArrayEquals(data,
                mmu.read("000000000000000000000000000000000000000000000000", len));
    }
    // 段式
    @Test
    public void test3() {
        memory.setSEGMENT(true);
        memory.setPAGE(false);
        String eip = "00000000000000000000000000000000";
        int len = 1024 * 1024;
        char[] data = fillData((char)0b00001111, len);
        memory.alloc_seg_force(0, eip, len, false, eip);
        assertArrayEquals(data,
                mmu.read("000000000000000000000000000000000000000000000000", len));
    }

    public char[] fillData(char dataUnit, int len) {
        char[] data = new char[len];
        Arrays.fill(data, dataUnit);
        return data;
    }

}
