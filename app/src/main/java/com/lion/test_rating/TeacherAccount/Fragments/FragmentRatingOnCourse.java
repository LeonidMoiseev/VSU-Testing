package com.lion.test_rating.TeacherAccount.Fragments;

import android.app.Fragment;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Collections;

public class FragmentRatingOnCourse extends Fragment {

    private ArrayList<String> mListPlaceNumber = new ArrayList<>();
    private ArrayList<String> mListNameStudent = new ArrayList<>();
    private ArrayList<String> mListCourseAndGroup = new ArrayList<>();
    private ArrayList<String> mListStudentRating = new ArrayList<>();

    private ArrayList<String> mChangeListPlaceNumber = new ArrayList<>();
    private ArrayList<String> mChangeListNameStudent = new ArrayList<>();
    private ArrayList<String> mChangeListCourseAndGroup = new ArrayList<>();
    private ArrayList<String> mChangeListStudentRating = new ArrayList<>();

    View fragmentView;

    DatabaseReference userDatabase;

    int number;

    TextView textNoData;
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_for_teacher_rating_on_course, container, false);

        textNoData = fragmentView.findViewById(R.id.text_no_data);
        textNoData.setText(getString(R.string.not_data_about_students));

        getActivity().setTitle(getString(R.string.rating_on_course));

        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.chooseList
                , R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        spinner = fragmentView.findViewById(R.id.spinner);
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

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
                        sortingList();
                        initRecyclerView();
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
            if (FragmentRatingForTeacherAccount.courseOnRatingCourse.equals(users.child(ConstantsNames.COURSE).getValue())) {
                textNoData.setText("");
                number++;
                String courseAndGroup = users.child(ConstantsNames.COURSE).getValue() + " курс "
                        + users.child(ConstantsNames.GROUP).getValue() + " группа";

                initList(Integer.toString(number), (String) users.child(ConstantsNames.FULL_NAME).getValue()
                        , courseAndGroup, (String) users.child(ConstantsNames.RATING).getValue());
            }
        }
    }

    private void initList(String number, String name, String courseAndGroup, String rating) {
        mListPlaceNumber.add(number);
        mListNameStudent.add(name);
        mListCourseAndGroup.add(courseAndGroup);
        mListStudentRating.add(rating);
    }

    private void initRecyclerView() {
        switch (spinner.getSelectedItemPosition()) {
            case 0:
                changeListStudents(1, false);
                break;
            case 1:
                changeListStudents(2, false);
                break;
            case 2:
                changeListStudents(3, false);
                break;
            case 3:
                changeListStudents(1, true);
                break;
            case 4:
                changeListStudents(2, true);
                break;
            case 5:
                changeListStudents(3, true);
                break;
            case 6:
                changeListStudents(mListPlaceNumber.size(), true);
                break;
        }

        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view_rating_on_course);
        RVARatingOnCourseForTeacherAccount adapter = new RVARatingOnCourseForTeacherAccount(getActivity()
                , mChangeListPlaceNumber, mChangeListNameStudent, mChangeListCourseAndGroup, mChangeListStudentRating);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }

    private void sortingList() {
        int t1;
        String t2;
        String t3;
        for (int i = 0; i < mListPlaceNumber.size(); i++)
            for (int j = i + 1; j < mListPlaceNumber.size(); j++)
                if (Integer.parseInt(mListStudentRating.get(j)) < Integer.parseInt(mListStudentRating.get(i))) {

                    t1 = Integer.parseInt(mListStudentRating.get(i));
                    mListStudentRating.set(i, mListStudentRating.get(j));
                    mListStudentRating.set(j, Integer.toString(t1));

                    t2 = mListCourseAndGroup.get(i);
                    mListCourseAndGroup.set(i, mListCourseAndGroup.get(j));
                    mListCourseAndGroup.set(j, t2);

                    t3 = mListNameStudent.get(i);
                    mListNameStudent.set(i, mListNameStudent.get(j));
                    mListNameStudent.set(j, t3);
                }

        Collections.sort(mListPlaceNumber, Collections.reverseOrder());
    }

    private void changeListStudents(int sizeList, boolean reverseList) {
        mChangeListPlaceNumber.clear();
        mChangeListNameStudent.clear();
        mChangeListCourseAndGroup.clear();
        mChangeListStudentRating.clear();

        int sizeOriginalList = mListPlaceNumber.size();
        if (sizeList > sizeOriginalList) {
            sizeList = sizeOriginalList;
        }

        if (!reverseList) {
            for (int i = sizeOriginalList - 1; i >= sizeOriginalList - sizeList; i--) {
                mChangeListPlaceNumber.add(mListPlaceNumber.get(i));
                mChangeListNameStudent.add(mListNameStudent.get(i));
                mChangeListCourseAndGroup.add(mListCourseAndGroup.get(i));
                mChangeListStudentRating.add(mListStudentRating.get(i));
            }
        } else {
            for (int i = sizeList - 1; i >= 0; i--) {
                mChangeListPlaceNumber.add(mListPlaceNumber.get(i));
                mChangeListNameStudent.add(mListNameStudent.get(i));
                mChangeListCourseAndGroup.add(mListCourseAndGroup.get(i));
                mChangeListStudentRating.add(mListStudentRating.get(i));
            }
        }
    }

    private void errorNull() {
        Log.d("Errors", "NullPointerException");
        Toast.makeText(getActivity(), "Ошибка соединения с сервером..", Toast.LENGTH_LONG).show();
    }

    private void clearLists() {
        mListPlaceNumber.clear();
        mListNameStudent.clear();
        mListCourseAndGroup.clear();
        mListStudentRating.clear();
        number = 0;
    }
}
