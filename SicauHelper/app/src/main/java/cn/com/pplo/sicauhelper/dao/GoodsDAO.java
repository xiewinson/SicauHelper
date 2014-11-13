package cn.com.pplo.sicauhelper.dao;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import cn.com.pplo.sicauhelper.model.Goods;
import cn.com.pplo.sicauhelper.provider.TableContract;

/**
 * Created by winson on 2014/11/12.
 */
public class GoodsDAO implements BaseDAO<Goods> {
    @Override
    public void save(Goods goods, SaveCallback callback) {

    }

    @Override
    public void update(Goods goods) {

    }

    /**
     * 查询指定校区的goods列表
     * @param school
     * @param callback
     */
    public void find(int school, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>("TestGoods");
        avQuery.setLimit(10);
        avQuery.orderByDescending(TableContract.TableGoods._UPDATED_AT);
        avQuery.include("student");
        avQuery.findInBackground(callback);
    }

    /**
     * 查询指定校区的goods列表
     * @param school
     * @param callback
     */
    public void find2(int school, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>("TestGoods");
        avQuery.setLimit(10);
        avQuery.orderByDescending(TableContract.TableGoods._UPDATED_AT);
        avQuery.include("student");
        avQuery.findInBackground(callback);
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public Goods toModel(AVObject avObject) {
        Goods goods = new Goods();
        goods.setAddress(avObject.getString(TableContract.TableGoods._ADDRESS));
        goods.setBrand(avObject.getString(TableContract.TableGoods._BRAND));
        goods.setCategory(avObject.getInt(TableContract.TableGoods._CATEGORY));
        goods.setContent(avObject.getString(TableContract.TableGoods._CONTENT));
        goods.setVersion(avObject.getString(TableContract.TableGoods._VERSION));
        goods.setModel(avObject.getString(TableContract.TableGoods._MODEL));
        goods.setPrice(avObject.getInt(TableContract.TableGoods._PRICE));
        goods.setSchool(avObject.getInt(TableContract.TableGoods._SCHOOL));
        goods.setTitle(avObject.getString(TableContract.TableGoods._TITLE));
        goods.setId(avObject.getLong("goods_id"));

        goods.setLatitude(avObject.getAVGeoPoint("location").getLatitude() + "");
        goods.setLongitude(avObject.getAVGeoPoint("location").getLongitude() + "");
        goods.setUpdatedAt(avObject.getUpdatedAt().toString());
        goods.setCreatedAt(avObject.getCreatedAt().toString());
        goods.setStudent(new StudentDAO().toModel(avObject.getAVObject(TableContract.TableGoods._STUDENT)));
        goods.setObjectId(avObject.getObjectId());

        return goods;
    }
}
