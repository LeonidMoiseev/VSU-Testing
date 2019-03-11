package com.lion.test_rating.TeacherAccount.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lion.test_rating.R;
import com.lion.test_rating.TeacherAccount.ResultsActivityForTeachers;
import com.lion.test_rating.TeacherAccount.StatisticsActivity;

import java.util.ArrayList;

public class RVATestsForTeachersAccount extends RecyclerView.Adapter<RVATestsForTeachersAccount.ViewHolder>{

    private ArrayList<String> mSubject;
    private ArrayList<String> mData;
    private ArrayList<String> mNumberTest;
    private ArrayList<String> mTopicName;
    private Context mContext;

    private AlertDialog dialog;

    public RVATestsForTeachersAccount(Context mContext, ArrayList<String> mSubject, ArrayList<String> mData
            , ArrayList<String> mNumberTest, ArrayList<String> mTopic) {
        this.mSubject = mSubject;
        this.mData = mData;
        this.mContext = mContext;
        this.mNumberTest = mNumberTest;
        this.mTopicName = mTopic;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_teachers_list_tests, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.subjectTV.setText(mSubject.get(position));
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
        View mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_open_results_or_statistics, null);

        Button btnResults = mView.findViewById(R.id.btn_results);
        Button btnStatistics = mView.findViewById(R.id.btn_statistics);

        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();

        btnResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStartTest = new Intent(mContext, ResultsActivityForTeachers.class);
                intentStartTest.putExtra("numberTest", mNumberTest.get(position));
                mContext.startActivity(intentStartTest);
                dialog.dismiss();
            }
        });

        btnStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStartTest = new Intent(mContext, StatisticsActivity.class);
                intentStartTest.putExtra("subjectName", mSubject.get(position));
                intentStartTest.putExtra("topicName", mTopicName.get(position));
                intentStartTest.putExtra("dataCreateTest", mData.get(position));
                intentStartTest.putExtra("numberTest", mNumberTest.get(position));
                mContext.startActivity(intentStartTest);
                dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSubject.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView subjectTV, dataCreateTestTV, topicTV;
        RelativeLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            subjectTV = itemView.findViewById(R.id.text_name_subject);
            topicTV = itemView.findViewById(R.id.text_topic_name);
            dataCreateTestTV = itemView.findViewById(R.id.text_data);
            parentLayout = itemView.findViewById(R.id.parent_layout_tests_for_teachers_account);
        }
    }
}
