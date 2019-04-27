package com.lion.test_rating.StudentAccount;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Scroller;
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
import java.util.Calendar;
import java.util.Collections;

public class StartTestActivity extends AppCompatActivity {

    private ProgressDialog mProgress;
    TextView mQuestion;
    TextView mNumberOfQuestion;
    @SuppressLint("StaticFieldLeak")
    public static TextView mTimeLeft;
    Button answer1;
    Button answer2;
    Button answer3;
    Button answer4;
    ImageButton previousQuestion;
    ImageButton nextQuestion;
    Button finishTest;
    AlertDialog dialog;

    public static boolean testFinished;
    private int destroyTest = 0;
    private int countTrueAnswers = 0;
    private int countQuestion = 0;
    private int numberQuestion;
    private int numberAllQuestion;
    public static int testTime;
    private String numberTest;
    private String nameTeacher;
    private String nameStudent;
    private String courseStudent;
    private String groupStudent;
    private String ratingStudent;
    private Integer[] masNumberQuestion;
    private Integer[] masNumberAnswers;

    static AlarmManager alarmManager;
    static PendingIntent pendingIntent;

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

    private String dataCreateTest;
    private String nameSubject;
    private String topicName;

    private boolean testTimeOut = false;

    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.lion.close".equals(intent.getAction())) {
                finishTest();
                testTimeOut = true;
            }
        }
    };

    @SuppressLint({"HandlerLeak", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_student_start_test);

        testFinished = false;

        Log.d("TAG", "created");

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.lion.close");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child(ConstantsNames.TESTS);
        myRef.keepSynced(true);

        Intent intent = getIntent();
        nameTeacher = intent.getStringExtra("nameTeacher");
        numberTest = intent.getStringExtra("numberTest");
        dataCreateTest = intent.getStringExtra("dataCreateTest");
        nameSubject = intent.getStringExtra("nameSubject");
        topicName = intent.getStringExtra("topicName");
        numberQuestion = Integer.parseInt(intent.getStringExtra("restrictionTest"));
        testTime = Integer.parseInt(intent.getStringExtra("testTime"));
        nameStudent = StudentAccountActivity.mListUserInformation.get(0);
        courseStudent = StudentAccountActivity.mListUserInformation.get(2);
        groupStudent = StudentAccountActivity.mListUserInformation.get(3);
        ratingStudent = StudentAccountActivity.mListUserInformation.get(4);

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

        setScrollButton(answer1);
        setScrollButton(answer2);
        setScrollButton(answer3);
        setScrollButton(answer4);
        mQuestion.setScroller(new Scroller(this));
        mQuestion.setVerticalScrollBarEnabled(true);
        mQuestion.setMovementMethod(new ScrollingMovementMethod());

        mProgress = new ProgressDialog(this);

        if (testTime < 10) {
            StartTestActivity.mTimeLeft.setText("0" + Integer.toString(testTime) + " : 00");
        } else {
            StartTestActivity.mTimeLeft.setText(Integer.toString(testTime) + " : 00");
        }

        try {
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
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

                        initAlarmManager(StartTestActivity.this);
                        mProgress.dismiss();
                    }
                    mProgress.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    errorNull();
                }
            });
        } catch (NullPointerException ex) {
            errorNull();
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.answer1:
                        changeListAndSetColorBtn(mListColorButton1, mListColorButton2
                                , mListColorButton3, mListColorButton4
                                , answer1, answer2, answer3, answer4);
                        checkRightAnswer(mListAnswer1);
                        break;
                    case R.id.answer2:
                        changeListAndSetColorBtn(mListColorButton2, mListColorButton1
                                , mListColorButton3, mListColorButton4
                                , answer2, answer1, answer3, answer4);
                        checkRightAnswer(mListAnswer2);
                        break;
                    case R.id.answer3:
                        changeListAndSetColorBtn(mListColorButton3, mListColorButton1
                                , mListColorButton2, mListColorButton4
                                , answer3, answer1, answer2, answer4);
                        checkRightAnswer(mListAnswer3);
                        break;
                    case R.id.answer4:
                        changeListAndSetColorBtn(mListColorButton4, mListColorButton1
                                , mListColorButton2, mListColorButton3
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

    private void setScrollButton(Button button) {
        button.setScroller(new Scroller(this));
        button.setVerticalScrollBarEnabled(true);
        button.setMovementMethod(new ScrollingMovementMethod());
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
    }

    private void changeListAndSetColorBtn(ArrayList<Integer> listColorBtn1, ArrayList<Integer> listColorBtn2
            , ArrayList<Integer> listColorBtn3, ArrayList<Integer> listColorBtn4
            , Button btn, Button other_btn_1, Button other_btn_2, Button other_btn_3) {

        btn.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
        listColorBtn1.set(countQuestion, R.drawable.btn_yellow);
        listColorBtn2.set(countQuestion, R.drawable.btn_orange);
        listColorBtn3.set(countQuestion, R.drawable.btn_orange);
        listColorBtn4.set(countQuestion, R.drawable.btn_orange);
        other_btn_1.setBackground(getResources().getDrawable(R.drawable.btn_orange));
        other_btn_2.setBackground(getResources().getDrawable(R.drawable.btn_orange));
        other_btn_3.setBackground(getResources().getDrawable(R.drawable.btn_orange));
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
            mListColorButton1.add(R.drawable.btn_orange);
            mListColorButton2.add(R.drawable.btn_orange);
            mListColorButton3.add(R.drawable.btn_orange);
            mListColorButton4.add(R.drawable.btn_orange);
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
        mQuestion.scrollTo(0, 0);
        answer1.scrollTo(0, 0);
        answer2.scrollTo(0, 0);
        answer3.scrollTo(0, 0);
        answer4.scrollTo(0, 0);
        mNumberOfQuestion.setText(countQuestion + 1 + "/" + numberQuestion);
    }

    private void previousAndNextQuestionButton() {

        nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countQuestion + 1 <= numberQuestion - 1) {
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
            testFinished = true;
            mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
            stopAlarmManager(StartTestActivity.this);
            countTrueAnswers = Collections.frequency(mListUserAnswer, 1);
            startResultActivity();
            setColorButtonAfterFinishTest();
            finishTest.setText(R.string.main_menu);
            finishTest.setBackgroundColor(getResources().getColor(R.color.gray3));
        } else {
            finish();
        }
    }

    private void finishTestWhenDestroyActivity() {
        countTrueAnswers = Collections.frequency(mListUserAnswer, 1);
        destroyTest = 1;
        startResultActivity();
        finish();
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
        resultTest.putExtra("dataCreateTest", dataCreateTest);
        resultTest.putExtra("nameSubject", nameSubject);
        resultTest.putExtra("topicName", topicName);
        resultTest.putExtra("destroyTest", destroyTest);
        resultTest.putExtra("nameStudent", nameStudent);
        resultTest.putExtra("courseStudent", courseStudent);
        resultTest.putExtra("groupStudent", groupStudent);
        resultTest.putExtra("ratingStudent", ratingStudent);
        startActivity(resultTest);
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
        Toast.makeText(this, R.string.server_connection_error, Toast.LENGTH_LONG).show();
        finish();
    }

    private void dialogCloseTest() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams")
        View mView = getLayoutInflater().inflate(R.layout.dialog_close_test, null);

        Button btnYes = mView.findViewById(R.id.btn_yes);
        Button btnNo = mView.findViewById(R.id.btn_no);

        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLists();
                finish();
                dialog.dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public static void initAlarmManager(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        Intent intent = new Intent(context, TimerReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public static void stopAlarmManager(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        Intent intent = new Intent(context, TimerReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 100, intent
                , PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    @Override
    protected void onDestroy() {
        if (!testFinished) {
            mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
            stopAlarmManager(StartTestActivity.this);
            finishTestWhenDestroyActivity();
        }
        Log.d("TAG", "destroy");
        super.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        if (testTimeOut) {
            mTimeLeft.setText("00 : 00");
        }
    }

    @Override
    public void onBackPressed() {
        if (!testFinished) {
            dialogCloseTest();
        } else finish();
    }
}
