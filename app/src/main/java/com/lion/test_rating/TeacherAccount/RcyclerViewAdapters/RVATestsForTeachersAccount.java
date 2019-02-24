package com.lion.test_rating.TeacherAccount.RcyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lion.test_rating.R;
import com.lion.test_rating.TeacherAccount.ResultsTeachersActivity;

import java.util.ArrayList;

public class RVATestsForTeachersAccount extends RecyclerView.Adapter<RVATestsForTeachersAccount.ViewHolder>{

    private ArrayList<String> mSubject;
    private ArrayList<String> mData;
    private ArrayList<String> mNumberTest;
    private Context mContext;

    private String full_name;

    public RVATestsForTeachersAccount(Context mContext, ArrayList<String> mSubject, ArrayList<String> mData
            , ArrayList<String> mNumberTest, String full_name) {
        this.mSubject = mSubject;
        this.mData = mData;
        this.mContext = mContext;
        this.mNumberTest = mNumberTest;
        this.full_name = full_name;
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

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStartTest = new Intent(mContext, ResultsTeachersActivity.class);
                intentStartTest.putExtra("full_name", full_name);
                intentStartTest.putExtra("numberTest", mNumberTest.get(position));
                mContext.startActivity(intentStartTest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSubject.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView subjectTV, dataCreateTestTV;
        RelativeLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            subjectTV = itemView.findViewById(R.id.text_name_subject);
            dataCreateTestTV = itemView.findViewById(R.id.text_data);
            parentLayout = itemView.findViewById(R.id.parent_layout_tests_for_teachers_account);
        }
    }
}
