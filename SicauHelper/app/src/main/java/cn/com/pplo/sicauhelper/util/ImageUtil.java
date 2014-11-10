package cn.com.pplo.sicauhelper.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedVignetteBitmapDisplayer;

import java.io.File;

import cn.com.pplo.sicauhelper.R;

/**
 * Created by Administrator on 2014/11/7.
 */
public class ImageUtil {

    /**
     * 取得展示图片选项
     * @return
     */
    public static DisplayImageOptions getDisplayImageOption(Context context) {
        DisplayImageOptions options = null;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.ic_launcher)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ic_launcher)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .build();//构建完成
        return options;
    }

    /**
     * 通过uri取得图片path
     * @param context
     * @param uri
     * @return
     */
    public static String imageUriToPath(Context context, Uri uri) {
        String result = "";
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            return result;
        }
    }

    public static String getImageFolderPath(Context context) {
        String packPath = "";
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            packPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        }
        else {
            packPath = context.getFilesDir() + File.separator + "Pictures";
        }
        return packPath;
    }

}
