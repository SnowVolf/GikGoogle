package ru.SnowVolf.gikgoogle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Snow Volf on 11.05.2017, 5:40
 */

public class UrlsAdapter extends ArrayAdapter<UrlItem> {

    public UrlsAdapter(Context context, ArrayList<UrlItem> items) {
        super(context, R.layout.list_container ,items);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UrlItem item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_container, null);
            assert item != null;
            ((TextView) convertView.findViewById(R.id.url_title)).setText(item.getTitle());
            ((TextView) convertView.findViewById(R.id.url_text)).setText(item.getUrl());
        }
        return convertView;
    }
}

