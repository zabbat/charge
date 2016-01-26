package net.wandroid.charge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.wandroid.charge.http.AbstractHttpTask;
import net.wandroid.charge.http.Async;
import net.wandroid.charge.http.GetTask;
import net.wandroid.charge.http.PostTask;

public class MainActivity extends AppCompatActivity {

    public static final int MIN_PASSWORD_LENGTH = 6;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmailEditText = (EditText) findViewById(R.id.email_view);
        mPasswordEditText = (EditText) findViewById(R.id.pass_view);
        mLoginButton = (Button) findViewById(R.id.login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString();
                //TODO: enable check
                //if (isCredentialsFormatted(email, password)) {

                login(email, password);
                //}
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void login(String email, String password) {
        //new PostTask(email, password).execute();
        new PostTask("programming-assignment@thenewmotion.com", "Zea2E5RA", new PostTask.IPostCompletionListener() {
            @Override
            public void onComplete(@Nullable AbstractHttpTask.HttpException httpException, PostTask.AuthResponse authResponse) {
                onAuthCompleted(httpException, authResponse);
            }
        }).execute();
    }

    @Async
    private void onAuthCompleted(AbstractHttpTask.HttpException httpException, PostTask.AuthResponse authResponse) {
        if (httpException != null) {
            Log.d("egg", "auth except=" + httpException.getMessage());
        } else {
            Log.d("egg", "token=" + authResponse.getAccessToken());
            mToken = authResponse.getAccessToken();
            displayUserName();
        }
    }

    private void displayUserName() {
        new GetTask(mToken, new GetTask.IGetCompleteListener() {
            @Override
            public void onCompleted(AbstractHttpTask.HttpException httpException, GetTask.UserInfo userInfo) {
                onGetUserInfoCompleted(httpException, userInfo);
            }
        }).execute();
    }

    @Async
    private void onGetUserInfoCompleted(AbstractHttpTask.HttpException httpException, GetTask.UserInfo userInfo) {
        if (httpException != null) {
            Log.d("egg", "user error:" + httpException.getMessage());
        } else {
            Log.d("egg", "user info:" + userInfo.getFirstName() + " " + userInfo.getLastName());
            Toast.makeText(MainActivity.this, "Logged in as " + userInfo.getFirstName() + " " + userInfo.getLastName(), Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isCredentialsFormatted(String email, String password) {
        boolean isEmailValid = !email.isEmpty() && email.contains("@");

        if (!isEmailValid) {
            mEmailEditText.setError("Not a valid Email");
        } else {
            mEmailEditText.setError(null);
        }

        boolean isPasswordValid = password.length() >= MIN_PASSWORD_LENGTH;

        if (!isPasswordValid) {
            mPasswordEditText.setError("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
        } else {
            mPasswordEditText.setError(null);
        }


        return isEmailValid && isPasswordValid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
