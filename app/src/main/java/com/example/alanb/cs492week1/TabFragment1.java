package com.example.alanb.cs492week1;

/**
 * Created by shinjaemin on 2015. 12. 23..
 */

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

public class TabFragment1 extends Fragment {
    private final static String TAG = "TabFragment1";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        /* adds the appropriate child fragment to this fragment.
         * adds FBLoginFragment if the user has not logged in; otherwise, adds FBShowFragment.
         */
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (AccessToken.getCurrentAccessToken() == null) {
            Fragment currentFragment = new FBLoginFragment();
            fragmentTransaction.add(R.id.fb_fragment_container, currentFragment, FBLoginFragment.TAG);
        } else {
            Fragment currentFragment = new FBShowFragment();
            fragmentTransaction.add(R.id.fb_fragment_container, currentFragment, FBShowFragment.TAG);
        }
        fragmentTransaction.commit();

        return view;
    }
}