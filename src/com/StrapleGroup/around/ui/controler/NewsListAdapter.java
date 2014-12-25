package com.StrapleGroup.around.ui.controler;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;

/**
 * Created by Robert on 2014-12-25.
 */
public class NewsListAdapter extends CursorAdapter implements Constants {
    public NewsListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.news_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
