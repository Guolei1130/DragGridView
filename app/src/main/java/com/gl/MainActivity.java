package com.gl;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.gl.bean.ProvinceItem;
import com.gl.draggridview.DragAdapter;
import com.gl.draggridview.DragGridView;
import com.gl.draggridview.R;
import com.gl.model.ProvinceModel;
import com.gl.tools.Constant;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private DragGridView gridView;

    private List<ProvinceItem> items = new ArrayList<ProvinceItem>();

    private SharedPreferences mShared;
    private SharedPreferences.Editor mEditor;

    ProvinceModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mShared = getSharedPreferences(Constant.USER, 0);
        mEditor = mShared.edit();
        model = new ProvinceModel(this);
        if (mShared.getBoolean(Constant.IS_FIRST,true)){
            initData();
            mEditor.putBoolean(Constant.IS_FIRST, false);
            mEditor.commit();
        }else {
            model.getProvinceListFromCache();
            items = model.list;
            Log.e("tag","error");
        }
        initView();
    }

    private void initView() {
        gridView = (DragGridView) findViewById(R.id.userGridView);
        gridView.setAdapter(new DragAdapter(this,items));
    }

    private void initData() {
        items.add(new ProvinceItem(1, "北京"));
        items.add(new ProvinceItem(2, "天津"));
        items.add(new ProvinceItem(3, "河北"));
        items.add(new ProvinceItem(4, "山西"));
        items.add(new ProvinceItem(5, "内蒙古"));
        items.add(new ProvinceItem(6, "黑龙江"));
        items.add(new ProvinceItem(7, "山东"));
        items.add(new ProvinceItem(8, "河南"));
        items.add(new ProvinceItem(9, "甘肃"));
        items.add(new ProvinceItem(10, "湖南"));
        items.add(new ProvinceItem(11, "湖北"));
        items.add(new ProvinceItem(12, "福建"));
        items.add(new ProvinceItem(13, "广东"));
        items.add(new ProvinceItem(14, "深圳"));
        items.add(new ProvinceItem(15, "香港"));
        items.add(new ProvinceItem(16, "澳门"));
        items.add(new ProvinceItem(17, "四川"));
    }
}
