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

public class FragmentTeachersForStudentsAccount extends Fragment {

    View fragmentView;

    String course;
    String group;
    String full_name;

    final String USERS = "Пользователи";
    final String STUDENTS = "Студенты";
    final String COURSE = "Курс";
    final String GROUP = "Группа";
    final String FULL_NAME = "ФИО";
    final String RESULTS = "Результаты";

    private ArrayList<String> mTeacherName = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_teachers, container, false);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference teachersDatabase = mFirebaseDatabase.getReference();
        teachersDatabase.keepSynced(true);

        try {
            teachersDatabase.addValueEventListener(new ValueEventListener() {
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
        if (dataSnapshot.hasChild(RESULTS)) {
            for (DataSnapshot teachers : dataSnapshot.child(RESULTS).getChildren()) {
                initListTests(teachers.getKey());
            }
        }
        initRecyclerView();
    }

    private void initListTests(String teacher) {
        mTeacherName.add(teacher);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view_teachers);
        RecyclerViewAdapterTeachers adapter = new RecyclerViewAdapterTeachers(getActivity(), mTeacherName, course, group, full_name);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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

    private void errorNull() {
        Log.d("Errors", "NullPointerException");
        Toast.makeText(getActivity(), "Ошибка соединения с сервером..", Toast.LENGTH_LONG).show();
        getActivity().finish();
    }

    private void clearLists() {
        mTeacherName.clear();
    }

}
