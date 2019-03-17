package com.lion.test_rating.TeacherAccount.Fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lion.test_rating.ConstantsNames;
import com.lion.test_rating.R;

public class FragmentRatingStudent extends Fragment {

    View fragmentView;

    DatabaseReference userDatabase;

    TextView textNoData;
    TextView tvName;
    TextView tvCourseAndGroup;
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

    LinearLayout layoutStudRating;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_for_teacher_rating_student, container, false);

        textNoData = fragmentView.findViewById(R.id.text_no_data);
        textNoData.setText(getString(R.string.not_data_about_students));
        layoutStudRating = fragmentView.findViewById(R.id.layout_stud_rating);

        tvName = fragmentView.findViewById(R.id.text_name_student);
        tvCourseAndGroup = fragmentView.findViewById(R.id.text_course_and_group);
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
        for (DataSnapshot users : dataSnapshot.getChildren()) {
            if (users.child(ConstantsNames.FULL_NAME).getValue()
                    .equals(FragmentRatingForTeacherAccount.nameStudentOnStudentRating)) {
                textNoData.setText("");
                layoutStudRating.setVisibility(LinearLayout.VISIBLE);
                sRating = (String) users.child(ConstantsNames.RATING).getValue();
                sCourse = (String) users.child(ConstantsNames.COURSE).getValue();
                sGroup = (String) users.child(ConstantsNames.GROUP).getValue();


                for (DataSnapshot users2 : dataSnapshot.getChildren()) {
                    if (users2.child(ConstantsNames.COURSE).getValue().equals(sCourse)) {
                        countStudentsOnCourse++;
                        if (Integer.parseInt(sRating) > Integer.parseInt((String) users2
                                .child(ConstantsNames.RATING).getValue())) {
                            sPlaceOnCourse++;
                        }
                        if (users2.child(ConstantsNames.GROUP).getValue().equals(sGroup)) {
                            countStudentsOnGroup++;
                            if (Integer.parseInt(sRating) > Integer.parseInt((String) users2
                                    .child(ConstantsNames.RATING).getValue())) {
                                sPlaceOnGroup++;
                            }
                        }
                    }
                }
                break;
            }
        }
        initInfo();
    }

    @SuppressLint("SetTextI18n")
    private void initInfo() {
        tvName.setText(FragmentRatingForTeacherAccount.nameStudentOnStudentRating);
        tvCourseAndGroup.setText(sCourse + " курс " + sGroup + " группа");
        tvRating.setText("Рейтинг: " + sRating);
        tvPlaceOnCourse.setText("Место на курсе: " + Integer.toString(countStudentsOnCourse - sPlaceOnCourse));
        tvPlaceOnGroup.setText("Место в группе: " + Integer.toString(countStudentsOnGroup - sPlaceOnGroup));
    }

    private void errorNull() {
        Log.d("Errors", "NullPointerException");
        Toast.makeText(getActivity(), "Ошибка соединения с сервером..", Toast.LENGTH_LONG).show();
    }
}
