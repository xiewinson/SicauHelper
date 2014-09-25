package cn.com.pplo.sicauhelper.util;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.provider.TableContract;

/**
 * Created by winson on 2014/9/22.
 */
public class CursorUtil {
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
}
