package com.sjjd.wyl.ebooks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sjjd.wyl.ebooks.utils.AphidLog;
import com.sjjd.wyl.ebooks.utils.IO;
import com.sjjd.wyl.ebooks.utils.UI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyl on 2019/1/15.
 */

public class TravelAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private int repeatCount = 1;

    private List<String> travelData;

    public TravelAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        travelData = new ArrayList<String>();
        for (int i = 1; i <= 5; i++) {
            travelData.add("p" + i+".jpg");
        }
    }

    @Override
    public int getCount() {
        return travelData.size() * repeatCount;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = convertView;
        if (convertView == null) {
            layout = inflater.inflate(R.layout.pages, null);
            AphidLog.d("created new view from adapter: %d", position);
        }

        final String data = travelData.get(position % travelData.size());


        UI.<ImageView>findViewById(layout, R.id.imgPage)
                .setImageBitmap(IO.readBitmap(inflater.getContext().getAssets(), data));


        return layout;
    }

    public void removeData(int index) {
        if (travelData.size() > 1) {
            travelData.remove(index);
        }
    }
}

