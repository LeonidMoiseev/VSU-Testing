package com.lion.test_rating;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResultTestActivity extends AppCompatActivity {

    private int countTrueAnswers;
    private int numberQuestions;
    private int points;
    TextView rightAnswerTV;
    TextView pointsTV;
    Button checkTest;
    Button backMenu;

    final String TESTS = "Tests";
    final String USER_COMPLETE_TEST = "Пользователи прошедшие тест";
    final String COMPLETE = "Complete";
    final String RESULTS = "Результаты";
    final String SUBJECT = "Предмет";
    final String DATA_CREATE = "Дата создания";

    String course;
    String group;
    String full_name;
    String nameTeacher;
    String numberTest;
    String dataCreateTest;
    String nameSubject;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_test);

        Toolbar toolbar = findViewById(R.id.myToolBar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        rightAnswerTV = findViewById(R.id.rightAnswer);
        pointsTV = findViewById(R.id.points);
        checkTest = findViewById(R.id.check_test);
        backMenu = findViewById(R.id.backMenu);

        Intent intent = getIntent();
        course = intent.getStringExtra("course");
        group = intent.getStringExtra("group");
        full_name = intent.getStringExtra("full_name");
        countTrueAnswers = intent.getIntExtra("countTrueAnswers", 0);
        numberQuestions = intent.getIntExtra("numberQuestions", 0);
        nameTeacher = intent.getStringExtra("nameTeacher");
        numberTest = intent.getStringExtra("numberTest");
        dataCreateTest = intent.getStringExtra("dataCreateTest");
        nameSubject = intent.getStringExtra("nameSubject");

        countPoints();
        showResult();
        writeUserCompletedTest();
        writeResultTest();

        checkTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        backMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainMenu = new Intent(ResultTestActivity.this, AccountStudentActivity.class);
                mainMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainMenu);
                finish();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showResult() {
        rightAnswerTV.setText("Правильных ответов: " + countTrueAnswers + "/" + numberQuestions);
        pointsTV.setText("Баллы: " + points);
    }

    private void countPoints() {
        points = Math.round((countTrueAnswers * 50) / numberQuestions);
    }

    private void writeUserCompletedTest() {
        mDatabase.child(TESTS).child(nameTeacher).child(numberTest).child(USER_COMPLETE_TEST).child(full_name).setValue(COMPLETE);
    }

    private void writeResultTest() {
        mDatabase.child(RESULTS).child(nameTeacher).child(numberTest).child(course).child(group).child(full_name).setValue(Integer.toString(points));
        mDatabase.child(RESULTS).child(nameTeacher).child(numberTest).child(SUBJECT).setValue(nameSubject);
        mDatabase.child(RESULTS).child(nameTeacher).child(numberTest).child(DATA_CREATE).setValue(dataCreateTest);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
