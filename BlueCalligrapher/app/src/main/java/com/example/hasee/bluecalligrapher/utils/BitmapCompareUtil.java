package com.example.hasee.bluecalligrapher.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

/**
 * Created by hasee on 2018/7/31.
 */

@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public class BitmapCompareUtil {

    private static int t=0,f=0;

    public static String similarity (Bitmap b,Bitmap viewBt) {
        //把图片转换为Bitmap
        t=f=0;
        Bitmap bm_one = b;
        Bitmap bm_two = viewBt;
        //保存图片所有像素个数的数组，图片宽×高
        int[] pixels_one = new int[bm_one.getWidth()*bm_one.getHeight()];
        int[] pixels_two = new int[bm_two.getWidth()*bm_two.getHeight()];
        //获取每个像素的RGB值
        bm_one.getPixels(pixels_one,0,bm_one.getWidth(),0,0,bm_one.getWidth(),bm_one.getHeight());
        bm_two.getPixels(pixels_two,0,bm_two.getWidth(),0,0,bm_two.getWidth(),bm_two.getHeight());
        //如果图片一个像素大于图片2的像素，就用像素少的作为循环条件。避免报错
        if (pixels_one. length >= pixels_two. length) {
            //对每一个像素的RGB值进行比较
            for( int i = 0; i < pixels_two. length; i++){
                int clr_one = pixels_one[i];
                int clr_two = pixels_two[i];
                //RGB值一样就加一（以便算百分比）
                if (clr_one == clr_two) {
                    t++;
                } else {
                    f++;
                }
            }
        } else {
            for( int i = 0; i < pixels_one. length; i++){
                int clr_one = pixels_one[i];
                int clr_two = pixels_two[i];
                if (clr_one == clr_two) {
                    t++;
                } else {
                    f++;
                }
            }

        }

        return ""+myPercent ( t, t+ f );

    }
    /**
     * 百分比的计算
     * @author xupp
     * @param y(母子)
     * @param z（分子）
     * @return 百分比（保留小数点后两位）
     */
    public static String myPercent (int y, int z)
    {
        String baifenbi= ""; //接受百分比的值
        double baiy=y*1.0;
        double baiz=z*1.0;
        double fen=baiy/baiz;
        DecimalFormat df1 = new DecimalFormat( "00.00%"); //##.00%   百分比格式，后面不足2位的用0补齐
        baifenbi= df1.format(fen);
        return baifenbi;
    }

    public static int getT(){
        return t;
    }
    public static int getF(){
        return f;
    }

    public static String similarity2 (Bitmap b,Bitmap viewBt) {
        //把图片转换为Bitmap
        t=f=0;
        int width=b.getWidth()>viewBt.getWidth()?viewBt.getWidth():b.getWidth();
        int height=b.getHeight()>viewBt.getHeight()?viewBt.getHeight():b.getHeight();
        /*
        //保存图片所有像素个数的数组，图片宽×高
        int[] pixels_one = new int[bm_one.getWidth()*bm_one.getHeight()];
        int[] pixels_two = new int[bm_two.getWidth()*bm_two.getHeight()];
        //获取每个像素的RGB值
        bm_one.getPixels(pixels_one,0,bm_one.getWidth(),0,0,bm_one.getWidth(),bm_one.getHeight());
        bm_two.getPixels(pixels_two,0,bm_two.getWidth(),0,0,bm_two.getWidth(),bm_two.getHeight());*/

        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                int color1=b.getPixel(i,j);
                int color2=viewBt.getPixel(i,j);
                int r1=Color.red(color1),g1=Color.green(color1),b1=Color.blue(color1);
                int r2=Color.red(color2),g2=Color.green(color2),b2=Color.blue(color2);
                if(r1!=255&&g1!=255&&b1!=255){
                    if(r1==r2&&g1==g2&&b1==b2){
                        t++;
                    }else{
                        f++;
                    }
                }
            }
        }
        return ""+myPercent ( t, t+ f );
    }
}