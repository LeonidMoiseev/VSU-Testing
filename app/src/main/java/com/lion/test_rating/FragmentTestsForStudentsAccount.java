package com.lion.test_rating;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentTestsForStudentsAccount extends Fragment {

    final String TESTS = "Tests";
    final String SUBJECT = "Предмет";
    final String DATA_CREATE = "Дата создания";
    final String RESTRICTION = "Ограничение на количество вопросов";
    final String TIME_TEST = "Время теста (мин)";
    final String USERS = "Пользователи";
    final String STUDENTS = "Студенты";
    final String COURSE = "Курс";
    final String GROUP = "Группа";
    final String USER_COMPLETE_TEST = "Пользователи прошедшие тест";
    final String FULL_NAME = "ФИО";
    final String STATUS_TEST = "Статус теста";
    final String OPEN = "Открыт";

    private ArrayList<String> mSubjectName = new ArrayList<>();
    private ArrayList<String> mTeacherName = new ArrayList<>();
    private ArrayList<String> mNumberTeacherTest = new ArrayList<>();
    private ArrayList<String> mDataName = new ArrayList<>();
    private ArrayList<String> mAllTeachers = new ArrayList<>();
    private ArrayList<String> mRestrictionCountQuestion = new ArrayList<>();
    private ArrayList<String> mTestTime = new ArrayList<>();

    String course;
    String group;
    String full_name;

    View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_tests, container, false);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference testsDatabase = mFirebaseDatabase.getReference();
        testsDatabase.keepSynced(true);

        try {
            testsDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    clearLists();
                    usersInformation(dataSnapshot);
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
            DataSnapshot dataTests = dataSnapshot.child(TESTS);

            for (DataSnapshot teachers : dataTests.getChildren()) {
                mAllTeachers.add(teachers.getKey());
            }

            for (int k = 0; k < mAllTeachers.size(); k++) {

                for (DataSnapshot testNumber : dataTests.child(mAllTeachers.get(k)).getChildren()) {
                    String m = testNumber.getKey();

                    assert m != null;
                    String openTest = (String) dataTests.child(mAllTeachers.get(k)).child(m).child(STATUS_TEST).getValue();
                    assert openTest != null;
                    //Проверка статуса теста
                    if (openTest.equals(OPEN)) {

                        //Проверка: для каких курсов открыт тест
                        if (dataTests.child(mAllTeachers.get(k)).child(m).hasChild(course)) {

                            String groups = (String) dataTests.child(mAllTeachers.get(k)).child(m).child(course).getValue();
                            assert groups != null;
                            //Проверка: для каких групп открыт тест
                            if (groups.contains(group)) {

                                //Проверка: кто уже прошёл тест
                                if (!dataTests.child(mAllTeachers.get(k)).child(m).child(USER_COMPLETE_TEST).hasChild(full_name)) {

                                    initListTests((String) dataTests.child(mAllTeachers.get(k)).child(m).child(SUBJECT).getValue()
                                            , mAllTeachers.get(k)
                                            , (String) dataTests.child(mAllTeachers.get(k)).child(m).child(DATA_CREATE).getValue()
                                            , m
                                            , (String) dataTests.child(mAllTeachers.get(k)).child(m).child(RESTRICTION).getValue()
                                            , (String) dataTests.child(mAllTeachers.get(k)).child(m).child(TIME_TEST).getValue());
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

    private void usersInformation(DataSnapshot dataSnapshot) {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            String userID = user.getUid();
            course = (String) dataSnapshot.child(USERS).child(STUDENTS).child(userID).child(COURSE).getValue();
            group = (String) dataSnapshot.child(USERS).child(STUDENTS).child(userID).child(GROUP).getValue();
            full_name = (String) dataSnapshot.child(USERS).child(STUDENTS).child(userID).child(FULL_NAME).getValue();
        } catch (NullPointerException ex) {
            errorNull();
        }
    }

    private void initListTests(String subject, String teacher, String data, String numberTest, String restriction, String time) {
        mSubjectName.add(subject);
        mTeacherName.add(teacher);
        mDataName.add(data);
        mNumberTeacherTest.add(numberTest);
        mRestrictionCountQuestion.add(restriction);
        mTestTime.add(time);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view);
        RecyclerViewAdapterTests adapter = new RecyclerViewAdapterTests(getActivity(), mSubjectName, mTeacherName, mDataName
                , mNumberTeacherTest, mRestrictionCountQuestion, mTestTime
                , course, group, full_name);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
    }
}
