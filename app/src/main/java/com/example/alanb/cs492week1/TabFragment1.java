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

        if (AccessToken.getCurrentAccessToken() == null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

            FBLoginFragment fbLoginFragment = new FBLoginFragment();
            fragmentTransaction.add(R.id.fb_fragment_container, fbLoginFragment);
            fragmentTransaction.commit();
        } else {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

            FBShowFragment fbShowFragment = new FBShowFragment();
            fragmentTransaction.add(R.id.fb_fragment_container, fbShowFragment);
            fragmentTransaction.commit();
        }

        return view;
    }
}