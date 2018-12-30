package com.example.hp.wission_test.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hp.wission_test.CommentListAdapter;
import com.example.hp.wission_test.DashboardNavigationDrawer;
import com.example.hp.wission_test.DataObjectClass.CommentsDO;
import com.example.hp.wission_test.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentActivity extends AppCompatActivity {

    @BindView(R.id.tv_video_caption)
    TextView tv_VideoCaption;

    @BindView(R.id.iv_video_image)
    ImageView iv_VideoImage;

    @BindView(R.id.backbutton)
    ImageView iv_Close;

    private ListView list;

    private String videoId, videoCaption, videoPicUrl;
    private DatabaseReference commentDB_Ref;
    private List<CommentsDO> commentsList;

    private String[] emails;

    private String[] comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        videoId = intent.getStringExtra("video_id");
        videoCaption = intent.getStringExtra("video_caption");
        videoPicUrl = intent.getStringExtra("video_picurl");

        Glide.with(getApplicationContext()).load(videoPicUrl)
                .thumbnail(0.5f)
                .into(iv_VideoImage);
        tv_VideoCaption.setText(videoCaption);
        commentDB_Ref = FirebaseDatabase.getInstance().getReference("Comments");

        iv_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNavigateDrawer = new Intent(CommentActivity.this, DashboardNavigationDrawer.class);
                startActivity(intentNavigateDrawer);
            }
        });

        commentsList = new ArrayList<>();
        commentDB_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CommentsDO commentsDO = postSnapshot.getValue(CommentsDO.class);
                    commentsList.add(commentsDO);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();

            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                emails = new String[commentsList.size()];
                comments = new String[commentsList.size()];

                int j = 0;
                for (int i = commentsList.size() - 1; i >= 0; i--) {

                    if (commentsList.get(i).getVideoId().equalsIgnoreCase(videoId)) {
                        emails[j] = commentsList.get(i).getName();
                        comments[j] = commentsList.get(i).getComment();
                        j++;
                    }

                }

                CommentListAdapter adapter = new CommentListAdapter(CommentActivity.this, emails, comments);
                list = (ListView) findViewById(R.id.list);
                list.setAdapter(adapter);

            }
        }, 1000);

    }
}
