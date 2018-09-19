package Structs;

/**
 * @program: pvacs_java
 * @description: 整个算法用到的参数
 * @author: Yongjie Lv
 * @create: 2018/09/10/14:45
 **/
public class Parameters {
    // 工件的数量
    public static final int JOBNUM=100;
    // 机器的最大容量
    public static final int C=10;
    // 阶段数
    public static final int KMAX=4;
    // 迭代的次数
    public static final int EPOCH=1000;
    // 蚂蚁的个数
    public static final int ANTNUM=200;
    // 信息素局部挥发系数
    public static final float PL=0.1f;
    // 信息素全局挥发系数
    public static final float PG=0.2f;
    // 计算参考点的参数
    public static final float LAMBDA=0.1f;
    public static final float SIGMA=0.1f;
    // 蚁群概率转移公式参数
    public static final int ALPHA=1;
    public static final int BATA=3;
    public static final float Q0=0.5f;
    // 最大最小蚁群参数
    public static final int MAXTAO=100;
    public static final int MINTAO=1;
    // 初始化信息素值
    public static final float TAO=1f;
    // 每个阶段机器的数量
    public static final int MACHINENUM=4;

}
