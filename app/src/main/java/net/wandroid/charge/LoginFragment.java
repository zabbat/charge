package net.wandroid.charge;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.wandroid.charge.http.AbstractHttpTask;
import net.wandroid.charge.http.Async;
import net.wandroid.charge.http.PostTask;


public class LoginFragment extends Fragment {

    public static final int MIN_PASSWORD_LENGTH = 6;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private ILoginFragmentListener mLoginFragmentListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_login, container, false);

        mEmailEditText = (EditText) view.findViewById(R.id.email_view);
        mPasswordEditText = (EditText) view.findViewById(R.id.pass_view);
        mLoginButton = (Button) view.findViewById(R.id.login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString();

                if (isCredentialsFormatted(email, password)) {

                    login(email, password);
                }
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ILoginFragmentListener) {
            mLoginFragmentListener = (ILoginFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        mLoginFragmentListener = null;

        super.onDetach();

    }

    /**
     * Authorize through http.
     * @param email email of the user
     * @param password password of the user
     */
    private void login(String email, String password) {
        mLoginButton.setEnabled(false);
        mEmailEditText.setEnabled(false);
        mPasswordEditText.setEnabled(false);

        new PostTask(email, password, new PostTask.IPostCompletionListener() {
            @Override
            public void onComplete(@Nullable AbstractHttpTask.HttpException httpException, PostTask.AuthResponse authResponse) {
                onAuthCompleted(httpException, authResponse);
            }
        }).execute();
    }

    /**
     * Called when authorizing is completed
     * @param httpException will be non null if there wa an error, otherwise null
     * @param authResponse The response. Will be null if there was an exception
     */
    @Async
    private void onAuthCompleted(AbstractHttpTask.HttpException httpException, PostTask.AuthResponse authResponse) {
        mLoginButton.setEnabled(true);
        mEmailEditText.setEnabled(true);
        mPasswordEditText.setEnabled(true);

        if (httpException != null) {
            //since async the activity might be dead
            Activity activity = getActivity();
            if(activity!=null){
                Toast.makeText(activity, R.string.auth_failed, Toast.LENGTH_LONG).show();
            }
        } else {

            if (mLoginFragmentListener != null) {
                mLoginFragmentListener.onLoggedIn(authResponse.getAccessToken());
            }
        }
    }

    /**
     * checks if email/password is valid. Will display error message otherwise
     * @param email the email address
     * @param password the password
     * @return true if credentials are valid, otherwise false
     */
    private boolean isCredentialsFormatted(String email, String password) {
        boolean isEmailValid = !email.isEmpty() && email.contains("@");

        if (!isEmailValid) {
            mEmailEditText.setError(getResources().getString(R.string.error_email));
        } else {
            mEmailEditText.setError(null);
        }

        boolean isPasswordValid = password.length() >= MIN_PASSWORD_LENGTH;

        if (!isPasswordValid) {
            mPasswordEditText.setError(getResources().getString(R.string.error_passwor_short, MIN_PASSWORD_LENGTH));
        } else {
            mPasswordEditText.setError(null);
        }


        return isEmailValid && isPasswordValid;
    }

    public interface ILoginFragmentListener {
        /**
         * Callback when successfully logged in
         * @param token the auth token received when logged in
         */
        void onLoggedIn(String token);
    }

}
