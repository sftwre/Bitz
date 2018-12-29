package com.bitz.isaacbuitrago.bitz.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.bitz.isaacbuitrago.bitz.Model.User;
import com.bitz.isaacbuitrago.bitz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static android.support.constraint.Constraints.TAG;

/**
 * Activity to create an account with Bitz.
 *
 * @author isaacbuitrago
 */
public class CreateAccountActivity extends AppCompatActivity
{
    // UI references.
    private AutoCompleteTextView mEmailView;

    private EditText mFirstNameView;

    private EditText mLastNameView;

    private EditText mUsernameView;

    private EditText mPasswordView;

    private View mProgressView;

    private View mLoginFormView;

    private TextView mSignInLink;

    private Button mCreateAccountButton;

    private FirebaseAuth mAuth;         // firebase reference

    private FirebaseDatabase mDatabase; // firebase database reference

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_account);

        // bind UI references to layout controls
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mFirstNameView = (EditText) findViewById(R.id.firstName);
        mLastNameView = (EditText) findViewById(R.id.lastName);
        mUsernameView = (EditText) findViewById(R.id.username);
        mSignInLink = (TextView) findViewById(R.id.signInLink);
        mCreateAccountButton = (Button) findViewById(R.id.create_accont_button);

        // default to disabled
        mCreateAccountButton.setEnabled(false);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance();
    }

    /**
     *
     */
    @Override
    protected void onStart()
    {
        super.onStart();

        // Defines listeners for UI controls
        mSignInLink.setOnClickListener((l) ->
        {
            Intent intent = new Intent(CreateAccountActivity.this, SignInActivity.class);

            startActivity(intent);

            finish();
        });

        mCreateAccountButton.setOnClickListener((l) ->
        {
            createAccount();
        });

        mUsernameView.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {

                String username = ((EditText) v).getText().toString();

                if(! hasFocus && ! username.isEmpty())
                {

                        // verify that the username does not already exists
                        mDatabase.getReference(getString(R.string.dbname_usernames))
                                .child(username)
                                .addListenerForSingleValueEvent(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        if (dataSnapshot.getValue() != null)
                                        {
                                            // username is already in use
                                            mUsernameView.setError(getString(R.string.error_user_name_taken));
                                            mUsernameView.requestFocus();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }

                                });
                }
            }
        });

        // check if the user name already exists
        mEmailView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                String email = ((EditText) v).getText().toString();

                if(! hasFocus && ! email.isEmpty())
                {
                    try
                    {
                        // verify that the email does not already exists
                        FirebaseAuth
                                .getInstance()
                                .fetchSignInMethodsForEmail(((AutoCompleteTextView) v).getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            boolean check = !task.getResult().getSignInMethods().isEmpty();

                                            // email is in use
                                            if (check)
                                            {
                                                mEmailView.setError(getString(R.string.error_email_taken));
                                                mEmailView.requestFocus();
                                            }
                                        }
                                    }
                                });
                    } catch (Exception e)
                    {
                        // invalid email
                    }
                }

            }
        });

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void createAccount()
    {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        User newUser = new User(mFirstNameView.getText().toString().trim(),
                                mLastNameView.getText().toString().trim(),
                                mUsernameView.getText().toString().trim());

        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // validate input

        // Check for a valid password
        if (TextUtils.isEmpty(password) || !isPasswordValid(password))
        {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email) || !isEmailValid(email))
        {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel)
        {
            // There was an error; don't attempt account creation and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else {

            newUser.setEmail(email);
            newUser.setPassword(password);

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            mAuth.createUserWithEmailAndPassword(newUser.getEmail(), newUser.getPassword())

                    .addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                // user successfully created and authenticated
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                                // Store account information in the database
                                newUser.setId(currentUser.getUid());

                                //TODO write the data in the User node

                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");

                                showProgress(false);
                            }
                            else {

                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());

                                Toast.makeText(CreateAccountActivity.this, "Error with authentication", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private boolean isEmailValid(String email)
    {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password)
    {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

