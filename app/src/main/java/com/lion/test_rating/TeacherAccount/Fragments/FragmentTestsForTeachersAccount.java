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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lion.test_rating.ConstantsNames;
import com.lion.test_rating.R;
import com.lion.test_rating.TeacherAccount.RcyclerViewAdapters.RVATestsForTeachersAccount;

import java.util.ArrayList;

public class FragmentTestsForTeachersAccount extends Fragment {

    private ArrayList<String> mSubjectName = new ArrayList<>();
    private ArrayList<String> mDataName = new ArrayList<>();
    private ArrayList<String> mNumberTest = new ArrayList<>();
    private ArrayList<String> mTopicName = new ArrayList<>();

    String full_name;

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
            for (DataSnapshot tests : dataSnapshot.child(ConstantsNames.RESULTS).child(full_name).getChildren()) {
                initList((String) tests.child(ConstantsNames.SUBJECT).getValue(),
                        (String) tests.child(ConstantsNames.DATA_CREATE).getValue(),
                        tests.getKey(),
                        (String) tests.child(ConstantsNames.TOPIC_NAME).getValue());
            }
        }
        initRecyclerView();
    }

    private void usersInformation(DataSnapshot dataSnapshot) {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            String userID = user.getUid();
            full_name = (String) dataSnapshot.child(ConstantsNames.USERS).child(ConstantsNames.TEACHERS).child(userID).child(ConstantsNames.FULL_NAME).getValue();
        } catch (NullPointerException ex) {
            errorNull();
        }
    }

    private void initList(String subject, String data, String numberTest, String topic) {
        mSubjectName.add(subject);
        mDataName.add(data);
        mNumberTest.add(numberTest);
        mTopicName.add(topic);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view_tests_for_teachers_account);
        RVATestsForTeachersAccount adapter = new RVATestsForTeachersAccount(getActivity(), mSubjectName, mDataName, mNumberTest, full_name, mTopicName);
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
        mDataName.clear();
        mNumberTest.clear();
        mTopicName.clear();
    }
}
