package com.example.hp.wission_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hp.wission_test.Activity.LoginActivity;
import com.example.hp.wission_test.Activity.ProfilePageActivity;
import com.parse.ParseUser;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.Arrays;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class DashboardNavigationDrawer extends AppCompatActivity implements DuoMenuView.OnMenuClickListener {
    int count = 0;
    private DashboardMenuAdapter mDashboardMenuAdapter;
    private ViewHolder mViewHolder;
    private ArrayList<String> mTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_navigation);
        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.dashboard_navigation)));

        // Initialize the views
        mViewHolder = new ViewHolder();

        // Handle toolbar actions
        handleToolbar();

        // Handle menu actions
        handleMenu();

        // Handle drawer actions
        handleDrawer();

        // Show main fragment in container
        goToFragment(new DashboardHomeFragment(), false);
        mDashboardMenuAdapter.setViewSelected(0, true);
        setTitle(mTitles.get(0));
    }

    private void handleToolbar() {
        setSupportActionBar(mViewHolder.mToolbar);
    }

    private void handleDrawer() {
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                mViewHolder.mDuoDrawerLayout,
                mViewHolder.mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

    }

    private void handleMenu() {
        mDashboardMenuAdapter = new DashboardMenuAdapter(mTitles);

        mViewHolder.mDuoMenuView.setOnMenuClickListener(this);
        mViewHolder.mDuoMenuView.setAdapter(mDashboardMenuAdapter);
    }

    @Override
    public void onFooterClicked() {
    }

    @Override
    public void onHeaderClicked() {
    }

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.add(R.id.container, fragment).commit();
    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {
        // Set the toolbar title
        setTitle(mTitles.get(position));

        // Set the right options selected
        mDashboardMenuAdapter.setViewSelected(position, true);

        // Navigate to the right fragment
        switch (position) {
            case 0:
                goToFragment(new DashboardHomeFragment(), false);
                break;
            case 1:
                Intent intentEditProfile = new Intent(this, ProfilePageActivity.class);
                intentEditProfile.putExtra("fromLogin", "false");
                intentEditProfile.putExtra("fromNavigationDrawer", "true");
                startActivity(intentEditProfile);
                break;
            case 2:

                ParseUser.logOut();
                MDToast mdToast = MDToast.makeText(getApplicationContext(), "Logged Out", 5000, MDToast.TYPE_WARNING);
                mdToast.show();
                Intent intentLogOut = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intentLogOut);
                break;
        }

        // Close the drawer
        mViewHolder.mDuoDrawerLayout.closeDrawer();
    }

    @Override
    public void onBackPressed() {

        count = count + 1;

        if (count >= 2) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        } else {
            MDToast mdToast = MDToast.makeText(this, "Press one more time to close app", 5000, MDToast.TYPE_WARNING);
            mdToast.show();
        }

    }

    private class ViewHolder {
        private DuoDrawerLayout mDuoDrawerLayout;
        private DuoMenuView mDuoMenuView;
        private Toolbar mToolbar;

        ViewHolder() {
            mDuoDrawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);

            TextView User = (TextView) mDuoMenuView.findViewById(R.id.duo_view_header_text_title);
            TextView Balance = (TextView) mDuoMenuView.findViewById(R.id.duo_view_header_text_sub_title);
            Button footer = (Button) mDuoMenuView.findViewById(R.id.duo_view_footer_text);
            footer.setVisibility(View.GONE);

            User.setText(GlobalVariables.name);
            Balance.setVisibility(View.GONE);

        }
    }
}
