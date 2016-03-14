package com.gl.bean;

import com.gl.base.BaseItem;
import com.gl.tools.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by guolei on 16-3-14.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */

public class ProvinceItem extends BaseItem implements Serializable {

    private static final long serialVersionUID = -6465237897027410019L;;

    private JSONObject jsonObject;

    private int id;
    private String name;

    public ProvinceItem(){

    }

    public ProvinceItem(int id,String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 将bean 转化为　jsonobject
     * @return
     * @throws JSONException
     */
    @Override
    public JSONObject toJson() throws JSONException {
        if (null == jsonObject){
            jsonObject = new JSONObject();
        }
        jsonObject.put(Constant.PROVINCE_ID,id);
        jsonObject.put(Constant.PROVINCE_NAME,name);
        return jsonObject;
    }

    @Override
    public void fromJson(JSONObject jsonObject){
        if (null == jsonObject){
            return;
        }
        this.id = jsonObject.optInt(Constant.PROVINCE_ID);
        this.name = jsonObject.optString(Constant.PROVINCE_NAME);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
