package com.gl.draggridview;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gl.base.BaseDragAdapter;
import com.gl.base.BaseItem;
import com.gl.bean.ProvinceItem;
import com.gl.tools.Constant;
import com.gl.tools.ListToJson;

import java.util.List;

/**
 * Created by guolei on 16-3-14.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */

public class DragAdapter extends BaseDragAdapter {

    private static final String TAG = "DragAdapter";

    private boolean isItemShow = false;
    private Context context;

    private int holdPosition;

    private boolean isChanged = false;

    boolean isVisiable = true;

    private List<ProvinceItem> provinceList;

    private TextView item_text;

    public int remove_position = -1;

    private SharedPreferences mShared;

    private SharedPreferences.Editor mEditor;

    public DragAdapter(Context context,List<ProvinceItem> provinceList){
        this.context = context;
        this.provinceList = provinceList;
        mShared = context.getSharedPreferences(Constant.USER,0);
        mEditor = mShared.edit();
    }

    @Override
    public int getCount() {
        return provinceList == null ? 0 : provinceList.size();
    }

    @Override
    public Object getItem(int position) {
        if (null != provinceList && provinceList.size() != 0){
            return provinceList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, null);
        item_text = (TextView) view.findViewById(R.id.text_item);
        ProvinceItem provinceItem = (ProvinceItem) getItem(position);
        item_text.setText(provinceItem.getName());
        if ((position == 0) || (position == 1)){
//			item_text.setTextColor(context.getResources().getColor(R.color.black));
            item_text.setEnabled(false);
        }
        if (isChanged && (position == holdPosition) && !isItemShow) {
            item_text.setText("");
            item_text.setSelected(true);
            item_text.setEnabled(true);
            isChanged = false;
        }
        if (!isVisiable && (position == -1 + provinceList.size())) {
            item_text.setText("");
            item_text.setSelected(true);
            item_text.setEnabled(true);
        }
        if(remove_position == position){
            item_text.setText("");
        }
        return view;
    }

    @Override
    public void addItem(BaseItem item) {
        provinceList.add((ProvinceItem) item);
        notifyDataSetChanged();
    }

    @Override
    public void exchange(int dragPosition, int dropPosition) {
        holdPosition = dropPosition;
        ProvinceItem dragItem = (ProvinceItem) getItem(dragPosition);
        Log.d(TAG, "startPostion=" + dragPosition + ";endPosition=" + dropPosition);
        if (dragPosition < dropPosition) {
            provinceList.add(dropPosition + 1, dragItem);
            provinceList.remove(dragPosition);
        } else {
            provinceList.add(dropPosition, dragItem);
            provinceList.remove(dragPosition + 1);
        }
        isChanged = true;
        mEditor.putString(Constant.PROVINCE, ListToJson.toJson(provinceList).toString());
        mEditor.commit();
        notifyDataSetChanged();
    }

    @Override
    public List<? extends BaseItem> getList() {
        return provinceList;
    }

    @Override
    public void removeItem(BaseItem item) {
        if (provinceList.contains((ProvinceItem)item)){
            provinceList.remove((ProvinceItem)item);
            notifyDataSetChanged();
        }
    }

    @Override
    public void removePosition(int position) {
        if (position >=0 && position<provinceList.size()){
            provinceList.remove(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public void setVisiable(boolean visiable) {

    }

    @Override
    public void setShowDropItem(boolean show) {
        isVisiable = show ;
    }


}
