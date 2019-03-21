package com.lion.test_rating.StudentAccount.Fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lion.test_rating.ConstantsNames;
import com.lion.test_rating.R;
import com.lion.test_rating.StudentAccount.AccountStudentActivity;
import com.lion.test_rating.StudentAccount.RecyclerViewAdapters.RVARatingOnGroupForStudentAccount;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentRatings extends Fragment {

    View fragmentView;

    DatabaseReference userDatabase;

    TextView tvName;
    TextView tvRating;
    TextView tvPlaceOnCourse;
    TextView tvPlaceOnGroup;

    String sCourse;
    String sGroup;
    String sRating;
    int sPlaceOnCourse;
    int sPlaceOnGroup;
    int countStudentsOnCourse;
    int countStudentsOnGroup;
    int number;

    RelativeLayout layoutStudRating;

    private ArrayList<String> mListPlaceNumber = new ArrayList<>();
    private ArrayList<String> mListNameStudent = new ArrayList<>();
    private ArrayList<String> mListStudentRating = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_for_student_ratings, container, false);

        getActivity().setTitle(getString(R.string.rating_item));

        layoutStudRating = fragmentView.findViewById(R.id.layout_stud_rating);
        tvName = fragmentView.findViewById(R.id.text_name_student);
        tvRating = fragmentView.findViewById(R.id.text_rating);
        tvPlaceOnCourse = fragmentView.findViewById(R.id.text_place_on_course);
        tvPlaceOnGroup = fragmentView.findViewById(R.id.text_place_on_group);

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
        sPlaceOnCourse = 0;
        sPlaceOnGroup = 0;
        countStudentsOnCourse = 0;
        countStudentsOnGroup = 0;
        sRating = AccountStudentActivity.mListUserInformation.get(4);
        sCourse = AccountStudentActivity.mListUserInformation.get(2);
        sGroup = AccountStudentActivity.mListUserInformation.get(3);

        for (DataSnapshot users : dataSnapshot.getChildren()) {
            if (users.child(ConstantsNames.COURSE).getValue().equals(sCourse)) {
                countStudentsOnCourse++;
                if (Integer.parseInt(sRating) > Integer.parseInt((String) users
                        .child(ConstantsNames.RATING).getValue())) {
                    sPlaceOnCourse++;
                }
                if (users.child(ConstantsNames.GROUP).getValue().equals(sGroup)) {
                    countStudentsOnGroup++;
                    if (Integer.parseInt(sRating) > Integer.parseInt((String) users
                            .child(ConstantsNames.RATING).getValue())) {
                        sPlaceOnGroup++;
                    }
                }
            }
        }
        for (DataSnapshot users : dataSnapshot.getChildren()) {
            if (users.child(ConstantsNames.COURSE).getValue().equals(sCourse)
                    &&users.child(ConstantsNames.GROUP).getValue().equals(sGroup)) {
                number++;

                initList(Integer.toString(number), (String) users.child(ConstantsNames.FULL_NAME).getValue()
                        , (String) users.child(ConstantsNames.RATING).getValue());
            }
        }
        initRecyclerView();
        initInfo();
    }

    private void initList(String number, String name, String rating) {
        mListPlaceNumber.add(number);
        mListNameStudent.add(name);
        mListStudentRating.add(rating);
    }

    private void initRecyclerView() {
        sortingList();

        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view_for_student_rating_on_group);
        RVARatingOnGroupForStudentAccount adapter = new RVARatingOnGroupForStudentAccount(getActivity()
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

    @SuppressLint("SetTextI18n")
    private void initInfo() {
        tvRating.setText("Рейтинг: " + sRating);
        tvPlaceOnCourse.setText("Место на курсе: " + Integer.toString(countStudentsOnCourse - sPlaceOnCourse));
        tvPlaceOnGroup.setText("Место в группе: " + Integer.toString(countStudentsOnGroup - sPlaceOnGroup));
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
