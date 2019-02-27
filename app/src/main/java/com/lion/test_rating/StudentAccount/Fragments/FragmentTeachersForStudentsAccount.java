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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lion.test_rating.ConstantsNames;
import com.lion.test_rating.R;
import com.lion.test_rating.StudentAccount.RecyclerViewAdapters.RVAListTeachersForStudentAccount;

import java.util.ArrayList;

public class FragmentTeachersForStudentsAccount extends Fragment {

    View fragmentView;

    String course;
    String group;
    String full_name;

    private ArrayList<String> mTeacherName = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_for_student_list_teachers, container, false);

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
        if (dataSnapshot.hasChild(ConstantsNames.RESULTS)) {
            for (DataSnapshot teachers : dataSnapshot.child(ConstantsNames.RESULTS).getChildren()) {
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
        RVAListTeachersForStudentAccount adapter = new RVAListTeachersForStudentAccount(getActivity(), mTeacherName, course, group, full_name);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void usersInformation(DataSnapshot dataSnapshot) {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            String userID = user.getUid();
            DataSnapshot dataStudents = dataSnapshot.child(ConstantsNames.USERS).child(ConstantsNames.STUDENTS).child(userID);
            course = (String) dataStudents.child(ConstantsNames.COURSE).getValue();
            group = (String) dataStudents.child(ConstantsNames.GROUP).getValue();
            full_name = (String) dataStudents.child(ConstantsNames.FULL_NAME).getValue();
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
