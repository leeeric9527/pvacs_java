package Structs;

import java.util.Arrays;
import java.util.Objects;

/**
 * @program: pvacs_java
 * @description: 解的结构
 * @author: Yongjie Lv
 * @create: 2018/09/12/18:34
 **/
public class Solution {
    // 用户偏好
    public float vmk;
    public float vepc;
    //解得序列，从1开始
    public int[] batchSeq=new int[150];
    // 优化的目标
    public float cmax;
    public float epc;

    public Solution(){
        this.vepc=-1;
        this.vmk=-1;
        for(int i=0;i<150;i++){
            this.batchSeq[i]=-1;
        }
        this.cmax=-1;
        this.epc=-1;
    }

    /*@Override
    public int compareTo(Object o) {
        Solution solution=(Solution)o;
        // this比solution更优
        if (this.epc<=solution.epc&&this.cmax<=solution.cmax){
            return -1;
        }
        // this比solution差
        else if(this.epc>solution.epc&&this.cmax>solution.cmax){
            return 1;
        }
        // 两者非占优关系
        return 0;
    }*/

    @Override
    public String toString(){
        String str="";
        for (int i=1;i<=StaticVar.batchNum;i++){
            str+=(batchSeq[i]+",");
        }
        return "vmk="+vmk+'\t'+"vepc="+vepc+'\n'+
                "cmax="+cmax+'\t'+"epc="+epc+'\n'+
                 str;
    }


    @Override
    public boolean equals(Object o){
        if (o==this)    {return true;}
        if (!(o instanceof Solution))   {return false;}
        Solution s=(Solution)o;
        return s.vmk==vmk&&
                s.cmax==cmax&&
                s.epc==epc&&
                Arrays.equals(s.batchSeq,batchSeq);
    }
    @Override
    public int hashCode(){

        return new Float(vmk).hashCode()+new Float(cmax).hashCode()+
                new Float(epc).hashCode()+
                Arrays.hashCode(batchSeq);

    }
    public static void main(String[] args) {
        Solution s1=new Solution();
        Solution s2=new Solution();
        s1.batchSeq[0]=1;
        s1.batchSeq[1]=2;
        s1.batchSeq[2]=3;
        s1.vepc=0.2f;
        s1.epc=140;
        s1.cmax=100;

        s2.batchSeq[0]=1;
        s2.batchSeq[1]=2;
        s2.batchSeq[2]=3;
        s2.vepc=0.2f;
        s2.epc=130;
        s2.cmax=110;

        System.out.println(Arrays.hashCode(s1.batchSeq));
        System.out.println(Arrays.hashCode(s2.batchSeq));
    }


}
