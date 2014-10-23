package com.codepath.hackthehood.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.hackthehood.R;
import com.codepath.hackthehood.fragments.AssetCollectionFragment;
import com.parse.ParseUser;

public class AssetCollectionActivity extends FragmentActivity {

    private AssetCollectionFragment assetCollectionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_collection);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        assetCollectionFragment = new AssetCollectionFragment();
        ft.replace(R.id.flAssetCollection, assetCollectionFragment);
        ft.commit();
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
        ParseUser.logOut();
        startActivity(new Intent(this, PitchDeckActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
