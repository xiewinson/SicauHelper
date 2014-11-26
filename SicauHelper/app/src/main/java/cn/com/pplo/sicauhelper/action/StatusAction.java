package cn.com.pplo.sicauhelper.action;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;

import cn.com.pplo.sicauhelper.provider.TableContract;

/**
 * Created by winson on 2014/11/12.
 */
public class StatusAction {

    /**
     * 在缓存中查询指定校区的goods列表
     * @param callback
     */
    public void findInCacheThenNetwork(FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableStatus.TABLE_NAME);
        avQuery.setCachePolicy(AVQuery.CachePolicy.CACHE_THEN_NETWORK);
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending(TableContract.TableStatus._STATUS_ID);
        avQuery.include(TableContract.TableStatus._USER);
        avQuery.findInBackground(callback);
    }

    /**
     * 查询指定校区的最新goods列表
     * @param callback
     */
    public void findNewData(FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableStatus.TABLE_NAME);
        avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);

        //若有缓存则清空
        if(avQuery.hasCachedResult()) {
            avQuery.clearCachedResult();
        }
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending(TableContract.TableStatus._STATUS_ID);
        avQuery.include(TableContract.TableStatus._USER);
        avQuery.findInBackground(callback);
    }

    /**
     * 查询指定校区的从指定id开始的goods列表
     * @param callback
     */
    public void findSinceId(long status_id, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableStatus.TABLE_NAME);
        avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
        //小于指定id
        avQuery.whereLessThan(TableContract.TableStatus._STATUS_ID, status_id);
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending(TableContract.TableStatus._STATUS_ID);
        avQuery.include(TableContract.TableStatus._USER);
        avQuery.findInBackground(callback);
    }

    /**
     * 查询指定objectId对应的goods
     * @param callback
     */
    public void findByObjectId(AVQuery.CachePolicy cachePolicy, String objectId, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableStatus.TABLE_NAME);
        avQuery.setCachePolicy(cachePolicy);
        avQuery.whereEqualTo(TableContract.TableStatus._OBJECTID, objectId);
        avQuery.include(TableContract.TableStatus._USER);
        avQuery.findInBackground(callback);
    }


    //TODO 需要使用CQL查询匹配用户昵称查询
    /**
     * 查询指定校区的匹配字符串的最新goods列表
     * @param callback
     */
    public void findDataByTitle(String str, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableStatus.TABLE_NAME);
        avQuery.whereContains(TableContract.TableStatus._TITLE, str);
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending(TableContract.TableStatus._STATUS_ID);
        avQuery.include(TableContract.TableStatus._USER);
        avQuery.findInBackground(callback);
    }

    /**
     * 查询指定校区的匹配字符串从指定id开始的goods列表
     * @param callback
     */
    public void findDataByTitleSinceId(String str, long status_id, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableStatus.TABLE_NAME);
        avQuery.whereContains(TableContract.TableStatus._TITLE, str);

        //小于指定id
        avQuery.whereLessThan(TableContract.TableStatus._STATUS_ID, status_id);
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending(TableContract.TableStatus._STATUS_ID);
        //选校区
        avQuery.include(TableContract.TableStatus._USER);
        avQuery.findInBackground(callback);
    }


    /**
     * 查询指定人的最新goods列表
     * @param objectId
     * @param callback
     */
    public void findNewDataByUser(String objectId, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableStatus.TABLE_NAME);
        avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
        //选人
        avQuery.whereEqualTo(TableContract.TableStatus._USER,  AVUser.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));

        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending(TableContract.TableStatus._STATUS_ID);

        avQuery.include(TableContract.TableStatus._USER);
        avQuery.findInBackground(callback);
    }

    /**
     * 查询指定人的从指定id开始的status列表
     * @param objectId
     * @param callback
     */
    public void findDataByUserSinceId(String objectId, long status_id, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableStatus.TABLE_NAME);

        //小于指定id
        avQuery.whereLessThan(TableContract.TableStatus._STATUS_ID, status_id);
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending(TableContract.TableStatus._STATUS_ID);
        //选人
        avQuery.whereEqualTo(TableContract.TableStatus._USER, AVUser.createWithoutData(TableContract.TableUser._NAME, objectId));
        avQuery.include(TableContract.TableStatus._USER);
        avQuery.findInBackground(callback);
    }

    /**
     * 删除
     */
    public void delete(AVObject avStatus, DeleteCallback callback) {
//        List<AVFile> avFiles = ImageUtil.getAVFileListByAVObject(avStatus);
        //删除关联图片,暂时不用。AVFile的文件存储没有上限，并且删除并不太方便
//        for (AVFile avFile : avFiles) {
//            if(avFile != null) {
//                avFile.deleteInBackground(new DeleteCallback() {
//                    @Override
//                    public void done(AVException e) {
//                        if(e != null) {
//                            Log.d("winson", "删除图片失败：" + e.getMessage());
//                        }
//                        else {
//                            Log.d("winson", "删除图片成功");
//                        }
//                    }
//                });
//            }
//        }
        //删除帖子相关评论
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableStatusComment.TABLE_NAME);
        avQuery.whereEqualTo(TableContract.TableStatusComment._STATUS, avStatus);
        avQuery.deleteAllInBackground(new DeleteCallback() {
            @Override
            public void done(AVException e) {
                if(e == null) {
                    Log.d("winson", "删除帖子相关评论成功");
                }
                else {
                    Log.d("winson", "删除帖子相关评论失败：" + e.getMessage());
                }
            }
        });
        //删除帖子
        avStatus.deleteInBackground(callback);
    }
}
