package com.lion.test_rating;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.lion.test_rating.StudentAccount.StudentRegistrationActivity;
import com.lion.test_rating.TeacherAccount.TeacherRegistrationActivity;

public class PreRegistrationActivity extends AppCompatActivity {

    Button student, teacher;
    Intent registerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_registration);

        Toolbar toolbar = findViewById(R.id.myToolBar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        student = findViewById(R.id.student_btn);
        teacher = findViewById(R.id.teacher_btn);

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationActivity(ConstantsNames.STUDENTS);
            }
        });

        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationActivity(ConstantsNames.TEACHERS);
            }
        });
    }

    private void openRegistrationActivity(String userType) {
        if (userType.equals(ConstantsNames.STUDENTS)) {
            registerIntent = new Intent(PreRegistrationActivity.this, StudentRegistrationActivity.class);
        } else if (userType.equals(ConstantsNames.TEACHERS)) {
            registerIntent = new Intent(PreRegistrationActivity.this, TeacherRegistrationActivity.class);
        }
        registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(registerIntent);
        finish();
    }
}
