package cn.com.pplo.sicauhelper.util;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.model.ScoreStats;
import cn.com.pplo.sicauhelper.model.Student;

/**
 * Created by winson on 2014/9/14.
 */
public class StringUtil {
    /**
     * 用正则表达式取得密匙
     *
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
     *
     * @param dcode
     * @param pswd
     * @return
     */
    public static String encodePswd(String dcode, String pswd) {
        String result = "";
        try {

            BigInteger dcodeInteger = new BigInteger(dcode);
            dcodeInteger = dcodeInteger.multiply(new BigInteger("137"));
            String dcodeString = dcodeInteger.toString();
            String tmpstr = "";
            for (int i = 1; i <= pswd.length(); i++) {
                tmpstr = pswd.substring(i - 1, i);
                result += (char) ((int) tmpstr.charAt(0) - i - Integer.parseInt(dcodeString.substring(i - 1, i)));
            }
        } catch (Exception e){
            Log.d("winson", "encodePswd error..................");
        }
        finally {
            return result;
        }

    }

    /**
     * 从个人主页中获取学生资料
     *
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
                if (currentText.contains("姓名")) {
                    student.setName(list.get(i + 1));
                } else if (currentText.contains("身份")) {
                    student.setRole(list.get(i + 1));
                } else if (currentText.contains("所属校区")) {
                    student.setSchool(list.get(i + 1));
                }
            }
            Log.d("winson", "结束：" + System.currentTimeMillis() + "      " + list);
        } catch (Exception e) {
            student = null;
        }
        return student;
    }

    /**
     * 解析成绩信息
     *
     * @param htmlStr
     * @return
     */
    public static void parseScoreInfo(String htmlStr, final Callback callback) {
        new AsyncTask<String, Integer, List<Score>>(){

            @Override
            protected List<Score> doInBackground(String... params) {
                List<Score> scores = new ArrayList<Score>();
                try {
                    Document document = Jsoup.parse(params[0]);
                    Elements courseElements = document.select("td[width=20%] > font");
                    for (Element e : courseElements){
                        Log.d("winson", e.text());
                    }
                    Elements elements = document.select("td[width=5%] > font");
                    for (int i = 0; i < courseElements.size(); i++) {
                        Score score = new Score();
                        score.setId(i + 1);
                        score.setCourse(courseElements.get(i).text());
                        score.setMark(elements.get(i * 5 + 6 + 0).text());
                        score.setCredit(Float.parseFloat(elements.get(i * 5 + 6 + 1).text()));
                        score.setCategory(elements.get(i * 5 + 6 + 2).text());
//                score.setYear(Integer.parseInt(elements.get(i * 5 + 6 + 3).text()));
                        score.setGrade(Float.parseFloat(Integer.parseInt(elements.get(i * 5 + 6 + 3).text()) + "." + Integer.parseInt(elements.get(i * 5 + 6 + 4).text())));
                        scores.add(score);
                    }
                } catch (Exception e) {
                    Log.d("winson", "解析成绩信息出错：" + e.getMessage());
                    scores = null;
                } finally {
                    return scores;
                }
            }

            @Override
            protected void onPostExecute(List<Score> tempList) {
                super.onPostExecute(tempList);
                callback.handleParseResult(tempList);
            }
        }.execute(htmlStr);
    }

    /**
     * 从成绩信息中算出成绩统计
     * @param scoreList
     * @return
     */
    public static List<ScoreStats> parseScoreStatsList(List<Score> scoreList){

        List<Integer> years = new ArrayList<Integer>();
        if(scoreList != null && scoreList.size() > 0){
            for (int i = 0; i < scoreList.size(); i++){
                Score currentScore = scoreList.get(i);
                String currentGrade = ((currentScore.getGrade() + "").split("\\."))[0];
                if(!currentScore.getCourse().contains("军训")){
                    if(i == 0){
                        years.add(Integer.parseInt(currentGrade));
                    }
                    else {
                        String lastGrade = ((scoreList.get(i - 1).getGrade() + "").split("\\."))[0];
                        if(lastGrade.equals(currentGrade)){
                            years.add(Integer.parseInt(currentGrade));
                        }
                    }
                }
            }
        }
        Log.d("winson", "结果：" +  years);
        Set set = new HashSet<Integer>();
        set.addAll(years);
        years.clear();
        years.addAll(set);
        Collections.sort(years);


        List<ScoreStats> list = new ArrayList<ScoreStats>();
        for(int i = 0; i < years.size(); i++){
            int mustNumCount = 0;
            int choiceNumCount = 0;
            float mustScoreCount = 0;
            float choiceScoreCount = 0;
            float mustCreditCount = 0;
            float choiceCreditCount = 0;
            ScoreStats scoreStats = new ScoreStats();
            for (int j = 0; j < scoreList.size(); j++) {
                Score currentScore = scoreList.get(j);
                if(!currentScore.getCourse().contains("军训")&&(currentScore.getGrade() + "").contains(years.get(i) + "")){
                    if(currentScore.getCategory().equals("必修") || currentScore.getCategory().equals("实践")){
                        mustNumCount ++;
                        mustCreditCount += currentScore.getCredit();
                        mustScoreCount += (Float.parseFloat(currentScore.getMark()) * currentScore.getCredit());
                    }
                    else if (currentScore.getCategory().equals("公选") || currentScore.getCategory().equals("推选") || currentScore.getCategory().equals("任选")){
                        choiceNumCount ++;
                        choiceCreditCount += currentScore.getCredit();
                        choiceScoreCount += (Float.parseFloat(currentScore.getMark()) * currentScore.getCredit()); // * currentScore.getCredit()
                    }
                };
            }
            scoreStats.setYear(String.valueOf(years.get(i)));
            scoreStats.setMustNum(mustNumCount);
            scoreStats.setChoiceNum(choiceNumCount);

            if(mustCreditCount == 0 ){
                mustCreditCount = 1;
            }
            scoreStats.setMustAvgScore(mustScoreCount/mustCreditCount);

            if(choiceCreditCount == 0 ){
                choiceCreditCount = 1;
                choiceNumCount = 1;
            }
            scoreStats.setChoiceAvgScore(choiceScoreCount/choiceCreditCount);

            scoreStats.setMustCredit(mustCreditCount);
            scoreStats.setChoiceCredit(choiceCreditCount);
            list.add(scoreStats);
        }
        return list;
    }

    public interface Callback {
        public void handleParseResult(List<Score> scores);
    }

}
