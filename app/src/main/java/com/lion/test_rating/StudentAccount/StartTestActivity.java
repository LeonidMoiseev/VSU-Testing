package com.lion.test_rating.StudentAccount;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lion.test_rating.ConstantsNames;
import com.lion.test_rating.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class StartTestActivity extends AppCompatActivity {

    private ProgressDialog mProgress;
    TextView mQuestion;
    TextView mNumberOfQuestion;
    TextView mTimeLeft;
    Button answer1;
    Button answer2;
    Button answer3;
    Button answer4;
    ImageButton previousQuestion;
    ImageButton nextQuestion;
    Button finishTest;
    CountDownTimer cTimer;

    private boolean listDownload = false;
    private boolean testFinished = false;
    private int countTrueAnswers = 0;
    private int countQuestion = 0;
    private int numberQuestion;
    private int numberAllQuestion;
    private int testTime;
    private String numberTest;
    private String nameTeacher;
    private Integer[] masNumberQuestion;
    private Integer[] masNumberAnswers;

    private ArrayList<String> mListQuestion = new ArrayList<>();
    private ArrayList<String> mListAnswer1 = new ArrayList<>();
    private ArrayList<String> mListAnswer2 = new ArrayList<>();
    private ArrayList<String> mListAnswer3 = new ArrayList<>();
    private ArrayList<String> mListAnswer4 = new ArrayList<>();
    private ArrayList<String> mListCorrectAnswer = new ArrayList<>();
    private ArrayList<Integer> mListUserAnswer = new ArrayList<>();
    private ArrayList<String> mListAnswers = new ArrayList<>();

    private ArrayList<Integer> mListColorButton1 = new ArrayList<>();
    private ArrayList<Integer> mListColorButton2 = new ArrayList<>();
    private ArrayList<Integer> mListColorButton3 = new ArrayList<>();
    private ArrayList<Integer> mListColorButton4 = new ArrayList<>();

    private String course;
    private String group;
    private String full_name;
    private String dataCreateTest;
    private String nameSubject;
    private String topicName;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_student_start_test);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child(ConstantsNames.TESTS);
        myRef.keepSynced(true);

        Intent intent = getIntent();
        course = intent.getStringExtra("course");
        group = intent.getStringExtra("group");
        full_name = intent.getStringExtra("full_name");
        nameTeacher = intent.getStringExtra("nameTeacher");
        numberTest = intent.getStringExtra("numberTest");
        dataCreateTest = intent.getStringExtra("dataCreateTest");
        nameSubject = intent.getStringExtra("nameSubject");
        topicName = intent.getStringExtra("topicName");
        numberQuestion = Integer.parseInt(intent.getStringExtra("restrictionTest"));
        testTime = Integer.parseInt(intent.getStringExtra("testTime"));

        previousQuestion = findViewById(R.id.previous_question);
        nextQuestion = findViewById(R.id.next_question);
        finishTest = findViewById(R.id.finish_test);
        mTimeLeft = findViewById(R.id.time_tv);
        mQuestion = findViewById(R.id.question);
        mNumberOfQuestion = findViewById(R.id.numberQuestion);
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        answer4 = findViewById(R.id.answer4);

        mProgress = new ProgressDialog(this);

        try {
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!listDownload) {
                        mProgress.setMessage("Загрузка вопросов ...");
                        mProgress.show();
                        clearLists();
                        numberAllQuestion = (int) dataSnapshot.child(nameTeacher).child(numberTest).
                                child(ConstantsNames.QUESTION_TEST).getChildrenCount();
                        createListColorBtnAndListUserAnswer();
                        numberAnswer();
                        randomNumberQuestion();
                        addQuestionAndAnswer(dataSnapshot);
                        changeQuestionAndAnswer();
                        previousAndNextQuestionButton();
                        countDownTimer();
                    }
                    mProgress.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    errorNull();
                }
            });
        } catch (NullPointerException ex) {
            Log.d("Errors", "NullPointerException");
            errorNull();
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.answer1:
                        changeListAndSetColorBtn(mListColorButton1, mListColorButton2, mListColorButton3, mListColorButton4
                                , answer1, answer2, answer3, answer4);
                        checkRightAnswer(mListAnswer1);
                        break;
                    case R.id.answer2:
                        changeListAndSetColorBtn(mListColorButton2, mListColorButton1, mListColorButton3, mListColorButton4
                                , answer2, answer1, answer3, answer4);
                        checkRightAnswer(mListAnswer2);
                        break;
                    case R.id.answer3:
                        changeListAndSetColorBtn(mListColorButton3, mListColorButton1, mListColorButton2, mListColorButton4
                                , answer3, answer1, answer2, answer4);
                        checkRightAnswer(mListAnswer3);
                        break;
                    case R.id.answer4:
                        changeListAndSetColorBtn(mListColorButton4, mListColorButton1, mListColorButton2, mListColorButton3
                                , answer4, answer1, answer2, answer3);
                        checkRightAnswer(mListAnswer4);
                        break;
                }
            }
        };
        answer1.setOnClickListener(onClickListener);
        answer2.setOnClickListener(onClickListener);
        answer3.setOnClickListener(onClickListener);
        answer4.setOnClickListener(onClickListener);
    }

    private void addQuestionAndAnswer(DataSnapshot dataSnapshot) {
        for (int k = 0; k < numberQuestion; k++) {
            randomNumberAnswers();
            String number = Integer.toString(masNumberQuestion[k]);
            DataSnapshot testDB = dataSnapshot.child(nameTeacher).child(numberTest).
                    child(ConstantsNames.QUESTION_TEST).child(number);

            mListQuestion.add((String) testDB.child(ConstantsNames.QUESTION).getValue());
            mListAnswer1.add((String) testDB.child(mListAnswers.get(masNumberAnswers[0])).getValue());
            mListAnswer2.add((String) testDB.child(mListAnswers.get(masNumberAnswers[1])).getValue());
            mListAnswer3.add((String) testDB.child(mListAnswers.get(masNumberAnswers[2])).getValue());
            mListAnswer4.add((String) testDB.child(mListAnswers.get(masNumberAnswers[3])).getValue());
            mListCorrectAnswer.add((String) testDB.child(ConstantsNames.CORRECT_ANSWER).getValue());
        }
        listDownload = true;
    }

    private void changeListAndSetColorBtn(ArrayList<Integer> listColorBtn1, ArrayList<Integer> listColorBtn2
            , ArrayList<Integer> listColorBtn3, ArrayList<Integer> listColorBtn4
            , Button btn, Button other_btn_1, Button other_btn_2, Button other_btn_3) {

        btn.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
        listColorBtn1.set(countQuestion, R.drawable.btn_yellow);
        listColorBtn2.set(countQuestion, R.drawable.ripple_btn);
        listColorBtn3.set(countQuestion, R.drawable.ripple_btn);
        listColorBtn4.set(countQuestion, R.drawable.ripple_btn);
        other_btn_1.setBackground(getResources().getDrawable(R.drawable.ripple_btn));
        other_btn_2.setBackground(getResources().getDrawable(R.drawable.ripple_btn));
        other_btn_3.setBackground(getResources().getDrawable(R.drawable.ripple_btn));
    }

    private void checkRightAnswer(ArrayList<String> listAnswer) {
        if (listAnswer.get(countQuestion).equals(mListCorrectAnswer.get(countQuestion))) {
            mListUserAnswer.set(countQuestion, 1);
        } else {
            mListUserAnswer.set(countQuestion, 0);
        }
    }

    private void createListColorBtnAndListUserAnswer() {
        for (int i = 0; i < numberQuestion; i++) {
            mListColorButton1.add(R.drawable.ripple_btn);
            mListColorButton2.add(R.drawable.ripple_btn);
            mListColorButton3.add(R.drawable.ripple_btn);
            mListColorButton4.add(R.drawable.ripple_btn);
            mListUserAnswer.add(0);
        }
    }

    private void numberAnswer() {
        mListAnswers.add(ConstantsNames.ANSWER_1);
        mListAnswers.add(ConstantsNames.ANSWER_2);
        mListAnswers.add(ConstantsNames.ANSWER_3);
        mListAnswers.add(ConstantsNames.ANSWER_4);
    }

    private void randomNumberAnswers() {
        masNumberAnswers = new Integer[4];
        for (int i = 0; i < 4; i++) {
            masNumberAnswers[i] = i;
        }
        Collections.shuffle(Arrays.asList(masNumberAnswers));
    }

    private void randomNumberQuestion() {
        masNumberQuestion = new Integer[numberAllQuestion];
        for (int i = 0; i < masNumberQuestion.length; i++) {
            masNumberQuestion[i] = i;
        }
        Collections.shuffle(Arrays.asList(masNumberQuestion));
    }

    @SuppressLint("SetTextI18n")
    private void changeQuestionAndAnswer() {
        answer1.setBackground(getResources().getDrawable(mListColorButton1.get(countQuestion)));
        answer2.setBackground(getResources().getDrawable(mListColorButton2.get(countQuestion)));
        answer3.setBackground(getResources().getDrawable(mListColorButton3.get(countQuestion)));
        answer4.setBackground(getResources().getDrawable(mListColorButton4.get(countQuestion)));
        mQuestion.setText(mListQuestion.get(countQuestion));
        answer1.setText(mListAnswer1.get(countQuestion));
        answer2.setText(mListAnswer2.get(countQuestion));
        answer3.setText(mListAnswer3.get(countQuestion));
        answer4.setText(mListAnswer4.get(countQuestion));
        mNumberOfQuestion.setText(countQuestion + 1 + "/" + numberQuestion);
    }

    private void previousAndNextQuestionButton() {

        nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countQuestion + 1 <= numberQuestion-1) {
                    countQuestion++;
                    changeQuestionAndAnswer();
                }
            }
        });

        previousQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countQuestion - 1 >= 0) {
                    countQuestion--;
                    changeQuestionAndAnswer();
                }
            }
        });

        finishTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishTest();
            }
        });

    }

    private void finishTest() {
        if (!testFinished) {
            cTimer.cancel();
            countTrueAnswers = Collections.frequency(mListUserAnswer, 1);
            startResultActivity();
            setColorButtonAfterFinishTest();
            finishTest.setText("Главное меню");
            finishTest.setBackgroundColor(getResources().getColor(R.color.gray3));
        } else finish();
        //Toast.makeText(StartTestActivity.this, Integer.toString(countTrueAnswers), Toast.LENGTH_SHORT).show();
        testFinished = true;
    }

    private void setColorButtonAfterFinishTest() {
        answer1.setEnabled(false);
        answer2.setEnabled(false);
        answer3.setEnabled(false);
        answer4.setEnabled(false);

        for (int i = 0; i < numberQuestion; i++) {
            if (mListUserAnswer.get(i) == 1) {
                if (mListColorButton1.get(i) == R.drawable.btn_yellow) {
                    mListColorButton1.set(i, R.drawable.btn_green);
                } else if (mListColorButton2.get(i) == R.drawable.btn_yellow) {
                    mListColorButton2.set(i, R.drawable.btn_green);
                } else if (mListColorButton3.get(i) == R.drawable.btn_yellow) {
                    mListColorButton3.set(i, R.drawable.btn_green);
                } else if (mListColorButton4.get(i) == R.drawable.btn_yellow) {
                    mListColorButton4.set(i, R.drawable.btn_green);
                }
            } else {
                if (mListColorButton1.get(i) == R.drawable.btn_yellow) {
                    mListColorButton1.set(i, R.drawable.btn_red);
                } else if (mListColorButton2.get(i) == R.drawable.btn_yellow) {
                    mListColorButton2.set(i, R.drawable.btn_red);
                } else if (mListColorButton3.get(i) == R.drawable.btn_yellow) {
                    mListColorButton3.set(i, R.drawable.btn_red);
                } else if (mListColorButton4.get(i) == R.drawable.btn_yellow) {
                    mListColorButton4.set(i, R.drawable.btn_red);
                }
            }
        }

        answer1.setBackground(getResources().getDrawable(mListColorButton1.get(countQuestion)));
        answer2.setBackground(getResources().getDrawable(mListColorButton2.get(countQuestion)));
        answer3.setBackground(getResources().getDrawable(mListColorButton3.get(countQuestion)));
        answer4.setBackground(getResources().getDrawable(mListColorButton4.get(countQuestion)));
    }

    private void startResultActivity() {
        Intent resultTest = new Intent(StartTestActivity.this, ResultTestActivity.class);
        resultTest.putExtra("countTrueAnswers", countTrueAnswers);
        resultTest.putExtra("numberQuestions", numberQuestion);
        resultTest.putExtra("nameTeacher", nameTeacher);
        resultTest.putExtra("numberTest", numberTest);
        resultTest.putExtra("course", course);
        resultTest.putExtra("group", group);
        resultTest.putExtra("full_name", full_name);
        resultTest.putExtra("dataCreateTest", dataCreateTest);
        resultTest.putExtra("nameSubject", nameSubject);
        resultTest.putExtra("topicName", topicName);
        startActivity(resultTest);
    }

    private void countDownTimer() {
        cTimer = new CountDownTimer(60000, 1) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                //mTimeLeft.setText(Integer.toString((int) (millisUntilFinished / 1000)));
                if (((millisUntilFinished / 1000) < 10) && testTime < 10) {
                    mTimeLeft.setText("0" +Integer.toString(testTime-1) + " : 0" +Integer.toString((int) (millisUntilFinished / 1000)));
                } else if ((millisUntilFinished / 1000) < 10) {
                    mTimeLeft.setText(Integer.toString(testTime-1) + " : 0" +Integer.toString((int) (millisUntilFinished / 1000)));
                } else if (testTime < 10) {
                    mTimeLeft.setText("0" +Integer.toString(testTime-1) + " : " +Integer.toString((int) (millisUntilFinished / 1000)));
                } else {
                    mTimeLeft.setText(Integer.toString(testTime - 1) + " : " + Integer.toString((int) (millisUntilFinished / 1000)));
                }

                if (testTime-1 == 0) {
                    mTimeLeft.setTextColor(getResources().getColor(R.color.red_btn));
                } else mTimeLeft.setTextColor(getResources().getColor(R.color.white));
            }

            public void onFinish() {
                if (testTime-1 == 0) {
                    finishTest();
                } else {
                    testTime--;
                    cTimer.start();
                }
            }

        };
        cTimer.start();
    }

    private void clearLists() {
        mListQuestion.clear();
        mListAnswer1.clear();
        mListAnswer2.clear();
        mListAnswer3.clear();
        mListAnswer4.clear();
        mListCorrectAnswer.clear();
        mListUserAnswer.clear();
        mListColorButton1.clear();
        mListColorButton2.clear();
        mListColorButton3.clear();
        mListColorButton4.clear();
        mListAnswers.clear();
    }

    private void errorNull() {
        Log.d("Errors", "NullPointerException");
        Toast.makeText(this, "Ошибка соединения с сервером..", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        clearLists();
        cTimer.cancel();
        super.onBackPressed();
    }
}
