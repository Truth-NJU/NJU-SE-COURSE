package memory;

//注意，在磁盘读写的实现中，需要加入CRC，并存在扇区中，
// 因为校验和和数据在磁盘中都是字节形式存在，但是校验是在Bit级别上进行运算的，所以大家需要实现两个转换方法
//大家需要在类里面添加一些方法来修改扇区里面的数据。
// 另外我们在Disk类中提供了一个校验磁盘的多项式 11000000000100001 ，大家只能使用这个来校验磁盘中的数据。

import transformer.Transformer;
import util.CRC;

/**
 * 磁盘抽象类，磁盘大小为64M
 */
public class Disk {

    public static int DISK_SIZE_B = 64 * 1024 * 1024;      // 磁盘大小 64 MB

    private static Disk diskInstance = new Disk();

    /**
     * 请勿修改下列属性，至少不要修改一个扇区的大小，如果要修改请保证磁盘的大小为64MB
     */
    public static final int CYLINDER_NUM = 8; //柱面数量
    public static final int TRACK_PRE_PLATTER = 16; //磁道数量
    public static final int SECTOR_PRE_TRACK = 128; //每个磁道上扇区的数量
    public static final int BYTE_PRE_SECTOR = 512; //每个扇区的字节数
    public static final int PLATTER_PRE_CYLINDER = 8; //每个柱面上有多少磁盘

    public static final String POLYNOMIAL = "11000000000100001";
    public disk_head DISK_HEAD = new disk_head();

    RealDisk disk = new RealDisk();

    /**
     * 初始化
     */
    private Disk() { }

    public static Disk getDisk() {
        return diskInstance;
    }

    public static void main(String[] args){
        System.out.println(getBit((char) 123));
    }
    /**
     * 读磁盘
     * @param eip
     * @param len
     * @return
     */
    public char[] read(String eip, int len) {
        //TODO
        //调整磁头位置
        int start = Integer.parseInt(new Transformer().binaryToInt("0" + eip));
        DISK_HEAD.Seek(start);
        char[] res = new char[len];
        int i=0;
        while(len>0){
            if(DISK_HEAD.need_adjust()) {
                DISK_HEAD.adjust();
            }
            res[i] = disk.get(DISK_HEAD);
            i++;
            if (len!=1) {
                DISK_HEAD.point++;
            }
            len--;
        }
        return res;
    }

    /**
     * 写磁盘
     * @param eip
     * @param len
     * @param data
     */
    public void write(String eip, int len, char[] data) {
        //TODO
        int start=Integer.parseInt(new Transformer().binaryToInt("0"+eip));
        write(start,len,data);
    }

    /**
     * 写磁盘（地址为Integer型）
     * 测试会调用该方法
     * @param eip
     * @param len
     * @param data
     */
    public void write(int eip, int len, char[] data) {
        //TODO
        DISK_HEAD.Seek(eip);
        int i=0;
        while(len>0){
            //将数据写入磁盘
            disk.setData(DISK_HEAD, data[i]);
            char[] diskData = disk.getData(DISK_HEAD);
            //重新计算并设置校验码
            char[] checkCode = CRC.Calculate(ToBitStream(diskData), POLYNOMIAL);
            disk.setCRC(DISK_HEAD, ToByteStream(checkCode));
            if (len!=  1) {
                DISK_HEAD.point++;
                DISK_HEAD.adjust();
            }
            len--;
            i++;
        }
    }

    /**
     * 该方法仅用于测试
     */
    public char[] getCRC() {
        return disk.getCRC(DISK_HEAD);
    }

    /**
     * 磁头
     */
    private class disk_head {
        int cylinder;//柱面
        int platter;//磁盘
        int track;//磁道
        int sector;//扇区
        int point;//磁头

        public boolean need_adjust(){
            if (point == BYTE_PRE_SECTOR|| sector == SECTOR_PRE_TRACK
                    || track == TRACK_PRE_PLATTER
                    ||platter == PLATTER_PRE_CYLINDER
                    ||cylinder == CYLINDER_NUM) {
                return true;
            }
            return false;
        }

        /**
         * 调整磁头的位置
         */
        public void adjust() {
            //如果磁头到达扇区的末尾，则磁头置为0，进入下一个扇区
            if (point == BYTE_PRE_SECTOR) {
                point = 0;
                sector++;
            }
            //若磁头到达磁道的最后一个扇区，则进入下一个磁道的扇区
            if (sector == SECTOR_PRE_TRACK) {
                sector = 0;
                track++;
            }
            //若磁头到达一个磁盘的末尾，进入下一个磁盘的第一个磁道
            if (track == TRACK_PRE_PLATTER) {
                track = 0;
                platter++;
            }
            //若磁头到达一个柱面的末尾，进入下一个柱面的第一个磁盘
            if (platter == PLATTER_PRE_CYLINDER) {
                platter = 0;
                cylinder++;
            }
            //若磁头到达所有柱面的末尾，磁头回到第一个柱面
            if (cylinder == CYLINDER_NUM) {
                cylinder = 0;
            }
        }

        /**
         * 磁头回到起点
         */
        public void Init() {
//            try {
//                Thread.sleep(1000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            cylinder = 0;
            track = 0;
            sector = 0;
            point = 0;
            platter = 0;
        }

        /**
         * 将磁头移动到目标位置
         * @param start
         */
        public void Seek(int start) {
//            try {
//                Thread.sleep(0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            for (int i = cylinder; i < CYLINDER_NUM; i++) {
                for (int t = platter; t < PLATTER_PRE_CYLINDER; t++) {
                    for (int j = track; j < TRACK_PRE_PLATTER; j++) {
                        for (int z = sector; z < SECTOR_PRE_TRACK; z++) {
                            for (int k = point; k < BYTE_PRE_SECTOR; k++) {
                                if ((i * PLATTER_PRE_CYLINDER * TRACK_PRE_PLATTER * SECTOR_PRE_TRACK * BYTE_PRE_SECTOR + t * TRACK_PRE_PLATTER * SECTOR_PRE_TRACK * BYTE_PRE_SECTOR + j * SECTOR_PRE_TRACK * BYTE_PRE_SECTOR + z * BYTE_PRE_SECTOR + k) == start) {
                                    cylinder = i;
                                    track = j;
                                    sector = z;
                                    point = k;
                                    platter = t;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            Init();
            Seek(start);
        }

        @Override
        public String toString() {
            return "The Head Of Disk Is In\n" +
                    "platter:\t" + cylinder + "\n" +
                    "track:\t\t" + track + "\n" +
                    "sector:\t\t" + sector + "\n" +
                    "point:\t\t" + point;
        }
    }

    //磁盘抽象
    /**
     * 600 Bytes/Sector
     */
    //每个扇区600B
    public class Sector {
        char[] gap1 = new char[17];
        IDField idField = new IDField();
        char[] gap2 = new char[41];
        DataField dataField = new DataField();
        char[] gap3 = new char[20];

        public char[] getData(){
            return dataField.getData();
        }
        public void setData(char[] data){ dataField.setData(data);}
    }

    /**
     * 7 Bytes/IDField
     */
    private class IDField {
        char SynchByte;
        char[] Track = new char[2];
        char Head;
        char sector;
        char[] CRC = new char[2];
    }

    /**
     * 515 Bytes/DataField
     */
    private class DataField {
        char SynchByte;
        char[] Data = new char[512];
        char[] CRC = new char[2];
        public char[] getData(){
            return Data;
        }
        public void setData(char[] data){this.Data=data;}
    }

    /**
     * 128 sectors pre track
     */
    private class Track {
        Sector[] sectors = new Sector[SECTOR_PRE_TRACK];

        Track() {
            for (int i = 0; i < SECTOR_PRE_TRACK; i++) sectors[i] = new Sector();
        }
    }


    /**
     * 32 tracks pre platter
     */
    private class Platter {
        Track[] tracks = new Track[TRACK_PRE_PLATTER];

        Platter() {
            for (int i = 0; i < TRACK_PRE_PLATTER; i++) tracks[i] = new Track();
        }
    }

    /**
     * 8 platter pre Cylinder
     */
    private class Cylinder {
        Platter[] platters = new Platter[PLATTER_PRE_CYLINDER];

        Cylinder() {
            for (int i = 0; i < PLATTER_PRE_CYLINDER; i++) platters[i] = new Platter();
        }
    }


    private class RealDisk {
        Cylinder[] cylinders = new Cylinder[CYLINDER_NUM];

        public RealDisk() {
            for (int i = 0; i < CYLINDER_NUM; i++) cylinders[i] = new Cylinder();
        }

        public char[] getCRC(disk_head d) {
            return cylinders[d.cylinder].platters[d.platter].tracks[d.track].sectors[d.sector].dataField.CRC;
        }

        public void setCRC(disk_head d,char[] crc) {
                cylinders[d.cylinder].platters[d.platter].tracks[d.track].sectors[d.sector].dataField.CRC=crc;
        }

        public char[] getData(disk_head d) {
            return cylinders[d.cylinder].platters[d.platter].tracks[d.track].sectors[d.sector].dataField.Data;
        }

        public char get(disk_head d){
            return cylinders[d.cylinder].platters[d.platter].tracks[d.track].sectors[d.sector].dataField.Data[d.point];}

        public void setData(disk_head d,char data) {
            cylinders[d.cylinder].platters[d.platter].tracks[d.track].sectors[d.sector].dataField.Data[d.point]=data;
        }

    }

    /**
     * 将Byte流转换成Bit流
     * @param data
     * @return
     */
    public static char[] ToBitStream(char[] data) {
        char[] t = new char[data.length * 8];
        //TODO
        int count=0;
       for(int i=0;i<data.length;i++){
           char[] res=getBit(data[i]);
           for(int j=0;j<8;j++){
               t[8*count+j]=res[j];
           }
           count++;
       }
       return t;
    }

    public static char[] getBit(char by) {
//        StringBuffer sb = new StringBuffer();
//        byte b=(byte) by;
//        sb.append((b>>7)&0x1)
//                .append((b>>6)&0x1)
//                .append((b>>5)&0x1)
//                .append((b>>4)&0x1)
//                .append((b>>3)&0x1)
//                .append((b>>2)&0x1)
//                .append((b>>1)&0x1)
//                .append((b>>0)&0x1);
//        return sb.toString().toCharArray();
//    }
        byte b=(byte) by;
        char[] res=new char[8];
        int m= b;
        for(int i=7;i>=0;i--){
            res[i]= String.valueOf(m%2).toCharArray()[0];
            m=m/2;
        }
        return res;
    }


    /**
     *
     * 将Bit流转换为Byte流
     * @param data
     * @return
     */
    public static char[] ToByteStream(char[] data) {
        char[] t = new char[data.length / 8];
        //TODO
        int count=0;
        for(int i=0;i<data.length/8;i++){
            char[] res=new char[8];
            for(int j=0;j<8;j++){
                res[j]=data[count*8+j];
            }
            t[i]= bitToByte(res);
            count++;
        }
        return t;
    }

    public static char bitToByte(char[] b) {
        int temp=1;
        int res=0;
        for(int i=7;i>=0;i--){
            if(b[i]=='1'){
                res+=temp;
            }
            temp*=2;
        }
        return (char)res;
    }

    /**
     * 这个方法仅供测试，请勿修改
     * @param eip
     * @param len
     * @return
     */
    public char[] readTest(String eip, int len){
        char[] data = read(eip, len);
        System.out.print(data);
        return data;
    }
}
