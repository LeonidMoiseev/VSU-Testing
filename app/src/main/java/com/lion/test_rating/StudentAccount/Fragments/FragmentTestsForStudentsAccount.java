package com.lion.test_rating.StudentAccount.Fragments;

import android.os.Bundle;
import android.app.Fragment;
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
import com.lion.test_rating.StudentAccount.RecyclerViewAdapters.RVAListTestsForStudentAccount;

import java.util.ArrayList;

public class FragmentTestsForStudentsAccount extends Fragment {

    private ArrayList<String> mSubjectName = new ArrayList<>();
    private ArrayList<String> mTeacherName = new ArrayList<>();
    private ArrayList<String> mTopicName = new ArrayList<>();
    private ArrayList<String> mNumberTeacherTest = new ArrayList<>();
    private ArrayList<String> mDataName = new ArrayList<>();
    private ArrayList<String> mAllTeachers = new ArrayList<>();
    private ArrayList<String> mRestrictionCountQuestion = new ArrayList<>();
    private ArrayList<String> mTestTime = new ArrayList<>();

    View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_for_student_list_tests, container, false);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference testsDatabase = mFirebaseDatabase.getReference();
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
        try {
            DataSnapshot dataTests = dataSnapshot.child(ConstantsNames.TESTS);

            for (DataSnapshot teachers : dataTests.getChildren()) {
                mAllTeachers.add(teachers.getKey());
            }

            for (int k = 0; k < mAllTeachers.size(); k++) {

                for (DataSnapshot testNumber : dataTests.child(mAllTeachers.get(k)).getChildren()) {
                    String m = testNumber.getKey();

                    assert m != null;
                    String openTest = (String) dataTests.child(mAllTeachers.get(k)).child(m)
                            .child(ConstantsNames.STATUS_TEST).getValue();
                    assert openTest != null;
                    //Проверка статуса теста
                    if (openTest.equals(ConstantsNames.OPEN)) {

                        //Проверка: для каких курсов открыт тест
                        if (dataTests.child(mAllTeachers.get(k)).child(m)
                                .hasChild(AccountStudentActivity.mListUserInformation.get(2))) {

                            String groups = (String) dataTests.child(mAllTeachers.get(k)).child(m)
                                    .child(AccountStudentActivity.mListUserInformation.get(2)).getValue();
                            assert groups != null;
                            //Проверка: для каких групп открыт тест
                            if (groups.contains(AccountStudentActivity.mListUserInformation.get(3))) {

                                //Проверка: кто уже прошёл тест
                                if (!dataTests.child(mAllTeachers.get(k)).child(m)
                                        .child(ConstantsNames.USER_COMPLETE_TEST)
                                        .hasChild(AccountStudentActivity.mListUserInformation.get(0))) {

                                    initListTests((String) dataTests.child(mAllTeachers.get(k))
                                                    .child(m).child(ConstantsNames.SUBJECT).getValue()
                                            , mAllTeachers.get(k)
                                            , (String) dataTests.child(mAllTeachers.get(k))
                                                    .child(m).child(ConstantsNames.DATE_CREATE).getValue()
                                            , m
                                            , (String) dataTests.child(mAllTeachers.get(k))
                                                    .child(m).child(ConstantsNames.RESTRICTION).getValue()
                                            , (String) dataTests.child(mAllTeachers.get(k))
                                                    .child(m).child(ConstantsNames.TIME_TEST).getValue()
                                            , (String) dataTests.child(mAllTeachers.get(k))
                                                    .child(m).child(ConstantsNames.TOPIC_NAME).getValue());
                                }
                            }
                        }
                    }
                }
            }

            initRecyclerView();

        } catch (NullPointerException ex) {
            errorNull();
        }
    }

    private void initListTests(String subject, String teacher, String data, String numberTest,
                               String restriction, String time, String topic) {
        mSubjectName.add(subject);
        mTeacherName.add(teacher);
        mDataName.add(data);
        mNumberTeacherTest.add(numberTest);
        mRestrictionCountQuestion.add(restriction);
        mTestTime.add(time);
        mTopicName.add(topic);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view);
        RVAListTestsForStudentAccount adapter = new RVAListTestsForStudentAccount(getActivity(), mSubjectName, mTeacherName, mDataName
                , mNumberTeacherTest, mRestrictionCountQuestion, mTestTime, mTopicName);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void errorNull() {
        Log.d("Errors", "NullPointerException");
        Toast.makeText(getActivity(), "Ошибка соединения с сервером..", Toast.LENGTH_LONG).show();
        getActivity().finish();
    }

    private void clearLists() {
        mAllTeachers.clear();
        mSubjectName.clear();
        mTeacherName.clear();
        mDataName.clear();
        mNumberTeacherTest.clear();
        mRestrictionCountQuestion.clear();
        mTestTime.clear();
        mTopicName.clear();
    }
}
