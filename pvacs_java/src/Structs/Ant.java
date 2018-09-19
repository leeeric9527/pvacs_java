package Structs;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * @program: pvacs_java
 * @description: 蚁群类
 * @author: Yongjie Lv
 * @create: 2018/09/12/10:23
 **/
public class Ant {
    // 用户偏好向量
    private float vmk;
    private float vepc;

    // 最大完工时间
    private int cmax;
    // 消耗的电能
    private int epc;
    // 每个蚂蚁找到的解
    Solution solution;
    // 蚂蚁的序号
    public int id;
    // 将原始的批信息拷贝一份
    Batch[] tempB=new Batch[150];
    public Ant(){
        initAnt();
    }
    private  void initAnt(){
        Random r=new Random();
        this.vmk=r.nextFloat();
        this.vepc=1-this.vmk;
        this.epc=0;
        this.cmax=0;
        this.id=0;
        solution=new Solution();
        solution.vmk=this.vmk;
        solution.vepc=this.vepc;
        for(int i=0;i<=StaticVar.batchNum;i++){
            tempB[i]=new Batch(-1);
            tempB[i]=StaticVar.B[i].clone();
        }
    }
    /**
     * @description: 为当前的这只蚂蚁构建一个解
     * @param :
     * @return: void
     * @author: Yongjie Lv
     * @date: 18:47 2018/9/14
    */
    public void createBatchSeq(){
        // 蚂蚁选择每个批的概率，通过此概率选择第一个批，从1开始编号
        float[] pb=new float[150];
        pb[0]=-1;

        // 计算公式的分母
        int sum=0;
        for(int i=1;i<=StaticVar.batchNum;i++){
            sum+=(StaticVar.batchNum-StaticVar.pos[i]+1)*(StaticVar.batchNum-StaticVar.pos[i]+1);
        }

        //分别计算每个批的概率
        for(int i=1;i<=StaticVar.batchNum;i++){
            pb[i]=(StaticVar.batchNum-StaticVar.pos[i]+1)*(StaticVar.batchNum-StaticVar.pos[i]+1)/(float)sum;
        }

        // 使用轮盘赌进行选择第一个批
        float m=0;
        Random random=new Random();
        float r=random.nextFloat();
        for (int i=1;i<=StaticVar.batchNum;i++){
            m+=pb[i];
            if(r<m){
                solution.batchSeq[1]=i;
                tempB[i].flag=0;
                tempB[i].BJob[0]=new Job();
                break;
            }
        }

        // 完成剩下的批的选择
        for (int num=2;num<=StaticVar.batchNum;num++){
            float q=random.nextFloat();
            double max=Integer.MIN_VALUE;
            // 选择的批序号
            int select=-1;
            // 当前已经完成选择的批序列最后一个元素
            int last=solution.batchSeq[num-1];
            if (q<=Parameters.Q0){
                for (int j=1;j<=StaticVar.batchNum;j++){
                    if (tempB[j].flag==1){
                        double temp=Math.pow(vmk*StaticVar.phmk[last][j]+vepc*StaticVar.phepc[last][j],Parameters.ALPHA)*Math.pow(StaticVar.eta[last][j],Parameters.BATA);
                        if (temp>max){
                            max=temp;
                            select=j;
                        }
                    }
                }
                // 找到了最大值和所选择的批
                solution.batchSeq[num]=select;
                tempB[select].flag=0;
            }else {
                // 计算分母
                double sum1=0.0;
                for (int l=1;l<=StaticVar.batchNum;l++){
                    if (tempB[l].flag==1){
                        sum1+=Math.pow(vmk*StaticVar.phmk[last][l]+vepc*StaticVar.phepc[last][l],Parameters.ALPHA)*Math.pow(StaticVar.eta[last][l],Parameters.BATA);
                    }
                }

                // 剩下未选择的批概率
                double P[]=new double[150];
                for (int p=1;p<=StaticVar.batchNum;p++){
                    if (tempB[p].flag==1){
                        P[p]=(Math.pow(vmk*StaticVar.phmk[last][p]+vepc*StaticVar.phepc[last][p],Parameters.ALPHA)*Math.pow(StaticVar.eta[last][p],Parameters.BATA))/sum1;
                    }else {
                        P[p]=0;
                    }
                }

                r=random.nextFloat();
                float temp=0;
                for (int t=1;t<=StaticVar.batchNum;t++){
                    temp+=P[t];
                    if (r<temp){
                        solution.batchSeq[num]=t;
                        tempB[t].flag=0;
                        break;
                    }
                }
            }
        }

        // 输出蚂蚁的序列
        /*System.out.println("\n************************************");
        System.out.println("蚂蚁"+id+"序列：");
        int[] test=new int[150];
        for (int i=1;i<=StaticVar.batchNum;i++){
            System.out.print(solution.batchSeq[i]+",");
            test[solution.batchSeq[i]]++;
        }
        System.out.println();
        for (int i=0;i<=StaticVar.batchNum;i++){
            System.out.print(test[i]+" ");
        }
        System.out.println();*/
    }
    // 计算每个解的两个目标值
    public void getFitness(){
        // 清除上只蚂蚁残留在机器上的信息
        for (int i=1;i<=Parameters.KMAX;i++){
            for (int j=1;j<=Parameters.MACHINENUM;j++){
                StaticVar.machines[i][j].avt=0;
                StaticVar.machines[i][j].list.clear();
            }
        }

        int[] SOL=Arrays.copyOf(solution.batchSeq,StaticVar.batchNum+1);

        Machine[][] M=StaticVar.machines;
        // 对于每个阶段
        for (int k=1;k<=Parameters.KMAX;k++){
            // 遍历每一个批，使得每个批在当前阶段分配在合适的机器上
            for (int h=1;h<=StaticVar.batchNum;h++){
                // 整个式子的最小值
                float min=(float) Integer.MAX_VALUE;
                // 第一个式子分母的最大值
                float max1=-Integer.MAX_VALUE;
                // 第二个式子分母的最大值
                float max2=-Integer.MAX_VALUE;
                // 最终选择的机器
                int selectMachine=-1;
                // 批h上阶段完成的时间
                int CT=tempB[SOL[h]].BC[k-1];

                // 分别计算两个分母的最大值
                for (int i=1;i<=Parameters.MACHINENUM;i++){
                    int temp1;
                    temp1=Math.max(CT,M[k][i].avt)+(int)Math.ceil((float)tempB[SOL[h]].BP[k]/M[k][i].v)-CT;
                    if (temp1>max1){max1=temp1;}

                    float temp2;
                    temp2=computeEpc(Math.max(CT,M[k][i].avt),(int)Math.ceil((float)tempB[SOL[h]].BP[k] / M[k][i].v),k,i);
                    if (temp2>max2){max2=temp2;}
                }

                // 计算整个式子的最小值，并确定该批放在哪一个机器上
                for (int i=1;i<=Parameters.MACHINENUM;i++){
                    float temp;
                    temp=solution.vmk*(Math.max(CT,M[k][i].avt)+(int)Math.ceil((float)tempB[SOL[h]].BP[k]/M[k][i].v-CT))/max1+
                            solution.vepc*computeEpc(Math.max(CT,M[k][i].avt),(int)Math.ceil((float)tempB[SOL[h]].BP[k]/M[k][i].v),k,i)/max2;
                    if (temp<min){
                        min=temp;
                        selectMachine=i;
                    }
                }

                // 计算待机电费
                // 选择的机器当前是空的，从0开始计算电费
                if (M[k][selectMachine].list.isEmpty()){
                    epc+=computeEpc(0,Math.max(CT,M[k][selectMachine].avt),0,0);
                } else {
                    // 机器非空，需要计算该批开始加工时间和该机器的等待时间的待机功耗
                    int size=M[k][selectMachine].list.size();
                    // 该机器批队列最后一个批的完成时间
                    int t0=M[k][selectMachine].list.get(size-1).BC[k];
                    epc+=computeEpc(t0,Math.max(CT,M[k][selectMachine].avt)-t0,0,0);
                }
                // 计算该批加工所需要的能耗
                epc+=computeEpc(Math.max(CT,M[k][selectMachine].avt),(int)Math.ceil((float)tempB[SOL[h]].BP[k]/M[k][selectMachine].v),k,selectMachine);

                // 更新这个批的完成时间
                tempB[SOL[h]].BC[k] = Math.max(CT, M[k][selectMachine].avt) + (int)Math.ceil((float)tempB[SOL[h]].BP[k] / M[k][selectMachine].v);
                // 计算这个批当前阶段的完工时间，为了后面按照完成时间排序
                tempB[SOL[h]].ct = tempB[SOL[h]].BC[k];
                // 更新批的开始时间
                tempB[SOL[h]].BS[k] = Math.max(CT, M[k][selectMachine].avt);
                tempB[SOL[h]].MID[k]=selectMachine;
                // 更新该机器的可用时间
                M[k][selectMachine].avt=tempB[SOL[h]].BC[k];
                // 更新机器队列
                M[k][selectMachine].list.add(tempB[SOL[h]]);
            } // 当前阶段，所有批分配完成
            // 将该阶段所有批按照完成时间递增排序
            Batch[] tempBB=new Batch[150];
            for(int i=0;i<=StaticVar.batchNum;i++){
                tempBB[i]=new Batch(-1);
                tempBB[i]=tempB[i].clone();
            }
            Arrays.sort(tempBB,0,StaticVar.batchNum+1, new Comparator<Batch>() {
                @Override
                public int compare(Batch o1, Batch o2) {
                    if (o1.ct<o2.ct){
                        return -1;
                    }else if (o1.ct>o2.ct){
                        return 1;
                    }else {
                        return 0;
                    }
                }
            });
            // 获取当前阶段的最大完工时间
            cmax=tempBB[StaticVar.batchNum].BC[k];
            // 获取下一个阶段的批序列
            for (int j=1;j<=StaticVar.batchNum;j++){
                SOL[j]=tempBB[j].BId;
            }
        } // 所有的阶段都已经完成
        // 前面计算电费时，由于没计算某台机器最后一个批完成后，还要待机多久（因为当时还不知道Cmax）
        for (int k=1;k<=Parameters.KMAX;k++){
            for (int i=1;i<=Parameters.MACHINENUM;i++){
                // 该机器上没有任何一个批，一直处于待机状态
                if (M[k][i].list.isEmpty()){
                    epc+=computeEpc(0,cmax,0,0);
                }else{
                    // 该机器上最后一个批的完成时间到最终的cmax是处于待机状态的
                    int size=M[k][i].list.size();
                    int t=M[k][i].list.get(size-1).BC[k];
                    epc+=computeEpc(t,cmax-t,0,0);
                }
            }
        }

        //输出每个阶段每台机器上的批情况
       /* for (int k = 1; k <=Parameters.KMAX; k++)
        {
            for (int i = 1; i <=Parameters.MACHINENUM; i++)
            {
                System.out.println("第"+k+"阶段，第"+i+"台机器：");
                Iterator it=M[k][i].list.iterator();
                while (it.hasNext()){
                    Batch b=(Batch)it.next();
                    System.out.println("\t批："+b.BId+"\t开始时间："+b.BS[k]+"\t完成时间："+b.BC[k]+"\t机器："+b.MID[k]);
                }
            }
        }*/

        solution.cmax=cmax;
        solution.epc=epc;
        // System.out.println("----------Right_Shift之前-------------：");
        // System.out.println(solution);
        rightShift();
    }

    private void rightShift(){
        Machine[][] M=StaticVar.machines;
        Iterator it;
        for (int k=Parameters.KMAX;k>=1;k--){
            // 将该阶段所有批按照完成时间递增排序
            Batch[] tempBB=new Batch[150];
            for(int i=0;i<=StaticVar.batchNum;i++){
                tempBB[i]=new Batch(-1);
                tempBB[i]=tempB[i].clone();
                tempBB[i].ct=tempBB[i].BC[k];
            }
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
            Arrays.sort(tempBB,1,StaticVar.batchNum+1, new Comparator<Batch>() {
                @Override
                public int compare(Batch o1, Batch o2) {
                    if (o1.ct>o2.ct){
                        return -1;
                    }
                    else if (o1.ct<o2.ct){
                        return 1;
                    }else {
                        return 0;
                    }
                }
            });

            // 通过上面排序得到批序列
            int[] D=new int[StaticVar.batchNum+1];
            D[0]=-1;
            for (int i=1;i<=StaticVar.batchNum;i++){
                D[i]=tempBB[i].BId;
            }

            for (int h=1;h<=StaticVar.batchNum;h++){
                int tmin=tempB[D[h]].BS[k];
                // 在批D[h]所在机器上，并紧接着在D[h]之后的批开始加工的时间
                int STnext=Integer.MAX_VALUE;
                // 批D[h]在下一阶段的开始加工时间
                int ST;

                // 计算STnext
                it=M[k][tempB[D[h]].MID[k]].list.iterator();
                Batch batch=null;
                while (it.hasNext()){
                    batch=(Batch)it.next();
                    if (batch.BId==tempB[D[h]].BId){
                        if (!it.hasNext()){
                            STnext=Integer.MAX_VALUE;
                        }else {
                            STnext=((Batch)it.next()).BS[k];
                        }
                        break;
                    }
                }
                // 判断是否是最后一个阶段,计算ST
                if (k==Parameters.KMAX){
                    ST=Integer.MAX_VALUE;
                } else {
                    ST=tempB[D[h]].BS[k+1];
                }

                // 计算tmax
                int tmax=Math.min(Math.min(cmax,STnext),ST)-(int)Math.ceil((float)tempB[D[h]].BP[k]/M[k][tempB[D[h]].MID[k]].v);

                // 寻找最小的t
                int minEPC=Integer.MAX_VALUE;
                int t0=-1;
                int tempEPC;
                for (int t=tmin;t<=tmax;t++){
                    tempEPC=computeEpc(t,(int)Math.ceil((float)tempB[D[h]].BP[k]/M[k][tempB[D[h]].MID[k]].v),k,tempB[D[h]].MID[k]);
                    if (tempEPC<=minEPC){
                        minEPC=tempEPC;
                        t0=t;
                    }
                }

                // 更新开始加工时间
                tempB[D[h]].BS[k]=t0;
                // 更新完成时间
                tempB[D[h]].BC[k]=t0+(int)Math.ceil((float)tempB[D[h]].BP[k]/M[k][tempB[D[h]].MID[k]].v);
                // 更新机器队列???
            }
        }

        // 输出rightShift之后的情况
        Batch batch=null;
       /* System.out.println("----------Right_Shift之后-------------：");

        for (int k=1;k<=Parameters.KMAX;k++){
            for (int i=1;i<=Parameters.MACHINENUM;i++){
                System.out.println("第"+k+"阶段，第"+i+"台机器：");
                it=M[k][i].list.iterator();
                while (it.hasNext()){
                    batch=(Batch)it.next();
                    System.out.println("\t批："+batch.BId+"\t开始时间："+batch.BS[k]+"\t完成时间："+batch.BC[k]+"\t机器："+batch.MID[k]);
                }
            }
        }*/

        // 计算改进后的EPC
        int newEPC=0;
        // 同一个机器上前一个批的完成时间
        int preCt=-1;
        for (int k=1;k<=Parameters.KMAX;k++){
            for (int i=1;i<=Parameters.MACHINENUM;i++){
                if (M[k][i].list.isEmpty()){
                    newEPC+=computeEpc(0,cmax,0,0);
                }else {
                    it=M[k][i].list.iterator();
                    // 先算第一个批，因为该批之前机器可能会有等待
                    batch=(Batch)it.next();
                    newEPC+=computeEpc(0,batch.BS[k],0,0);
                    newEPC+=computeEpc(batch.BS[k],(int)Math.ceil((float)batch.BP[k]/M[k][i].v),k,i);
                    preCt=batch.BC[k];
                    // 计算后续的批
                    while (it.hasNext()){
                        batch=(Batch)it.next();
                        newEPC+=computeEpc(preCt,batch.BS[k]-preCt,0,0);
                        newEPC+=computeEpc(batch.BS[k],(int)Math.ceil((float)batch.BP[k]/M[k][i].v),k,i);
                        preCt=batch.BC[k];
                    }
                    // 最后一个批结束到Cmax之间可能会有待机时间
                    newEPC+=computeEpc(preCt,cmax-preCt,0,0);
                }
            }
        }
        solution.epc=newEPC;
        /*System.out.println(solution);
        System.out.println("************************************");*/
    }


    /**
     * @description: 计算没每段时间机器消耗的电能
     * @param t: 起始时间
     * @param detaT: 该段时间长度
     * @param k: 阶段编号
     * @param i:  机器编号
     * @return: float 返回计算出的电能
     * @author: Yongjie Lv
     * @date: 10:47 2018/9/13
    */
    private int computeEpc(int t,int detaT,int k,int i){
        int n1=t/24;
        int n2=(t+detaT)/24;
        int sum=0;
        int t1=t%24;
        int t2=(t+detaT)%24;

        if (n1==n2){
            sum+=f(t2)-f(t1);
        }else {
            sum+=(n2-n1)*174+f(t2)-f(t1);
        }
        return sum*StaticVar.machines[k][i].pw;
    }

    /**
     * @description: 计算从0-t这段时间功耗
     * @param t:
     * @return: float
     * @author: Yongjie Lv
     * @date: 15:57 2018/9/13
    */
    private float f(int t){
        if (t>=0&&t<7){
            return 5*t;
        }else if (t>=7&&t<11){
            return 35+(t-7)*8;
        }else if (t>=11&&t<17){
            return 67+(t-11)*10;
        }else if (t>=17&&t<21){
            return 127+(t-17)*8;
        }else if (t>=21&&t<24){
            return 159+(t-21)*5;
        }else {
            System.out.println("EPC计算出错！！！");
            return -1;
        }
    }
    /**
     * @description: 蚁群进行局部信息素更新
     * @param:
     * @return: void
     * @author: Yongjie Lv
     * @date: 19:42 2018/9/17
    */
    public void localUpdate(){
        int[] s=solution.batchSeq;
        for (int i=1;i<StaticVar.batchNum;i++){
            for (int j=i+1;j<=StaticVar.batchNum;j++){
                StaticVar.phmk[s[i]][s[j]]=(1-Parameters.PL)*StaticVar.phmk[s[i]][s[j]]+Parameters.PL*Parameters.TAO;
                StaticVar.phepc[s[i]][s[j]]=(1-Parameters.PL)*StaticVar.phepc[s[i]][s[j]]+Parameters.PL*Parameters.TAO;
            }
        }
    }

    /**
     * @description: 蚁群信息素的局部更新
     * @param :
     * @return: void
     * @author: Yongjie Lv
     * @date: 21:07 2018/9/17
    */
    public void updateNDS(){
        Set<Solution> nds=StaticVar.NDS;
        Iterator it=nds.iterator();
        Solution s;
        int flag=0;
        while (it.hasNext()){
            s=(Solution)it.next();
            if (solution.cmax>s.cmax&&solution.epc>s.epc){
                flag=1;
                break;
            }
            else if (solution.cmax<s.cmax&&solution.epc<s.epc){
                it.remove();
            }
            else if (solution.cmax==s.cmax&&solution.epc==s.epc){
                flag=1;
                break;
            }
            else if (solution.cmax==s.cmax||solution.epc==s.epc){
                if (solution.cmax>s.cmax||solution.epc>s.epc){
                    flag=1;
                    break;
                }else {
                    it.remove();
                }
            }
        }
        if (flag==0){
            nds.add(solution);
        }
    }

    public static void globalUpdate(){
        // 分别求出NDS中两个目标的最小值
        Iterator it=StaticVar.NDS.iterator();
        Solution s=null;
        float minCmax=(float)Integer.MAX_VALUE;
        float minEPC=(float)Integer.MAX_VALUE;

        while (it.hasNext()){
            s=(Solution)it.next();
            if (s.epc<minEPC){
                minEPC=s.epc;
            }
            if (s.cmax<minCmax){
                minCmax=s.cmax;
            }
        }

        float[][]detaPhmk=new float[150][];
        float[][]detaPhepc=new float[150][];
        for (int i=0;i<150;i++){
            detaPhmk[i]=new float[150];
            detaPhepc[i]=new float[150];
        }

        it=StaticVar.NDS.iterator();
        while (it.hasNext()){
            s=(Solution)it.next();
            float detaMK=minCmax/s.cmax;
            float detaEPC=minEPC/s.epc;
            for (int i=1;i<StaticVar.batchNum;i++){
                for (int j=i+1;j<=StaticVar.batchNum;j++){
                    detaPhmk[s.batchSeq[i]][s.batchSeq[j]]+=detaMK;
                    detaPhepc[s.batchSeq[i]][s.batchSeq[j]]+=detaEPC;
                }
            }
        }

        float[][]phmk=StaticVar.phmk;
        float[][]phepc=StaticVar.phepc;

        it=StaticVar.NDS.iterator();
        while (it.hasNext()){
            s=(Solution)it.next();
            for (int i=1;i<StaticVar.batchNum;i++){
                for (int j=i+1;j<=StaticVar.batchNum;j++){
                    phmk[s.batchSeq[i]][s.batchSeq[j]]=(1-Parameters.PG)*phmk[s.batchSeq[i]][s.batchSeq[j]]+Parameters.PG*detaPhmk[s.batchSeq[i]][s.batchSeq[j]];
                    phepc[s.batchSeq[i]][s.batchSeq[j]]=(1-Parameters.PG)*phepc[s.batchSeq[i]][s.batchSeq[j]]+Parameters.PG*detaPhepc[s.batchSeq[i]][s.batchSeq[j]];
                    if (phmk[s.batchSeq[i]][s.batchSeq[j]]<Parameters.MINTAO){
                        phmk[s.batchSeq[i]][s.batchSeq[j]]=Parameters.MINTAO;
                    }
                    if (phmk[s.batchSeq[i]][s.batchSeq[j]]>Parameters.MAXTAO){
                        phmk[s.batchSeq[i]][s.batchSeq[j]]=Parameters.MAXTAO;
                    }
                    if (phepc[s.batchSeq[i]][s.batchSeq[j]]<Parameters.MINTAO){
                        phepc[s.batchSeq[i]][s.batchSeq[j]]=Parameters.MINTAO;
                    }
                    if (phepc[s.batchSeq[i]][s.batchSeq[j]]>Parameters.MAXTAO){
                        phepc[s.batchSeq[i]][s.batchSeq[j]]=Parameters.MAXTAO;
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        new Batch().BFLPT(Job.initJob());
        Ant ant=new Ant();
        ant.initAnt();
        ant.createBatchSeq();
        Machine.initMachine();
        ant.getFitness();

        /*ant.vmk=0.6662887f;
        ant.vepc=0.33371133f;
        ant.solution.vmk=ant.vmk;
        ant.solution.vepc=ant.vepc;
        ant.solution.batchSeq= new int[]{-1,16,9,12,19,14,20,11,13,2,1,5,7,17,8,6,15,10,3,4,18};
        ant.getFitness();
        System.out.println(ant.solution);*/
    }
}
