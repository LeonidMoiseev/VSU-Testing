package com.lion.test_rating.TeacherAccount;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateInformation {

    private Context context;
    private int countInformationBlocks = 0;

    private AlertDialog dialog;

    private EditText courseET;
    private EditText groupET;
    private EditText textInformationET;
    private TextView coursesAndGroups;

    private String course_inf;
    private String group_inf;
    private String text_inf;
    private String textCoursesAndGroups;

    private ArrayList<String> listCourses = new ArrayList<>();
    private ArrayList<String> listGroups = new ArrayList<>();
    private ArrayList<String> listNumberInformationBlock = new ArrayList<>();

    private DatabaseReference dataInformation;

    public CreateInformation(Context context) {
        this.context = context;
    }

    public void openData() {
        dataInformation = FirebaseDatabase.getInstance().getReference();

        dataInformation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(ConstantsNames.INFORMATION)) {

                    if (dataSnapshot.child(ConstantsNames.INFORMATION)
                            .hasChild(AccountTeacherActivity.mListUserInformation.get(0))) {

                        for (DataSnapshot lastInfo : dataSnapshot.child(ConstantsNames.INFORMATION)
                                .child(AccountTeacherActivity.mListUserInformation.get(0)).getChildren()) {
                            listNumberInformationBlock.add(lastInfo.getKey());
                        }
                        countInformationBlocks = Integer.parseInt(listNumberInformationBlock
                                .get(listNumberInformationBlock.size() - 1)) + 1;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        createInformation();
    }

    private void createInformation() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        @SuppressLint("InflateParams")
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_create_information, null);

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
                text_inf = textInformationET.getText().toString().trim();
                validateForm2();
                if (!TextUtils.isEmpty(text_inf) && !TextUtils.isEmpty(coursesAndGroups.getText()) && isOnline()) {
                    sendInformation();
                } else if (!isOnline()) {
                    Toast.makeText(context, "Нет соединения с интернетом!", Toast.LENGTH_LONG).show();
                }
            }
        });

        addCourseAndGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                course_inf = courseET.getText().toString().trim();
                group_inf = groupET.getText().toString().trim();
                validateForm1();
                if (!TextUtils.isEmpty(course_inf) && !TextUtils.isEmpty(group_inf)) {
                    addCoursesAndGroups();
                }
            }
        });

        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();
    }

    private void sendInformation() {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String date = df.format(Calendar.getInstance().getTime());

        dataInformation.child(ConstantsNames.INFORMATION).child(AccountTeacherActivity.mListUserInformation.get(0))
                .child(Integer.toString(countInformationBlocks)).child(ConstantsNames.INFORMATION).setValue(text_inf);
        dataInformation.child(ConstantsNames.INFORMATION).child(AccountTeacherActivity.mListUserInformation.get(0))
                .child(Integer.toString(countInformationBlocks)).child(ConstantsNames.DATE_CREATE).setValue(date);

        for (int i = 0; i < listCourses.size(); i++) {
            String text = listCourses.get(i) + " курс " + listGroups.get(i) + " группа";
            dataInformation.child(ConstantsNames.INFORMATION).child(AccountTeacherActivity.mListUserInformation.get(0))
                    .child(Integer.toString(countInformationBlocks)).child(ConstantsNames.COURSES_AND_GROUPS)
                    .child(Integer.toString(i)).setValue(text);
        }

        dialog.dismiss();
    }

    private void addCoursesAndGroups() {
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
        listNumberInformationBlock.clear();
    }

    private void validateForm1() {

        if (TextUtils.isEmpty(course_inf)) {
            courseET.setError(context.getString(R.string.required));
        } else {
            courseET.setError(null);
        }

        if (TextUtils.isEmpty(group_inf)) {
            groupET.setError(context.getString(R.string.required));
        } else {
            groupET.setError(null);
        }
    }

    private void validateForm2() {
        if (TextUtils.isEmpty(text_inf)) {
            textInformationET.setError(context.getString(R.string.required));
        } else {
            textInformationET.setError(null);
        }

        if (TextUtils.isEmpty(coursesAndGroups.getText())) {
            Toast.makeText(context, "Добавьте курсы и группы для отправки информации", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else {
            return true;
        }
    }
}
