package com.lion.test_rating.TeacherAccount;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.lion.test_rating.ConstantsNames;
import com.lion.test_rating.R;

public class RegistrationTeacherActivity extends AppCompatActivity {

    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mCodeField;
    private EditText mDepartmentField;
    private AlertDialog dialogProgressBar;
    private ProgressBar progressBar;

    TextView progressBarText;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;

    String name;
    String email;
    String password;
    String code;
    String department;
    String completeCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_teacher_registration);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegistrationTeacherActivity.this);
        @SuppressLint("InflateParams")
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
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseUsers = mFirebaseDatabase.getReference();
        mDatabaseUsers.keepSynced(true);

        mNameField = findViewById(R.id.nameField);
        mDepartmentField = findViewById(R.id.departmentField);
        mEmailField = findViewById(R.id.emailField);
        mPasswordField = findViewById(R.id.passwordField);
        mCodeField = findViewById(R.id.codeField);
        Button mRegisterBtn = findViewById(R.id.registerBtn);

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

        validateForm();

        if ((!TextUtils.isEmpty(name) && !TextUtils.isEmpty(department) && !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(code))) {

            dialogProgressBar.show();
            progressBar.setVisibility(ProgressBar.VISIBLE);

            try {
                mDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            validateOfEnteredData(dataSnapshot);
                        } else {
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } catch (NullPointerException ex) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                dialogProgressBar.dismiss();
            }
        }
    }

    private void validateOfEnteredData(DataSnapshot dataSnapshot) {

        if (dataSnapshot.child(ConstantsNames.ACTIVATION_CODES)
                .child(ConstantsNames.TEACHERS).hasChild(name)) {

            completeCode = (String) dataSnapshot.child(ConstantsNames.ACTIVATION_CODES)
                    .child(ConstantsNames.TEACHERS).child(name).getValue();

            if (code.equals(completeCode)) {
                createAccount();
            } else {
                errorRegistration(getString(R.string.error_code_activation));
            }

        } else {
            errorRegistration(getString(R.string.error_data_name));
        }
    }

    private void createAccount() {
        try {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                assert user != null;
                                String userID = user.getUid();
                                String user_email = user.getEmail();

                                DatabaseReference current_user_db = mDatabaseUsers.child(ConstantsNames.USERS).
                                        child(ConstantsNames.TEACHERS).child(userID);

                                current_user_db.child(ConstantsNames.FULL_NAME).setValue(name);
                                current_user_db.child(ConstantsNames.DEPARTMENT).setValue(department);
                                current_user_db.child(ConstantsNames.EMAIL).setValue(user_email);

                                progressBar.setVisibility(ProgressBar.INVISIBLE);
                                dialogProgressBar.dismiss();

                                mDatabaseUsers.child(ConstantsNames.ACTIVATION_CODES)
                                        .child(ConstantsNames.TEACHERS).child(name).removeValue();

                                Intent mainIntent = new Intent(RegistrationTeacherActivity.this
                                        , AccountTeacherActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mainIntent);
                                finish();

                            } else {
                                errorRegistration(getString(R.string.registration_error_with_email));
                            }
                        }
                    });
        } catch (NullPointerException ex) {
            errorRegistration(getString(R.string.registration_error));
        }
    }

    private void validateForm() {

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

    private void errorRegistration(String message) {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        dialogProgressBar.dismiss();
        Toast.makeText(RegistrationTeacherActivity.this, message,
                Toast.LENGTH_SHORT).show();
    }
}
