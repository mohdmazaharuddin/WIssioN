package com.example.hp.wission_test.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.hp.wission_test.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.valdesekamdem.library.mdtoast.MDToast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {

    @BindView(R.id.username)
    EditText et_Username;

    @BindView(R.id.email)
    EditText et_Email;

    @BindView(R.id.password)
    EditText et_Password;

    @BindView(R.id.confirm_password)
    EditText et_ConfirmPassword;

    @BindView(R.id.signup)
    Button bt_SignUp;

    @BindView(R.id.backbutton)
    ImageView iv_Close;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ButterKnife.bind(this);

        Typeface UbuntuFont = Typeface.createFromAsset(getAssets(), "Ubuntu-L.ttf");

        dialog = ProgressDialog.show(SignupActivity.this, "",
                "Progressing.... Please wait...", true
        );
        dialog.setCancelable(false);
        dialog.cancel();

        et_Email.setTypeface(UbuntuFont);
        et_Password.setTypeface(UbuntuFont);
        et_ConfirmPassword.setTypeface(UbuntuFont);
        bt_SignUp.setTypeface(UbuntuFont);

        bt_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isNetworkAvailable()) {
                    MDToast mdToast = MDToast.makeText(getApplicationContext(), "Please Check Your Internet Connection", 5000, MDToast.TYPE_ERROR);
                    mdToast.show();
                } else {
                    if (et_Username.getText().toString().equalsIgnoreCase("")) {
                        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Please Enter Username", 5000, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } else if (et_Email.getText().toString().equalsIgnoreCase("")) {
                        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Please Enter Email", 5000, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } else if (et_Password.getText().toString().equalsIgnoreCase("")) {
                        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Please Enter Password", 5000, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } else if (et_ConfirmPassword.getText().toString().equalsIgnoreCase("")) {
                        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Please Enter Confirm Password", 5000, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } else if (!et_Password.getText().toString().equalsIgnoreCase(et_ConfirmPassword.getText().toString())) {
                        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Password and Confirm Password Should be same", 9000, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } else {
                        dialog.show();
                        try {

                            // Sign up with Parse
                            ParseUser user = new ParseUser();
                            user.setUsername(et_Username.getText().toString());
                            user.setPassword(et_Password.getText().toString());
                            user.setEmail(et_Email.getText().toString());

                            user.signUpInBackground(new SignUpCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        dialog.cancel();
                                        ParseUser.logOut();
                                        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Account Created Successfully!\", \"Please verify your email before Login", 9000, MDToast.TYPE_SUCCESS);
                                        mdToast.show();

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(intent);

                                            }
                                        }, 4000);

                                    } else {
                                        dialog.cancel();
                                        ParseUser.logOut();
                                        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Error Account Creation failed\", \"Account could not be created", 5000, MDToast.TYPE_ERROR);
                                        mdToast.show();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        iv_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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
}
