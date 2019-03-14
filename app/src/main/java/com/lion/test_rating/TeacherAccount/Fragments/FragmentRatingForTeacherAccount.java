package com.lion.test_rating.TeacherAccount.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.lion.test_rating.R;
import com.lion.test_rating.TeacherAccount.AccountTeacherActivity;

public class FragmentRatingForTeacherAccount extends Fragment {

    Button searchRatingOnCourseBtn;
    Button searchRatingOnGroupBtn;
    Button searchStudentRatingBtn;

    EditText courseOnRatingCourseET;
    EditText courseOnRatingGroupET;
    EditText groupOnRatingGroupET;
    EditText nameStudentOnStudentRatingET;

    static String courseOnRatingCourse;
    static String courseOnRatingGroup;
    static String groupOnRatingGroup;
    static String nameStudentOnStudentRating;

    View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_for_teacher_rating_select, container, false);

        searchRatingOnCourseBtn = fragmentView.findViewById(R.id.search_rating_on_course);
        searchRatingOnGroupBtn = fragmentView.findViewById(R.id.search_rating_on_group);
        searchStudentRatingBtn = fragmentView.findViewById(R.id.search_student_rating);

        courseOnRatingCourseET = fragmentView.findViewById(R.id.course_rating_on_course_ET);
        courseOnRatingGroupET = fragmentView.findViewById(R.id.course_rating_on_group_ET);
        groupOnRatingGroupET = fragmentView.findViewById(R.id.group_rating_on_group_ET);
        nameStudentOnStudentRatingET = fragmentView.findViewById(R.id.student_ET);

        searchRatingOnCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCourseRating();
            }
        });

        searchRatingOnGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchGroupRating();
            }
        });

        searchStudentRatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchStudentRating();
            }
        });

        return fragmentView;
    }

    private void searchCourseRating() {
        courseOnRatingCourse = courseOnRatingCourseET.getText().toString().trim();
        validateForm1();
        if (!TextUtils.isEmpty(courseOnRatingCourse)) {
            FragmentRatingOnCourse fragment = new FragmentRatingOnCourse();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            AccountTeacherActivity.checkFragment = 1;
            hideKeyboardFrom(getActivity(), fragmentView);
        }
    }

    private void searchGroupRating() {
        courseOnRatingGroup = courseOnRatingGroupET.getText().toString().trim();
        groupOnRatingGroup = groupOnRatingGroupET.getText().toString().trim();
        validateForm2();
        if (!TextUtils.isEmpty(courseOnRatingGroup) && !TextUtils.isEmpty(groupOnRatingGroup)) {

        }
    }

    private void searchStudentRating() {
        nameStudentOnStudentRating = nameStudentOnStudentRatingET.getText().toString().trim();
        validateForm3();
        if (!TextUtils.isEmpty(nameStudentOnStudentRating)) {

        }
    }

    private void validateForm1() {
        if (TextUtils.isEmpty(courseOnRatingCourse)) {
            courseOnRatingCourseET.setError(getActivity().getString(R.string.required));
        } else {
            courseOnRatingCourseET.setError(null);
        }
    }

    private void validateForm2() {
        if (TextUtils.isEmpty(courseOnRatingGroup)) {
            courseOnRatingGroupET.setError(getActivity().getString(R.string.required));
        } else {
            courseOnRatingGroupET.setError(null);
        }

        if (TextUtils.isEmpty(groupOnRatingGroup)) {
            groupOnRatingGroupET.setError(getActivity().getString(R.string.required));
        } else {
            groupOnRatingGroupET.setError(null);
        }
    }

    private void validateForm3() {
        if (TextUtils.isEmpty(nameStudentOnStudentRating)) {
            nameStudentOnStudentRatingET.setError(getActivity().getString(R.string.required));
        } else {
            nameStudentOnStudentRatingET.setError(null);
        }
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
