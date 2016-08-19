package com.example.jack.myapplication.Util;

/**
 * 常数
 */
public interface Constant {
    /**
     * 二维码请求的type
     */
    public static final String REQUEST_SCAN_TYPE="type";
    /**
     * 普通类型，扫完即关闭
     */
    public static final int REQUEST_SCAN_TYPE_COMMON=0;
    /**
     * 服务商登记类型，扫描
     */
    public static final int REQUEST_SCAN_TYPE_REGIST=1;


    /**
     * 扫描类型
     * 条形码或者二维码：REQUEST_SCAN_MODE_ALL_MODE
     * 条形码： REQUEST_SCAN_MODE_BARCODE_MODE
     * 二维码：REQUEST_SCAN_MODE_QRCODE_MODE
     *
     */
    public static final String REQUEST_SCAN_MODE="ScanMode";
    /**
     * 条形码： REQUEST_SCAN_MODE_BARCODE_MODE
     */
    public static final int REQUEST_SCAN_MODE_BARCODE_MODE = 0X100;
    /**
     * 二维码：REQUEST_SCAN_MODE_ALL_MODE
     */
    public static final int REQUEST_SCAN_MODE_QRCODE_MODE = 0X200;
    /**
     * 条形码或者二维码：REQUEST_SCAN_MODE_ALL_MODE
     */
    public static final int REQUEST_SCAN_MODE_ALL_MODE = 0X300;
    /**
     * 查询条形码
     */
    public static final int REQUEST_INTERNET_BAR = 0X400;
    /**
     * 查询购物车
     */
    public static final int REQUEST_Cart = 0X500;
    /**
     * 搜索商品
     */
    public static final int REQUEST_Search = 0X600;
}
