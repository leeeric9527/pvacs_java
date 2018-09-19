package Structs;

import java.awt.*;
import java.io.*;
import java.util.Random;

/**
 * @program: pvacs_java
 * @description: 工件的相关信息
 * @author: Yongjie Lv
 * @create: 2018/09/10/15:00
 **/
public class Job {
    /**
     * 工件编号
     *
     */
    public int JobId;
    /**
     *工件尺寸
     */
    public int JobSize;
    /**
     * 工件不同阶段的处理时间，从1开始编号
     */
    public int JobPT[]=new int[Parameters.KMAX+1];


    public static Job[] initJob(){
        Job[] job=new Job[Parameters.JOBNUM+1];
        for(int i=0;i<=Parameters.JOBNUM;i++){
            job[i]=new Job();
        }
        randGenerate(job);
        //readFromFile(job);
        return job;
    }

    /**
     * @description: 随机生成工件信息
     * @param job:  最终生成的工件数组
     * @return: void
     * @author: Yongjie Lv
     * @date: 18:14 2018/9/10
    */
    private static void randGenerate(Job[] job){
        Random r=new Random();
        for(int i=1;i<=Parameters.JOBNUM;i++){
            job[i].JobId=i;
            job[i].JobSize=r.nextInt(10)+1;
            for(int k=1;k<=Parameters.KMAX;k++){
                job[i].JobPT[k]=r.nextInt(41)+10;
            }
        }
        writeInFile(job);
    }

    /**
     * @description: 从文件中读取工件信息
     * @param job:  最终生成的工件数组
     * @return: void
     * @author: Yongjie Lv
     * @date: 18:14 2018/9/10
    */
    private static void readFromFile(Job[] job){
        try (BufferedReader br=new BufferedReader(new FileReader(new File("src\\File\\jobInfo.txt")))){
            String str=null;
            String[] strs;
            int i=1;
            while ((str=br.readLine())!=null){
                strs=str.split("\t");
                job[i].JobId=Integer.parseInt(strs[0]);
                job[i].JobSize=Integer.parseInt(strs[1]);
                for(int j=1;j<=Parameters.KMAX;j++){
                    job[i].JobPT[j]=Integer.parseInt(strs[j+1]);
                }
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @description: 将随机生成的工件信息写入文件中
     * @param job:  之前随机生成的工件数组
     * @return: void
     * @author: Yongjie Lv
     * @date: 18:15 2018/9/10
    */
    private static void writeInFile(Job[] job){
        try(BufferedWriter bw=new BufferedWriter(new FileWriter("src\\File\\jobInfo2.txt",false))){
            for(int i=1;i<=Parameters.JOBNUM;i++){
                bw.write(job[i].JobId+"\t");
                bw.write(job[i].JobSize+"\t");
                for(int j=1;j<=Parameters.KMAX;j++){
                    bw.write(job[i].JobPT[j]+"\t");
                }
                bw.write("\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * @description: 工件信息的输出格式
     * @param :
     * @return: java.lang.String
     * @author: Yongjie Lv
     * @date: 18:16 2018/9/10
    */
    @Override
    public String toString(){
        String str="id:"+this.JobId+'\t'+"size:"+this.JobSize+'\t';
        for(int i=1;i<=Parameters.KMAX;i++){
            str+=(this.JobPT[i]+"\t");
        }
        return str;
    }

    @Override
    public Job clone(){
        Job job=new Job();
        job.JobId=this.JobId;
        job.JobSize=this.JobSize;
        for(int i=0;i<=Parameters.KMAX;i++){
            job.JobPT[i]=this.JobPT[i];
        }
        return job;
    }
    public static void main(String[] args) {
        Job[] jobs=Job.initJob();
        for(int i=1;i<jobs.length;i++){
            System.out.println(jobs[i]);
        }
    }

}
