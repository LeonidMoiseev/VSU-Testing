package com.lion.test_rating.StudentAccount;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lion.test_rating.ConstantsNames;
import com.lion.test_rating.R;

public class ResultTestActivity extends AppCompatActivity {

    private int countTrueAnswers;
    private int numberQuestions;
    private int points;
    TextView rightAnswerTV;
    TextView pointsTV;
    Button checkTest;
    Button backMenu;

    String nameTeacher;
    String numberTest;
    String dataCreateTest;
    String nameSubject;
    String topicName;

    DatabaseReference mDatabaseUserComplete;
    DatabaseReference mDatabaseUserResult;
    DatabaseReference mDatabaseRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_student_result_test);

        Toolbar toolbar = findViewById(R.id.myToolBar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        rightAnswerTV = findViewById(R.id.rightAnswer);
        pointsTV = findViewById(R.id.points);
        checkTest = findViewById(R.id.check_test);
        backMenu = findViewById(R.id.backMenu);

        Intent intent = getIntent();
        countTrueAnswers = intent.getIntExtra("countTrueAnswers", 0);
        numberQuestions = intent.getIntExtra("numberQuestions", 0);
        nameTeacher = intent.getStringExtra("nameTeacher");
        numberTest = intent.getStringExtra("numberTest");
        dataCreateTest = intent.getStringExtra("dataCreateTest");
        nameSubject = intent.getStringExtra("nameSubject");
        topicName = intent.getStringExtra("topicName");

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
        mDatabaseUserComplete = FirebaseDatabase.getInstance().getReference();
        mDatabaseUserComplete.keepSynced(true);

        mDatabaseUserComplete.child(ConstantsNames.TESTS).child(nameTeacher).child(numberTest)
                .child(ConstantsNames.USER_COMPLETE_TEST)
                .child(AccountStudentActivity.mListUserInformation.get(0)).setValue(ConstantsNames.COMPLETE);
    }

    private void writeResultTest() {
        mDatabaseUserResult = FirebaseDatabase.getInstance().getReference()
                .child(ConstantsNames.RESULTS).child(nameTeacher).child(numberTest);
        mDatabaseUserResult.keepSynced(true);

        mDatabaseUserResult.child(AccountStudentActivity.mListUserInformation.get(2))
                .child(AccountStudentActivity.mListUserInformation.get(3))
                .child(AccountStudentActivity.mListUserInformation.get(0))
                .setValue(Integer.toString(points));
        mDatabaseUserResult.child(ConstantsNames.SUBJECT).setValue(nameSubject);
        mDatabaseUserResult.child(ConstantsNames.DATE_CREATE).setValue(dataCreateTest);
        mDatabaseUserResult.child(ConstantsNames.TOPIC_NAME).setValue(topicName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        mDatabaseRating = FirebaseDatabase.getInstance().getReference().child(ConstantsNames.USERS)
                .child(ConstantsNames.STUDENTS).child(userID);

        int newPoints = Integer.parseInt(AccountStudentActivity.mListUserInformation.get(4)) + points;
        mDatabaseRating.child(ConstantsNames.RATING).setValue(Integer.toString(newPoints));

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
