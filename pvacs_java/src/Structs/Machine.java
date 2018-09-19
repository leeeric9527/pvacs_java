package Structs;

import javax.crypto.Mac;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @program: pvacs_java
 * @description: 机器相关信息
 * @author: Yongjie Lv
 * @create: 2018/09/10/15:54
 **/
public class Machine {
    // 各个阶段不同机器的速度
    public int v;

    // 各个机器在不同阶段下的工作效率
    public int pw;

    // 在k阶段，每个机器的当前可用时间
    public int avt;

    // 记录机器上加工的批
    public List<Batch> list=new ArrayList<>(100);

    /** 
     * @description: 初始化机器信息，可以通过随机初始和从文件读取
     * @param :  
     * @return: Structs.Machine[][] 
     * @author: Yongjie Lv 
     * @date: 10:35 2018/9/11 
    */ 
    public static void initMachine(){
        StaticVar.machines=new Machine[Parameters.KMAX+1][];
        for(int i=0;i<=Parameters.KMAX;i++){
            StaticVar.machines[i]=new Machine[Parameters.MACHINENUM+1];
            for(int j=0;j<=Parameters.MACHINENUM;j++){
                StaticVar.machines[i][j]=new Machine();
            }
        }
        // 0号单元作为待机下的情况
        StaticVar.machines[0][0].pw=1;
        //readFromFile();
        randGenerate();

    }
    /**
     * @description: 随机产生机器信息
     * @param :
     * @return: void
     * @author: Yongjie Lv
     * @date: 10:08 2018/9/12
    */
    private static void randGenerate(){
        Random r=new Random();
        for (int i=1;i<=Parameters.KMAX;i++){
            for (int j=1;j<=Parameters.MACHINENUM;j++){
                StaticVar.machines[i][j].v=r.nextInt(5)+1;
                StaticVar.machines[i][j].pw=r.nextInt(6)+5;
                StaticVar.machines[i][j].avt=0;
                StaticVar.machines[i][j].list.clear();
            }
        }
        writeInFile();
    }

    /**
     * @description: 从文件中读取机器的信息
     * @param :
     * @return: void
     * @author: Yongjie Lv
     * @date: 10:08 2018/9/12
    */
    private static void readFromFile(){
        try(BufferedReader br=new BufferedReader(new FileReader(new File("src\\File\\machineInfo.txt")))){
            String str=null;
            String strs[]=new String[2];
            int i=1;
            int j=1;
            while ((str=br.readLine())!=null){
                strs=str.split("\t");
                StaticVar.machines[i][j].v=Integer.parseInt(strs[0]);
                StaticVar.machines[i][j].pw=Integer.parseInt(strs[1]);
                if(j==Parameters.MACHINENUM){
                    j=0;
                    i++;
                }
                j++;
            }
            //输出机器的信息
            /*for (i=1;i<=Parameters.KMAX;i++) {
                for (j = 1; j <= Parameters.MACHINENUM; j++) {
                    System.out.println(StaticVar.machines[i][j].v + "\t" + StaticVar.machines[i][j].pw);
                }
            }*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeInFile(){
        try (BufferedWriter bw=new BufferedWriter(new FileWriter(new File("src\\File\\machineInfo2.txt"),false))){
            for (int i=1;i<=Parameters.KMAX;i++){
                for (int j=1;j<=Parameters.MACHINENUM;j++){
                    bw.write(StaticVar.machines[i][j].v+"\t"+StaticVar.machines[i][j].pw);
                    bw.write("\r\n");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Machine.initMachine();
    }
}
