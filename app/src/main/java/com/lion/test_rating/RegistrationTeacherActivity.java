package com.lion.test_rating;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegistrationTeacherActivity extends AppCompatActivity {

    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mCodeField;
    private EditText mDepartmentField;
    private Button mRegisterBtn;
    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseUsers;

    String name;
    String email;
    String password;
    String code;
    String department;
    String completeCode;
    Boolean dataUse = false;

    final String USERS = "Пользователи";
    final String TEACHERS = "Преподаватели";
    final String EMAIL = "Email";
    final String FULL_NAME = "ФИО";
    final String DEPARTMENT = "Кафедра";
    final String ACTIVATION_CODES = "Коды активации";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_teachers);

        Toolbar toolbar = findViewById(R.id.myToolBar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseUsers = mFirebaseDatabase.getReference();
        mDatabaseUsers.keepSynced(true);
        mProgress = new ProgressDialog(this);


        mNameField = findViewById(R.id.nameField);
        mDepartmentField = findViewById(R.id.departmentField);
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
        department = mDepartmentField.getText().toString().trim();
        email = mEmailField.getText().toString().trim();
        password = mPasswordField.getText().toString().trim();
        code = mCodeField.getText().toString().trim();

        validateForm2();

        if ((!TextUtils.isEmpty(name) && !TextUtils.isEmpty(department) && !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(code))) {

            mProgress.setMessage(getString(R.string.progressMessage));
            mProgress.show();

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
                mProgress.dismiss();
                Log.d("Errors", "NullPointerException");
            }
        }
    }

    private void showData(DataSnapshot dataSnapshot) {

        completeCode = (String) dataSnapshot.child(ACTIVATION_CODES).child(TEACHERS).child(name).getValue();

        try {
            if (completeCode.equals(code)) {
                createAccount();
            } else {
                mProgress.dismiss();
                Toast.makeText(RegistrationTeacherActivity.this, "Неправильный код активации",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException ex) {
            mProgress.dismiss();
            Toast.makeText(RegistrationTeacherActivity.this, "Данные указаны неправильно",
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
                        DatabaseReference current_user_db = mDatabaseUsers.child(USERS).child(TEACHERS).child(user_id);
                        current_user_db.child(FULL_NAME).setValue(name);
                        current_user_db.child(DEPARTMENT).setValue(department);
                        current_user_db.child(EMAIL).setValue(user_email);

                        mProgress.dismiss();

                        mDatabaseUsers.child(ACTIVATION_CODES).child(TEACHERS).child(name).removeValue();

                        Intent mainIntent = new Intent(RegistrationTeacherActivity.this, AccountTeacherActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                        finish();

                    } else {
                        mProgress.dismiss();
                        Toast.makeText(RegistrationTeacherActivity.this, getResources().getString(R.string.registration_error),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (NullPointerException ex) {
            mProgress.dismiss();
            Toast.makeText(RegistrationTeacherActivity.this, "Ошибка регистрации",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void validateForm2() {

        if (TextUtils.isEmpty(name)) {
            mNameField.setError(getString(R.string.required));
        } else {
            mNameField.setError(null);
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

        if (TextUtils.isEmpty(department)) {
            mDepartmentField.setError(getString(R.string.required));
        } else {
            mDepartmentField.setError(null);
        }

        if (TextUtils.isEmpty(code)) {
            mCodeField.setError(getString(R.string.required));
        } else {
            mCodeField.setError(null);
        }
    }
}
