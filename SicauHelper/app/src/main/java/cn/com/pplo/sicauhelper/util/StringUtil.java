package cn.com.pplo.sicauhelper.util;

import android.os.AsyncTask;
import android.text.TextUtils;
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

import cn.com.pplo.sicauhelper.model.Classroom;
import cn.com.pplo.sicauhelper.model.Course;
import cn.com.pplo.sicauhelper.model.News;
import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.model.ScoreStats;
import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.provider.TableContract;

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
        } catch (Exception e) {
            Log.d("winson", "encodePswd error..................");
        } finally {
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
        new AsyncTask<String, Integer, List<Score>>() {

            @Override
            protected List<Score> doInBackground(String... params) {
                List<Score> scores = new ArrayList<Score>();
                try {
                    Document document = Jsoup.parse(params[0]);
                    Elements courseElements = document.select("td[width=20%] > font");
                    for (Element e : courseElements) {
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
     *
     * @param scoreList
     * @return
     */
    public static List<ScoreStats> parseScoreStatsList(List<Score> scoreList) {

        List<Integer> years = new ArrayList<Integer>();
        if (scoreList != null && scoreList.size() > 0) {
            for (int i = 0; i < scoreList.size(); i++) {
                Score currentScore = scoreList.get(i);
                String currentGrade = ((currentScore.getGrade() + "").split("\\."))[0];
                if (!currentScore.getCourse().contains("军训")) {
                    if (i == 0) {
                        years.add(Integer.parseInt(currentGrade));
                    } else {
                        String lastGrade = ((scoreList.get(i - 1).getGrade() + "").split("\\."))[0];
                        if (lastGrade.equals(currentGrade)) {
                            years.add(Integer.parseInt(currentGrade));
                        }
                    }
                }
            }
        }
        Log.d("winson", "结果：" + years);
        Set set = new HashSet<Integer>();
        set.addAll(years);
        years.clear();
        years.addAll(set);
        Collections.sort(years);


        List<ScoreStats> list = new ArrayList<ScoreStats>();
        for (int i = 0; i < years.size(); i++) {
            int mustNumCount = 0;
            int choiceNumCount = 0;
            float mustScoreCount = 0;
            float choiceScoreCount = 0;
            float mustCreditCount = 0;
            float choiceCreditCount = 0;
            ScoreStats scoreStats = new ScoreStats();
            for (int j = 0; j < scoreList.size(); j++) {
                Score currentScore = scoreList.get(j);
                if (!currentScore.getCourse().contains("军训") && (currentScore.getGrade() + "").contains(years.get(i) + "")) {
                    if (currentScore.getCategory().equals("必修") || currentScore.getCategory().equals("实践")) {
                        mustNumCount++;
                        mustCreditCount += currentScore.getCredit();
                        mustScoreCount += (Float.parseFloat(currentScore.getMark()) * currentScore.getCredit());
                    } else if (currentScore.getCategory().equals("公选") || currentScore.getCategory().equals("推选") || currentScore.getCategory().equals("任选")) {
                        choiceNumCount++;
                        choiceCreditCount += currentScore.getCredit();
                        choiceScoreCount += (Float.parseFloat(currentScore.getMark()) * currentScore.getCredit()); // * currentScore.getCredit()
                    }
                }
                ;
            }
            scoreStats.setYear(String.valueOf(years.get(i)));
            scoreStats.setMustNum(mustNumCount);
            scoreStats.setChoiceNum(choiceNumCount);

            if (mustCreditCount == 0) {
                mustCreditCount = 1;
            }
            ;
            scoreStats.setMustAvgScore((float) (Math.round(mustScoreCount * 100 / mustCreditCount) / 100.0));

            if (choiceCreditCount == 0) {
                choiceCreditCount = 1;
                choiceNumCount = 1;
            }
            scoreStats.setChoiceAvgScore((float) (Math.round(choiceScoreCount * 100 / choiceCreditCount) / 100.0));

            scoreStats.setMustCredit(mustCreditCount);
            scoreStats.setChoiceCredit(choiceCreditCount);
            list.add(scoreStats);
        }
        return list;
    }

    /**
     * 解析课程表信息
     *
     * @param htmlStr
     */
    public static List<Course> parseCourseInfo(String htmlStr) {
        List<Course> list = new ArrayList<Course>();
        try {
            Document document = Jsoup.parse(htmlStr);
            Elements courseElements = document.select("td[width=200]");
//            for (int i = 0; i < courseElements.size(); i++) {
//                Log.d("winson", "课程编号：" + i + "_____课程名：" + courseElements.get(i).text().trim());
//            }
            ;

            Elements someElements = document.select("td[width=100]");
//            for (int i = 0; i < someElements.size(); i++) {
//                Log.d("winson", "编号：" + i + "_____杂乱：" + someElements.get(i).text().trim());
//            }

            Elements creditElements = document.select("td[width=40]");
//            for (int i = 0; i < creditElements.size(); i++) {
//                Log.d("winson", "编号：" + i + "_____学分：" + creditElements.get(i).text().trim());
//            }

            Elements weekElements = document.select("td[width=60]");
//            for (int i = 0; i < weekElements.size(); i++) {
//                Log.d("winson", "编号：" + i + "_____周期：" + weekElements.get(i).text().trim());
//            }

            Elements teacherElements = document.select("td[width=80]");
//            for (int i = 0; i < teacherElements.size(); i++) {
//                Log.d("winson", "编号：" + i + "_____教师：" + teacherElements.get(i).text().trim());
//            }

            Elements numElements = document.select("td[width=50]");
//            for (int i = 0; i < numElements.size(); i++) {
//                Log.d("winson", "编号：" + i + "_____人数：" + numElements.get(i).text().trim());
//            }

            Log.d("winson", "-------------------------------------------------------------------");

            //去掉课程名的第一个无用的
            courseElements.remove(0);
            for (int i = 0; i < courseElements.size(); i++) {
                Course course = new Course();
                //添加课程名
                course.setName(courseElements.get(i).text().toString().replaceAll("\\s", "").replaceAll("[0-9]{3,10}", ""));

                //添加课程性质
                String category = someElements.get(i * 3 + 7).text().toString().replaceAll("\\s", "");
                if (category.equals("公共选修课")) {
                    category = "公选";
                } else if (category.equals("推荐选修课")) {
                    category = "推选";
                } else if (category.equals("其他选修课")) {
                    category = "任选";
                } else if (category.equals("专业段任意选修课")) {
                    category = "任选";
                } else if (category.equals("实践教学")) {
                    category = "实践";
                }
                course.setCategory(category);

                //添加课程时间
                course.setTime(someElements.get(i * 3 + 7 + 1).text().toString());
                //添加教室
                course.setClassroom(someElements.get(i * 3 + 7 + 2).text().toString());

                //添加学分
                course.setCredit(Float.parseFloat(creditElements.get(i * 4 + 4 + 1).text().toString().replaceAll("\\s", "")));

                //添加周期
                course.setWeek(weekElements.get(i * 2 + 1 + 1).text().toString().replaceAll("\\s", ""));

                //添加教师
                course.setTeacher(teacherElements.get(i * 2 + 2).text().toString().replaceAll("\\s", ""));

                //添加计划人数
                course.setScheduleNum(Integer.parseInt(numElements.get(i * 4 + 4 + 2).text().toString().replaceAll("\\s", "")));
                //添加实际人数
                course.setScheduleNum(Integer.parseInt(numElements.get(i * 4 + 4 + 3).text().toString().replaceAll("\\s", "")));

                list.add(course);
            }
            ;
        } catch (Exception e) {
            Log.d("winson", "解析课程表失败");
            list = null;
        } finally {
            Log.d("winson", list + "");
            return list;
        }
    }


    /**
     * 解析得出每日的课程列表
     *
     * @param list
     * @return
     */
    public static List<List<Course>> parseCourseDateInfo(List<Course> list) {
        List<List<Course>> data = new ArrayList<List<Course>>();
        List<Course> list0 = new ArrayList<Course>();
        List<Course> list1 = new ArrayList<Course>();
        List<Course> list2 = new ArrayList<Course>();
        List<Course> list3 = new ArrayList<Course>();
        List<Course> list4 = new ArrayList<Course>();
        List<Course> list5 = new ArrayList<Course>();
        List<Course> list6 = new ArrayList<Course>();
        try {
            for (Course item : list) {


                for (int position = 0; position < 7; position++) {

                    Course course = item.clone();
                    String posStr = (position + 1) + "";
                    if (item.getTime().contains(posStr + "-")) {
                        String time = item.getTime();
                        String classroom = item.getClassroom();
                        String[] timeArray = time.split("\\s+");
                        String[] classroomArray = classroom.split("\\s+");
                        for (int i = 0; i < timeArray.length; i++) {
                            if (timeArray[i].contains(posStr + "-")) {
                                course.setTime(timeArray[i].replace(posStr + "-", "").replaceAll(",", "-"));
                                course.setClassroom(classroomArray[i]);
                            }
                        }
                        switch (position) {
                            case 0:
                                list0.add(course);
                                break;
                            case 1:
                                list1.add(course);
                                break;
                            case 2:
                                list2.add(course);
                                break;
                            case 3:
                                list3.add(course);
                                break;
                            case 4:
                                list4.add(course);
                                break;
                            case 5:
                                list5.add(course);
                                break;
                            case 6:
                                list6.add(course);
                                break;
                        }
                    }
                }
                Collections.sort(list0);
                Collections.sort(list1);
                Collections.sort(list2);
                Collections.sort(list3);
                Collections.sort(list4);
                Collections.sort(list5);
                Collections.sort(list6);
                data.add(list0);
                data.add(list1);
                data.add(list2);
                data.add(list3);
                data.add(list4);
                data.add(list5);
                data.add(list6);
            }
        } catch (Exception e) {
            data.clear();
            Log.d("winson", "解析出错：" + e);
        } finally {

            return data;
        }
    }

    /**
     * 解析新闻列表
     *
     * @param htmlStr
     * @return
     */
    public static List<News> parseNewsListInfo(String htmlStr) {
        List<News> list = new ArrayList<News>();
        try {
            Document document = Jsoup.parse(htmlStr);
            Elements aElements = document.select("a.body");
            for (Element e : aElements) {
                Log.d("winson", "文字----   " + e.text());
                Log.d("winson", "url----   " + e.attr("href"));
            }
            Elements cElements = document.select("font[color=gray]");

            List<String> dateList = new ArrayList<String>();
            Pattern pattern = Pattern.compile("nbsp[\\S]{1}[\\S]{1,2}[\\S]{1}[0-9]{1,2}[\\S]{1}");
            Matcher matcher = pattern.matcher(htmlStr);
            while (matcher.find()) {
                dateList.add(matcher.group().replace("nbsp(", ""));
            }


            for (int i = 0; i < aElements.size(); i++) {
                News news = new News();
                String categoryStr = cElements.get(i).text();
                news.setCategory((categoryStr.split("-"))[0].replace("[", ""));
                news.setTitle(aElements.get(i).text());
                String urlStr = aElements.get(i).attr("href");
                news.setUrl("http://jiaowu.sicau.edu.cn/web/web/web/" + urlStr);
                news.setContent("");
                news.setSrc("");
                news.setDate(dateList.get(i));
                news.setId(Integer.parseInt((urlStr.split("="))[1]));
                list.add(news);
            }
        } catch (Exception e) {
            list = null;
            Log.d("winson", "解析错误： " + e);
        }
        Log.d("winson", "news----   " + list);
        return list;
    }

    /**
     * 解析新闻内容
     *
     * @param htmlStr
     * @return
     */
    public static String parseNewsInfo(String htmlStr) {
        String result = "";
        try {
            Document document = Jsoup.parse(htmlStr);
            Elements pElements = null;
            pElements = document.select("p");
            if (pElements.size() < 1) {
                pElements = document.select("td[vAlign=top]");
            }
            StringBuilder sb = new StringBuilder();
            for (Element e : pElements) {
                String str = e.text();
                sb.append(str + "\n");
            }
            result = sb.toString();
        } catch (Exception e) {
            result = "";
            Log.d("winson", "解析错误： " + e);
        }
        return result;
    }

    /**
     * 处理新闻内容
     *
     * @param htmlStr
     * @return
     */
    public static void handleNewsHtmlStr(String htmlStr, final Callback callback) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... params) {
                String result = "";
                try {
                    Document document = Jsoup.parse(params[0]);
                    Elements pElements = document.select("table[width=800]");
                    StringBuilder sb = new StringBuilder();
                    for (Element e : pElements) {
                        String str = e.html();
                        sb.append(str + "\n");
                    }
                    result = sb.toString();

                } catch (Exception e) {
                    result = "";
                    Log.d("winson", "解析错误： " + e);
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                callback.handleParseResult(s);
            }
        }.execute(htmlStr);
    }

    /**
     * 最恶心的一个，解析空闲教室那网页
     * @param htmlStr
     * @param callback
     */
    public static void parseClassroomListHtmlStr(String htmlStr, final Callback callback) {
        new AsyncTask<String, Void, List<Classroom>>() {

            @Override
            protected List<Classroom> doInBackground(String... params) {
                List<Classroom> list = new ArrayList<Classroom>();

                Document document = Jsoup.parse(params[0]);
                Elements pElements = document.select("p");
                int count = 0;
                for (Element p : pElements) {
                    String[] array = p.html().split("<br />");
                    for (int i = 0; i < array.length; i++) {
                        String[] strs = array[i].split("&nbsp;&nbsp;");

                        for (int j = 0; j < strs.length; j++) {
                            String str = strs[j].trim();
                            if (!TextUtils.isEmpty(str) && !str.contains("font") && !str.contains("空教室")){
                                Classroom classroom = new Classroom();
                                if (count == 0) {
                                    classroom.setName(str);
                                    classroom.setSchool("雅安");
                                    classroom.setTime("今天上午");
                                } else if (count == 1) {
                                    classroom.setName(str);
                                    classroom.setSchool("雅安");
                                    classroom.setTime("今天下午");
                                } else if (count == 2) {
                                    classroom.setName(str);
                                    classroom.setSchool("雅安");
                                    classroom.setTime("今天晚上");
                                } else if (count == 3) {
                                    classroom.setName(str);
                                    classroom.setSchool("雅安");
                                    classroom.setTime("明天上午");
                                } else if (count == 4) {
                                    classroom.setName(str);
                                    classroom.setSchool("雅安");
                                    classroom.setTime("明天下午");
                                } else if (count == 5) {
                                    classroom.setName(str);
                                    classroom.setSchool("雅安");
                                    classroom.setTime("明天晚上");
                                } else if (count == 6) {
                                    classroom.setName(str);
                                    classroom.setSchool("成都");
                                    classroom.setTime("今天上午");
                                } else if (count == 7) {
                                    classroom.setName(str);
                                    classroom.setSchool("成都");
                                    classroom.setTime("今天下午");
                                } else if (count == 8) {
                                    classroom.setName(str);
                                    classroom.setSchool("成都");
                                    classroom.setTime("今天晚上");
                                } else if (count == 9) {
                                    classroom.setName(str);
                                    classroom.setSchool("成都");
                                    classroom.setTime("明天上午");
                                } else if (count == 10) {
                                    classroom.setName(str);
                                    classroom.setSchool("成都");
                                    classroom.setTime("明天下午");
                                } else if (count == 11)

                                {
                                    classroom.setName(str);
                                    classroom.setSchool("成都");
                                    classroom.setTime("明天晚上");
                                } else if (count == 12)

                                {
                                    classroom.setName(str);
                                    classroom.setSchool("都江堰");
                                    classroom.setTime("今天上午");
                                } else if (count == 13)

                                {
                                    classroom.setName(str);
                                    classroom.setSchool("都江堰");
                                    classroom.setTime("今天下午");
                                } else if (count == 14)

                                {
                                    classroom.setName(str);
                                    classroom.setSchool("都江堰");
                                    classroom.setTime("今天晚上");
                                } else if (count == 15) {
                                    classroom.setName(str);
                                    classroom.setSchool("都江堰");
                                    classroom.setTime("明天上午");
                                } else if (count == 16) {
                                    classroom.setName(str);
                                    classroom.setSchool("都江堰");
                                    classroom.setTime("明天下午");
                                } else if (count == 17) {
                                    classroom.setName(str);
                                    classroom.setSchool("都江堰");
                                    classroom.setTime("明天晚上");
                                }
                                list.add(classroom);
                            }

                            }
                    }
                    count++;
                }
                return list;
            }

            @Override
            protected void onPostExecute(List<Classroom> classrooms) {
                super.onPostExecute(classrooms);
                callback.handleParseResult(classrooms);
            }
        }.execute(htmlStr);
    }


    public interface Callback {
        public void handleParseResult(Object obj);
    }

}
