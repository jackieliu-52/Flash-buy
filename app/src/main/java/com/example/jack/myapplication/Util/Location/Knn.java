package com.example.jack.myapplication.Util.Location;

/**
 * knn算法
 */
public class Knn {

    private Knn(){}
    private static int totaltest = 16;

    private static int totalbeacon = 8;//number of beacon

    private static int top = 4;//top change to 4

    private static double [][] RSSI = new double [totaltest][totalbeacon+2];

//	private static double temp[] = new double [totalbeacon+2];

    public static double[] formain(double[] RSSI4){
        double[][] RSSI = init();
        double [] out = new double [2];
        double temp[] = new double [totalbeacon+2];
        double[] coff = new double[totaltest];
        double[] tmpcoff = new double[totaltest];
        Sort_fliter(RSSI4);

        for(int i = 0;i < totaltest ; i++){
            for(int j = 0;j < totalbeacon+2 ; j++){
                temp[j] = RSSI[i][j];
            }
            Sort_fliter(temp);
        //放入邻居案例集合
            if(likely(temp,RSSI4) > -1)
                coff[i] = likely(temp,RSSI4);
            else
                coff[i] = -10;
        }
        for(int i = 0;i < totaltest ; i++){
            //	System.out.print(coff[i]+" ");
            tmpcoff[i] = coff[i];
        }

        Sort(coff);  //按照增序排列
        double nowx = 0;
        double nowy = 0;
        for(int i = 0;i < totaltest ; i++){
            for(int k = 1;k <= top ; k++){
                if(coff[totaltest-k] == tmpcoff[i]){
                    nowx += RSSI[i][totalbeacon] ;
                    nowy += RSSI[i][totalbeacon+1] ;
                    break;
                }
            }
        }

        //取所有的平均值
        nowx = nowx/top;
        nowy = nowy/top;
        out[0] = nowx;
        out[1] = nowy;
        return out;

    }


    /**
     * 空间指印
     * @return
     */
    public static double[][] init(){
        //if cannot read : -150
        double[][] RSSI = {{-66,-68,-73,-71,-76,-79,-82,-83,1,0},//o
                {-67,-66,-79,-80,-79,-82,-91,-82,1,3},//o
                {-63,-50,-76,-70,-74,-74,-78,-81,1,6},//o
                {-70,-68,-80,-75,-80,-78,-84,-84,1,9},//o
                {-65,-69,-65,-70,-76,-71,-75,-82,6,0},//d
                {-68,-68,-71,-73,-74,-76,-80,-81,6,3},//d
                {-72,-74,-77,-63,-68,-66,-85,-79,6,6},//d
                {-72,-67,-74,-70,-77,-73,-84,-79,6,9},//d
                {-74,-79,-71,-77,-68,-75,-70,-78,11,0},//d
                {-71,-69,-71,-72,-62,-70,-75,-74,11,3},//d
                {-73,-72,-73,-76,-71,-69,-80,-72,11,6},//d
                {-71,-75,-71,-66,-71,-63,-77,-78,11,9},//d
                {-75,-80,-78,-82,-75,-80,-68,-72,17,0},//d
                {-71,-77,-69,-87,-72,-83,-65,-72,17,3},//d
                {-77,-82,-74,-76,-73,-72,-71,-60,17,7},//d*
                {-76,-70,-77,-77,-72,-68,-75,-70,17,10}//d
        }; //data base

        return RSSI;

    }

    /**
     * 选取最高的几个Rssi值来进行定位
     * @param rSSI
     */
    public static void Sort_fliter(double[] rSSI){ //sort and choose top 5
        int j;
        double n;
        //----------------------------------------
        int minsave = 4;//choose top 5(8-3)
        double A[] = new double [rSSI.length-2];
        int flag[] = new int [rSSI.length-2];
        for (int i = 0; i < rSSI.length-2; i++){
            A[i] = rSSI[i];
            flag[i] =0;
        }

        for (int i = 1; i < A.length; ++i){
            n = A[i];
            for (j = i - 1; j >= 0 && A[j] > n; --j)
                A[j + 1] = A[j];
            A[j + 1] = n;
        }

        for (int i = 0; i < minsave; i++){
            A[i] = 0;

        }
        for (int i = 0; i < rSSI.length-2; i++){
            for (int k = 0; k < rSSI.length-2; k++){
                if(rSSI[i] == A[k]) //«O¯d
                    flag[i] = 1;
            }
        }
        for (int i = 0; i < rSSI.length-2; i++){
            if(flag[i] != 1)
                rSSI[i] = 0;
        }
    }

    /**
     * 增序排序
     * @param array
     */
    public static void Sort(double[] array)
    {
        int j;
        double n;
        for (int i = 1; i < array.length; ++i)
        {
            n = array[i];
            for (j = i - 1; j >= 0 && array[j] > n; --j)
                array[j + 1] = array[j];
            array[j + 1] = n;
        }
    }

    public static double avg(double[] b){ //couculate average
        double average = 0;
        double total = 0.0;

        for (int i = 0; i < b.length; i++){
            total += b[i];
        }
        average = total / b.length ;

        return average;
    }

    public static double add(double[] array){ //add all
        double sum = 0;
        for (int i = 0; i < array.length; ++i){
            sum = sum + array[i];
        }
        return sum;
    }

    /**
     * 看看两组Rssi值是否类似
     * @param RSSI 在线
     * @param RSSI2 离线
     * @return
     */
    public static double likely(double[] RSSI,double[] RSSI2){ //calculate R1 and R2 correlation coefficient
        int enough = 0;
        double corr = -1;
        int c = 0;
        double tempup = 0;
        for(int i=0;i<totalbeacon;i++){
            if(RSSI[i] != 0 && RSSI2[i] != 0)
                enough ++;
        }

        if(enough >= 1){
            double A[] = new double [enough];
            double B[] = new double [enough];

            for(int i=0;i<totalbeacon;i++){
                if(RSSI[i] != 0 && RSSI2[i] != 0){
                    A[c] = RSSI[i];
                    B[c] = RSSI2[i];
                    c++;
                }
            }

            double a_avg = avg(A);
            double b_avg = avg(B);
            double []temX = new double[enough];
            double []temY = new double[enough];

            for(int j=0;j<enough;j++){
                tempup += (A[j] - a_avg)*(B[j] - b_avg);
                temX[j] = (A[j] - a_avg)*(A[j] - a_avg);
                temY[j] = (B[j] - b_avg)*(B[j] - b_avg);
            }
            double a = add(temX);
            double b = add(temY);
            a = Math.sqrt(a);
            b = Math.sqrt(b);

            corr = tempup / (a*b);
        }
        return corr;
    }
}
