package cn.com.pplo.sicauhelper.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.R;

/**
 * Created by Administrator on 2014/11/7.
 */
public class ImageUtil {

    public static final int REQUEST_CODE_PICK_IMAGE = 1991;
    public static final int REQUEST_CODE_CAPTURE_IMAGE = 1992;
    public static final int REQUEST_CODE_CROP_IMAGE = 1993;

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
     * 通过AVFile list取得图片url数组
     * @return
     */
    public static String[] getImageUrlsByAVFileList(List<AVFile> avFiles) {
        String[] result = new String[avFiles.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = avFiles.get(i).getUrl();
        }
        return result;
    }

    /**
     * 取得图片文件夹位置
     * @param context
     * @return
     */
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

    /**
     * 取得AVFile
     * @param context
     * @param pathList
     * @return
     * @throws IOException
     */
    public static List<AVFile> getAVFileList(Context context, List<String> pathList) throws IOException {
       List<AVFile> avFiles = new ArrayList<AVFile>();
        for (String path : pathList) {
           avFiles.add(AVFile.withAbsoluteLocalPath(SharedPreferencesUtil.get(context, SharedPreferencesUtil.LOGIN_SID, "") + "_" + System.currentTimeMillis() + ".jpg",
                   path));
       }
        return avFiles;
    }

    /**
     * 取得AVGoods中存放的图片
     * @param avObject
     * @return
     */
    public static List<AVFile> getAVFileListByAVObject(AVObject avObject) {
        List<AVFile> result = new ArrayList<AVFile>();
        AVFile imageFile0 = avObject.getAVFile("image0");
        AVFile imageFile1 = avObject.getAVFile("image1");
        AVFile imageFile2 = avObject.getAVFile("image2");
        AVFile imageFile3 = avObject.getAVFile("image3");
        if(imageFile0 != null) {
            result.add(imageFile0);
        }
        if(imageFile1 != null) {
            result.add(imageFile1);
        }
        if(imageFile2 != null) {
            result.add(imageFile2);
        }
        if(imageFile3 != null) {
            result.add(imageFile3);
        }
        return result;
    }

    /**
     * 裁剪图片
     * @param activity
     * @param imageUri
     * @return
     */
    public static void cropImage(Activity activity, Uri imageUri, String resultPath) {
        //创建图片文件
        File resultFile = new File(resultPath);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 4);
        intent.putExtra("aspectY", 3);
        intent.putExtra("scale", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 800 );
        intent.putExtra("outputY", 600);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(resultFile));
        activity.startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }

    public static abstract class SaveImageCallback extends SaveCallback {

        public Context context;

        protected SaveImageCallback(Context context) {
            this.context = context;
        }

        @Override
        public void done(AVException e) {
            if(e == null) {
                onSuccess();
            }
            else {
                Log.d("winson", "图片出错：" + e.getMessage() + "           ");
                e.printStackTrace();
                UIUtil.showShortToast(context, "图片上传出错");
            }
        }

        public abstract void onSuccess();
    }

    public interface OnImageUploadFinishListener {
        public void onFinish(List<AVFile> avFiles);
    }

}
