package com.lion.test_rating.StudentAccount.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lion.test_rating.ConstantsNames;
import com.lion.test_rating.R;
import com.lion.test_rating.StudentAccount.AccountStudentActivity;
import com.lion.test_rating.StudentAccount.RecyclerViewAdapters.RVAResultsForStudentAccount;

import java.util.ArrayList;

public class FragmentResultsForStudent extends Fragment {

    private ArrayList<String> mSubjectName = new ArrayList<>();
    private ArrayList<String> mDataName = new ArrayList<>();
    private ArrayList<String> mPoints = new ArrayList<>();
    private ArrayList<String> mTopicName = new ArrayList<>();

    View fragmentView;

    String nameTeacher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_for_student_list_results, container, false);

        nameTeacher = this.getArguments().getString("teacher");

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference resultDatabase = mFirebaseDatabase.getReference()
                .child(ConstantsNames.RESULTS).child(nameTeacher);
        resultDatabase.keepSynced(true);

        try {
            resultDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        clearLists();
                        openDatabaseResults(dataSnapshot);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    errorNull();
                }
            });
        } catch (NullPointerException ex) {
            errorNull();
        }

        return fragmentView;
    }

    private void openDatabaseResults(DataSnapshot dataSnapshot) {

        for (DataSnapshot numberTest : dataSnapshot.getChildren()) {

            if (numberTest.hasChild(AccountStudentActivity.mListUserInformation.get(2))) {

                if (numberTest.child(AccountStudentActivity.mListUserInformation.get(2))
                        .hasChild(AccountStudentActivity.mListUserInformation.get(3))) {

                    if (numberTest.child(AccountStudentActivity.mListUserInformation.get(2))
                            .child(AccountStudentActivity.mListUserInformation.get(3))
                            .hasChild(AccountStudentActivity.mListUserInformation.get(0))) {

                        initListTests((String) numberTest.child(ConstantsNames.SUBJECT).getValue()
                                , (String) numberTest.child(ConstantsNames.DATE_CREATE).getValue()
                                , (String) numberTest.child(AccountStudentActivity.mListUserInformation.get(2))
                                        .child(AccountStudentActivity.mListUserInformation.get(3))
                                        .child(AccountStudentActivity.mListUserInformation.get(0)).getValue()
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
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view_results_students);
        RVAResultsForStudentAccount adapter = new RVAResultsForStudentAccount(getActivity()
                , mSubjectName, mDataName, mPoints, mTopicName);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void errorNull() {
        Log.d("Errors", "NullPointerException");
        Toast.makeText(getActivity(), "Ошибка соединения с сервером..", Toast.LENGTH_LONG).show();
    }

    private void clearLists() {
        mPoints.clear();
        mSubjectName.clear();
        mDataName.clear();
        mTopicName.clear();
    }
}
