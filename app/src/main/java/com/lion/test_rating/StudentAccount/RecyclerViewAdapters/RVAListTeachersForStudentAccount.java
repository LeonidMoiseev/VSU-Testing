package com.lion.test_rating.StudentAccount.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lion.test_rating.R;
import com.lion.test_rating.StudentAccount.AccountStudentActivity;
import com.lion.test_rating.StudentAccount.Fragments.FragmentResultsForStudent;

import java.util.ArrayList;

public class RVAListTeachersForStudentAccount extends RecyclerView.Adapter<RVAListTeachersForStudentAccount.ViewHolder>{

    private ArrayList<String> mTeacher;
    private ArrayList<String> mDepartment;
    private Context mContext;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    FragmentResultsForStudent fragmentResultsForStudent;

    public RVAListTeachersForStudentAccount(Context mContext, ArrayList<String> teacher, ArrayList<String> department) {
        this.mTeacher = teacher;
        this.mDepartment = department;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_students_list_teachers, parent, false);

        Activity activity = (Activity) mContext;
        fragmentManager = activity.getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentResultsForStudent = new FragmentResultsForStudent();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.nameTeacherTV.setText(mTeacher.get(position));
        holder.departmentTV.setText(mDepartment.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                String myMessage = mTeacher.get(position);
                bundle.putString("teacher", myMessage);
                fragmentResultsForStudent.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, fragmentResultsForStudent);
                fragmentTransaction.commit();
                AccountStudentActivity.checkFragment = 1;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTeacher.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTeacherTV, departmentTV;
        LinearLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            nameTeacherTV = itemView.findViewById(R.id.text_name_teacher);
            departmentTV = itemView.findViewById(R.id.text_department);
            parentLayout = itemView.findViewById(R.id.parent_layout_teachers);
        }
    }
}
