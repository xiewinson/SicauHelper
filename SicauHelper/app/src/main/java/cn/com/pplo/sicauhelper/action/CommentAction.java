package cn.com.pplo.sicauhelper.action;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;

import cn.com.pplo.sicauhelper.provider.TableContract;

/**
 * Created by winson on 2014/11/16.
 */
public class CommentAction {

    public static final int COMMENT_GOODS = 888;
    public static final int COMMENT_STATUS = 999;

    /**
     * 在缓存中查询指定商品的评论列表
     *
     * @param callback
     */
    public void findInCacheThenNetwork(int type, String objectId, FindCallback callback) {
        AVQuery<AVObject> avQuery = null;
        if(type == COMMENT_GOODS) {
            avQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
            avQuery.whereEqualTo(TableContract.TableGoodsComment._GOODS, AVObject.createWithoutData(TableContract.TableGoods.TABLE_NAME, objectId));
            //id降序
            avQuery.orderByAscending(TableContract.TableGoodsComment._GOODS_COMMENT_ID);
            //取出关联的对象
            avQuery.include(TableContract.TableGoodsComment._GOODS);
            avQuery.include(TableContract.TableGoodsComment._SEND_USER);
            avQuery.include(TableContract.TableGoodsComment._RECEIVE_USER);
        }
        else if(type == COMMENT_STATUS) {
            avQuery = new AVQuery<AVObject>(TableContract.TableStatusComment.TABLE_NAME);
            avQuery.whereEqualTo(TableContract.TableStatusComment._STATUS, AVObject.createWithoutData(TableContract.TableStatus.TABLE_NAME, objectId));
            //id降序
            avQuery.orderByAscending(TableContract.TableStatusComment._STATUS_COMMENT_ID);
            //取出关联的对象
            avQuery.include(TableContract.TableStatusComment._STATUS);
            avQuery.include(TableContract.TableStatusComment._SEND_USER);
            avQuery.include(TableContract.TableStatusComment._RECEIVE_USER);
        }
        avQuery.setCachePolicy(AVQuery.CachePolicy.CACHE_THEN_NETWORK);
        //取10条
        avQuery.setLimit(10);
        avQuery.findInBackground(callback);
    }

    /**
     * 查询指定商品的最新评论列表
     *
     * @param callback
     */
    public void findNewData(int type, String objectId, FindCallback callback) {
        AVQuery<AVObject> avQuery = null;
        if(type == COMMENT_GOODS) {
            avQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
            avQuery.whereEqualTo(TableContract.TableGoodsComment._GOODS, AVObject.createWithoutData(TableContract.TableGoods.TABLE_NAME, objectId));
            //id降序
            avQuery.orderByAscending(TableContract.TableGoodsComment._GOODS_COMMENT_ID);
            //取出关联的对象
            avQuery.include(TableContract.TableGoodsComment._GOODS);
            avQuery.include(TableContract.TableGoodsComment._SEND_USER);
            avQuery.include(TableContract.TableGoodsComment._RECEIVE_USER);
        }
        else if(type == COMMENT_STATUS) {
            avQuery = new AVQuery<AVObject>(TableContract.TableStatusComment.TABLE_NAME);
            avQuery.whereEqualTo(TableContract.TableStatusComment._STATUS, AVObject.createWithoutData(TableContract.TableStatus.TABLE_NAME, objectId));
            //id降序
            avQuery.orderByAscending(TableContract.TableStatusComment._STATUS_COMMENT_ID);
            //取出关联的对象
            avQuery.include(TableContract.TableStatusComment._STATUS);
            avQuery.include(TableContract.TableStatusComment._SEND_USER);
            avQuery.include(TableContract.TableStatusComment._RECEIVE_USER);
        }
        avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
        //若有缓存则清空
        if (avQuery.hasCachedResult()) {
            avQuery.clearCachedResult();
        }
        //取10条
        avQuery.setLimit(10);
        avQuery.findInBackground(callback);
    }

    /**
     * 查询评论的从指定id开始的评论列表
     *
     * @param callback
     */
    public void findSinceId(int type, String objectId, long comment_id, FindCallback callback) {
        AVQuery<AVObject> avQuery = null;
        if(type == COMMENT_GOODS) {
            avQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
            avQuery.whereEqualTo(TableContract.TableGoodsComment._GOODS, AVObject.createWithoutData(TableContract.TableGoods.TABLE_NAME, objectId));
            //小于指定id
            avQuery.whereGreaterThan(TableContract.TableGoodsComment._GOODS_COMMENT_ID, comment_id);
            //id降序
            avQuery.orderByAscending(TableContract.TableGoodsComment._GOODS_COMMENT_ID);
            //取出关联的对象
            avQuery.include(TableContract.TableGoodsComment._GOODS);
            avQuery.include(TableContract.TableGoodsComment._SEND_USER);
            avQuery.include(TableContract.TableGoodsComment._RECEIVE_USER);
        }
        else if(type == COMMENT_STATUS) {
            avQuery = new AVQuery<AVObject>(TableContract.TableStatusComment.TABLE_NAME);
            avQuery.whereEqualTo(TableContract.TableStatusComment._STATUS, AVObject.createWithoutData(TableContract.TableStatus.TABLE_NAME, objectId));
            //小于指定id
            avQuery.whereGreaterThan(TableContract.TableStatusComment._STATUS_COMMENT_ID, comment_id);
            //id降序
            avQuery.orderByAscending(TableContract.TableStatusComment._STATUS_COMMENT_ID);
            //取出关联的对象
            avQuery.include(TableContract.TableStatusComment._STATUS);
            avQuery.include(TableContract.TableStatusComment._SEND_USER);
            avQuery.include(TableContract.TableStatusComment._RECEIVE_USER);
        }

        avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
        //取10条
        avQuery.setLimit(10);

        avQuery.findInBackground(callback);
    }

    /**
     * 统计评论数量
     *
     * @param avGoods
     * @param callback
     */
    public void count(int type, AVObject avGoods, CountCallback callback) {
        AVQuery<AVObject> avQuery = null;
        //商品评论
        if(type == COMMENT_GOODS) {
            avQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
            avQuery.whereEqualTo(TableContract.TableGoodsComment._GOODS, avGoods);
        }
        else if(type == COMMENT_STATUS) {
            avQuery = new AVQuery<AVObject>(TableContract.TableStatusComment.TABLE_NAME);
            avQuery.whereEqualTo(TableContract.TableStatusComment._STATUS, avGoods);
        }


        avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
        avQuery.countInBackground(callback);
    }

    /**
     * 删除指定评论
     *
     * @param avComment
     */
    public void delete(AVObject avComment, DeleteCallback callback) {
        avComment.deleteInBackground(callback);
    }
}
