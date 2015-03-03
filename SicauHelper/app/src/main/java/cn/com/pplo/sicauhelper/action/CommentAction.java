package cn.com.pplo.sicauhelper.action;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.util.SharedPreferencesUtil;

/**
 * Created by winson on 2014/11/16.
 */
public class CommentAction {

    public static final int COMMENT_GOODS = 888;
    public static final int COMMENT_STATUS = 999;

    /**
     * 评论类型
     */
    public static final int GOODS_SEND_COMMENT = 1001;
    public static final int GOODS_RECEIVE_COMMENT = 1002;
    public static final int STATUS_SEND_COMMENT = 1003;
    public static final int STATUS_RECEIVE_COMMENT = 1004;


    /**
     * 在缓存中查询指定商品的评论列表
     *
     * @param callback
     */
    public void findNewDataByObjectId(Context context, AVQuery.CachePolicy cachePolicy, int type, String objectId, FindCallback callback) {
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
        avQuery.setCachePolicy(cachePolicy);
        //取(Integer) SharedPreferencesUtil.get(context, SharedPreferencesUtil.PER_COMMENT_COUNT, 10)条
        avQuery.setLimit((Integer) SharedPreferencesUtil.get(context, SharedPreferencesUtil.PER_COMMENT_COUNT, 10));
        avQuery.findInBackground(callback);
    }


    /**
     * 查询评论的从指定id开始的评论列表
     *
     * @param callback
     */
    public void findSinceId(Context context, int type, String objectId, long comment_id, FindCallback callback) {
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
        //取(Integer) SharedPreferencesUtil.get(context, SharedPreferencesUtil.PER_COMMENT_COUNT, 10)条
        avQuery.setLimit((Integer) SharedPreferencesUtil.get(context, SharedPreferencesUtil.PER_COMMENT_COUNT, 10));

        avQuery.findInBackground(callback);
    }

    /**
     * 查询指定类型的评论最新列表
     *
     * @param callback
     */
    public void findNewDataByType(Context context, AVQuery.CachePolicy cachePolicy, int type, String objectId, FindCallback callback) {
        AVQuery<AVObject> avQuery = null;
        if(type == GOODS_SEND_COMMENT) {
            avQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
            avQuery.whereEqualTo(TableContract.TableGoodsComment._SEND_USER, AVUser.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
            avQuery.whereNotEqualTo(TableContract.TableGoodsComment._GOODS, null);
            //id降序
            avQuery.orderByDescending(TableContract.TableGoodsComment._GOODS_COMMENT_ID);
            //取出关联的对象
            avQuery.include(TableContract.TableGoodsComment._GOODS);
            avQuery.include(TableContract.TableGoodsComment._SEND_USER);
            avQuery.include(TableContract.TableGoodsComment._RECEIVE_USER);
        }
        else if(type == GOODS_RECEIVE_COMMENT) {

            //别人@我的评论
            AVQuery<AVObject> mentionAvQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
            mentionAvQuery.whereEqualTo(TableContract.TableGoodsComment._RECEIVE_USER, AVUser.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
            mentionAvQuery.whereNotEqualTo(TableContract.TableGoodsComment._GOODS, null);


            //别人给我的商品发出的评论
            AVQuery<AVObject> goodsCommentQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
            AVQuery<AVObject> innerQuery = new AVQuery<>(TableContract.TableGoods.TABLE_NAME);
            innerQuery.whereEqualTo(TableContract.TableGoods._USER, AVObject.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
            goodsCommentQuery.whereNotEqualTo(TableContract.TableGoodsComment._SEND_USER, AVObject.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
            goodsCommentQuery.whereMatchesQuery(TableContract.TableGoodsComment._GOODS, innerQuery);

            List<AVQuery<AVObject>> queries = new ArrayList<AVQuery<AVObject>>();
            queries.add(mentionAvQuery);
            queries.add(goodsCommentQuery);

            avQuery = AVQuery.or(queries);
            //id降序
            avQuery.orderByDescending(TableContract.TableGoodsComment._GOODS_COMMENT_ID);
            //取出关联的对象
            avQuery.include(TableContract.TableGoodsComment._GOODS);
            avQuery.include(TableContract.TableGoodsComment._SEND_USER);
            avQuery.include(TableContract.TableGoodsComment._RECEIVE_USER);
        }
        else if(type == STATUS_SEND_COMMENT) {
            avQuery = new AVQuery<AVObject>(TableContract.TableStatusComment.TABLE_NAME);
            avQuery.whereEqualTo(TableContract.TableStatusComment._SEND_USER, AVUser.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
            avQuery.whereNotEqualTo(TableContract.TableStatusComment._STATUS, null);
            //id降序
            avQuery.orderByDescending(TableContract.TableStatusComment._STATUS_COMMENT_ID);
            //取出关联的对象
            avQuery.include(TableContract.TableStatusComment._STATUS);
            avQuery.include(TableContract.TableStatusComment._SEND_USER);
            avQuery.include(TableContract.TableStatusComment._RECEIVE_USER);
        }
        else if(type == STATUS_RECEIVE_COMMENT) {

            //别人@我的评论
            AVQuery<AVObject> mentionAvQuery = new AVQuery<AVObject>(TableContract.TableStatusComment.TABLE_NAME);
            mentionAvQuery.whereEqualTo(TableContract.TableStatusComment._RECEIVE_USER, AVUser.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
            mentionAvQuery.whereNotEqualTo(TableContract.TableStatusComment._STATUS, null);

            //别人给我的商品发出的评论
            AVQuery<AVObject> statusCommentQuery = new AVQuery<AVObject>(TableContract.TableStatusComment.TABLE_NAME);
            AVQuery<AVObject> innerQuery = new AVQuery<>(TableContract.TableStatus.TABLE_NAME);
            innerQuery.whereEqualTo(TableContract.TableStatus._USER, AVObject.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
            statusCommentQuery.whereNotEqualTo(TableContract.TableStatusComment._SEND_USER, AVObject.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
            statusCommentQuery.whereMatchesQuery(TableContract.TableStatusComment._STATUS, innerQuery);

            List<AVQuery<AVObject>> queries = new ArrayList<AVQuery<AVObject>>();
            queries.add(mentionAvQuery);
            queries.add(statusCommentQuery);

            avQuery = AVQuery.or(queries);
            //id降序
            avQuery.orderByDescending(TableContract.TableGoodsComment._GOODS_COMMENT_ID);
            //取出关联的对象
            avQuery.include(TableContract.TableGoodsComment._GOODS);
            avQuery.include(TableContract.TableGoodsComment._SEND_USER);
            avQuery.include(TableContract.TableGoodsComment._RECEIVE_USER);
        }
        avQuery.setCachePolicy(cachePolicy);

        //取(Integer) SharedPreferencesUtil.get(context, SharedPreferencesUtil.PER_COMMENT_COUNT, 10)条
        avQuery.setLimit((Integer) SharedPreferencesUtil.get(context, SharedPreferencesUtil.PER_COMMENT_COUNT, 10));
        avQuery.findInBackground(callback);
    }

    /**
     * 查询评论的从指定id指定类型开始的评论列表
     *
     * @param callback
     */
    public void findSinceIdByType(Context context, int type, String objectId, long comment_id, FindCallback callback) {
        AVQuery<AVObject> avQuery = null;
        if(type == GOODS_SEND_COMMENT) {
            avQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
            avQuery.whereEqualTo(TableContract.TableGoodsComment._SEND_USER, AVUser.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
            avQuery.whereNotEqualTo(TableContract.TableGoodsComment._GOODS, null);
            //小于指定id
            avQuery.whereLessThan(TableContract.TableGoodsComment._GOODS_COMMENT_ID, comment_id);
            //id降序
            avQuery.orderByDescending(TableContract.TableGoodsComment._GOODS_COMMENT_ID);
            //取出关联的对象
            avQuery.include(TableContract.TableGoodsComment._GOODS);
            avQuery.include(TableContract.TableGoodsComment._SEND_USER);
            avQuery.include(TableContract.TableGoodsComment._RECEIVE_USER);
        }
        else if(GOODS_RECEIVE_COMMENT == type) {

            //别人@我的评论
            AVQuery<AVObject> mentionAvQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
            mentionAvQuery.whereEqualTo(TableContract.TableGoodsComment._RECEIVE_USER, AVUser.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
            mentionAvQuery.whereNotEqualTo(TableContract.TableGoodsComment._GOODS, null);


            //别人给我的商品发出的评论
            AVQuery<AVObject> goodsCommentQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
            AVQuery<AVObject> innerQuery = new AVQuery<>(TableContract.TableGoods.TABLE_NAME);
            innerQuery.whereEqualTo(TableContract.TableGoods._USER, AVObject.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
            goodsCommentQuery.whereNotEqualTo(TableContract.TableGoodsComment._SEND_USER, AVObject.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
            goodsCommentQuery.whereMatchesQuery(TableContract.TableGoodsComment._GOODS, innerQuery);

            List<AVQuery<AVObject>> queries = new ArrayList<AVQuery<AVObject>>();
            queries.add(mentionAvQuery);
            queries.add(goodsCommentQuery);

            avQuery = AVQuery.or(queries);
            //小于指定id
            avQuery.whereLessThan(TableContract.TableGoodsComment._GOODS_COMMENT_ID, comment_id);
            //id降序
            avQuery.orderByDescending(TableContract.TableGoodsComment._GOODS_COMMENT_ID);
            //取出关联的对象
            avQuery.include(TableContract.TableGoodsComment._GOODS);
            avQuery.include(TableContract.TableGoodsComment._SEND_USER);
            avQuery.include(TableContract.TableGoodsComment._RECEIVE_USER);
        }
        else if(type == STATUS_SEND_COMMENT) {
            avQuery = new AVQuery<AVObject>(TableContract.TableStatusComment.TABLE_NAME);
            avQuery.whereEqualTo(TableContract.TableStatusComment._SEND_USER, AVUser.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
            avQuery.whereNotEqualTo(TableContract.TableStatusComment._STATUS, null);
            //小于指定id
            avQuery.whereLessThan(TableContract.TableStatusComment._STATUS_COMMENT_ID, comment_id);
            //id降序
            avQuery.orderByDescending(TableContract.TableStatusComment._STATUS_COMMENT_ID);
            //取出关联的对象
            avQuery.include(TableContract.TableStatusComment._STATUS);
            avQuery.include(TableContract.TableStatusComment._SEND_USER);
            avQuery.include(TableContract.TableStatusComment._RECEIVE_USER);
        }
        else if(STATUS_RECEIVE_COMMENT == type) {

            //别人@我的评论
            AVQuery<AVObject> mentionAvQuery = new AVQuery<AVObject>(TableContract.TableStatusComment.TABLE_NAME);
            mentionAvQuery.whereEqualTo(TableContract.TableStatusComment._RECEIVE_USER, AVUser.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
            mentionAvQuery.whereNotEqualTo(TableContract.TableStatusComment._STATUS, null);

            //别人给我的商品发出的评论
            AVQuery<AVObject> statusCommentQuery = new AVQuery<AVObject>(TableContract.TableStatusComment.TABLE_NAME);
            AVQuery<AVObject> innerQuery = new AVQuery<>(TableContract.TableStatus.TABLE_NAME);
            innerQuery.whereEqualTo(TableContract.TableStatus._USER, AVObject.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
            statusCommentQuery.whereNotEqualTo(TableContract.TableStatusComment._SEND_USER, AVObject.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
            statusCommentQuery.whereMatchesQuery(TableContract.TableStatusComment._STATUS, innerQuery);

            List<AVQuery<AVObject>> queries = new ArrayList<AVQuery<AVObject>>();
            queries.add(mentionAvQuery);
            queries.add(statusCommentQuery);

            avQuery = AVQuery.or(queries);
            //小于指定id
            avQuery.whereLessThan(TableContract.TableStatusComment._STATUS_COMMENT_ID, comment_id);
            //id降序
            avQuery.orderByDescending(TableContract.TableGoodsComment._GOODS_COMMENT_ID);
            //取出关联的对象
            avQuery.include(TableContract.TableGoodsComment._GOODS);
            avQuery.include(TableContract.TableGoodsComment._SEND_USER);
            avQuery.include(TableContract.TableGoodsComment._RECEIVE_USER);
        }

        avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
        //取(Integer) SharedPreferencesUtil.get(context, SharedPreferencesUtil.PER_COMMENT_COUNT, 10)条
        avQuery.setLimit((Integer) SharedPreferencesUtil.get(context, SharedPreferencesUtil.PER_COMMENT_COUNT, 10));

        avQuery.findInBackground(callback);
    }

    /**
     * 根据类型统计评论数量
     *
     * @param callback
     */
    public void countCommentByType(Context context, int type, String objectId, CountCallback callback) {
      try {
          AVQuery<AVObject> avQuery = null;
          if(type == GOODS_SEND_COMMENT) {
              avQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
              avQuery.whereEqualTo(TableContract.TableGoodsComment._SEND_USER, AVUser.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
              avQuery.whereNotEqualTo(TableContract.TableGoodsComment._GOODS, null);

          }
          else if(type == GOODS_RECEIVE_COMMENT) {

              //别人@我的评论
              AVQuery<AVObject> mentionAvQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
              mentionAvQuery.whereEqualTo(TableContract.TableGoodsComment._RECEIVE_USER, AVUser.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
              mentionAvQuery.whereNotEqualTo(TableContract.TableGoodsComment._GOODS, null);


              //别人给我的商品发出的评论
              AVQuery<AVObject> goodsCommentQuery = new AVQuery<AVObject>(TableContract.TableGoodsComment.TABLE_NAME);
              AVQuery<AVObject> innerQuery = new AVQuery<>(TableContract.TableGoods.TABLE_NAME);
              innerQuery.whereEqualTo(TableContract.TableGoods._USER, AVObject.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
              goodsCommentQuery.whereNotEqualTo(TableContract.TableGoodsComment._SEND_USER, AVObject.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
              goodsCommentQuery.whereMatchesQuery(TableContract.TableGoodsComment._GOODS, innerQuery);

              List<AVQuery<AVObject>> queries = new ArrayList<AVQuery<AVObject>>();
              queries.add(mentionAvQuery);
              queries.add(goodsCommentQuery);

              avQuery = AVQuery.or(queries);

          }
          else if(type == STATUS_SEND_COMMENT) {
              avQuery = new AVQuery<AVObject>(TableContract.TableStatusComment.TABLE_NAME);
              avQuery.whereEqualTo(TableContract.TableStatusComment._SEND_USER, AVUser.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
              avQuery.whereNotEqualTo(TableContract.TableStatusComment._STATUS, null);

          }
          else if(type == STATUS_RECEIVE_COMMENT) {

              //别人@我的评论
              AVQuery<AVObject> mentionAvQuery = new AVQuery<AVObject>(TableContract.TableStatusComment.TABLE_NAME);
              mentionAvQuery.whereEqualTo(TableContract.TableStatusComment._RECEIVE_USER, AVUser.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
              mentionAvQuery.whereNotEqualTo(TableContract.TableStatusComment._STATUS, null);

              //别人给我的商品发出的评论
              AVQuery<AVObject> statusCommentQuery = new AVQuery<AVObject>(TableContract.TableStatusComment.TABLE_NAME);
              AVQuery<AVObject> innerQuery = new AVQuery<>(TableContract.TableStatus.TABLE_NAME);
              innerQuery.whereEqualTo(TableContract.TableStatus._USER, AVObject.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
              statusCommentQuery.whereNotEqualTo(TableContract.TableStatusComment._SEND_USER, AVObject.createWithoutData(TableContract.TableUser.TABLE_NAME, objectId));
              statusCommentQuery.whereMatchesQuery(TableContract.TableStatusComment._STATUS, innerQuery);

              List<AVQuery<AVObject>> queries = new ArrayList<AVQuery<AVObject>>();
              queries.add(mentionAvQuery);
              queries.add(statusCommentQuery);

              avQuery = AVQuery.or(queries);

          }
          avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
          avQuery.countInBackground(callback);
      }catch (Exception e) {
          Log.d("winson", e.toString());
      }
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
