package com.example.hp.wission_test.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.hp.wission_test.DashboardNavigationDrawer;
import com.example.hp.wission_test.DataObjectClass.ProfileDO;
import com.example.hp.wission_test.DataObjectClass.ProfileVisitedDO;
import com.example.hp.wission_test.GlobalVariables;
import com.example.hp.wission_test.R;
import com.example.hp.wission_test.Utility;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfilePageActivity extends AppCompatActivity {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    @BindView(R.id.profile_picture)
    ImageView iv_ProfilePicture;
    @BindView(R.id.backbutton)
    ImageView backButton;
    @BindView(R.id.name)
    EditText et_Name;
    @BindView(R.id.gender)
    EditText et_Gender;
    @BindView(R.id.age)
    EditText et_Age;
    @BindView(R.id.save)
    Button bt_Save;
    private DatabaseReference databaseAllUsers;
    private DatabaseReference databaseProfile;
    private ProfileVisitedDO profileVisitedDO;
    private ProfileDO profileDO;
    private String profileUrl;
    private DatabaseReference ProfileRef;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Bitmap bitmap;
    private String profilePicPath = "";

    private Uri filePath;
    private String Email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        ButterKnife.bind(this);

        Email = GlobalVariables.email_Id;

        Typeface UbuntuFont = Typeface.createFromAsset(getAssets(), "Ubuntu-L.ttf");

        et_Name.setTypeface(UbuntuFont);
        et_Gender.setTypeface(UbuntuFont);
        et_Age.setTypeface(UbuntuFont);
        bt_Save.setTypeface(UbuntuFont);

        FirebaseApp.initializeApp(getApplicationContext());
        databaseAllUsers = FirebaseDatabase.getInstance().getReference("AllUsers");
        databaseProfile = FirebaseDatabase.getInstance().getReference("ProfileUserData");

        ProfileRef = FirebaseDatabase.getInstance().getReference("ProfileUserData");

        ProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<ProfileDO> profileDataList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ProfileDO profileDO = postSnapshot.getValue(ProfileDO.class);
                    profileDataList.add(profileDO);
                }

                for (int i = 0; i < profileDataList.size(); i++) {
                    if (profileDataList.get(i).getEmail().equalsIgnoreCase(Email)) {
                        et_Name.setText(profileDataList.get(i).getName());
                        et_Gender.setText(profileDataList.get(i).getGender());
                        et_Age.setText(profileDataList.get(i).getAge());
                        if (!profileDataList.get(i).getProfileUrl().equalsIgnoreCase(""))
                            Picasso.with(ProfilePageActivity.this).load(profileDataList.get(i).getProfileUrl()).into(iv_ProfilePicture);
                        profilePicPath = profileDataList.get(i).getProfileUrl();
                        loadImageFromStorage(profilePicPath);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();

            }
        });

        iv_ProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestPermissions();
                selectImage();
            }
        });

        bt_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isNetworkAvailable()) {
                    MDToast mdToast = MDToast.makeText(getApplicationContext(), "Please Check Your Internet Connection", 5000, MDToast.TYPE_ERROR);
                    mdToast.show();
                } else {
                    if (et_Name.getText().toString().isEmpty()) {
                        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Name is Mandatory", 9000, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } else if (profilePicPath.equalsIgnoreCase("")) {
                        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Please set Profile Picture", 9000, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } else {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                GlobalVariables.name = et_Name.getText().toString();
                                MDToast mdToast = MDToast.makeText(getApplicationContext(), "Successfully Saved Data", 5000, MDToast.TYPE_SUCCESS);
                                mdToast.show();

                                profileVisitedDO = new ProfileVisitedDO(GlobalVariables.email_Id, true);
                                profileDO = new ProfileDO(et_Name.getText().toString(), et_Age.getText().toString(), et_Gender.getText().toString(), profilePicPath, GlobalVariables.email_Id);

                                databaseAllUsers.child(et_Name.getText().toString()).setValue(profileVisitedDO);

                                databaseProfile.child(et_Name.getText().toString()).setValue(profileDO);

                                Intent intent = new Intent(getApplicationContext(), DashboardNavigationDrawer.class);
                                startActivity(intent);
                            }
                        }, 1000);

                    }
                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = getIntent();

                if (intent.getStringExtra("fromLogin").equalsIgnoreCase("true")) {
                    MDToast mdToast = MDToast.makeText(getApplicationContext(), "Please Complete Profile...", 9000, MDToast.TYPE_ERROR);
                    mdToast.show();
                } else {
                    Intent intentNavigateDrawer = new Intent(ProfilePageActivity.this, DashboardNavigationDrawer.class);
                    startActivity(intentNavigateDrawer);
                }
            }
        });
    }

    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePageActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(ProfilePageActivity.this);

                if (items[item].equals("Take Photo")) {
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        bitmap = (Bitmap) data.getExtras().get("data");
        filePath = getImageUri(getApplicationContext(), bitmap);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(
                Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg"
        );

        FileOutputStream fileOutputStream;
        try {
            destination.createNewFile();
            fileOutputStream = new FileOutputStream(destination);
            fileOutputStream.write(bytes.toByteArray());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        iv_ProfilePicture.setImageBitmap(bitmap);
        profilePicPath = saveToInternalStorage(bitmap);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        bitmap = null;
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        iv_ProfilePicture.setImageBitmap(bitmap);
        filePath = getImageUri(getApplicationContext(), bitmap);
        profilePicPath = saveToInternalStorage(bitmap);
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, GlobalVariables.email_Id + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        profilePicPath = directory.getAbsolutePath();

        SharedPreferences.Editor editor = getSharedPreferences("profile_data", MODE_PRIVATE).edit();
        editor.putString("profile_path", profilePicPath);
        editor.apply();

        return profilePicPath;
    }

    private void loadImageFromStorage(String path) {

        try {

            if (!profilePicPath.equalsIgnoreCase("")) {
                File f = new File(profilePicPath, GlobalVariables.email_Id + ".jpg");
                Bitmap bitmapProfile = BitmapFactory.decodeStream(new FileInputStream(f));
                iv_ProfilePicture.setImageBitmap(bitmapProfile);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    // Check Internet Connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
