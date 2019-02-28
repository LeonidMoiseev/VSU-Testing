package com.lion.test_rating.StudentAccount;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lion.test_rating.ConstantsNames;
import com.lion.test_rating.R;
import com.lion.test_rating.StudentAccount.RecyclerViewAdapters.RVAResultsForStudentAccount;

import java.util.ArrayList;

public class ResultsActivityForStudents extends AppCompatActivity {

    String nameTeacher;

    private ArrayList<String> mSubjectName = new ArrayList<>();
    private ArrayList<String> mDataName = new ArrayList<>();
    private ArrayList<String> mPoints = new ArrayList<>();
    private ArrayList<String> mTopicName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_student_results);

        Toolbar toolbar = findViewById(R.id.myToolBar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle(ConstantsNames.RESULTS);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        nameTeacher = intent.getStringExtra("nameTeacher");

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference resultDatabase = mFirebaseDatabase.getReference().child(ConstantsNames.RESULTS).child(nameTeacher);
        resultDatabase.keepSynced(true);

        try {
            resultDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    clearLists();
                    checkExistenceTests(dataSnapshot);
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

    private void checkExistenceTests(DataSnapshot dataSnapshot) {

        for (DataSnapshot numberTest : dataSnapshot.getChildren()) {

            if (numberTest.hasChild(AccountStudentActivity.mList.get(2))) {

                if (numberTest.child(AccountStudentActivity.mList.get(2)).hasChild(AccountStudentActivity.mList.get(3))) {

                    if (numberTest.child(AccountStudentActivity.mList.get(2)).child(AccountStudentActivity.mList.get(3))
                            .hasChild(AccountStudentActivity.mList.get(0))) {

                        initListTests((String) numberTest.child(ConstantsNames.SUBJECT).getValue()
                                , (String) numberTest.child(ConstantsNames.DATA_CREATE).getValue()
                                , (String) numberTest.child(AccountStudentActivity.mList.get(2))
                                        .child(AccountStudentActivity.mList.get(3))
                                        .child(AccountStudentActivity.mList.get(0)).getValue()
                                , (String) numberTest.child(ConstantsNames.TOPIC_NAME).getValue());
                    }
                }
            }
        }
        initRecyclerView();
    }

    private void initListTests(String subject, String dataCreateTest, String points, String topic) {
        mDataName.add(dataCreateTest);
        mPoints.add(points);
        mSubjectName.add(subject);
        mTopicName.add(topic);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_results_students);
        RVAResultsForStudentAccount adapter = new RVAResultsForStudentAccount(this, mSubjectName, mDataName, mPoints, mTopicName);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void errorNull() {
        Log.d("Errors", "NullPointerException");
        Toast.makeText(ResultsActivityForStudents.this, "Ошибка соединения с сервером..", Toast.LENGTH_LONG).show();
        finish();
    }

    private void clearLists() {
        mPoints.clear();
        mSubjectName.clear();
        mDataName.clear();
        mTopicName.clear();
    }
}
