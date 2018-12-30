package com.example.hp.wission_test;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hp.wission_test.DataObjectClass.LikesDO;
import com.example.hp.wission_test.DataObjectClass.MainDO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.parse.Parse.getApplicationContext;


public class DashboardHomeFragment extends Fragment {

    public static List<LikesDO> likesList;
    private List<MainDO> movieList = new ArrayList<>();
    private List<MainDO> movieListTemp = new ArrayList<>();
    private RecyclerView recyclerView;
    private VideoListAdapter mAdapter;
    private ProgressDialog dialog;
    private int max_results = 10;
    private String nextPageToken = "";
    private String nextPageTokenTemp = "";
    private String youtubeAPIUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet,id%2Cid&maxResults=" + max_results + "&pageToken=" + nextPageToken + "&type=video&videoCategoryId=23&key=AIzaSyBFaH9N6AgXhaDcbOJDmcfykWsQiU-04Bw";
    private String response = "";
    private DatabaseReference myRef;
    private int likesCount = 0;
    private String videoId;
    private String title;
    private String imageUrl;
    private String videoUrl;
    private String category;
    private int youtubeLikes = 0;
    private int youtubeViews = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        getLikesCountFromFrebase();

        mAdapter = new VideoListAdapter(getContext(), movieList);

//        recyclerView.setHasFixedSize(true);

        // vertical RecyclerView
        // keep video_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // horizontal RecyclerView
        // keep video_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                MainDO mainDO = movieList.get(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (!isNetworkAvailable()) {
                    MDToast mdToast = MDToast.makeText(getApplicationContext(), "Please Check Your Internet Connection", 5000, MDToast.TYPE_ERROR);
                    mdToast.show();
                } else {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (!recyclerView.canScrollVertically(1)) {
                        dialog.show();
                        max_results += 10;
                        if (max_results <= 50) {
                            String youtubeAPIUrlTemp = "https://www.googleapis.com/youtube/v3/search?part=snippet,id%2Cid&maxResults=" + max_results + "&pageToken=" + nextPageToken + "&type=video&videoCategoryId=23&key=AIzaSyBFaH9N6AgXhaDcbOJDmcfykWsQiU-04Bw";
                            prepareMovieDataForScroll(youtubeAPIUrlTemp);
                        } else {
                            max_results = 10;
                            nextPageToken = nextPageTokenTemp;
                            dialog.cancel();
                        }

                    }
                }
            }
        });

        dialog = ProgressDialog.show(getContext(), "",
                "Loading. Please wait...", true);

        prepareMovieData();

        return view;

    }

    /**
     * Prepares sample data to provide data set to adapter
     */
    private void prepareMovieData() {

        MyTask taskLoad = new MyTask();

        if (!isNetworkAvailable()) {
            dialog.cancel();
        } else {
            taskLoad.execute(youtubeAPIUrl);
        }

    }

    private void prepareMovieDataForScroll(String youtubeAPIUrlTemp) {

        MyTask taskLoad = new MyTask();

        if (!isNetworkAvailable()) {
            dialog.cancel();
        } else {
            taskLoad.execute(youtubeAPIUrlTemp);
        }

    }

    private int getLikesCountFromFrebase() {

        final int[] count = {0};

        myRef = FirebaseDatabase.getInstance().getReference("Likes");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                likesList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    if (postSnapshot != null) {
                        for (DataSnapshot roomDataSnapshot : postSnapshot.getChildren()) {
                            LikesDO likesDO = roomDataSnapshot.getValue(LikesDO.class);
                            likesList.add(likesDO);
                        }
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();

            }
        });

        return count[0];

    }

    // Check Internet Connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Async Task for run Background
    private class MyTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            HttpGet request = new HttpGet(urls[0]);
            String _response = "";

            try {
                HttpResponse response = httpclient.execute(request);
                HttpEntity resEntity = response.getEntity();
                _response = EntityUtils.toString(resEntity); // content will be consume only once
            } catch (Exception e) {
                e.printStackTrace();
            }

            httpclient.getConnectionManager().shutdown();
            response = _response;
            return _response;

        }

        @Override
        protected void onPostExecute(String result) {
            dialog.cancel();

            try {
                JSONObject jsonResponse = new JSONObject(response);
                nextPageTokenTemp = jsonResponse.getString("nextPageToken");
                JSONArray jsonArray;

                jsonArray = jsonResponse.getJSONArray("items");
                for (int i = max_results - 10; i < jsonArray.length(); i++) {
                    videoId = jsonArray.getJSONObject(i).getJSONObject("id").getString("videoId");
                    title = jsonArray.getJSONObject(i).getJSONObject("snippet").getString("title");
                    imageUrl = jsonArray.getJSONObject(i).getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url");
                    videoUrl = videoId;
                    category = "";

                    MainDO mainDO = new MainDO(imageUrl, category, videoUrl, title, likesCount, videoId, youtubeLikes, youtubeViews);
                    movieListTemp.add(mainDO);

                }

                for (int i = 0; i < movieListTemp.size(); i++) {
                    movieList.add(movieListTemp.get(i));
                }
                movieListTemp.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mAdapter.notifyDataSetChanged();
        }
    }

}
