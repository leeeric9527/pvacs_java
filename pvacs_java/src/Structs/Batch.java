package Structs;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @program: pvacs_java
 * @description: 批的相关信息
 * @author: Yongjie Lv
 * @create: 2018/09/10/15:47
 **/
public class Batch {
    // 批编号，从1开始
    public int BId;

    // 批再不同阶段名义处理时间,从1开始
    public int BP[]=new int[Parameters.KMAX+1];

    // 各个批在不同阶段的完成时间
    public int BC[]=new int[Parameters.KMAX+1];

    // 批中工件个数
    public int JNum;

    // 记录批的剩余空间
    public int freeSpace;

    // 批里面存放的工件
    public Job BJob[]=new Job[200];

    // 批的倾斜度
    public float SI;

    // 1表示该批还没有加入队列
    public int flag;

    // 当前阶段的额完工时间
    public int ct;

    // 批不同阶段的开始时间
    public int BS[]=new int[Parameters.KMAX+1];

    // 批每个阶段在哪个机器上
    public int MID[]=new int[Parameters.KMAX+1];


    public Batch(){}

    /**
     * @description:  根据批的id新建一个空白批
     * @param id:  当前新建的批的id值
     * @return:  构造函数，无返回值
     * @author: Yongjie Lv
     * @date: 8:25 2018/9/11
    */
    public Batch(int id){
       for(int i=1;i<=Parameters.KMAX;i++){
           this.BId=id;
           this.BP[i]=-1;
           this.freeSpace=Parameters.C;
           this.JNum=0;
           this.flag=1;
           this.BC[0]=0;
       }
    }

    /**
     * @description: BFLPT启发式算法，用来将工件进行分批
     * @param job:  待分批的工件
     * @return: void
     * @author: Yongjie Lv
     * @date: 8:23 2018/9/11
    */
    public void BFLPT(Job[] job){

        Arrays.sort(job,1,Parameters.JOBNUM+1, new Comparator<Job>() {
            @Override
            public int compare(Job o1, Job o2) {
                return o1.JobPT[1]>o2.JobPT[1]?-1:1;
            }
        });

        // 0号单元不用
        Batch[] B1=StaticVar.B;
        B1[0]=new Batch(-1);
        B1[0].freeSpace=-10;
        B1[0].flag=-1;

        // 批的初始编号
        int initId=1;
        for(int j=1;j<=Parameters.JOBNUM;j++){
            int min=Integer.MAX_VALUE;
            int bId=-1;
            for(int b=1;b<=StaticVar.batchNum;b++){
                int diff=B1[b].freeSpace-job[j].JobSize;
                if(diff>=0){
                    if(diff<min){
                        min=diff;
                        bId=b;
                    }
                }
            }
            // 不存在合适的批，此时新建一个批
            if(bId==-1){
                StaticVar.batchNum++;
                B1[StaticVar.batchNum]=new Batch(initId);
                initId++;
                B1[StaticVar.batchNum].freeSpace-=job[j].JobSize;
                B1[StaticVar.batchNum].JNum++;
                B1[StaticVar.batchNum].BJob[B1[StaticVar.batchNum].JNum]=job[j];
            }else{
                B1[bId].freeSpace-=job[j].JobSize;
                B1[bId].JNum++;
                B1[bId].BJob[B1[bId].JNum]=job[j];
            }
        }
        getPbk(B1);
        computeSIb(B1);
        // displayBatch(B1);
    }

    /**
     * @description: 打印每个批的详细信息
     * @param B:  目标批
     * @return: void
     * @author: Yongjie Lv
     * @date: 8:21 2018/9/11
    */
    private void displayBatch(Batch[] B){
        for(int i=1;i<=StaticVar.batchNum;i++){
            System.out.println("第"+i+"个批的编号："+B[i].BId);
            System.out.println("第"+i+"个批所含工件数："+B[i].JNum);
            System.out.println("第"+i+"个批倾斜度："+B[i].SI);
            System.out.print("第"+i+"个批包含的工件为（编号）");
            for(int p=1;p<=B[i].JNum;p++){
                System.out.print(B[i].BJob[p].JobId+" ");
            }
            System.out.println();

            for(int j=1;j<=Parameters.KMAX;j++){
                System.out.println("   第"+j+"阶段的名义加工时间："+B[i].BP[j]);
            }
        }
    }

    /**
     * @description: 计算每个批的名义加工时间（=该批中所有工件最大的加工时间）
     * @param B:  目标批
     * @return: void
     * @author: Yongjie Lv
     * @date: 8:22 2018/9/11
    */
    private void getPbk(Batch[] B){
        // 第i个批
        for(int i=1;i<=StaticVar.batchNum;i++){
            // 第p个阶段
            for(int p=1;p<=Parameters.KMAX;p++){
                int maxPT=Integer.MIN_VALUE;
                // 第i个批，第p阶段的第j个工件
                for(int j=1;j<=B[i].JNum;j++){
                    if(B[i].BJob[j].JobPT[p]>maxPT){
                        maxPT=B[i].BJob[j].JobPT[p];
                    }
                }
                B[i].BP[p]=maxPT;
            }
        }
    }

    private void computeSIb(Batch[] B){
        Batch[] temp=B.clone();

        for(int i=1;i<=StaticVar.batchNum;i++){
            float sum=0f;
            for(int k=1;k<=Parameters.KMAX;k++){
                sum+=(Parameters.KMAX-2*k+1)/2.0*B[i].BP[k];
            }
            B[i].SI=(-1)*sum;
            temp[i].SI=(-1)*sum;
        }
        Arrays.sort(temp, 1, StaticVar.batchNum + 1, new Comparator<Batch>() {
            @Override
            public int compare(Batch o1, Batch o2) {
                return o1.SI>o2.SI?-1:1;
            }
        });
        // displayBatch(temp);
        // 计算pos
        for(int i=1;i<=StaticVar.batchNum;i++){
            StaticVar.pos[temp[i].BId]=i;
        }

       /* for(int i=1;i<=StaticVar.batchNum;i++){
            System.out.print(StaticVar.pos[i]+" ");
        }
        System.out.println();*/
        // 计算启发式信息
        for(int i=1;i<=StaticVar.batchNum;i++){
            StaticVar.eta[i]=new int[200];
            for(int j=1;j<=StaticVar.batchNum;j++){
                StaticVar.eta[i][j]=StaticVar.batchNum-Math.abs(StaticVar.pos[i]-StaticVar.pos[j]);
            }
        }
    }

    @Override
    public Batch clone(){
        Batch newBatch=new Batch();
        newBatch.BJob=this.BJob;
        newBatch.JNum=this.JNum;
        newBatch.flag=this.flag;
        newBatch.freeSpace=this.freeSpace;
        newBatch.BId=this.BId;
        newBatch.ct=this.ct;

        for(int i=0;i<=Parameters.KMAX;i++){
            newBatch.BS[i]=this.BS[i];
            newBatch.BP[i]=this.BP[i];
            newBatch.BC[i]=this.BC[i];
            newBatch.MID[i]=this.MID[i];
        }
        for(int i=0;i<=Parameters.JOBNUM;i++){
            newBatch.BJob[i]=new Job();
            newBatch.BJob[i]=this.BJob[i].clone();
        }
        return newBatch;
    }
    public static void main(String[] args) {
        new Batch().BFLPT(Job.initJob());
    }
}
