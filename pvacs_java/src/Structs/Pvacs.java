package Structs;

import java.util.Iterator;

/**
 * @program: pvacs_java
 * @description: 主函数
 * @author: Yongjie Lv
 * @create: 2018/09/17/22:17
 **/
public class Pvacs {
    public static void main(String[] args) {
        long begintime = System.nanoTime();

        new Batch().BFLPT(Job.initJob());
        Machine.initMachine();

        for (int epoch=1;epoch<=Parameters.EPOCH;epoch++){
            System.out.println("第"+epoch+"次迭代：");

            for (int i=1;i<=Parameters.ANTNUM;i++){
                Ant ant=new Ant();
                ant.id=i;
                ant.createBatchSeq();
                ant.getFitness();
                ant.localUpdate();
                ant.updateNDS();
            }

            Ant.globalUpdate();
            System.out.println("第"+epoch+"次迭代：NDS.size="+StaticVar.NDS.size());
        }
        long endtime = System.nanoTime();
        Iterator it=StaticVar.NDS.iterator();
        Solution s;
        while (it.hasNext()){
            s=(Solution)it.next();
            System.out.println("Cmax="+s.cmax+"\tEPC="+s.epc);
        }
        long costTime = (endtime - begintime)/1000000000;
        System.out.println("time="+costTime);
    }
}
