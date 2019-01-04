package com.bitz.isaacbuitrago.bitz.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.regex.Pattern;
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

    private View mCreateAccountFormView;

    private TextView mSignInLink;

    private Button mCreateAccountButton;

    private FirebaseAuth mAuth;         // firebase reference

    private FirebaseDatabase mDatabase; // firebase database reference

    // constants
    private static final int MIN_PASS_LEN = 8;
    private static final int DELAY_TIME = 5000;

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

        mCreateAccountFormView = findViewById(R.id.userInfoForm);
        mProgressView = findViewById(R.id.createAccountProgress);

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
    private void createAccount() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String firstName = mFirstNameView.getText().toString().trim();
        String lastName = mLastNameView.getText().toString().trim();
        String userName = mUsernameView.getText().toString().trim();
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // validate password
        if (password.isEmpty() || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // validate email
        if (email.isEmpty() || !isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // validate first name
        if (firstName.isEmpty() || !isNameValid(firstName)) {
            mFirstNameView.setError(getString(R.string.error_invalid_first_name));
            focusView = mFirstNameView;
            cancel = true;
        }

        // validate last name
        if (lastName.isEmpty() || !isNameValid(lastName)) {
            mLastNameView.setError(getString(R.string.error_invalid_last_name));
            focusView = mLastNameView;
            cancel = true;
        }

        // validate username
        if (userName.isEmpty())
        {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel)
        {
            // There was an error; don't attempt account creation and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else {

            User user = new User();

            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(userName);

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            mAuth.createUserWithEmailAndPassword(email, password)

                    .addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {

                                HashMap<String, Object> childUpdates = new HashMap<>();

                                // user successfully created and authenticated
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                                // write user name and user data in the database
                                user.setId(currentUser.getUid());

                                final String users = String.format("%s/%s", getString(R.string.dbname_users), user.getId());

                                childUpdates.put(users, user);

                                childUpdates.put(getString(R.string.dbname_usernames), 1);

                                mDatabase.getReference()
                                        .updateChildren(childUpdates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>()
                                        {

                                            // data successfully stored, continue to Home Activity
                                            @Override
                                            public void onSuccess(Void aVoid)
                                            {
                                                Intent intent = new Intent(CreateAccountActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener()
                                        {
                                            @Override
                                            public void onFailure(@NonNull Exception e)
                                            {
                                                currentUser.delete();

                                                mFirstNameView.getText().clear();
                                                mLastNameView.getText().clear();
                                                mUsernameView.getText().clear();
                                                mEmailView.getText().clear();
                                                mPasswordView.getText().clear();

                                                // Warn the user
                                                AlertDialog.Builder alert = new AlertDialog.Builder(CreateAccountActivity.this)
                                                        .setMessage(getString(R.string.error_account_creation));

                                                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener(){

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which)
                                                    {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                final AlertDialog dialog = alert.create();
                                                dialog.show();

                                                Runnable run = new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        if(dialog.isShowing())
                                                            dialog.dismiss();
                                                    }
                                                };

                                                // close the Dialog after 5 seconds
                                                mCreateAccountFormView.postDelayed(run, DELAY_TIME);

                                            }
                                        });


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

    /**
     * Matches the user's Email against a Pattern for a legit email
     *
     * @param email to validate
     * @return true if the Email matches the Pattern, false otherwise
     */
    private boolean isEmailValid(String email)
    {
        return Pattern.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$", email);
    }

    /**
     * Checks if the password length is >= 8.
     *
     * It is assumed that that password is not null or empty.
     *
     * @param password
     * @return True if password length is at least 8, false otherwise
     */
    private boolean isPasswordValid(String password)
    {
        return password.length() >= MIN_PASS_LEN;
    }

    /**
     * Validates if the user's first & last name contains alphabetic characters.
     *
     * @param name to validate
     * @return True if full name matches the pattern, false otherwise
     */
    private boolean isNameValid(String name)
    {
        return(Pattern.matches("[a-zA-Z]+", name));
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mCreateAccountFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mCreateAccountFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCreateAccountFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mCreateAccountFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

