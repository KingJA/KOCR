package lib.kingja.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 项目名称：物联网城市防控(警用版)
 * 类描述：SD卡管理类
 * 创建人：KingJA
 * 创建时间：2016/5/13 8:50
 * 修改备注：
 */
public class OCRUtil {
    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static File getFile(String dirPath, String fileName, Context context) {
        File mLogDir;
        if (isSDCardAvailable()) {
            mLogDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()  +File.separator+ dirPath);
        } else {
            mLogDir = new File(context.getFilesDir().getAbsolutePath() +File.separator+  dirPath);
        }
        if (!mLogDir.exists()) {
            mLogDir.mkdirs();
        }
        return new File(mLogDir.getAbsolutePath()+File.separator+fileName);
    }

    public static String getFilePath(String dirPath, String fileName, String extName,Context context) {
        File mLogDir;
        if (isSDCardAvailable()) {
            mLogDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()  +File.separator+ dirPath);
        } else {
            mLogDir = new File(context.getFilesDir().getAbsolutePath() +File.separator+  dirPath);
        }
        if (!mLogDir.exists()) {
            mLogDir.mkdirs();
        }
        return mLogDir.getAbsolutePath()+File.separator+fileName+"."+extName;
    }



    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     * 比例压缩
     *
     * @return
     */
    public static Bitmap compressScaleFromF2B(byte[] data) {
        Log.i("比例压缩", "比例压缩");
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        //newOpts.inJustDecodeBounds = true只加载图片尺寸，获取原始尺寸
        BitmapFactory.decodeByteArray(data, 0, data.length,newOpts);
        int imgWidth = newOpts.outWidth;
        int imgHeight = newOpts.outHeight;
        Log.i("outWidth", imgWidth + "");
        Log.i("outHeight", imgHeight + "");
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float scaleWidth =1280 / 4;
        float scaleHeight = 720 / 4;
        Log.i("scaleWidth", scaleWidth + "");
        Log.i("scaleHeight", scaleHeight + "");
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放,越大表示缩得越小
        if (imgWidth > imgHeight && imgWidth > scaleWidth) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / scaleWidth);
            Log.i("if be", be + "");
        } else if (imgWidth < imgHeight && imgHeight > scaleHeight) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / scaleHeight);
            Log.i("else be", be + "");
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        newOpts.inJustDecodeBounds = false;
        // newOpts.inJustDecodeBounds = false 真实加载图片
        Bitmap  bitmap =  BitmapFactory.decodeByteArray(data, 0, data.length,newOpts);
        return bitmap;
    }


    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 50&&options>0) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            Log.i("压缩", "compressImage: " + baos.toByteArray().length / 1024);
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

}
