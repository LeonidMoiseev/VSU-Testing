package com.lion.test_rating.TeacherAccount.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import com.lion.test_rating.TeacherAccount.AccountTeacherActivity;
import com.lion.test_rating.TeacherAccount.CreateInformation;
import com.lion.test_rating.R;
import com.lion.test_rating.TeacherAccount.RecyclerViewAdapters.RVAInformationForTeacherAccount;

import java.util.ArrayList;

public class FragmentInformationForTeacherAccount extends Fragment {

    private ArrayList<String> mDateName = new ArrayList<>();
    private ArrayList<String> mInformation = new ArrayList<>();
    private ArrayList<String> mCoursesAndGroups = new ArrayList<>();
    private ArrayList<String> mNumberInformation = new ArrayList<>();

    View fragmentView;

    DatabaseReference testsDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_for_teacher_list_information, container, false);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        testsDatabase = mFirebaseDatabase.getReference();
        testsDatabase.keepSynced(true);

        try {
            testsDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    clearLists();
                    OpenDataInformation(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    errorNull();
                }
            });
        } catch (NullPointerException ex) {
            errorNull();
        }


        FloatingActionButton fab = fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateInformation createInformation = new CreateInformation(getActivity());
                createInformation.openData();
            }
        });

        return fragmentView;
    }

    private void OpenDataInformation(DataSnapshot dataSnapshot) {
        if (dataSnapshot.hasChild(ConstantsNames.INFORMATION)) {

            if (dataSnapshot.child(ConstantsNames.INFORMATION)
                    .hasChild(AccountTeacherActivity.mListUserInformation.get(0))) {

                DataSnapshot dataInfo = dataSnapshot.child(ConstantsNames.INFORMATION)
                        .child(AccountTeacherActivity.mListUserInformation.get(0));

                for (DataSnapshot info : dataInfo.getChildren()) {

                    String text = "";
                    for (DataSnapshot coursesAndGroups : info.child(ConstantsNames.COURSES_AND_GROUPS).getChildren()) {
                        text = text + "\n" + coursesAndGroups.getValue();
                    }

                    initList((String) info.child(ConstantsNames.INFORMATION).getValue(),
                            (String) info.child(ConstantsNames.DATE_CREATE).getValue(),
                            text,
                            info.getKey());

                }
            }
        }
        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view_information_for_teachers_account);
        RVAInformationForTeacherAccount adapter = new RVAInformationForTeacherAccount(getActivity()
                , mInformation, mCoursesAndGroups, mDateName, mNumberInformation);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initList(String info, String date, String coursesAndGroups, String number) {
        mInformation.add(info);
        mDateName.add(date);
        mCoursesAndGroups.add(coursesAndGroups);
        mNumberInformation.add(number);
    }

    private void errorNull() {
        Log.d("Errors", "NullPointerException");
        Toast.makeText(getActivity(), "Ошибка соединения с сервером..", Toast.LENGTH_LONG).show();
    }

    private void clearLists() {
        mInformation.clear();
        mDateName.clear();
        mCoursesAndGroups.clear();
        mNumberInformation.clear();
    }
}
