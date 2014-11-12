package cn.com.pplo.sicauhelper.dao;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.List;

/**
 * Created by winson on 2014/11/9.
 */
public interface BaseDAO<T> {
    public void save(T t, SaveCallback callback);
    public void update(T t);
    public void delete(long id);
    public T toModel(AVObject avObject);
}
