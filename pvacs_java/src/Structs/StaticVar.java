package Structs;

import java.util.HashSet;
import java.util.Set;

/**
 * @program: pvacs_java
 * @description: 存放所有的静态变量，进行类之间的数据共享
 * @author: Yongjie Lv
 * @create: 2018/09/11/08:28
 **/
public class StaticVar {
    public static int batchNum=0;
    public static int[] pos=new int[150];
    public static int[][] eta=new int[150][];
    public static Machine[][] machines;
    public static Batch[] B=new Batch[150];
    public static float phmk[][]=new float[150][];
    public static float phepc[][]=new float[150][];

    public static Set<Solution> NDS=new HashSet<>();

    static  {
        for (int i=0;i<150;i++){
            phmk[i]=new float[150];
            phepc[i]=new float[150];
            for (int j=0;j<150;j++){
                phmk[i][j]=Parameters.Q0;
                phepc[i][j]=Parameters.Q0;
            }
        }
    }
}
