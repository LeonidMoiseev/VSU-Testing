package com.lion.test_rating.StudentAccount;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lion.test_rating.ConstantsNames;
import com.lion.test_rating.R;

public class RegistrationStudentActivity extends AppCompatActivity {

    private EditText mNameField;
    private EditText mCourseField;
    private EditText mGroupField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mCodeField;
    private Button mRegisterBtn;
    private AlertDialog dialogProgressBar;
    private ProgressBar progressBar;

    TextView progressBarText;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseUsers;

    String name;
    String course;
    String group;
    String email;
    String password;
    String code;

    String completeCode = "";
    Boolean dataUse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_student_registration);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegistrationStudentActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_progress_bar, null);
        mBuilder.setView(mView);
        dialogProgressBar = mBuilder.create();
        progressBar = mView.findViewById(R.id.progressBar);
        progressBarText = mView.findViewById(R.id.text_progress_bar);
        progressBarText.setText(getString(R.string.progressMessage));

        Toolbar toolbar = findViewById(R.id.myToolBar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseUsers = mFirebaseDatabase.getReference();
        mDatabaseUsers.keepSynced(true);


        mNameField = findViewById(R.id.nameField);
        mCourseField = findViewById(R.id.courseField);
        mGroupField = findViewById(R.id.groupField);
        mEmailField = findViewById(R.id.emailField);
        mPasswordField = findViewById(R.id.passwordField);
        mCodeField = findViewById(R.id.codeField);
        mRegisterBtn = findViewById(R.id.registerBtn);


        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegistration();
            }
        });
    }

    private void startRegistration() {
        name = mNameField.getText().toString().trim();
        course = mCourseField.getText().toString().trim();
        group = mGroupField.getText().toString().trim();
        email = mEmailField.getText().toString().trim();
        password = mPasswordField.getText().toString().trim();
        code = mCodeField.getText().toString().trim();

        validateForm();

        if ((!TextUtils.isEmpty(name) && !TextUtils.isEmpty(course) && !TextUtils.isEmpty(group)
                && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(code))) {

            dialogProgressBar.show();
            progressBar.setVisibility(ProgressBar.VISIBLE);

            try {
                mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataUse) {
                            showData(dataSnapshot);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } catch (NullPointerException ex) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                dialogProgressBar.dismiss();
                Log.d("Errors", "NullPointerException");
            }
        }
    }

    private void showData(DataSnapshot dataSnapshot) {

        completeCode = (String) dataSnapshot.child(ConstantsNames.ACTIVATION_CODES).child(ConstantsNames.STUDENTS).
                child(course).child(group).child(name).getValue();

        try {
            if (completeCode.equals(code)) {
                createAccount();
            } else {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                dialogProgressBar.dismiss();
                Toast.makeText(RegistrationStudentActivity.this, "Неправильный код активации",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException ex) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            dialogProgressBar.dismiss();
            Toast.makeText(RegistrationStudentActivity.this, "Данные указаны неправильно",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void createAccount() {
        try {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        dataUse = true;

                        String user_id = mAuth.getCurrentUser().getUid();
                        String user_email = mAuth.getCurrentUser().getEmail();

                        DatabaseReference current_user_db = mDatabaseUsers.child(ConstantsNames.USERS).
                                child(ConstantsNames.STUDENTS).child(user_id);
                        current_user_db.child(ConstantsNames.FULL_NAME).setValue(name);
                        current_user_db.child(ConstantsNames.COURSE).setValue(course);
                        current_user_db.child(ConstantsNames.GROUP).setValue(group);
                        current_user_db.child(ConstantsNames.RATING).setValue("0");
                        current_user_db.child(ConstantsNames.EMAIL).setValue(user_email);

                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        dialogProgressBar.dismiss();

                        mDatabaseUsers.child(ConstantsNames.ACTIVATION_CODES).child(ConstantsNames.STUDENTS).
                                child(course).child(group).child(name).removeValue();

                        Intent mainIntent = new Intent(RegistrationStudentActivity.this, AccountStudentActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                        finish();

                    } else {
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        dialogProgressBar.dismiss();
                        Toast.makeText(RegistrationStudentActivity.this, getResources().getString(R.string.registration_error),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (NullPointerException ex) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            dialogProgressBar.dismiss();
            Toast.makeText(RegistrationStudentActivity.this, "Ошибка регистрации",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void validateForm() {

        if (TextUtils.isEmpty(name)) {
            mNameField.setError(getString(R.string.required));
        } else {
            mNameField.setError(null);
        }

        if (TextUtils.isEmpty(course)) {
            mCourseField.setError(getString(R.string.required));
        } else {
            mCourseField.setError(null);
        }

        if (TextUtils.isEmpty(group)) {
            mGroupField.setError(getString(R.string.required));
        } else {
            mGroupField.setError(null);
        }

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

        if (TextUtils.isEmpty(code)) {
            mCodeField.setError(getString(R.string.required));
        } else {
            mCodeField.setError(null);
        }
    }
}
