package Structs;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * @program: pvacs_java
 * @description:
 * @author: Yongjie Lv
 * @create: 2018/09/10/16:09
 **/
public class Test {


    public static void main(String[] args) {
        Solution s1=new Solution();
        Solution s2=new Solution();
        s1.batchSeq[0]=1;
        s1.batchSeq[1]=2;
        s1.batchSeq[2]=3;
        s1.batchSeq[3]=4;
        s1.vepc=0.2f;
        s1.vmk=0.8f;
        s1.epc=130;
        s1.cmax=113;

        s2.batchSeq[0]=1;
        s2.batchSeq[1]=2;
        s2.batchSeq[2]=3;
        s2.batchSeq[3]=4;
        s2.vepc=0.2f;
        s2.vmk=0.8f;
        s2.epc=130;
        s2.cmax=110;

        Solution s3=new Solution();
        s3.batchSeq[0]=1;
        s3.batchSeq[1]=2;
        s3.batchSeq[2]=3;
        s3.batchSeq[3]=4;
        s3.vepc=0.2f;
        s3.vmk=0.8f;
        s3.epc=96;
        s3.cmax=115;
        Set<Solution> nds=StaticVar.NDS;

        Iterator it=nds.iterator();
        Solution s=null;
        StaticVar.batchNum=3;
        while (it.hasNext()){
            s=(Solution)it.next();
            System.out.println(s);
        }

        System.out.println("///////////////////");
        Ant a1=new Ant();
        a1.solution=s1;
        Ant a2=new Ant();
        a2.solution=s2;
        Ant a3=new Ant();
        a3.solution=s3;

        a1.updateNDS();
        a2.updateNDS();
        a3.updateNDS();

        it=nds.iterator();
        while (it.hasNext()){
            s=(Solution)it.next();
            System.out.println(s);
        }

    }

}


