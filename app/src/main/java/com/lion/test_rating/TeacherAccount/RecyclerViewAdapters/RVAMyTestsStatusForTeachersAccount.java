package com.lion.test_rating.TeacherAccount.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.lion.test_rating.TeacherAccount.AccountTeacherActivity;

import java.util.ArrayList;

public class RVAMyTestsStatusForTeachersAccount extends RecyclerView.Adapter<RVAMyTestsStatusForTeachersAccount.ViewHolder>{

    private ArrayList<String> mSubject;
    private ArrayList<String> mData;
    private ArrayList<String> mNumberTest;
    private ArrayList<String> mTopicName;
    private ArrayList<String> mStatusTest;
    private Context mContext;

    private AlertDialog dialog;

    private DatabaseReference databaseTestsReference;
    private DatabaseReference databaseReference;

    private String nameStudent;
    private EditText fioStudentET;

    public RVAMyTestsStatusForTeachersAccount(Context mContext, ArrayList<String> mSubject, ArrayList<String> mData
            , ArrayList<String> mNumberTest, ArrayList<String> mTopic, ArrayList<String> mStatus) {
        this.mSubject = mSubject;
        this.mData = mData;
        this.mContext = mContext;
        this.mNumberTest = mNumberTest;
        this.mTopicName = mTopic;
        this.mStatusTest = mStatus;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_teachers_list_my_tests_status, parent, false);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        databaseTestsReference = mFirebaseDatabase.getReference().child(ConstantsNames.TESTS)
                .child(AccountTeacherActivity.mListUserInformation.get(0));
        databaseTestsReference.keepSynced(true);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.subjectTV.setText(mSubject.get(position));
        holder.dataCreateTestTV.setText(mData.get(position));
        holder.topicTV.setText(mTopicName.get(position));
        holder.statusTV.setText("Статус теста: " + mStatusTest.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSubject.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView subjectTV, dataCreateTestTV, topicTV, statusTV;
        RelativeLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            subjectTV = itemView.findViewById(R.id.text_name_subject);
            topicTV = itemView.findViewById(R.id.text_topic_name);
            dataCreateTestTV = itemView.findViewById(R.id.text_data);
            statusTV = itemView.findViewById(R.id.text_status_test);
            parentLayout = itemView.findViewById(R.id.parent_layout_my_tests_status_for_teachers_account);
        }
    }

    private void showDialog(final int position) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        @SuppressLint("InflateParams")
        View mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_change_test, null);

        Button btnDeleteTest = mView.findViewById(R.id.btn_delete_test);
        Button btnCloseTest = mView.findViewById(R.id.btn_status_close);
        Button btnOpenTest = mView.findViewById(R.id.btn_status_open);
        Button btnOpenForStudent = mView.findViewById(R.id.btn_open_test_for_student);
        fioStudentET = mView.findViewById(R.id.fio_student_ET);

        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();

        btnDeleteTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(mNumberTest.get(position)).removeValue();
                Toast.makeText(mContext, R.string.test_deleted, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnCloseTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseTestsReference.child(mNumberTest.get(position))
                        .child(ConstantsNames.STATUS_TEST).setValue("Закрыт");
                Toast.makeText(mContext, R.string.status_changed, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnOpenTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseTestsReference.child(mNumberTest.get(position))
                        .child(ConstantsNames.STATUS_TEST).setValue("Открыт");
                Toast.makeText(mContext, R.string.status_changed, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnOpenForStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameStudent = fioStudentET.getText().toString().trim();
                validateForm();
                if (!TextUtils.isEmpty(nameStudent)) {
                    openTestForStudent(position);
                }
            }
        });
    }

    private void openTestForStudent(final int position) {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = mFirebaseDatabase.getReference();
        databaseReference.keepSynced(true);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataCompleteTest = dataSnapshot.child(ConstantsNames.TESTS)
                        .child(AccountTeacherActivity.mListUserInformation.get(0))
                        .child(mNumberTest.get(position))
                        .child(ConstantsNames.USER_COMPLETE_TEST);

                if (dataCompleteTest.exists()) {
                    if (dataCompleteTest.hasChild(nameStudent)) {

                        databaseReference.child(ConstantsNames.TESTS)
                                .child(AccountTeacherActivity.mListUserInformation.get(0))
                                .child(mNumberTest.get(position))
                                .child(ConstantsNames.USER_COMPLETE_TEST)
                                .child(nameStudent).removeValue();

                        String points = (String) dataCompleteTest.child(nameStudent).getValue();

                        for (DataSnapshot students : dataSnapshot.child(ConstantsNames.USERS)
                                .child(ConstantsNames.STUDENTS).getChildren()) {

                            if (nameStudent.equals(students.child(ConstantsNames.FULL_NAME).getValue())) {
                                String rating = (String) students.child(ConstantsNames.RATING).getValue();
                                String key = students.getKey();

                                assert key != null;
                                databaseReference.child(ConstantsNames.USERS).child(ConstantsNames.STUDENTS)
                                        .child(key).child(ConstantsNames.RATING)
                                        .setValue(Integer.toString(Integer.parseInt(rating) - Integer.parseInt(points)));
                                dialog.dismiss();
                                Toast.makeText(mContext, "Тест открыт для: " + nameStudent, Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }

                    } else Toast.makeText(mContext, R.string.this_student_not_exist, Toast.LENGTH_SHORT).show();
                } else Toast.makeText(mContext, R.string.this_student_not_exist, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void validateForm() {
        if (TextUtils.isEmpty(nameStudent)) {
            fioStudentET.setError(mContext.getString(R.string.required));
        } else {
            fioStudentET.setError(null);
        }
    }
}
