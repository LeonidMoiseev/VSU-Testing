package com.lion.test_rating;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class PreRegistrationActivity extends AppCompatActivity {

    Button student, teacher;

    final String STUDENTS = "Студенты";
    final String TEACHERS = "Преподаватели";
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
                openRegistrationActivity(STUDENTS);
            }
        });

        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationActivity(TEACHERS);
            }
        });
    }

    private void openRegistrationActivity(String userType) {
        if (userType.equals(STUDENTS)) {
            registerIntent = new Intent(PreRegistrationActivity.this, RegistrationStudentActivity.class);
        } else if (userType.equals(TEACHERS)) {
            registerIntent = new Intent(PreRegistrationActivity.this, RegistrationTeacherActivity.class);
        }
        registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(registerIntent);
        finish();
    }
}
