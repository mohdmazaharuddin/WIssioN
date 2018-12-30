package com.example.hp.wission_test.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hp.wission_test.DashboardNavigationDrawer;
import com.example.hp.wission_test.DataObjectClass.ProfileVisitedDO;
import com.example.hp.wission_test.GlobalVariables;
import com.example.hp.wission_test.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    //butterknife
    @BindView(R.id.email)
    EditText et_Email;

    @BindView(R.id.password)
    EditText et_Password;

    @BindView(R.id.login)
    Button bt_Login;

    @BindView(R.id.register)
    Button bt_Register;

    private int count = 0;
    private DatabaseReference allUsersRef;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        Typeface UbuntuFont = Typeface.createFromAsset(getAssets(), "Ubuntu-L.ttf");

        et_Email.setTypeface(UbuntuFont);
        et_Password.setTypeface(UbuntuFont);
        bt_Login.setTypeface(UbuntuFont);
        bt_Register.setTypeface(UbuntuFont);

        bt_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = ProgressDialog.show(LoginActivity.this, "",
                        "Autheticating.... Please wait...", true);
                dialog.setCancelable(false);
                dialog.show();

                if (isNetworkAvailable()) {
                    // Login with Parse
                    if (et_Email.getText().toString().equalsIgnoreCase("")) {
                        dialog.cancel();
                        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Please Enter Email", 5000, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } else if (et_Password.getText().toString().equalsIgnoreCase("")) {
                        dialog.cancel();
                        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Please Enter Password", 5000, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } else {
                        //verifying email and password
                        ParseUser.logInInBackground(et_Email.getText().toString(), et_Password.getText().toString(), new LogInCallback() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                if (parseUser != null) {
                                    if (parseUser.getBoolean("emailVerified")) {

                                        GlobalVariables.email_Id = et_Email.getText().toString();

                                        String emailPref = "";
                                        SharedPreferences prefs = getSharedPreferences("profile_data", MODE_PRIVATE);
                                        emailPref = prefs.getString("Email", "");

                                        if (!emailPref.equalsIgnoreCase(et_Email.getText().toString())) {
                                            SharedPreferences.Editor editor = getSharedPreferences("profile_data", MODE_PRIVATE).edit();
                                            editor.putString("profile_path", "");
                                            editor.apply();
                                        }

                                        SharedPreferences.Editor editor = getSharedPreferences("profile_data", MODE_PRIVATE).edit();
                                        editor.putString("Email", et_Email.getText().toString());
                                        editor.apply();

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.cancel();
                                                MDToast mdToast = MDToast.makeText(getApplicationContext(), "Login Successful", 5000, MDToast.TYPE_SUCCESS);
                                                mdToast.show();
                                            }
                                        }, 1000);

                                        FirebaseApp.initializeApp(getApplicationContext());

                                        allUsersRef = FirebaseDatabase.getInstance().getReference("AllUsers");

                                        allUsersRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                List<ProfileVisitedDO> profileVisitedList = new ArrayList<>();
                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                    ProfileVisitedDO profileVisitedDO = postSnapshot.getValue(ProfileVisitedDO.class);
                                                    profileVisitedList.add(profileVisitedDO);
                                                }

                                                int count = 0;
                                                for (int i = 0; i < profileVisitedList.size(); i++) {
                                                    if (et_Email.getText().toString().equalsIgnoreCase(profileVisitedList.get(i).getEmail())) {
                                                        boolean isVisited = profileVisitedList.get(i).isVisitedProfiePage();
                                                        if (isVisited) {
                                                            Intent intent = new Intent(getApplicationContext(), DashboardNavigationDrawer.class);
                                                            startActivity(intent);
                                                            count = 1;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (count == 0) {
                                                    Intent intent = new Intent(getApplicationContext(), ProfilePageActivity.class);
                                                    intent.putExtra("fromLogin", "true");
                                                    intent.putExtra("fromNavigationDrawer", "false");
                                                    startActivity(intent);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                throw databaseError.toException();

                                            }
                                        });

                                    } else {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.cancel();
                                                ParseUser.logOut();
                                                MDToast mdToast = MDToast.makeText(getApplicationContext(), "Login Failed....Please Check Your Credentials", 5000, MDToast.TYPE_ERROR);
                                                mdToast.show();
                                            }
                                        }, 1000);

                                    }
                                } else {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.cancel();
                                            ParseUser.logOut();
                                            MDToast mdToast = MDToast.makeText(getApplicationContext(), "Login Failed....Please Check Your Credentials", 5000, MDToast.TYPE_ERROR);
                                            mdToast.show();
                                        }
                                    }, 1000);

                                }
                            }
                        });
                    }

                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.cancel();
                            MDToast mdToast = MDToast.makeText(LoginActivity.this, "Please Check Your Interet Connection", 5000, MDToast.TYPE_ERROR);
                            mdToast.show();
                        }
                    }, 1000);
                }
            }
        });

        bt_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

    }

    // Check Internet Connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {

        count = count + 1;

        if (count == 2) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        } else {
            MDToast mdToast = MDToast.makeText(this, "Press one more time to close app", 5000, MDToast.TYPE_WARNING);
            mdToast.show();
        }

    }
}
