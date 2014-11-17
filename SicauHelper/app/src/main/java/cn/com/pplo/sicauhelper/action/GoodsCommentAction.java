package cn.com.pplo.sicauhelper.action;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;

import cn.com.pplo.sicauhelper.provider.TableContract;

/**
 * Created by winson on 2014/11/16.
 */
public class GoodsCommentAction {


    /**
     * 在缓存中查询指定商品的评论列表
     * @param callback
     */
    public void findInCacheThenNetwork(String objectId, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
        avQuery.setCachePolicy(AVQuery.CachePolicy.CACHE_THEN_NETWORK);
        avQuery.whereEqualTo(TableContract.TableGoodsComment._GOODS,  AVObject.createWithoutData(TableContract.TableGoods.TABLE_NAME, objectId));
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByAscending(TableContract.TableGoodsComment._GOODS_COMMENT_ID);
        //取出关联的对象
        avQuery.include(TableContract.TableGoodsComment._GOODS);
        avQuery.include(TableContract.TableGoodsComment._SEND_USER);
        avQuery.include(TableContract.TableGoodsComment._RECEIVE_USER);

        avQuery.findInBackground(callback);
    }

    /**
     * 查询指定商品的最新评论列表
     * @param callback
     */
    public void findNewData(String objectId, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
        avQuery.whereEqualTo(TableContract.TableGoodsComment._GOODS,  AVObject.createWithoutData(TableContract.TableGoods.TABLE_NAME, objectId));

        avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);

        //若有缓存则清空
        if(avQuery.hasCachedResult()) {
            avQuery.clearCachedResult();
        }
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByAscending(TableContract.TableGoodsComment._GOODS_COMMENT_ID);

        //取出关联的对象
        avQuery.include(TableContract.TableGoodsComment._GOODS);
        avQuery.include(TableContract.TableGoodsComment._SEND_USER);
        avQuery.include(TableContract.TableGoodsComment._RECEIVE_USER);

        avQuery.findInBackground(callback);
    }

    /**
     * 查询评论的从指定id开始的评论列表
     * @param callback
     */
    public void findSinceId(String objectId, long comment_id, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
        avQuery.whereEqualTo(TableContract.TableGoodsComment._GOODS, AVObject.createWithoutData(TableContract.TableGoods.TABLE_NAME, objectId));

        avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
        //小于指定id
        avQuery.whereGreaterThan(TableContract.TableGoodsComment._GOODS_COMMENT_ID, comment_id);
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByAscending(TableContract.TableGoodsComment._GOODS_COMMENT_ID);

        //取出关联的对象
        avQuery.include(TableContract.TableGoodsComment._GOODS);
        avQuery.include(TableContract.TableGoodsComment._SEND_USER);
        avQuery.include(TableContract.TableGoodsComment._RECEIVE_USER);

        avQuery.findInBackground(callback);
    }
    
    public void count(AVObject avGoods, CountCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
        avQuery.whereEqualTo(TableContract.TableGoodsComment._GOODS, avGoods);
        avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
        avQuery.countInBackground(callback);
    }
}
