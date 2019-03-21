package com.lion.test_rating.StudentAccount.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lion.test_rating.R;
import com.lion.test_rating.StudentAccount.StartTestActivity;

import java.util.ArrayList;

public class RVAListTestsForStudentAccount extends RecyclerView.Adapter<RVAListTestsForStudentAccount.ViewHolder>{

    private ArrayList<String> mSubject;
    private ArrayList<String> mTeacher;
    private ArrayList<String> mData;
    private ArrayList<String> mNumberTest;
    private ArrayList<String> mRestrictionTest;
    private ArrayList<String> mTestTime;
    private ArrayList<String> mTopicName;
    private Context mContext;

    private AlertDialog dialog;

    public RVAListTestsForStudentAccount(Context mContext, ArrayList<String> mSubject, ArrayList<String> mTeacher, ArrayList<String> mData
            , ArrayList<String> mNumberTest, ArrayList<String> mRestrictionTest, ArrayList<String> mTestTime, ArrayList<String> mTopic) {
        this.mSubject = mSubject;
        this.mTeacher = mTeacher;
        this.mData = mData;
        this.mNumberTest = mNumberTest;
        this.mRestrictionTest = mRestrictionTest;
        this.mTestTime = mTestTime;
        this.mContext = mContext;
        this.mTopicName = mTopic;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_students_list_tests, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.subjectTV.setText(mSubject.get(position));
        holder.nameTeacherTV.setText(mTeacher.get(position));
        holder.dataCreateTestTV.setText(mData.get(position));
        holder.topicTV.setText(mTopicName.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(position);
            }
        });
    }

    private void showDialog(final int position) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        @SuppressLint("InflateParams")
        View mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_open_test, null);

        Button btnYes = mView.findViewById(R.id.btn_yes);
        Button btnNo = mView.findViewById(R.id.btn_no);

        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStartTest = new Intent(mContext, StartTestActivity.class);
                intentStartTest.putExtra("nameTeacher", mTeacher.get(position));
                intentStartTest.putExtra("numberTest", mNumberTest.get(position));
                intentStartTest.putExtra("restrictionTest", mRestrictionTest.get(position));
                intentStartTest.putExtra("testTime", mTestTime.get(position));
                intentStartTest.putExtra("dataCreateTest", mData.get(position));
                intentStartTest.putExtra("nameSubject", mSubject.get(position));
                intentStartTest.putExtra("topicName", mTopicName.get(position));
                mContext.startActivity(intentStartTest);
                dialog.dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSubject.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView subjectTV, nameTeacherTV, dataCreateTestTV, topicTV;
        RelativeLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            subjectTV = itemView.findViewById(R.id.text_name_subject);
            nameTeacherTV = itemView.findViewById(R.id.text_name_teacher);
            dataCreateTestTV = itemView.findViewById(R.id.text_data);
            topicTV = itemView.findViewById(R.id.text_topic_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
