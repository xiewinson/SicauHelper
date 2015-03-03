package cn.com.pplo.sicauhelper.util;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.pplo.sicauhelper.model.Classroom;
import cn.com.pplo.sicauhelper.model.Course;
import cn.com.pplo.sicauhelper.model.Exam;
import cn.com.pplo.sicauhelper.model.News;
import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.provider.TableContract;

/**
 * Created by winson on 2014/9/22.
 */
public class CursorUtil {

    /**
     * 成绩表
     *
     * @param cursor
     * @return
     */
    public static List<Score> parseScoreList(Cursor cursor) {
        List<Score> list = new ArrayList<Score>();
        try {
            while (cursor.moveToNext()) {
                Score score = new Score();
                score.setCategory(cursor.getString(cursor.getColumnIndex(TableContract.TableScore._CATEGORY)));
                score.setCourse(cursor.getString(cursor.getColumnIndex(TableContract.TableScore._COURSE)));
                score.setCredit(cursor.getFloat(cursor.getColumnIndex(TableContract.TableScore._CREDIT)));
                score.setGrade(cursor.getFloat(cursor.getColumnIndex(TableContract.TableScore._GRADE)));
                score.setId(cursor.getInt(cursor.getColumnIndex(TableContract.TableScore._ID)));
                score.setMark(cursor.getString(cursor.getColumnIndex(TableContract.TableScore._MARK)));
                list.add(score);
            }
        } catch (Exception e) {
            list.clear();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            return list;
        }
    }

    /**
     * 课程信息
     *
     * @param cursor
     * @return
     */
    public static List<List<Course>> parseCourseList(Cursor cursor) {
        List<List<Course>> data = new ArrayList<List<Course>>();
        List<Course> list0 = new ArrayList<Course>();
        List<Course> list1 = new ArrayList<Course>();
        List<Course> list2 = new ArrayList<Course>();
        List<Course> list3 = new ArrayList<Course>();
        List<Course> list4 = new ArrayList<Course>();
        List<Course> list5 = new ArrayList<Course>();
        List<Course> list6 = new ArrayList<Course>();
        try {
            while (cursor.moveToNext()) {
                Course course = new Course();
                course.setCategory(cursor.getString(cursor.getColumnIndex(TableContract.TableCourse._CATEGORY)));
                course.setName(cursor.getString(cursor.getColumnIndex(TableContract.TableCourse._NAME)));
                course.setCredit(cursor.getFloat(cursor.getColumnIndex(TableContract.TableCourse._CREDIT)));
                course.setTime(cursor.getString(cursor.getColumnIndex(TableContract.TableCourse._TIME)));
                course.setTeacher(cursor.getString(cursor.getColumnIndex(TableContract.TableCourse._TEACHER)));
                course.setClassroom(cursor.getString(cursor.getColumnIndex(TableContract.TableCourse._CLASSROOM)));
                course.setId(cursor.getInt(cursor.getColumnIndex(TableContract.TableCourse._ID)));
                course.setScheduleNum(cursor.getInt(cursor.getColumnIndex(TableContract.TableCourse._SCHEDULENUM)));
                course.setSelectedNum(cursor.getInt(cursor.getColumnIndex(TableContract.TableCourse._SELECTNUM)));
                course.setWeek(cursor.getString(cursor.getColumnIndex(TableContract.TableCourse._WEEK)));

                for (int position = 0; position < 7; position++) {
                    Course course1 = course.clone();
                    String posStr = (position + 1) + "";

                    String time = course.getTime();
                    if (time.contains("单")) {
                        course1.setWeek(course.getWeek() + "单");
                    } else if (time.contains("双")) {
                        course1.setWeek(course.getWeek() + "双");
                    }

                    if (course.getTime().contains(posStr + "-")) {

                        String classroom = course.getClassroom();
                        String[] timeArray = time.split("\\s+");
                        String[] classroomArray = classroom.split("\\s+");
                        for (int i = 0; i < timeArray.length; i++) {
                            if (timeArray[i].contains(posStr + "-")) {
                                Course newCourse = course1.clone();
                                String result = timeArray[i].replace(posStr + "-", "").replaceAll(",", "-").replace("(单)", "").replace("(双)", "");
                                newCourse.setTime(result);
                                newCourse.setClassroom(classroomArray[i]);
                                switch (position) {
                                    case 0:
                                        list0.add(newCourse);
                                        break;
                                    case 1:
                                        list1.add(newCourse);
                                        break;
                                    case 2:
                                        list2.add(newCourse);
                                        break;
                                    case 3:
                                        list3.add(newCourse);
                                        break;
                                    case 4:
                                        list4.add(newCourse);
                                        break;
                                    case 5:
                                        list5.add(newCourse);
                                        break;
                                    case 6:
                                        list6.add(newCourse);
                                        break;
                                }
                            }
                        }

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
        } catch (Exception e) {
            data.clear();
            Log.d("winson", "解析出错：" + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            return data;
        }
    }


    /**
     * 从数据库解析实验课表
     *
     * @param cursor
     * @return
     */
    public static List<List<Course>> parseLabCourseList(Cursor cursor) {
        List<List<Course>> data = new ArrayList<List<Course>>();
        List<Course> list0 = new ArrayList<Course>();
        List<Course> list1 = new ArrayList<Course>();
        List<Course> list2 = new ArrayList<Course>();
        List<Course> list3 = new ArrayList<Course>();
        List<Course> list4 = new ArrayList<Course>();
        List<Course> list5 = new ArrayList<Course>();
        List<Course> list6 = new ArrayList<Course>();

        try {
            while (cursor.moveToNext()) {
                Course course = new Course();
                course.setCategory(cursor.getString(cursor.getColumnIndex(TableContract.TableLabCourse._CATEGORY)));
                course.setName(cursor.getString(cursor.getColumnIndex(TableContract.TableLabCourse._NAME)));
                course.setCredit(cursor.getFloat(cursor.getColumnIndex(TableContract.TableLabCourse._CREDIT)));
                course.setTime(cursor.getString(cursor.getColumnIndex(TableContract.TableLabCourse._TIME)));
                course.setTeacher(cursor.getString(cursor.getColumnIndex(TableContract.TableLabCourse._TEACHER)));
                course.setClassroom(cursor.getString(cursor.getColumnIndex(TableContract.TableLabCourse._CLASSROOM)));
                course.setId(cursor.getInt(cursor.getColumnIndex(TableContract.TableLabCourse._ID)));
                course.setScheduleNum(cursor.getInt(cursor.getColumnIndex(TableContract.TableLabCourse._SCHEDULENUM)));
                course.setSelectedNum(cursor.getInt(cursor.getColumnIndex(TableContract.TableLabCourse._SELECTNUM)));
                course.setWeek(cursor.getString(cursor.getColumnIndex(TableContract.TableLabCourse._WEEK)));
                switch (Integer.parseInt(course.getCategory())) {
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
        } catch (Exception e) {
            e.printStackTrace();
            data.clear();
        } finally {
            return data;
        }
    }

    /**
     * 解析新闻cursor
     *
     * @param cursor
     * @return
     */
    public static List<News> parseNewsList(Cursor cursor) {
        List<News> list = new ArrayList<News>();
        try {
            while (cursor.moveToNext()) {
                News news = new News();
                news.setCategory(cursor.getString(cursor.getColumnIndex(TableContract.TableNews._CATEGORY)));
                news.setTitle(cursor.getString(cursor.getColumnIndex(TableContract.TableNews._TITLE)));
                news.setUrl(cursor.getString(cursor.getColumnIndex(TableContract.TableNews._URL)));
                news.setDate(cursor.getString(cursor.getColumnIndex(TableContract.TableNews._DATE)));
                news.setId(cursor.getInt(cursor.getColumnIndex(TableContract.TableNews._ID)));
                news.setContent(cursor.getString(cursor.getColumnIndex(TableContract.TableNews._CONTENT)));
                news.setSrc(cursor.getString(cursor.getColumnIndex(TableContract.TableNews._SRC)));
                list.add(news);
            }
        } catch (Exception e) {
            list.clear();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            return list;
        }
    }

    /**
     * 解析空闲教室cursor
     *
     * @param cursor
     * @return
     */
    public static List<Classroom> parseClassroomList(Cursor cursor) {
        List<Classroom> list = new ArrayList<Classroom>();
        try {
            while (cursor.moveToNext()) {
                Classroom classroom = new Classroom();
                classroom.setId(cursor.getInt(cursor.getColumnIndex(TableContract.TableClassroom._ID)));
                classroom.setTime(cursor.getString(cursor.getColumnIndex(TableContract.TableClassroom._TIME)));
                classroom.setSchool(cursor.getString(cursor.getColumnIndex(TableContract.TableClassroom._SCHOOL)));
                classroom.setName(cursor.getString(cursor.getColumnIndex(TableContract.TableClassroom._NAME)));
                Log.d("winson", "取出时：" + classroom);
                list.add(classroom);
            }
        } catch (Exception e) {
            list.clear();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            return list;
        }
    }

    public static List<Exam> parseExamList(Cursor cursor) {
        List<Exam> list = new ArrayList<Exam>();
        try {
            while (cursor.moveToNext()) {
                Exam exam = new Exam();
                exam.setId(cursor.getInt(cursor.getColumnIndex(TableContract.TableExam._ID)));
                exam.setTime(cursor.getString(cursor.getColumnIndex(TableContract.TableExam._TIME)));
                exam.setClassroom(cursor.getString(cursor.getColumnIndex(TableContract.TableExam._CLASSROOM)));
                exam.setCourse(cursor.getString(cursor.getColumnIndex(TableContract.TableExam._COURSE)));
                exam.setNum(cursor.getString(cursor.getColumnIndex(TableContract.TableExam._NUM)));
                Log.d("winson", "取出时：" + exam);
                list.add(exam);
            }
        } catch (Exception e) {
            list.clear();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            return list;
        }
    }
}
