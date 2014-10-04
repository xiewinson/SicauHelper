package cn.com.pplo.sicauhelper.util;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.pplo.sicauhelper.model.Course;
import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.provider.TableContract;

/**
 * Created by winson on 2014/9/22.
 */
public class CursorUtil {

    /**
     * 成绩表
     * @param cursor
     * @return
     */
    public static List<Score> parseScoreList(Cursor cursor){
        List<Score> list = new ArrayList<Score>();
        try {
            while(cursor.moveToNext()){
                Score score = new Score();
                score.setCategory(cursor.getString(cursor.getColumnIndex(TableContract.TableScore._CATEGORY)));
                score.setCourse(cursor.getString(cursor.getColumnIndex(TableContract.TableScore._COURSE)));
                score.setCredit(cursor.getFloat(cursor.getColumnIndex(TableContract.TableScore._CREDIT)));
                score.setGrade(cursor.getFloat(cursor.getColumnIndex(TableContract.TableScore._GRADE)));
                score.setId(cursor.getInt(cursor.getColumnIndex(TableContract.TableScore._ID)));
                score.setMark(cursor.getString(cursor.getColumnIndex(TableContract.TableScore._MARK)));
                list.add(score);
            }
        }catch (Exception e){
            list.clear();
        }
        finally {
//            List<Score> aaa = new ArrayList<Score>();
//            for(int i = 0; i < 4; i++){
//                aaa.add(list.get(i));
//            }
            return list;
        }
    }

    /**
     * 课程信息
     * @param cursor
     * @return
     */
    public static List<Course> parseCourseList(Cursor cursor, int position){
        List<Course> list = new ArrayList<Course>();
        try {
            while(cursor.moveToNext()){
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

                String posStr = (position + 1) + "";
                if(course.getTime().contains(posStr + "-")){
                    String time = course.getTime();
                    String classroom = course.getClassroom();
                    String[] timeArray = time.split("\\s+");
                    String[] classroomArray = classroom.split("\\s+");
                    for(int i = 0; i < timeArray.length; i++){
                        if(timeArray[i].contains(posStr + "-")){
                            course.setTime(timeArray[i].replace(posStr + "-", "").replaceAll(",", "-"));
                            course.setClassroom(classroomArray[i]);
                        }
                    }
                    list.add(course);
                }
            }
        }catch (Exception e){
            list.clear();
        }
        finally {
            Collections.sort(list);
            return list;
        }
    }

}
