package com.example.hp.wission_test;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CommentListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] emails;
    private final String[] comments;

    public CommentListAdapter(Activity context, String[] emails, String[] comments) {
        super(context, R.layout.comment_list, emails);

        this.context = context;
        this.emails = emails;
        this.comments = comments;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.comment_list, null, true);

        TextView titleText = (TextView) rowView.findViewById(R.id.tv_email);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.tv_video_comment);

        titleText.setText(emails[position]);
        subtitleText.setText(comments[position]);

        return rowView;

    }

}
