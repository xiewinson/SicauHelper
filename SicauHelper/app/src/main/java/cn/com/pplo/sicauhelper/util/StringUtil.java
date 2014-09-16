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

import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.model.Student;

/**
 * Created by winson on 2014/9/14.
 */
public class StringUtil {
    /**
     * 用正则表达式取得密匙
     * @param htmlStr
     * @return
     */
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

    /**
     * 得到加密后的字符串
     * @param dcode
     * @param pswd
     * @return
     */
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

    /**
     * 从个人主页中获取学生资料
     * @param htmlStr
     * @return
     */
    public static Student parseStudentInfo(String htmlStr) {
        Student student = null;
        try {
            student = new Student();
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
        }catch (Exception e){
            student = null;
        }
        return student;
    }

    /**
     * 解析成绩信息
     * @param htmlStr
     * @return
     */
    public static List<Score> parseScoreInfo(String htmlStr){
        List<Score> scores = new ArrayList<Score>();
        try {
            Document document = Jsoup.parse(htmlStr);
            Elements courseElements = document.select("td[width=20%] > font");
            Elements elements = document.select("td[width=5%] > font");
            for (int i = 0; i < courseElements.size() - 2; i++){
                Score score = new Score();
                score.setId(i + 1);
                score.setCourse(courseElements.get(i + 2).text());
                score.setMark(elements.get(i * 5 + 6 + 0).text());
                score.setCredit(Float.parseFloat(elements.get(i * 5 + 6 + 1).text()));
                score.setCategory(elements.get(i * 5 + 6 + 2).text());
//                score.setYear(Integer.parseInt(elements.get(i * 5 + 6 + 3).text()));
                score.setGrade(Float.parseFloat(Integer.parseInt(elements.get(i * 5 + 6 + 3).text()) + "." + Integer.parseInt(elements.get(i * 5 + 6 + 4).text())));
                scores.add(score);
            }
        }catch (Exception e){
            Log.d("winson", "解析成绩信息出错：" + e.getMessage());
            scores = null;
        }
        finally {
            return scores;
        }
    }

}
