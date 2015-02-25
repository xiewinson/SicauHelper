package cn.com.pplo.sicauhelper.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by winson on 2014/11/15.
 */
public class TimeUtil {
    /**
     * Date转换为友好时间
     * @param time
     * @return
     */
    public static String timeToFriendlyTime(String time){

        String result = "";
        String timeStr = "";
        Date date = new Date();

        long longTime = 0;
        long nowLongTime = date.getTime();
        SimpleDateFormat format = null;
        if(time.contains("格林尼治标准时间")){
            format = new SimpleDateFormat("EEE MMM dd HH:mm:ss 格林尼治标准时间+0800 yyyy", Locale.US);
        }
        else {
            format = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.US);
        }

        try {
            date = format.parse(time);
            timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("时间错误");

        } finally {
            longTime = date.getTime();
        }

        long distanceSeconds = (nowLongTime - longTime)/1000;
        if(distanceSeconds <= 60){
            result = "刚刚";
        }
        else if(distanceSeconds > 60 && distanceSeconds <= 60 * 60){
            result = distanceSeconds/60 + "分钟前";
        }
        else if(distanceSeconds > 60 * 60 && distanceSeconds <= 24 * 60 * 60){
            result = distanceSeconds/60/60 + "小时前";
        }
        else if(distanceSeconds > 24 * 60 * 60 && distanceSeconds <=  11 * 24 * 60 * 60){
            result = distanceSeconds/60/60/24 + "天前";
        }
        else {
            result = timeStr;
        }
        return result;
    }

    /**
     * 取得xxxx-11-11 xx:xx:xx
     * @param time
     * @return
     */
//    public static String timeToCommentTime(String time){
//
//        String timeStr = "";
//        Date date = new Date();
//
//        long longTime = 0;
//        long nowLongTime = date.getTime();
//
//        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.US);
//
//        try {
//            date = format.parse(time);
//            timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//            timeStr = "";
//            System.out.println("时间错误");
//
//        } finally {
//            return timeStr;
//        }
//    }

    /**
     * 时间戳转换为年:月:日:小时:分钟时间
     * @return
     */
    public static String dateToChinaTime(Date date) {
        return new java.text.SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(date);
    }

    /**
     * 时间戳转换为年-月-日
     * @param time
     * @return
     */
    public static String dateToYMD(long time) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date(time));
    }
}
