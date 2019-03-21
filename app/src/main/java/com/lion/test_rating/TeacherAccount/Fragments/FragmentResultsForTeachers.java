package com.lion.test_rating.TeacherAccount.Fragments;

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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lion.test_rating.ConstantsNames;
import com.lion.test_rating.R;
import com.lion.test_rating.TeacherAccount.AccountTeacherActivity;
import com.lion.test_rating.TeacherAccount.RecyclerViewAdapters.RVAResultsForTeacherAccount;

import java.util.ArrayList;

public class FragmentResultsForTeachers extends Fragment {

    FirebaseDatabase mFirebaseDatabase;

    String numberTest;

    ImageButton previousList, nextList;
    TextView courseAndGroup;

    private int countCourseAndGroup;

    private ArrayList<String> mCourse = new ArrayList<>();
    private ArrayList<String> mGroup = new ArrayList<>();
    private ArrayList<String> mStudents = new ArrayList<>();
    private ArrayList<String> mPoints = new ArrayList<>();

    View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_for_teacher_list_results, container, false);

        getActivity().setTitle(getString(R.string.result_item));

        previousList = fragmentView.findViewById(R.id.previous_list);
        nextList = fragmentView.findViewById(R.id.next_list);
        courseAndGroup = fragmentView.findViewById(R.id.course_group);

        countCourseAndGroup = 0;

        numberTest = this.getArguments().getString("numberTest");

        studentsData();

        previousList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countCourseAndGroup - 1 >= 0) {
                    countCourseAndGroup--;
                    studentsData();
                }
            }
        });

        nextList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countCourseAndGroup + 1 < mCourse.size()) {
                    countCourseAndGroup++;
                    studentsData();
                }
            }
        });

        return fragmentView;
    }

    private void setCourseAndGroupList(DataSnapshot dataSnapshot) {
        for (DataSnapshot course : dataSnapshot.getChildren()) {
            if (course.hasChildren()) {
                for (DataSnapshot group : course.getChildren()) {
                    initList(course.getKey(), group.getKey());
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setStudentsList(DataSnapshot dataSnapshot, int count) {
        for (DataSnapshot students : dataSnapshot.child(mCourse.get(count)).child(mGroup.get(count)).getChildren()) {
            initListStudentsPoints(students.getKey(), (String) students.getValue());
        }
        initRecyclerView();
        courseAndGroup.setText(mCourse.get(count) + " курс " + mGroup.get(count) + " группа");
    }

    private void studentsData() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference resultDatabase = mFirebaseDatabase.getReference().child(ConstantsNames.RESULTS)
                .child(AccountTeacherActivity.mListUserInformation.get(0)).child(numberTest);
        resultDatabase.keepSynced(true);

        try {
            resultDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    clearLists();
                    if (dataSnapshot.exists()) {
                        setCourseAndGroupList(dataSnapshot);
                        try {
                            setStudentsList(dataSnapshot, countCourseAndGroup);
                        } catch (IndexOutOfBoundsException ex) {
                            errorNull();
                        }
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

    }

    private void initList(String course, String group) {
        mCourse.add(course);
        mGroup.add(group);
    }

    private void initListStudentsPoints(String student, String points) {
        mStudents.add(student);
        mPoints.add(points);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view_results_teachers);
        RVAResultsForTeacherAccount adapter = new RVAResultsForTeacherAccount(getActivity(), mStudents, mPoints);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void errorNull() {
        Log.d("Errors", "NullPointerException");
        Toast.makeText(getActivity(), "Ошибка соединения с сервером..", Toast.LENGTH_LONG).show();
    }

    private void clearLists() {
        mCourse.clear();
        mGroup.clear();
        mStudents.clear();
        mPoints.clear();
    }
}
