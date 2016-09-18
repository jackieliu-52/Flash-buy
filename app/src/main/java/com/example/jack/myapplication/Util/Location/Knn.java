package com.example.jack.myapplication.Util.Location;

/**
 * knn算法
 */
public class Knn {

    private Knn(){}
    private static int totaltest = 5;  //number of offline fingerprint

    private static int totalbeacon = 3;//number of beacon

    private static int k = 2;//k define to 2


    /**
     * Knn算法，k取2时候的情况，本来需要对beacons的信号进行一个筛选排序
     * 但是由于设备数量有限，暂时不处理
     * @param RSSI4 在线数据集
     * @return 定位结果
     */
    public static double[] formain(double[] RSSI4){
        double[][] RSSI = init();      //离线数据库
        double [] out = new double [2];  //结果
        double temp[] = new double [totalbeacon+2];     //单个离线数据集
        double[] coff = new double[totaltest];  //相似度
        double[] tmpcoff = new double[totaltest];


        for(int i = 0;i < totaltest ; i++){
            for(int j = 0;j < totalbeacon+2 ; j++){
                temp[j] = RSSI[i][j];
            }


            //放入邻居案例集合
            if(likely(temp,RSSI4) > -1)
                coff[i] = likely(temp,RSSI4);
            else
                coff[i] = -10;
        }

        boolean flag = false;

        for(int i = 0;i < totaltest ; i++){
            tmpcoff[i] = coff[i];
            if(coff[i] != -10)
                flag = true;
        }
        //如果没有匹配项，退出
        if(flag)
            return new double[]{-1,-1};


        Sort(coff);  //按照增序排列
        double nowx = 0;
        double nowy = 0;
        for(int i = 0;i < totaltest ; i++){
            for(int j = 1;j <= k ; j++){
                if(coff[totaltest-j] == tmpcoff[i]){
                    nowx += RSSI[i][totalbeacon] ;
                    nowy += RSSI[i][totalbeacon+1] ;
                    break;
                }
            }
        }

        //取所有的平均值
        nowx = nowx/k;
        nowy = nowy/k;
        out[0] = nowx;
        out[1] = nowy;
        return out;

    }


    /**
     * 离线采集的空间指印，暂时数据比较少
     * @return
     */
    public static double[][] init(){
        double[][] RSSI = {{-75.08,-74.72,-78.36,640,615},//o
                {-63.24,-71.16,-72.08,640,500},//o
                {-62.18,-71.16,-74.88,640,380},//o
                {-59.96,-68.64,-72.76,640,240},//o
                {-71.24,-73.92,-80.48,650,740},//d
        }; //data base

        return RSSI;

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
     * 参考资料：http://f.wanfangdata.com.cn/view/%e5%9f%ba%e4%ba%8e%e4%bd%8d%e7%bd%ae%e6%8c%87%e7%ba%b9%e7%9a%84WLAN%e5%ae%a4%e5%86%85%e5%ae%9a%e4%bd%8d%e7%ae%97%e6%b3%95%e7%a0%94%e7%a9%b6.aspx?ID=Thesis_D593262&transaction=%7b%22ExtraData%22%3a%5b%5d%2c%22IsCache%22%3afalse%2c%22Transaction%22%3a%7b%22DateTime%22%3a%22%5c%2fDate(1474165837176%2b0800)%5c%2f%22%2c%22Id%22%3a%22ec58b42e-c915-4941-9372-a68500ad3472%22%2c%22Memo%22%3anull%2c%22ProductDetail%22%3a%22Thesis_D593262%22%2c%22SessionId%22%3a%228396212b-6486-4f88-a849-ae98e827fbda%22%2c%22Signature%22%3a%22k4EMgMOThONZkZQ9%5c%2ftyJi72yTZ2y2%2byE6WSPiuLia5a%2bnEHcXWp0wB%2bebtcrLGOV%22%2c%22TransferIn%22%3a%7b%22AccountType%22%3a%22Income%22%2c%22Key%22%3a%22ThesisFulltext%22%7d%2c%22TransferOut%22%3a%7b%22AccountType%22%3a%22GTimeLimit%22%2c%22Key%22%3a%22zndx%22%7d%2c%22Turnover%22%3a30.00000%2c%22User%22%3anull%2c%22UserIP%22%3a%22218.76.28.116%22%7d%2c%22TransferOutAccountsStatus%22%3a%5b%5d%7d
     * 匹配算法
     * @param RSSI 在线
     * @param RSSI2 离线
     * @return 相似度
     */
    public static double likely(double[] RSSI,double[] RSSI2){
        double tempup = 0;
        double dis = 0;

        double A[] = new double [totalbeacon];
        double B[] = new double [totalbeacon];
        for(int i = 0;i<totalbeacon;i++){
            A[i] = RSSI[i];
            B[i] = RSSI2[i];
        }
        double a_avg = avg(A);
        double b_avg = avg(B);

        double []temX = new double[totalbeacon];
        double []temY = new double[totalbeacon];

        for(int i = 0;i<totalbeacon;i++){
            tempup +=  (A[i] - a_avg)*(B[i] - b_avg);
            temX[i] = (A[i] - a_avg)*(A[i] - a_avg);
            temY[i] = (B[i] - b_avg)*(B[i] - b_avg);
        }

        double a = add(temX);
        double b = add(temY);
        a = Math.sqrt(a);
        b = Math.sqrt(b);

        dis = tempup / (a*b);

        return dis;
    }
}
