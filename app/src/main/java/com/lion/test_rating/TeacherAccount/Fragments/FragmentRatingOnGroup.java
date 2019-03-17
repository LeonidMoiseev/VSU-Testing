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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lion.test_rating.ConstantsNames;
import com.lion.test_rating.R;
import com.lion.test_rating.TeacherAccount.RecyclerViewAdapters.RVARatingOnCourseForTeacherAccount;
import com.lion.test_rating.TeacherAccount.RecyclerViewAdapters.RVARatingOnGroupForTeacherAccount;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentRatingOnGroup extends Fragment {

    private ArrayList<String> mListPlaceNumber = new ArrayList<>();
    private ArrayList<String> mListNameStudent = new ArrayList<>();
    private ArrayList<String> mListStudentRating = new ArrayList<>();

    View fragmentView;

    DatabaseReference userDatabase;

    int number;

    TextView textNoData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_for_teacher_rating_on_group, container, false);

        textNoData = fragmentView.findViewById(R.id.text_no_data);
        textNoData.setText(getString(R.string.not_data_about_students));

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        userDatabase = mFirebaseDatabase.getReference().child(ConstantsNames.USERS).child(ConstantsNames.STUDENTS);
        userDatabase.keepSynced(true);

        try {
            userDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        clearLists();
                        openUserDatabase(dataSnapshot);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException ex) {
            errorNull();
        }

        return fragmentView;
    }

    private void openUserDatabase(DataSnapshot dataSnapshot) {
        for (DataSnapshot users : dataSnapshot.getChildren()) {
            if (users.child(ConstantsNames.COURSE).getValue().equals(FragmentRatingForTeacherAccount.courseOnRatingGroup)
                    &&users.child(ConstantsNames.GROUP).getValue().equals(FragmentRatingForTeacherAccount.groupOnRatingGroup)) {
                textNoData.setText("");
                number++;

                initList(Integer.toString(number), (String) users.child(ConstantsNames.FULL_NAME).getValue()
                        , (String) users.child(ConstantsNames.RATING).getValue());
            }
        }
        initRecyclerView();
    }

    private void initList(String number, String name, String rating) {
        mListPlaceNumber.add(number);
        mListNameStudent.add(name);
        mListStudentRating.add(rating);
    }

    private void initRecyclerView() {
        sortingList();

        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view_rating_on_group);
        RVARatingOnGroupForTeacherAccount adapter = new RVARatingOnGroupForTeacherAccount(getActivity()
                , mListPlaceNumber, mListNameStudent, mListStudentRating);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void sortingList() {
        int t1;
        String t2;
        for (int i = 0; i < mListPlaceNumber.size(); i++)
            for (int j = i + 1; j < mListPlaceNumber.size(); j++)
                if (Integer.parseInt(mListStudentRating.get(j)) < Integer.parseInt(mListStudentRating.get(i))) {

                    t1 = Integer.parseInt(mListStudentRating.get(i));
                    mListStudentRating.set(i, mListStudentRating.get(j));
                    mListStudentRating.set(j, Integer.toString(t1));

                    t2 = mListNameStudent.get(i);
                    mListNameStudent.set(i, mListNameStudent.get(j));
                    mListNameStudent.set(j, t2);
                }

        Collections.sort(mListPlaceNumber, Collections.reverseOrder());
    }

    private void errorNull() {
        Log.d("Errors", "NullPointerException");
        Toast.makeText(getActivity(), "Ошибка соединения с сервером..", Toast.LENGTH_LONG).show();
    }

    private void clearLists() {
        mListPlaceNumber.clear();
        mListNameStudent.clear();
        mListStudentRating.clear();
        number = 0;
    }
}
