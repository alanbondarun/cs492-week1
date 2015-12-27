package com.example.alanb.cs492week1;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

/**
 * Created by alanb on 12/26/2015.
 */
public class FBLoginFragment extends Fragment
{
    public static final String TAG = "FBLoginFragment";

    private CallbackManager m_callbackManager;
    private LoginButton loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fb_login, container, false);

        // add the facebook login button
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_about_me", "email", "user_birthday", "user_hometown"));
        loginButton.setFragment(this);

        // Callback function registration
        m_callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(m_callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken.setCurrentAccessToken(loginResult.getAccessToken());

                /* replace this fragment with FBShowFragment */
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fb_fragment_container, new FBShowFragment(), FBShowFragment.TAG);
                transaction.commit();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // forward to the callback manager
        m_callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
