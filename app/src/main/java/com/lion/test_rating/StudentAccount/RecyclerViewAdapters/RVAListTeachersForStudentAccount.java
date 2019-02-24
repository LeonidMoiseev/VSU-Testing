package com.lion.test_rating.StudentAccount.RecyclerViewAdapters;

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
import com.lion.test_rating.StudentAccount.ResultsStudentsActivity;

import java.util.ArrayList;

public class RVAListTeachersForStudentAccount extends RecyclerView.Adapter<RVAListTeachersForStudentAccount.ViewHolder>{

    private ArrayList<String> mTeacher;
    private Context mContext;

    private String course;
    private String group;
    private String full_name;

    public RVAListTeachersForStudentAccount(Context mContext, ArrayList<String> mTeacher, String course, String group, String full_name) {
        this.mTeacher = mTeacher;
        this.mContext = mContext;
        this.course = course;
        this.group = group;
        this.full_name = full_name;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_students_list_teachers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.nameTeacherTV.setText(mTeacher.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStartTest = new Intent(mContext, ResultsStudentsActivity.class);
                intentStartTest.putExtra("nameTeacher", mTeacher.get(position));
                intentStartTest.putExtra("course", course);
                intentStartTest.putExtra("group", group);
                intentStartTest.putExtra("full_name", full_name);
                mContext.startActivity(intentStartTest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTeacher.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTeacherTV;
        RelativeLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            nameTeacherTV = itemView.findViewById(R.id.text_name_teacher);
            parentLayout = itemView.findViewById(R.id.parent_layout_teachers);
        }
    }
}
