package cn.com.pplo.sicauhelper.util;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.pplo.sicauhelper.model.Student;

/**
 * Created by winson on 2014/9/14.
 */
public class StringUtil {
    //用正则表达式取得密匙
    public static String getDcode(String htmlStr) {
        String result = "";
        Pattern pattern = Pattern.compile("dcode2=[0-9]+");
        if (htmlStr != null && !htmlStr.equals("")) {
            Matcher matcher = pattern.matcher(htmlStr);
            if (matcher.find()) {
                result = matcher.group().replace("dcode2=", "");
            }
        }
        return result;
    }

    //得到加密后的字符串
    public static String encodePswd(String dcode, String pswd) {
        String result = "";
        BigInteger dcodeInteger = new BigInteger(dcode);
        dcodeInteger = dcodeInteger.multiply(new BigInteger("137"));
        String dcodeString = dcodeInteger.toString();
        String tmpstr = "";
        for (int i = 1; i <= pswd.length(); i++) {
            tmpstr = pswd.substring(i - 1, i);
            result += (char) ((int) tmpstr.charAt(0) - i - Integer.parseInt(dcodeString.substring(i - 1, i)));
        }
        return result;
    }

    //从个人主页中获取学生资料
    public static Student getStudentInfo(String htmlStr) {
        Student student = new Student();
        List<String> list = new ArrayList<String>();
        //使用正则表达式进行解析
//        Pattern pattern = Pattern.compile("<td width=\"99\" align=\"left\">[\u4e00-\u9fa5]+</td>");
//        Matcher matcher = pattern.matcher(htmlStr);
//        String sid = "没有";
//        if (matcher.find()){
//            sid = matcher.group();
//
//        }
//        Log.d("winson", "学号：" + sid);
        //使用jsoup进行解析
        Log.d("winson", "开始：" + System.currentTimeMillis());
        Document document = Jsoup.parse(htmlStr);
        Elements elements = document.select("td");
        for (Element e : elements) {
            if (!e.children().hasText() && e.hasText()) {
                String text = e.text().replaceAll("\\s", "");
                if (text != null && !text.trim().equals("")) {
                    list.add(text);
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            String currentText = list.get(i);
            if(currentText.contains("姓名")){
                student.setName(list.get(i + 1));
            }
            else if(currentText.contains("身份")){
                student.setRole(list.get(i + 1));
            }
            else if(currentText.contains("所属校区")){
                student.setSchool(list.get(i + 1));
            }
        }
        Log.d("winson", "结束：" + System.currentTimeMillis() + "      " + list);
        return student;
    }
}
