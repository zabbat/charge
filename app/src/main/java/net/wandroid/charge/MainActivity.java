package net.wandroid.charge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import net.wandroid.charge.http.AbstractHttpTask;
import net.wandroid.charge.http.Async;
import net.wandroid.charge.http.GetTask;

public class MainActivity extends AppCompatActivity implements LoginFragment.ILoginFragmentListener {


    public static final String LOGIN_FRAG = "LOGIN_FRAG";
    public static final String KEY_TOKEN = "KEY_TOKEN";
    public static final String KEY_USER_INFO = "KEY_USER_INFO";
    /**
     * time until map activity should be displayed
     */
    public static final int DELAY_MILLIS = 3000;

    private TextView mInfoText;

    private String mToken;

    private GetTask.UserInfo mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInfoText = (TextView) findViewById(R.id.info_text);

        if (savedInstanceState != null) {
            //Check if token or userInfo is saved
            if (savedInstanceState.containsKey(KEY_TOKEN)) {
                mToken = savedInstanceState.getString(KEY_TOKEN);
            }
            if (savedInstanceState.containsKey(KEY_USER_INFO)) {
                //If we already got UserInfo then we are in the display welcome message mode
                mUserInfo = (GetTask.UserInfo) savedInstanceState.getSerializable(KEY_USER_INFO);
                mInfoText.setText(getString(R.string.login_message, (mUserInfo.getFirstName() + " " + mUserInfo.getLastName())));
                mInfoText.setVisibility(View.VISIBLE);
            }
        } else {
            //First time the app is created attach a login fragment to it.
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frag_container, new LoginFragment(), LOGIN_FRAG).commit();
        }
    }


    /**
     * Retrieves UserInfo and displays the user name.
     * @param token the auth token
     */
    private void displayUserName(String token) {
        new GetTask(token, new GetTask.IGetCompleteListener() {
            @Override
            public void onCompleted(AbstractHttpTask.HttpException httpException, GetTask.UserInfo userInfo) {
                onGetUserInfoCompleted(httpException, userInfo);
            }
        }).execute();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //save members so we can keep track on the states
        if (mToken != null) {
            outState.putString(KEY_TOKEN, mToken);
        }
        if (mUserInfo != null) {
            outState.putSerializable(KEY_USER_INFO, mUserInfo);
        }
        super.onSaveInstanceState(outState);
    }


    /**
     * handles result when USerInfo is rerteived.
     * @param httpException is non null if retreiving failed. Will be null if no exception.
     * @param userInfo the UserInfo.Will be null if there was an exception
     */
    @Async
    private void onGetUserInfoCompleted(AbstractHttpTask.HttpException httpException, GetTask.UserInfo userInfo) {

        if (httpException != null) {
            //Retreiving UserInfo failed. Display toast and let user login again.
            Toast.makeText(this, R.string.connection_failed, Toast.LENGTH_LONG).show();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frag_container, new LoginFragment(), LOGIN_FRAG).commit();
            mToken=null;
        } else {
            mUserInfo = userInfo;
            mInfoText.setText(getString(R.string.login_message, (mUserInfo.getFirstName() + " " + mUserInfo.getLastName())));
            mInfoText.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                    finish();
                }
            }, DELAY_MILLIS);

        }
    }


    @Override
    @Async
    public void onLoggedIn(String token) {
        mToken = token;

        // remove login fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(LOGIN_FRAG);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }

        //Hide keyboard
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        displayUserName(mToken);
    }
}
