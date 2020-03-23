package com.example.hasee.bluecalligrapher.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.example.hasee.bluecalligrapher.encodebase64.MyBase64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by hasee on 2018/5/7.
 */

public class ImageUtil {

    //使用Bitmap加Matrix来缩放
    public static Bitmap resizeImage(Bitmap bitmap, int w, int h)
    {
        if(null==bitmap)return null;
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        float scaleWidth=((float)w)/width;
        float scaleHeight=((float)h)/height;
        Matrix matrix=new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        return Bitmap.createBitmap(bitmap,0,0,width,
                height,matrix,true);
    }

    //使用Bitmap加Matrix来缩放动态图片
    public static Bitmap resizeDynamicImage(Bitmap bitmap)
    {
        if(null==bitmap)return null;
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        float scaleWidth=((float)width)/(width*2);
        float scaleHeight=((float)height)/(height*2);
        Matrix matrix=new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        return Bitmap.createBitmap(bitmap,0,0,width,
                height,matrix,true);
    }

    //byte[]转Bitmap
    @Nullable
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            Bitmap bitmap= BitmapFactory.decodeByteArray(b, 0, b.length);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 80;//先压缩到80%
            while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于200kb,大于继续压缩
                if (options <= 0) {             //有的图片过大，可能当options小于或者等于0时，它的大小还是大于目标大小，于是就会发生异常，异常的原因是options超过规定值。所以此处需要判断一下
                    break;
                }
                baos.reset();// 重置baos即清空baos
                options -= 10;// 每次都减少10
                bitmap.compress(Bitmap.CompressFormat.PNG, options, baos);
            }
            return bitmap;
        } else {
            return null;
        }
    }

    public static byte[] getImgBytes(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); //设置位图的压缩格式，质量为80%，并放入字节数组输出流中
        bitmap.compress(Bitmap.CompressFormat.PNG,80,baos);
        return baos.toByteArray();
    }


    /**
     * 生成透明背景的圆形图片,！注意要生成透明背景的圆形，图片一定要png类型的，不能是jpg类型
     */
    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        if (null == bitmap){
            return null;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap drawBgBitmap(int color, Bitmap orginBitmap) {
        Paint paint = new Paint();
        paint.setColor(color);
        Bitmap bitmap = Bitmap.createBitmap(orginBitmap.getWidth(),
                orginBitmap.getHeight(), orginBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, orginBitmap.getWidth(), orginBitmap.getHeight(), paint);
        canvas.drawBitmap(orginBitmap, 0, 0, paint);
        return bitmap;
    }

    public static Bitmap drawBg2Bitmap(int color, Bitmap orginBitmap,int width,int height) {
        Paint paint = new Paint();
        paint.setColor(color);
        Bitmap bitmap = Bitmap.createBitmap(width,
                height, orginBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, orginBitmap.getWidth(), orginBitmap.getHeight(), paint);
        canvas.drawBitmap(orginBitmap, 0, 0, paint);
        return bitmap;
    }

    /**
     * 根据给定的宽和高进行拉伸
     * @param origin    原图
     * @param newWidth  新图的宽
     * @param newHeight 新图的高
     * @return new Bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        return Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
    }

    /**
     * 将彩色图转换为纯黑白二色
     * @return 返回转换好的位图
     */
    public static Bitmap convertBlackWhite(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Bitmap bitmap2 = Bitmap.createBitmap(w , h , Bitmap.Config.ARGB_8888);

        for(int i = 0 ; i < h ; i++)
            for(int j = 0 ; j < w; j ++)
            {
                int argb = bmp.getPixel(j , i );
                int r =( argb>>16)&0xff;
                int g =( argb>>8)&0xff;
                int b =argb&0xff;
                int a =(argb>>24)&0xff;
                System.out.println("a的值:"+a);
                int rgb =((a*256+r) * 256 + g) * 256 + b;
                bitmap2.setPixel(j , i , rgb);
            }
        return  bitmap2;
    }
}
