package com.lion.test_rating;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lion.test_rating.StudentAccount.AccountStudentActivity;
import com.lion.test_rating.TeacherAccount.AccountTeacherActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mEmailField;
    private EditText mPasswordField;
    private ProgressBar progressBar;
    private AlertDialog dialogProgressBar;

    String email;
    String password;
    Boolean dataUsed = false;
    Intent loginIntent;

    TextView progressBarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_progress_bar, null);
        mBuilder.setView(mView);
        dialogProgressBar = mBuilder.create();
        progressBar = mView.findViewById(R.id.progressBar);
        progressBarText = mView.findViewById(R.id.text_progress_bar);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    dialogProgressBar.show();
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    getUserInformationAndStartActivity();
                } else activityVisible();
            }
        };
    }


    public void signing() {
        email = mEmailField.getText().toString().trim();
        password = mPasswordField.getText().toString().trim();

        validateForm();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            progressBarText.setText(getString(R.string.progressMessage));
            dialogProgressBar.show();
            progressBar.setVisibility(ProgressBar.VISIBLE);

            try {

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getUserInformationAndStartActivity();
                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                            dialogProgressBar.dismiss();
                        } else {
                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                            dialogProgressBar.dismiss();
                            Toast.makeText(MainActivity.this, R.string.authorization_error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } catch (NullPointerException ex) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                dialogProgressBar.dismiss();
                Toast.makeText(MainActivity.this, R.string.authorization_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void activityVisible() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.myToolBar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        mEmailField = findViewById(R.id.et_email);
        mPasswordField = findViewById(R.id.et_password);
        Button mSigning = findViewById(R.id.btn_sign_in);
        Button mRegister = findViewById(R.id.btn_registration);

        mSigning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signing();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PreRegistrationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getUserInformationAndStartActivity() {

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userDatabase = mFirebaseDatabase.getReference().child(ConstantsNames.USERS);

        try {
            userDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataUsed) {
                        try {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            assert user != null;
                            String userID = user.getUid();
                            if (dataSnapshot.child(ConstantsNames.STUDENTS).hasChild(userID)) {
                                loginIntent = new Intent(MainActivity.this, AccountStudentActivity.class);
                            } else if (dataSnapshot.child(ConstantsNames.TEACHERS).hasChild(userID)) {
                                loginIntent = new Intent(MainActivity.this, AccountTeacherActivity.class);
                            }
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(loginIntent);
                            dataUsed = true;
                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                            dialogProgressBar.dismiss();
                            finish();

                        } catch (NullPointerException ex) {
                            errorNull();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    errorNull();
                }
            });
        } catch (NullPointerException ex) {
            errorNull();
        }
    }

    private void validateForm() {

        if (TextUtils.isEmpty(email)) {
            mEmailField.setError(getString(R.string.required));
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError(getString(R.string.required));
        } else {
            mPasswordField.setError(null);
        }
    }

    private void errorNull() {
        Log.d("Errors", "NullPointerException");
        Toast.makeText(this, "Ошибка соединения с сервером..", Toast.LENGTH_LONG).show();
        logout();
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        dialogProgressBar.dismiss();
    }

    private void logout() {
        mAuth.signOut();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
