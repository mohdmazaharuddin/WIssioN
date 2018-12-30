package com.example.hp.wission_test;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hp.wission_test.Activity.CommentActivity;
import com.example.hp.wission_test.DataObjectClass.CommentsDO;
import com.example.hp.wission_test.DataObjectClass.LikesDO;
import com.example.hp.wission_test.DataObjectClass.MainDO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.List;

import static com.parse.Parse.getApplicationContext;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.MyViewHolder> {

    private List<MainDO> videoDetailsList;
    private List<LikesDO> likesListTemp;
    private Context context;
    private DatabaseReference databaseLikes;
    private DatabaseReference databaseComments;
    private LikesDO likesDO;
    private CommentsDO commentsDO;
    private int likesCountClick = 0;
    private String playVideo = "https://www.youtube.com/watch?v=";
    private String userID;
    private DatabaseReference commentRef;


    public VideoListAdapter(Context context, List<MainDO> videoDetailsList) {
        this.context = context;
        this.videoDetailsList = videoDetailsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MainDO mainDO = videoDetailsList.get(position);
        holder.tvCategory.setText(mainDO.getCategory());
        holder.tvCaption.setText(mainDO.getCaption());
        holder.setIsRecyclable(false);
        Glide.with(context).load(mainDO.getPicUrl())
                .thumbnail(0.5f)
                .into(holder.ivVideoPic);

        likesListTemp = DashboardHomeFragment.likesList;
        for (int i = likesListTemp.size() - 1; i >= 0; i--) {
            if (likesListTemp.get(i).getVideoId().equalsIgnoreCase(mainDO.getVideoId())) {
                holder.tvLikes.setText(String.valueOf(likesListTemp.get(i).getLikesCount()));
                break;
            }
        }

        if (Integer.valueOf(holder.tvLikes.getText().toString()) >= 0) {
            for (int i = 0; i < likesListTemp.size(); i++) {
                if (likesListTemp.get(i).getVideoId().equalsIgnoreCase(mainDO.getVideoId())) {
                    if (likesListTemp.get(i).getEmailId().equalsIgnoreCase(GlobalVariables.email_Id)) {
                        holder.likeButton.setVisibility(View.GONE);
                        holder.alreadylikeButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        holder.allComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.tvComment.getText().toString().equals("No Comments Yet")) {
                    MDToast mdToast = MDToast.makeText(getApplicationContext(), "No Comments Yet", 5000, MDToast.TYPE_ERROR);
                    mdToast.show();
                } else {
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("video_id", mainDO.getVideoId());
                    intent.putExtra("video_caption", mainDO.getCaption());
                    intent.putExtra("video_picurl", mainDO.getPicUrl());
                    context.startActivity(intent);
                }
            }
        });

        commentRef = FirebaseDatabase.getInstance().getReference("Comments");

        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<CommentsDO> commentsList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CommentsDO commentsDO = postSnapshot.getValue(CommentsDO.class);
                    commentsList.add(commentsDO);
                }

                for (int i = commentsList.size() - 1; i >= 0; i--) {
                    if (mainDO.getVideoId().equalsIgnoreCase(commentsList.get(i).getVideoId())) {
                        holder.tvComment.setText(commentsList.get(i).getComment());
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();

            }
        });

        databaseLikes = FirebaseDatabase.getInstance().getReference("Likes");
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isNetworkAvailable()) {
                    MDToast mdToast = MDToast.makeText(getApplicationContext(), "Please Check your Internet Connection!", 5000, MDToast.TYPE_ERROR);
                    mdToast.show();
                } else {
                    int count = 0;
                    for (int i = 0; i < likesListTemp.size(); i++) {
                        if (likesListTemp.get(i).getVideoId().equalsIgnoreCase(mainDO.getVideoId())) {
                            if (likesListTemp.get(i).getEmailId().equalsIgnoreCase(GlobalVariables.email_Id)) {
                                MDToast mdToast = MDToast.makeText(getApplicationContext(), context.getString(R.string.already_like), 5000, MDToast.TYPE_WARNING);
                                mdToast.show();
                                count = 1;
                                break;
                            } else {
                                likesDO = new LikesDO(mainDO.getVideoId(), Integer.valueOf(holder.tvLikes.getText().toString()) + 1, GlobalVariables.email_Id);
                                userID = databaseLikes.push().getKey();
                                databaseLikes.child(mainDO.getVideoId()).child(userID).setValue(likesDO);

                                String tempV = (String) holder.tvLikes.getText();
                                likesCountClick = Integer.valueOf(tempV) + 1;

                                holder.tvLikes.setText(String.valueOf(likesCountClick));

                                mainDO.setLikesCount(likesCountClick);

                                holder.likeButton.setVisibility(View.GONE);
                                holder.alreadylikeButton.setVisibility(View.VISIBLE);

                                count = 1;

                                break;
                            }
                        }
                    }
                    if (count == 0) {
                        likesDO = new LikesDO(mainDO.getVideoId(), Integer.valueOf(holder.tvLikes.getText().toString()) + 1, GlobalVariables.email_Id);
                        userID = databaseLikes.push().getKey();
                        databaseLikes.child(mainDO.getVideoId()).child(userID).setValue(likesDO);

                        String tempV = (String) holder.tvLikes.getText();
                        likesCountClick = Integer.valueOf(tempV) + 1;

                        holder.tvLikes.setText(String.valueOf(likesCountClick));

                        mainDO.setLikesCount(likesCountClick);

                        holder.likeButton.setVisibility(View.GONE);
                        holder.alreadylikeButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        holder.alreadylikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isNetworkAvailable()) {
                    MDToast mdToast = MDToast.makeText(context, "Please Check Your Internet Connection", 5000, MDToast.TYPE_ERROR);
                    mdToast.show();
                } else {
                    MDToast mdToast = MDToast.makeText(context, "You already liked this video", 5000, MDToast.TYPE_WARNING);
                    mdToast.show();
                }
            }
        });


        databaseComments = FirebaseDatabase.getInstance().getReference("Comments");
        holder.ivCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isNetworkAvailable()) {
                    MDToast mdToast = MDToast.makeText(getApplicationContext(), "Please Check your Internet Connection!", 5000, MDToast.TYPE_ERROR);
                    mdToast.show();
                } else {
                    if (holder.etComment.getText().toString().equalsIgnoreCase("")) {
                        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Please Enter Something", 5000, MDToast.TYPE_WARNING);
                        mdToast.show();
                    } else {
                        String comment = holder.etComment.getText().toString();
                        userID = databaseLikes.push().getKey();
                        commentsDO = new CommentsDO(GlobalVariables.email_Id, comment, mainDO.getVideoId());
                        databaseComments.child(userID).setValue(commentsDO);
                        holder.etComment.setText("");
                    }
                }
            }
        });

        holder.ivVideoPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isNetworkAvailable()) {
                    MDToast mdToast = MDToast.makeText(context, "Please Check Your Internet Connection", 5000, MDToast.TYPE_ERROR);
                    mdToast.show();
                } else {
                    Uri uri = Uri.parse(playVideo + mainDO.getVideoId());

                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + mainDO.getVideoId()));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        context.startActivity(appIntent);
                    } catch (ActivityNotFoundException ex) {
                        context.startActivity(webIntent);
                    }
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return videoDetailsList.size();
    }

    // Check Internet Connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCategory, tvCaption, tvLikes, tvComment, allComments;
        public ImageView ivCommentSend, ivVideoPic, likeButton, alreadylikeButton;
        public EditText etComment;

        public MyViewHolder(View view) {
            super(view);
            tvCategory = (TextView) view.findViewById(R.id.video_category);
            tvCaption = (TextView) view.findViewById(R.id.video_caption);
            tvLikes = (TextView) view.findViewById(R.id.video_likes);
            tvComment = (TextView) view.findViewById(R.id.tv_comment);
            allComments = (TextView) view.findViewById(R.id.all_comments);
            ivCommentSend = (ImageView) view.findViewById(R.id.comment_send);
            ivVideoPic = (ImageView) view.findViewById(R.id.video_pic);
            likeButton = (ImageView) view.findViewById(R.id.like_button);
            alreadylikeButton = (ImageView) view.findViewById(R.id.already_like_button);
            etComment = (EditText) view.findViewById(R.id.et_video_comment);
        }
    }

}
