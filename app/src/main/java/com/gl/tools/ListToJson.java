package com.gl.tools;

import android.support.annotation.NonNull;

import com.gl.base.BaseItem;
import com.gl.bean.ProvinceItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by guolei on 16-3-14.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */

public class ListToJson {
    public static JSONObject toJson(@NonNull List<? extends BaseItem> list){
        if (list.size()>0){
            try{
                JSONObject jsonObject = new JSONObject();
                JSONArray arr = new JSONArray();
                for (BaseItem item: list) {
                    JSONObject jsonItem = item.toJson();
                    arr.put(jsonItem);
                }
                jsonObject.put(Constant.PROVINCE_ARR,arr);
                return jsonObject;
            }catch (JSONException e){
                e.printStackTrace();
            }

        }

        return null;
    }
}
