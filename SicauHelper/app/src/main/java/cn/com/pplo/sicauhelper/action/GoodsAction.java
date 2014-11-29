package cn.com.pplo.sicauhelper.action;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;

import java.util.List;

import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.util.ImageUtil;

/**
 * Created by winson on 2014/11/12.
 */
public class GoodsAction {

    /**
     * 查询指定校区的最新goods列表
     * @param school
     * @param callback
     */
    public void findNewData(AVQuery.CachePolicy cachePolicy, int school, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableGoods.TABLE_NAME);
        avQuery.setCachePolicy(cachePolicy);

        //若有缓存则清空
        if(avQuery.hasCachedResult()) {
            avQuery.clearCachedResult();
        }
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending(TableContract.TableGoods._GOODS_ID);
        //选校区
        avQuery.whereEqualTo(TableContract.TableGoods._SCHOOL, school);
        avQuery.include(TableContract.TableGoods._USER);
        avQuery.findInBackground(callback);
    }

    /**
     * 查询指定校区的从指定id开始的goods列表
     * @param school
     * @param callback
     */
    public void findSinceId(int school, long goods_id, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableGoods.TABLE_NAME);
        avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
        //小于指定id
        avQuery.whereLessThan(TableContract.TableGoods._GOODS_ID, goods_id);
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending(TableContract.TableGoods._GOODS_ID);
        //选校区
        avQuery.whereEqualTo(TableContract.TableGoods._SCHOOL, school);
        avQuery.include(TableContract.TableGoods._USER);
        avQuery.findInBackground(callback);
    }

    /**
     * 查询指定objectId对应的goods
     * @param callback
     */
    public void findByObjectId(AVQuery.CachePolicy cachePolicy, String objectId, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableGoods.TABLE_NAME);
        avQuery.setCachePolicy(cachePolicy);
        avQuery.whereEqualTo(TableContract.TableGoods._OBJECTID, objectId);
        avQuery.include(TableContract.TableGoods._USER);
        avQuery.findInBackground(callback);
    }


    //TODO 需要使用CQL查询匹配用户昵称查询
    /**
     * 查询指定校区的匹配字符串的最新goods列表
     * @param school
     * @param callback
     */
    public void findDataByTitle(int school, String str, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableGoods.TABLE_NAME);
        avQuery.whereContains(TableContract.TableGoods._TITLE, str);
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending(TableContract.TableGoods._GOODS_ID);
        //选校区
        avQuery.whereEqualTo(TableContract.TableGoods._SCHOOL, school);
        avQuery.include(TableContract.TableGoods._USER);
        avQuery.findInBackground(callback);
    }

    /**
     * 查询指定校区的匹配字符串从指定id开始的goods列表
     * @param school
     * @param callback
     */
    public void findDataByTitleSinceId(int school, String str, long goods_id, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableGoods.TABLE_NAME);
        avQuery.whereContains(TableContract.TableGoods._TITLE, str);

        //小于指定id
        avQuery.whereLessThan(TableContract.TableGoods._GOODS_ID, goods_id);
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending(TableContract.TableGoods._GOODS_ID);
        //选校区
        avQuery.whereEqualTo(TableContract.TableGoods._SCHOOL, school);
        avQuery.include(TableContract.TableGoods._USER);
        avQuery.findInBackground(callback);
    }


    /**
     * 查询指定人的最新goods列表
     * @param objectId
     * @param callback
     */
    public void findNewDataByUser(AVQuery.CachePolicy cachePolicy, String objectId, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableGoods.TABLE_NAME);
        avQuery.setCachePolicy(cachePolicy);
        //选人
        avQuery.whereEqualTo(TableContract.TableGoods._USER,  AVUser.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));

        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending(TableContract.TableGoods._GOODS_ID);

        avQuery.include(TableContract.TableGoods._USER);
        avQuery.findInBackground(callback);
    }

    /**
     * 查询指定人的从指定id开始的goods列表
     * @param objectId
     * @param callback
     */
    public void findDataByUserSinceId(String objectId, long goods_id, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableGoods.TABLE_NAME);

        //小于指定id
        avQuery.whereLessThan(TableContract.TableGoods._GOODS_ID, goods_id);
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending(TableContract.TableGoods._GOODS_ID);
        //选人
        avQuery.whereEqualTo(TableContract.TableGoods._USER, AVUser.createWithoutData(TableContract.TableUser._NAME, objectId));
        avQuery.include(TableContract.TableGoods._USER);
        avQuery.findInBackground(callback);
    }

    /**
     * 删除
     */
    public void delete(AVObject avGoods, DeleteCallback callback) {
//        List<AVFile> avFiles = ImageUtil.getAVFileListByAVObject(avGoods);
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
        //删除商品相关评论
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
        avQuery.whereEqualTo(TableContract.TableGoodsComment._GOODS, avGoods);

            avQuery.deleteAllInBackground(new DeleteCallback() {
                @Override
                public void done(AVException e) {
                    if(e == null) {
                        Log.d("winson", "删除商品相关评论成功");
                    }
                    else {
                        Log.d("winson", "删除商品相关评论失败：" + e.getMessage());
                    }
                }
            });

        //删除商品
        avGoods.deleteInBackground(callback);
    }


    /**
     * 计算指定人的商品数量
     * @param objectId
     * @param callback
     */
    public void countGoodsByUser(String objectId, CountCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableGoods.TABLE_NAME);
        avQuery.setCachePolicy(AVQuery.CachePolicy.CACHE_THEN_NETWORK);
        //指定人
        avQuery.whereEqualTo(TableContract.TableGoods._USER,  AVUser.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
        avQuery.countInBackground(callback);
    }
}
