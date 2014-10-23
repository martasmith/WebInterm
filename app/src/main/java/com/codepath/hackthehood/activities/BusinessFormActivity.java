package com.codepath.hackthehood.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.hackthehood.R;
import com.codepath.hackthehood.fragments.BusinessFormFragment;
import com.codepath.hackthehood.models.User;
import com.parse.ParseUser;

public class BusinessFormActivity extends FragmentActivity {

    private BusinessFormFragment businessFormFragment;

    final static public String USER = "USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_form);
        //get Fragment instance
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        businessFormFragment = new BusinessFormFragment();
        ft.replace(R.id.flBusinessForm, businessFormFragment);
        ft.commit();

        // check whether we should be skipping ahead
        User user = (User) ParseUser.getCurrentUser();
        try {
            user.fetch();
        } catch (Exception e) {}
        switch(user.getApplicationStatus()) {
            case User.APPSTATUS_ACCEPTED:
                startActivity(new Intent(this, AssetCollectionActivity.class));
            break;
            case User.APPSTATUS_STARTED:
            break;
            default:
                startActivity(new Intent(this, ConfirmationActivity.class));
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.business_form, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeUser(MenuItem menuItem) {
        businessFormFragment.storeCurrentForm();
        ParseUser.logOut();
        startActivity(new Intent(this, PitchDeckActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
