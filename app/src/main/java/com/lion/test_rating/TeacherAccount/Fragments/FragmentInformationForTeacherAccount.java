package com.lion.test_rating.TeacherAccount.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.ArrayList;

public class FragmentInformationForTeacherAccount extends Fragment {

    View fragmentView;
    private AlertDialog dialog;

    EditText courseET;
    EditText groupET;
    EditText textInformationET;
    TextView coursesAndGroups;

    String course_inf;
    String group_inf;
    String text_inf;
    String textCoursesAndGroups;

    ArrayList<String> listCourses = new ArrayList<>();
    ArrayList<String> listGroups = new ArrayList<>();

    ArrayList<String> listSendCourses = new ArrayList<>();
    ArrayList<String> listSendGroups = new ArrayList<>();

    DatabaseReference dataInformation;

    int countInformationBlocks = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_for_teacher_information, container, false);

        FloatingActionButton fab = fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createInformation();
            }
        });

        dataInformation = FirebaseDatabase.getInstance().getReference();

        dataInformation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(ConstantsNames.INFORMATION)) {
                    if (dataSnapshot.child(ConstantsNames.INFORMATION).hasChild(AccountTeacherActivity.mList.get(0))) {
                        countInformationBlocks = (int) dataSnapshot.child(ConstantsNames.INFORMATION)
                                .child(AccountTeacherActivity.mList.get(0)).getChildrenCount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return fragmentView;
    }

    private void createInformation() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        @SuppressLint("InflateParams")
        View mView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_information, null);

        clearLists();

        courseET = mView.findViewById(R.id.ET_course);
        groupET = mView.findViewById(R.id.ET_group);
        textInformationET = mView.findViewById(R.id.text_information);
        coursesAndGroups = mView.findViewById(R.id.courses_and_groups);
        ImageView addCourseAndGroup = mView.findViewById(R.id.addCourseAndGroup);
        Button cancel = mView.findViewById(R.id.btn_cancel);
        Button sendInfo = mView.findViewById(R.id.btn_send_information);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        sendInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInformation();
            }
        });

        addCourseAndGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCoursesAndGroups();
            }
        });

        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();
    }

    private void sendInformation() {
        text_inf = textInformationET.getText().toString().trim();

        dataInformation.child(ConstantsNames.INFORMATION).child(AccountTeacherActivity.mList.get(0))
                .child(Integer.toString(countInformationBlocks)).child(ConstantsNames.INFORMATION).setValue(text_inf);

        countListCourseAndGroup();

        for (int i = 0; i < listSendCourses.size(); i++) {
            dataInformation.child(ConstantsNames.INFORMATION).child(AccountTeacherActivity.mList.get(0))
                    .child(Integer.toString(countInformationBlocks)).child(listSendCourses.get(i))
                    .setValue(listSendGroups.get(i));
        }

        dialog.dismiss();
    }

    private void countListCourseAndGroup() {
        listSendCourses.add(listCourses.get(0));
        String group = listGroups.get(0);
        for (int i = 1; i < listCourses.size(); i++) {
            if (listCourses.get(i).equals(listCourses.get(i - 1))) {
                group = group + " " + listGroups.get(i);
            } else {
                listSendCourses.add(listCourses.get(i));
                listSendGroups.add(group);
                group = listGroups.get(i);
            }
        }
        listSendGroups.add(group);
    }

    private void addCoursesAndGroups() {
        course_inf = courseET.getText().toString().trim();
        group_inf = groupET.getText().toString().trim();

        listCourses.add(course_inf);
        listGroups.add(group_inf);

        textCoursesAndGroups = "";

        for (int i = 0; i < listCourses.size(); i++) {
            if (textCoursesAndGroups.equals("")) {
                textCoursesAndGroups = listCourses.get(i) + " курс " + listGroups.get(i) + " группа";
            } else {
                textCoursesAndGroups = textCoursesAndGroups + "\n" + listCourses.get(i) + " курс "
                        + listGroups.get(i) + " группа";
            }
        }

        coursesAndGroups.setText(textCoursesAndGroups);
    }

    private void clearLists() {
        listCourses.clear();
        listGroups.clear();
        listSendCourses.clear();
        listSendGroups.clear();
    }
}
