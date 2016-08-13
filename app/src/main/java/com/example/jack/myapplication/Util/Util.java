package com.example.jack.myapplication.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;

/**
 * Created by Jack on 2016/8/7.
 */
public  class Util {
    /**
     * 通过文件名获取资源id 例子：getResId("icon", R.drawable.class);
     *
     * @param variableName
     * @param c
     * @return
     */
    public static synchronized int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static synchronized int stringToId(Context context,String icon) {
        int image = context.getResources().getIdentifier(icon, "drawable", "com.example.jack.myapplication");
        return  image;
    }

    public static String getCurrentDate(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");
        String date = sdf.format(new java.util.Date());
        return date;
    }
    /**
     * 把输入流的内容转换成字符串
     * @param is
     * @return null解析失败， string读取成功
     */
    public static String readStream(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();

            String temptext = new String(baos.toByteArray());
            if (temptext.contains("charset=gb2312")) {//解析meta标签
                return new String(baos.toByteArray(), "gb2312");
            } else {
                return new String(baos.toByteArray(), "utf-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
