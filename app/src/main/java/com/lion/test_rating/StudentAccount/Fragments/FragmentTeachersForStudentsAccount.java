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
import com.lion.test_rating.StudentAccount.RecyclerViewAdapters.RVAListTeachersForStudentAccount;

import java.util.ArrayList;

public class FragmentTeachersForStudentsAccount extends Fragment {

    View fragmentView;

    private ArrayList<String> mTeacherName = new ArrayList<>();
    private ArrayList<String> mDepartmentName = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_for_student_list_teachers, container, false);

        getActivity().setTitle(getString(R.string.result_item));

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference teacherDatabase = mFirebaseDatabase.getReference();
        teacherDatabase.keepSynced(true);

        try {
            teacherDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(ConstantsNames.RESULTS).exists()) {
                        clearLists();
                        openDataTeachers(dataSnapshot);
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

    private void openDataTeachers(DataSnapshot dataSnapshot) {
        for (DataSnapshot teachers : dataSnapshot.child(ConstantsNames.RESULTS).getChildren()) {
            boolean teacherAdded = false;

            for (DataSnapshot tests : teachers.getChildren()) {

                if (teacherAdded) {
                    break;
                }

                for (DataSnapshot courses : tests.getChildren()) {

                    if (AccountStudentActivity.mListUserInformation.get(2).equals(courses.getKey())) {
                        if (courses.hasChild(AccountStudentActivity.mListUserInformation.get(3))) {

                            for (DataSnapshot teachersID : dataSnapshot.child(ConstantsNames.USERS)
                                    .child(ConstantsNames.TEACHERS).getChildren()) {

                                String teacherName = teachers.getKey();
                                assert teacherName != null;
                                if (teacherName.equals(teachersID.child(ConstantsNames.FULL_NAME).getValue())) {
                                    initList(teachers.getKey()
                                            , (String) teachersID.child(ConstantsNames.DEPARTMENT).getValue());
                                    teacherAdded = true;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        initRecyclerView();
    }

    private void initList(String teacher, String department) {
        mTeacherName.add(teacher);
        mDepartmentName.add(department);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view_teachers);
        RVAListTeachersForStudentAccount adapter = new RVAListTeachersForStudentAccount(getActivity()
                , mTeacherName, mDepartmentName);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void errorNull() {
        Toast.makeText(getActivity(), R.string.server_connection_error, Toast.LENGTH_LONG).show();
        getActivity().finish();
    }

    private void clearLists() {
        mDepartmentName.clear();
        mTeacherName.clear();
    }

}
