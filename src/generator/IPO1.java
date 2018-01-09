package generator;

import combinatorial.CTModel;
import combinatorial.TestCase;
import combinatorial.TestSuite;
import handler.CH_MFTVerifier;

import java.util.ArrayList;

public class IPO1 implements CAGenerator{
    public CTModel model;
    public class E
    {
        int x;
        int y;
    }
    int f0(int b[], int t, int m)                        //b[]中前m个数乘积;
    {
        int N = 1;
        if (t >= m)
        {
            for (int i = 0; i<m; i++)
                N *= b[i];
        }
        return N;
    }
    ///////////////
    int C(int t, int n)                                           //求组合数C(t,n)
    {
        int N1 = 1;
        int N2 = 1;
        for (int i = 0; i<t; i++)
        {
            N1 = N1*(i + 1);
            N2 = N2*(n - i);
        }
        if (N2 < N1)
            return 0;
        else
            return N2 / N1;
    }
    ////////////////Function 1：
    void f1(int Ar[], int f[], int n, int t)                        //计算n个变量中任t个数的乘积.结果存入array[]中.
    {
        int []p = new int[t];
        for (int i = 0; i<t; i++)
        {
            p[i] = i;
        }
        int cn = 0;
        while (p[0] != n - t + 1)
        {
            for (int i = 0; i<t; i++)
                Ar[cn] *= f[p[i]];
            cn++;
            p[t - 1]++;
            for (int i = t - 1; i>0; i--)
            {
                if (p[i] == n - t + i + 1)
                {
                    p[i - 1]++;
                    for (int j = 1; j<t - i + 1; j++)
                        p[i - 1 + j] = p[i - 1] + j;
                }
            }
        }
    }
    //////////////////////Function 2:         //组合对参数种类<--->B[][]表中的行号;
    int f2(int p[], int N, int t)
    {
        int M;
        int mi = N - p[t - 1];
        M = C(t, N) - C(1, mi);
        int S = 0;
        int []k = new int[t];
        k[0] = -1;
        for (int i = 1; i < t; i++)
            k[i] = N - 2 - p[t - i - 1];
        for (int i = 1; i < t; i++)
        {
            S += C(i + 1, k[i] + 1);
        }
        return M - S;
    }
    /////////////////////////Function 3:           //组合对具体值--->B[][]表中列号;
    int f3(int a[], int x[], int f[], int t, int n)
    {
        int N = 0;
        int []b = new int[t];
        int count = 0;
        for (int i = 0; i<t; i++)
        {
            b[count] = f[x[i]];
            count++;
        }
        for (int i = 0; i<t; i++)
            N += a[i] * f0(b, t, i);
        return N;
    }
    void f4(int N, int a[], int x[], int f[], int t)       //组合对总值+参数名+参数取值->组合对;
    {
        int []b = new int[t];
        int count = 0;
        for (int i = 0; i<t; i++)
        {
            b[count] = f[x[i]];
            count++;
        }
        for (int i = 0; i<t; i++)
        {
            a[i] = N%b[i];
            N = (N - N%b[i]) / b[i];
        }
    }
    int f5(int []tc,int a[],int N,int MAX)
    {
        int []b=new int[MAX];                     //b[i]表示i在a[]中出现的次数;
        for(int i=0;i<MAX;i++)
            b[i]=0;
        for(int i=0;i<a.length;i++) {
            //System.out.println(i+" "+a[i]);
            b[a[i]]++;
        }
        int min=10000000;
        int i_min=0;
        for(int i=1;i<MAX;i++) {
            int[] tmpTC =new int[tc.length];
            for(int j =0; j < tmpTC.length; j++)
                tmpTC[j] = tc[j];
            tmpTC[N] = i;
            if (b[i] < min&& model.isValid(tmpTC)) {
                min = b[i];
                i_min = i;
            }
        }
        return i_min;
    }
    int match_unity(int a[], int b[], int n)                  //匹配度算法;
    {
        int unity = 0;
        for (int i = 0; i < n; i++)
        {
            if (a[i] == b[i] && a[i]>-1)
                unity++;
        }
        return unity;
    }
    boolean judge_k(int a[], int n,int k)                                  //判k
    {
        for (int i = 0; i < n;i++)
            if (a[i] != k)
                return false;
        return true;
    }
    boolean judge_match(int a[], int b[], int n)                 //判断两个数组是否匹配;
    {
        for (int i = 0; i < n; i++)
            if (a[i] != b[i] && a[i]>-1 && b[i] > -1)
                return false;
        if(judge_k(b,n,-1))
            return false;
        int []tmpTC = new int[n];
        for(int i =0; i < n; i++) {
            if(a[i] != -1)
                tmpTC[i] = a[i];
            else
                tmpTC[i] = b[i];
           // System.out.print(tmpTC[i]+" ");
        }
       // System.out.println();

        if(!model.isValid(tmpTC))
            return false;
        return true;
    }
    void unions(int a[], int b[], int n)                       //合并两个数组a[],b[];
    {
        for (int i = 0; i < n; i++)
        {
           // if (a[i] == -1 && b[i] == -1)
            //    a[i] = -1;
            if (a[i] == -1 && b[i] != -1)
                a[i] = b[i];
        }
        for(int i = 0; i < n; i++)
            b[i] = -1;
    }
    public void generation(CTModel model, TestSuite ts) {
        this.model = model;
        //model.IPOsortValue();
        model.initialization();
        int n = model.parameter;                    //n表示参数个数;
        int t = model.t_way;                    //主维数为t;
        int[] f = new int[n];      //f[i]表示参数i的取值个数;
        for (int i = 0; i < n; i++) {
            f[i] = model.value[i];
           // System.out.print(f[i]);
        }
      //  System.out.println();

        /**
         对基维初始化;
         组合覆盖数组 行表示具体参数组合，例如3个2,2维，行序列为（0,1），（0,2），（1,2）
         列表示具体取值，上面的例子列为（0,0），（1,0），（0,1），（1,1）
         注意列是从高位增加的
         */

        int N0 = C(t, n);
        int[] Ar = new int[N0];   // Ar[i]表示B[][]数组中第i行元素个数;
        for (int i = 0; i < N0; i++)
            Ar[i] = 1;
        f1(Ar, f, n, t);        //给Ar[i]赋值;
        boolean[][] B = new boolean[N0][];
        for (int i = 0; i < N0; i++)
            B[i] = new boolean[Ar[i]];
        int[] tmp = new int[t];
        for (int k = 0; k < t; k++)
            tmp[k] = k;
        boolean[][] tmpMatrix = model.getComba().getMatrix();
        int tmpcount = 0;
        for (int i = 0; i < N0; i++){

            if (tmp[0] != n - t + 1) {

                int[] tmpValue = new int[t];
                for (int k = 0; k < t; k++)
                    tmpValue[k] = f[tmp[k]];
                int[] tuple = new int[t];
                for (int j = 0; j < Ar[i]; j++) {
                    if (tmpMatrix[i][j])
                        tmpcount++;
                    int tmpj = j;
                    for (int k = 0; k < t; k++) {
                        int kk = 1;
                        for (int ki = k + 1; ki < t; ki++)
                            kk = kk * tmpValue[ki];
                        tuple[k] = tmpj / kk;
                        tmpj = tmpj - tuple[k] * kk;
                    }
                    int jj = 0;
                    int jk = 1;
                    for (int k = 0; k < t; k++) {
                        jj += tuple[k] * jk;
                        jk = jk * tmpValue[k];
                    }
                    B[i][jj] = tmpMatrix[i][j];
                }
              //  while (tmp[0] != n - t + 1) {

                    tmp[t - 1]++;
                    for (int ii = t - 1; ii > 0; ii--) {
                        if (tmp[ii] == n - t + 1 + ii) {
                            tmp[ii - 1]++;
                            for (int j = 0; j < t - ii; j++)
                                tmp[ii + j] = tmp[ii - 1] + j + 1;
                        }

                    }
               // }
            }
    }
        System.out.println(tmpcount);
      /*  for(int i=0;i<B.length;i++){
            for(int j = 0;j<B[i].length;j++)
                System.out.print(B[i][j]+" ");
            System.out.println();

        }*/
        //System.out.println(B[0][0]);
        int[][]st = new int[100000][];        // st[][]即为最终生成的测试用例表;
        for (int i = 0; i < 100000; i++)
            st[i] = new int[n];
        int [][]testcase = new int[100000][];     //testcase[][]表示中介用例表（需要进行一系列合并及处理）;
        for (int i = 0; i < 100000; i++)
            testcase[i] = new int[n];
        for (int i = 0; i < 100000; i++)
            for (int j = 0; j < n; j++)
            {
                testcase[i][j] = -1;
                st[i][j] = 0;
            }
        //for (int i = 0; i < 100000; i++)
           // for (int j = 0; j < n; j++)
              //  st[i][j] = 0;
        /**
         *      下初始化st[][]；
         *      首先覆盖前t个参数
         *      */

        int []p1 = new int[n];
        int cn1 = 0;
        for (int i = 0; i < n; i++)
            p1[i] = -1;
        for(int i=0;i<t;i++)
            p1[i] = 0;
        while (p1[0] != f[0])
        {
            if(model.isValid(p1)) {
                for (int j = 0; j < t; j++)
                    st[cn1][j] = p1[j];
                cn1++;
            }
            p1[t - 1]++;
            for (int i = t - 1; i>0; i--)
            {
                if (p1[i] == f[i])
                {
                    p1[i - 1]++;
                    for (int j = 0; j < t - i; j++)
                        p1[i + j] = 0;
                }

            }
        }

        /*for(int i = 0;i < cn1; i++){
            for(int j = 0 ;j<n;j++)
                System.out.print(st[i][j]+" ");
            System.out.println();
        }*/
        ////构造Array[][],Array[i]记录是第t+i列元需要访问的B[][]行号;
        int N1 = C(t - 1, n - 1);
        int [][]Array = new int[n - t][];        //Array[][]记录了这样的信息:Array[i][]表示在后续算法中确定第t+i个参数取值时，需要依次访问的B[][]行号.
        for (int i = 0; i < n - t; i++)
            Array[i] = new int[N1];
        for (int i = 0; i < n - t; i++)
            for (int j = 0; j < N1; j++)
                Array[i][j] = 0;
        int []x1 = new int[t];                 //遍历任意t个的具体覆盖值
        int []q1 = new int[t - 1];             //遍历任意t-1个的所有覆盖
        for (int i = 0; i < n - t; i++)
        {
            for (int j = 0; j < t - 1; j++)
                q1[j] = j;
            int cn2 = 0;
            x1[t - 1] = t + i;
            while (q1[0] != i + 2)
            {
                for (int j = 0; j<t - 1; j++)
                    x1[j] = q1[j];
                q1[t - 2]++;
                for (int j = t - 2; j>0; j--)
                {
                    if (q1[j] == i + j + 2)
                    {
                        q1[j - 1]++;
                        for (int k = 1; k < t - j; k++)
                            q1[j - 1 + k] = q1[j - 1] + k;
                    }
                }
                Array[i][cn2] = f2(x1, n, t);
                cn2++;
            }
        }
        //////////////////////////下进行IPO算法环节（水平扩展+垂直扩展+部分贪心算法）
        int cn3 = t;            //cn2表示水平扩展的追踪下标;
        int []q2 = new int[t - 1];       //遍历需要用到;
        int []ex = new int[t];
        int []ey = new int[t];
        while (cn3 < n)           //一个一个参数的添加,目前st[][]具有cn3列，cn1行;
        {
            int N2 = C(t - 1, cn3);
            E[][] e = new E[N2][];
            for (int i = 0; i < N2; i++) {
                e[i] = new E[t];
                for (int j = 0; j < t; j++)
                    e[i][j] = new E();
            }


            ///////////进行水平扩展////////////
            for (int line_i = 0; line_i < cn1; line_i++)                //逐行确定第line1行第cn3位置的取值;
            {
                int e_line = 0;                                         //x[][]的跟踪行号;
                for (int i = 0; i < t - 1; i++)
                    q2[i] = i;
                while (q2[0] != cn3 + 2 - t) {
                    for (int i = 0; i < t - 1; i++)                       //对x[x_line][]逐行赋值;
                    {
                        e[e_line][i].x = st[line_i][q2[i]];
                        e[e_line][i].y = q2[i];
                    }
                    q2[t - 2]++;
                    for (int i = t - 2; i > 0; i--) {
                        if (q2[i] == cn3 + 2 - t + i) {
                            q2[i - 1]++;
                            for (int j = 0; j < t - i - 1; j++)
                                q2[i + j] = q2[i - 1] + j + 1;
                        }
                    }
                    e_line++;
                }
                int max = -1;
                for (int i = 0; i < N2; i++)
                    e[i][t - 1].y = cn3;
                for (int size = 0; size < f[cn3]; size++)                     //从0,1,2,...,f(cn3)-1中选一个数使覆盖组合对最多;
                {
                    int[] temTC = new int[n];
                    for (int i = 0; i < n; i++)
                        temTC[i] = st[line_i][i];
                    for (int i = cn3 + 1; i < n; i++)
                        temTC[i] = -1;
                    temTC[cn3] = size;
                   /* for(int i = 0;i<temTC.length;i++)
                        System.out.print(temTC[i]+",");
                    System.out.println("");*/
                    if (model.isValid(temTC)) {
                        // System.out.println("IsValid");
                        int count1 = 0;                                        //记录覆盖对数;
                        for (int i = 0; i < N2; i++)
                            e[i][t - 1].x = size;
                        for (int i = 0; i < N2; i++) {
                            for (int j = 0; j < t; j++) {
                                ex[j] = e[i][j].x;
                                ey[j] = e[i][j].y;
                            }
                            int N3 = f3(ex, ey, f, t, n);
                            int N4 = Array[cn3 - t][i];
                            if (!B[N4][N3])
                                count1++;
                        }


                        ////////////////////////////

                        if (count1 > max) {
                            // System.out.println("---"+max);
                            max = count1;
                            st[line_i][cn3] = size;
                        }
                    }
                }
                //System.out.println("---"+st[line_i][cn3]);
                //////////////////////////////////////在B[][]中将新近覆盖的组合对置true;
                for (int i = 0; i < N2; i++)
                    e[i][t - 1].x = st[line_i][cn3];
                for (int i = 0; i < N2; i++) {
                    for (int j = 0; j < t; j++) {
                        ex[j] = e[i][j].x;
                        ey[j] = e[i][j].y;
                    }
                    int N3 = f3(ex, ey, f, t, n);
                    int N4 = Array[cn3 - t][i];
                    B[N4][N3] = true;                       //新近覆盖元置true;
                }


                ///////////////
            }
           /* System.out.println("");
            for(int i = 0;i < cn1; i++){
                for(int j = 0 ;j<n;j++)
                    System.out.print(st[i][j]+" ");
                System.out.println();
            }


           /* System.out.println("水平：");
            for(int i = 0;i < B.length; i++){
                for(int j = 0 ;j<B[i].length;j++)
                    System.out.print(B[i][j]+" ");
                System.out.println();
            }*/

            ////////////////////////////////////垂直扩展
            for (int i = 0; i < 100000; i++)
                for (int j = 0; j < n; j++)
                    testcase[i][j] = -1;
            int count2 = 0;                                  // count2记录是testcase表行号;

            for (int i = 0; i < N2; i++) {
                int cm1 = Array[cn3 - t][i];
                for (int j = 0; j < t; j++)           // 记录此时t-way数对的具体参数名;
                    ey[j] = e[i][j].y;
                for (int j = 0; j < Ar[cm1]; j++)       //将未覆盖的组合对存入testcase中;
                {
                    if (B[cm1][j] == false) {
                        B[cm1][j] = true;
                        int[] ct = new int[t];
                        f4(j, ct, ey, f, t);
                        for (int k = 0; k < t; k++)
                            testcase[count2][ey[k]] = ct[k];
                        count2++;
                    }
                }
            }

           // System.out.println("压缩前：" + count2);
            /////////////////////
            ////////////////下通过匹配度的算法进行对testcase[][]压缩,testcase[][]具有count2行，n列；

            /////////////////////
            ////////////////下通过匹配度的算法进行对testcase[][]压缩,testcase[][]具有count2行，n列；
           /* int []r = new int[count2];
            for (int i = 0; i < count2; i++)
                r[i] = i;
            int r_m = count2;
            int cm2 = 0;                                    //cm2用于记录处理的趟数；
            int newAdd = 0;
            while (cm2 < count2)
            {
                if (judge_k(testcase[cm2],cn3+1,-1))
                    cm2++;
                else
                {
                    int r_i = 0;                                 //r[]不断缩小;
                    int max_unity = -1;                           //最大匹配;
                    int u_m = cm2;                                  //最大匹配所在的行;
                    ////////////寻找匹配最多的行
                    for (int i = cm2 + 1; i < r_m; i++)
                    {
                        if (judge_match(testcase[cm2], testcase[i], n))
                        {
                            r_i++;
                            r[r_i] = i;
                            int m1 = match_unity(testcase[cm2], testcase[i], cn3+1);
                            if (m1>max_unity)
                            {
                                u_m = i;
                                max_unity = m1;
                            }
                        }
                    }

                    ///下进行合并
                    if (max_unity >= 0)
                        unions(testcase[cm2], testcase[u_m], cn3+1);
                    else
                    {
                        for (int i = 0; i < cn3+1; i++)
                        {
                            if(testcase[cm2][i]>-1)
                                st[cn1 + cm2][i] = testcase[cm2][i];
                            else
                            {
                                int []ch=new int[cn1 + cm2];
                                for(int j=0;j<cn1+cm2;j++)
                                    ch[j]=st[j][i];
                                int []tmpTC = new int[n];
                                for(int k =0; k < n; k++)
                                    tmpTC[k] = testcase[i][k];
                                st[cn1 + cm2][i] = f5(tmpTC,ch,i,f[i]);
                                //  st[cn1+cm2][i]=rand()%f[i];
                            }
                        }
                        cm2++;
                        newAdd++;
                    }
                }
            }*/
             int newAdd=0;
            if (count2 > 0){
                int cm2 = 1;                                    //cm2用于记录处理的趟数；
                newAdd = 1;
            while (cm2 < count2) {
                int matchLine;
                for (matchLine = 0; matchLine < newAdd; matchLine++)
                    if (judge_match(testcase[matchLine], testcase[cm2], n))
                        break;
                if (matchLine < newAdd)
                    unions(testcase[matchLine], testcase[cm2], cn3 + 1);

                else {
                    for (int i = 0; i < cn3 + 1; i++) {
                        testcase[newAdd][i] = testcase[cm2][i];
                    }
                    newAdd++;
                }

                cm2++;
            }
        }
            for(int i=0;i<newAdd;i++)
                for(int j = 0; j<cn3+ 1;j++){
                    if(testcase[i][j]>-1)
                        st[cn1 + i][j] = testcase[i][j];
                    else
                    {
                        int []ch=new int[cn1 + i];
                            for(int k=0;k<cn1+i;k++)
                                ch[k]=st[k][j];
                            int []tmpTC = new int[n];
                            for(int k =0; k < n; k++)
                                tmpTC[k] = testcase[i][k];
                        st[cn1 + i][j] = f5(tmpTC, ch,j,f[j]);

                    //  st[cn1+cm2][i]=rand()%f[i];
                }
            }
            cn1 += newAdd;
            cn3++;
           // cn1 = cn1 + newAdd;
            System.out.println("before para"+cn3+" num= "+cn1);
            System.out.println("压缩后："+newAdd);
            //cn1 = cn1 + newAdd;
          /*  for(int i = 0;i < cn1;i++){
                for(int j = 0 ;j<n;j++)
                    System.out.print(st[i][j]+" ");
                System.out.println();
            }

           // System.out.println("垂直：");
           /* for(int i = 0;i < B.length; i++){
                for(int j = 0 ;j<B[i].length;j++)
                    System.out.print(B[i][j]+" ");
                System.out.println();
            }*/
            //System.out.println("para"+cn3+" num= "+cn1);
           // if(cn3 == 4)
               // break;
        }
        for(int i = 0; i< cn1; i++) {
            for (int j = 0; j < n; j++)
                System.out.print(st[i][j] + " ");
            System.out.println();
        }

        for(int i=0;i<cn1;i++)
                ts.suite.add(new TestCase(st[i]));



    }
    public static void main(String[] args){
        ArrayList<int[]> cons = new ArrayList<>();
        int[] cons1 = new int[2];
        cons1[0] = -1; cons1[1] = -4;
        int[] cons2 = new int[2];
        cons2[0] = -1; cons2[1] = -7;
        int[] cons3 = new int[2];
        cons3[0]=-2;cons3[1]=-10;
        int[] cons4 = new int[2];
        cons4[0]=-3;cons4[1]=-10;
        int[] cons5 = new int[2];
        cons5[0]=-5;cons5[1]=-10;
        cons.add(cons1);
         cons.add(cons2);
        // cons.add(cons3);
        //cons.add(cons4);
        // cons.add(cons5);
        int[] parameter = new int[4];
        for(int i = 0;i <4;i++)
            parameter[i] = 3;

        CH_MFTVerifier chso = new CH_MFTVerifier();
        CTModel test = new CTModel(4,parameter,2,cons,chso);
        TestSuite ts = new TestSuite();
        IPO1 ipo = new IPO1();
        ipo.generation(test,ts);

    }
}
