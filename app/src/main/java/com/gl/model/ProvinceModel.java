package com.gl.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.gl.bean.ProvinceItem;
import com.gl.tools.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guolei on 16-3-14.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */

public class ProvinceModel {

    public List<ProvinceItem> list = new ArrayList<>();

    private Context mContext;

    private SharedPreferences mShared;

    public ProvinceModel(Context context){
        this.mContext = context;
        mShared = mContext.getSharedPreferences(Constant.USER,0);
    }

    /**
     * 可以在这里请求网络数据
     */
    public void getProvinceList(){

    }

    /**
     * 从本地读取
     */
    public void getProvinceListFromCache(){
        list.clear();
        try {
            JSONObject jsonObject = new JSONObject(mShared.getString(Constant.PROVINCE,"{}"));
            JSONArray arr = (JSONArray) jsonObject.opt(Constant.PROVINCE_ARR);
            if (null != arr && arr.length()>0){
                for (int i = 0; i < arr.length(); i++) {
                    ProvinceItem item = new ProvinceItem();
                    item.fromJson(arr.getJSONObject(i));
                    list.add(item);
                }
            }else {
                getProvinceList();
            }

        } catch (JSONException e) {
            getProvinceList();
            e.printStackTrace();
        }
    }

}
