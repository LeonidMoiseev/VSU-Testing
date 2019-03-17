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
import com.lion.test_rating.StudentAccount.RecyclerViewAdapters.RVAListInformationForStudentAccount;

import java.util.ArrayList;

public class FragmentInformationForStudentsAccount extends Fragment {

    View fragmentView;

    private ArrayList<String> mListTeacherName = new ArrayList<>();
    private ArrayList<String> mListInformation = new ArrayList<>();
    private ArrayList<String> mListDate = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_for_student_list_information, container, false);

        DatabaseReference informationDatabase = FirebaseDatabase.getInstance()
                .getReference().child(ConstantsNames.INFORMATION);
        informationDatabase.keepSynced(true);

        try {
            informationDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        clearLists();
                        openDataInformation(dataSnapshot);
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

    private void openDataInformation(DataSnapshot dataSnapshot) {
        for (DataSnapshot teachers : dataSnapshot.getChildren()) {
            for (DataSnapshot numberInfo : teachers.getChildren()) {
                for (DataSnapshot checkCourseAndGroup : numberInfo.child(ConstantsNames.COURSES_AND_GROUPS).getChildren()) {

                    String courseAndGroup = (String) checkCourseAndGroup.getValue();
                    String course = Character.toString(courseAndGroup.charAt(0));
                    String group = Character.toString(courseAndGroup.charAt(7));
                    if (AccountStudentActivity.mListUserInformation.get(2).equals(course)
                            && AccountStudentActivity.mListUserInformation.get(3).equals(group)) {

                        initListTests(teachers.getKey(), (String) numberInfo.child(ConstantsNames.INFORMATION).getValue()
                                , (String) numberInfo.child(ConstantsNames.DATE_CREATE).getValue());

                    }

                }
            }
        }
        initRecyclerView();
    }

    private void initListTests(String teacher, String info, String date) {
        mListTeacherName.add(teacher);
        mListInformation.add(info);
        mListDate.add(date);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view_list_information_for_students);
        RVAListInformationForStudentAccount adapter = new RVAListInformationForStudentAccount(getActivity()
                , mListTeacherName, mListInformation, mListDate);
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
        mListTeacherName.clear();
        mListInformation.clear();
        mListDate.clear();
    }
}
