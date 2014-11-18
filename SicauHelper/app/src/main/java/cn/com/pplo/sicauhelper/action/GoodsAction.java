package cn.com.pplo.sicauhelper.action;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;

import cn.com.pplo.sicauhelper.provider.TableContract;

/**
 * Created by winson on 2014/11/12.
 */
public class GoodsAction {

    /**
     * 在缓存中查询指定校区的goods列表
     * @param school
     * @param callback
     */
    public void findInCacheThenNetwork(int school, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableGoods.TABLE_NAME);
        avQuery.setCachePolicy(AVQuery.CachePolicy.CACHE_THEN_NETWORK);
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending("goods_id");
        //选校区
        avQuery.whereEqualTo(TableContract.TableGoods._SCHOOL, school);
        avQuery.include(TableContract.TableGoods._USER);
        avQuery.findInBackground(callback);
    }

    /**
     * 查询指定校区的最新goods列表
     * @param school
     * @param callback
     */
    public void findNewData(int school, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(TableContract.TableGoods.TABLE_NAME);
        avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);

        //若有缓存则清空
        if(avQuery.hasCachedResult()) {
            avQuery.clearCachedResult();
        }
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending("goods_id");
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
        avQuery.whereLessThan("goods_id", goods_id);
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending("goods_id");
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
        avQuery.orderByDescending("goods_id");
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
        avQuery.whereLessThan("goods_id", goods_id);
        //取10条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending("goods_id");
        //选校区
        avQuery.whereEqualTo(TableContract.TableGoods._SCHOOL, school);
        avQuery.include(TableContract.TableGoods._USER);
        avQuery.findInBackground(callback);
    }


    /**
     * 删除
     */
    public void delete(AVObject avGoods, DeleteCallback callback) {
        //TODO 删除商品和跟商品有关的评论和图片
    }
}
