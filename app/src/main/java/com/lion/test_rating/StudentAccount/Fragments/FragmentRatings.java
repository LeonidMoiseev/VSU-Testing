package com.lion.test_rating.StudentAccount.Fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.lion.test_rating.StudentAccount.StudentAccountActivity;

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

    RelativeLayout layoutStudRating;

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
        userDatabase = mFirebaseDatabase.getReference().child(ConstantsNames.USERS)
                .child(ConstantsNames.STUDENTS);
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
                    errorNull();
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
        sRating = StudentAccountActivity.mListUserInformation.get(4);
        sCourse = StudentAccountActivity.mListUserInformation.get(2);
        sGroup = StudentAccountActivity.mListUserInformation.get(3);

        for (DataSnapshot users : dataSnapshot.getChildren()) {
            if (sCourse.equals(users.child(ConstantsNames.COURSE).getValue())) {
                countStudentsOnCourse++;
                if (Integer.parseInt(sRating) > Integer.parseInt((String) users
                        .child(ConstantsNames.RATING).getValue())) {
                    sPlaceOnCourse++;
                }
                if (sGroup.equals(users.child(ConstantsNames.GROUP).getValue())) {
                    countStudentsOnGroup++;
                    if (Integer.parseInt(sRating) > Integer.parseInt((String) users
                            .child(ConstantsNames.RATING).getValue())) {
                        sPlaceOnGroup++;
                    }
                }
            }
        }
        initInfo();
    }

    @SuppressLint("SetTextI18n")
    private void initInfo() {
        tvRating.setText("Рейтинг: " + sRating);
        tvName.setText(StudentAccountActivity.mListUserInformation.get(0));
        tvPlaceOnCourse.setText("Место на курсе: " + Integer.toString(countStudentsOnCourse - sPlaceOnCourse));
        tvPlaceOnGroup.setText("Место в группе: " + Integer.toString(countStudentsOnGroup - sPlaceOnGroup));
    }

    private void errorNull() {
        Log.d("Errors", "NullPointerException");
        Toast.makeText(getActivity(), "Ошибка соединения с сервером..", Toast.LENGTH_LONG).show();
    }
}
