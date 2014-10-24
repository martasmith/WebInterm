package com.codepath.hackthehood.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.codepath.hackthehood.activities.NetworkFragmentContainer;

/**
 * Created by thomasharte on 21/10/2014.
 */
public abstract class NetworkFragment extends Fragment {

    private int localNetworkActivityCount = 0;

    protected void didReceiveNetworkException(com.parse.ParseException e) {
        Activity activity = getActivity();
        if(activity instanceof NetworkFragmentContainer)
            ((NetworkFragmentContainer) activity).didReceiveException(e);
    }

    protected void incrementNetworkActivityCount() {
        localNetworkActivityCount++;
        Activity activity = getActivity();
        if(activity instanceof NetworkFragmentContainer)
            ((NetworkFragmentContainer) activity).incrementActivityCount();
    }

    protected void decrementNetworkActivityCount() {
        Activity activity = getActivity();
        if(activity instanceof NetworkFragmentContainer)
            ((NetworkFragmentContainer) activity).decrementActivityCount();
        localNetworkActivityCount--;
    }

    @Override
    public void onDestroy() {
        while(localNetworkActivityCount > 0) {
            decrementNetworkActivityCount();
        }

        super.onDestroy();
    }

    public void refresh() {

    }
}
