package com.lion.test_rating.TeacherAccount.Fragments;

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
import com.lion.test_rating.TeacherAccount.AccountTeacherActivity;
import com.lion.test_rating.TeacherAccount.RecyclerViewAdapters.RVATestsForTeachersAccount;

import java.util.ArrayList;

public class FragmentTestsForTeachersAccount extends Fragment {

    private ArrayList<String> mSubjectName = new ArrayList<>();
    private ArrayList<String> mDateName = new ArrayList<>();
    private ArrayList<String> mNumberTest = new ArrayList<>();
    private ArrayList<String> mTopicName = new ArrayList<>();

    View fragmentView;

    DatabaseReference testsDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_for_teacher_list_tests, container, false);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        testsDatabase = mFirebaseDatabase.getReference();
        testsDatabase.keepSynced(true);

        try {
            testsDatabase.addValueEventListener(new ValueEventListener() {
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

        return fragmentView;
    }

    private void checkExistenceTests(DataSnapshot dataSnapshot) {
        if (dataSnapshot.hasChild(ConstantsNames.RESULTS)) {
            for (DataSnapshot tests : dataSnapshot.child(ConstantsNames.RESULTS)
                    .child(AccountTeacherActivity.mListUserInformation.get(0)).getChildren()) {


                initList((String) tests.child(ConstantsNames.SUBJECT).getValue(),
                        (String) tests.child(ConstantsNames.DATE_CREATE).getValue(),
                        tests.getKey(),
                        (String) tests.child(ConstantsNames.TOPIC_NAME).getValue());

            }
        }
        initRecyclerView();
    }

    private void initList(String subject, String data, String numberTest, String topic) {
        mSubjectName.add(subject);
        mDateName.add(data);
        mNumberTest.add(numberTest);
        mTopicName.add(topic);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view_tests_for_teachers_account);
        RVATestsForTeachersAccount adapter = new RVATestsForTeachersAccount(getActivity(), mSubjectName, mDateName, mNumberTest, mTopicName);
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
        mSubjectName.clear();
        mDateName.clear();
        mNumberTest.clear();
        mTopicName.clear();
    }
}
